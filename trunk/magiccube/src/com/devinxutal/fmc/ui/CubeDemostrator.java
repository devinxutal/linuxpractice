package com.devinxutal.fmc.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.control.CubeController;
import com.devinxutal.fmc.control.Move;
import com.devinxutal.fmc.control.MoveController;
import com.devinxutal.fmc.control.MoveControllerListener;
import com.devinxutal.fmc.control.MoveSequence;
import com.devinxutal.fmc.control.MoveController.State;
import com.devinxutal.fmc.util.SymbolMoveUtil;

public class CubeDemostrator extends ViewGroup implements
		MoveControllerListener {
	private CubeController cubeController;

	private MoveController moveController;

	// widgets
	private CubeView cubeView;
	private MoveSequenceIndicator indicator;
	private LinearLayout buttonBar;
	private ImageButton prevButton;
	private ImageButton nextButton;
	private ImageButton playButton;

	//
	private String[] symbols;
	private int current;
	private Map<Integer, Integer> sequenceIndexToSymbolsIndex = new HashMap<Integer, Integer>();

	public CubeDemostrator(Context context, String[] symbols) {
		super(context);
		init();

		this.symbols = symbols;
		indicator.setMoveSymbols(symbols);
		current = 0;

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!changed) {
			return;
		}
		buttonBar.measure(r - l, b - t);
		int h_buttonBar = buttonBar.getMeasuredHeight();
		indicator.measure(r - l, b - t);
		int h_indicator = indicator.getMeasuredHeight();
		int h_cubeView = b - t - h_buttonBar - h_indicator;

		cubeView.layout(l, 0, r, h_cubeView);
		indicator.layout(l, h_cubeView, r, h_cubeView + h_indicator);
		buttonBar.layout(l, h_cubeView + h_indicator, r, b - t);

	}

	private void init() {

		cubeController = new CubeController(getContext(), true);
		moveController = new MoveController(cubeController);

		this.moveController.addMoveControllerListener(this);
		this.indicator = new MoveSequenceIndicator(this.getContext());
		this.cubeView = cubeController.getCubeView();

		this.addView(cubeView, new LayoutParams(320, 320));
		this.addView(indicator);

		prevButton = new ImageButton(getContext());
		prevButton.setBackgroundResource(R.drawable.play_button);
		prevButton.setImageResource(R.drawable.icon_prev);

		nextButton = new ImageButton(getContext());
		nextButton.setBackgroundResource(R.drawable.play_button);
		nextButton.setImageResource(R.drawable.icon_next);

		playButton = new ImageButton(getContext());
		playButton.setBackgroundResource(R.drawable.play_button);
		playButton.setImageResource(R.drawable.icon_play);

		buttonBar = new LinearLayout(getContext());
		buttonBar.setOrientation(LinearLayout.HORIZONTAL);
		buttonBar.setGravity(Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL);
		buttonBar.addView(prevButton, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		buttonBar.addView(playButton, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		buttonBar.addView(nextButton, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		this.addView(buttonBar);

		OnClickListener l = new ButtonsOnClick();
		nextButton.setOnClickListener(l);
		prevButton.setOnClickListener(l);
		playButton.setOnClickListener(l);

	}

	class ButtonsOnClick implements OnClickListener {

		public void onClick(View view) {
			if (view == prevButton) {
				if (moveController.getState() == MoveController.State.STOPPED) {
					int to = current;
					if (to == 0) {
						return;
					}
					Move toMove = null;
					for (to = current - 1; to >= 0; to--) {
						if ((toMove = SymbolMoveUtil.parseMoveFromSymbol(
								symbols[to], cubeController.getMagicCube()
										.getOrder())) != null) {
							break;
						}
					}
					if (toMove != null) {
						toMove.direction = -toMove.direction;
						if (moveController.startMove(toMove)) {
							indicator.moveTo(to);
							current = to;
						}
					} else {
						if (to < current) {
							indicator.moveTo(to);
							current = to;
						}
					}
				}

			} else if (view == nextButton) {
				if (moveController.getState() == MoveController.State.STOPPED) {
					int to = current;
					Move toMove = null;
					while (to < symbols.length) {
						if ((toMove = SymbolMoveUtil.parseMoveFromSymbol(
								symbols[to], cubeController.getMagicCube()
										.getOrder())) != null) {
							break;
						}
						to++;
					}
					if (toMove != null && to < symbols.length) {
						to++;
						// step to the left next movable symbol;
						while (to < symbols.length) {
							if (SymbolMoveUtil.parseMoveFromSymbol(symbols[to],
									cubeController.getMagicCube().getOrder()) != null) {
								break;
							}
							to++;
						}
						if (moveController.startMove(toMove)) {
							indicator.moveTo(to);
							current = to;
						}
					}
				}
			} else if (view == playButton) {
				if (moveController.getState() == MoveController.State.STOPPED) {
					MoveSequence seq = new MoveSequence();
					int seqIndex = 0;
					sequenceIndexToSymbolsIndex.clear();
					for (int i = current; i < symbols.length; i++) {
						Move mv = SymbolMoveUtil.parseMoveFromSymbol(
								symbols[i], cubeController.getMagicCube()
										.getOrder());
						if (mv != null) {
							seq.addMove(mv);
							sequenceIndexToSymbolsIndex.put(seqIndex++, i);
						}
					}
					if (seq.totalMoves() > 0) {
						moveController.startMove(seq);
					}
				}
			}
			// cubeController.turnBySymbol(indicator.getCurrentSymbol());
			// indicator.moveForward();
		}
	}

	public void moveSequenceStepped(int index) {
		Log.v("cc", "move controller stepped: " + index);
		final int to = sequenceIndexToSymbolsIndex.get(index);
		this.current = to;
		Log.v("cc", "correspond to : " + to);
		Context c = this.getContext();
		if (c instanceof Activity) {
			((Activity) c).runOnUiThread(new Runnable() {
				public void run() {

					indicator.moveTo(to + 1);
				}

			});
		}
	}

	public void statusChanged(State from, State to) {
		Log.v("cc", "move controller changes state from " + from + " to " + to);
	}
}
