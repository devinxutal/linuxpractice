package com.devinxutal.fc.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.devinxutal.fc.R;
import com.devinxutal.fc.activities.CubeDemonstratorActivity;
import com.devinxutal.fc.activities.Preferences;
import com.devinxutal.fc.control.CubeController;
import com.devinxutal.fc.control.Move;
import com.devinxutal.fc.control.MoveController;
import com.devinxutal.fc.control.MoveControllerListener;
import com.devinxutal.fc.control.MoveSequence;
import com.devinxutal.fc.control.MoveController.State;
import com.devinxutal.fc.model.CubeState;
import com.devinxutal.fc.util.SymbolMoveUtil;

public class CubeDemonstrator extends ViewGroup implements
		MoveControllerListener {
	private CubeController cubeController;

	private MoveController moveController;

	// widgets
	private CubeView cubeView;
	private MoveSequenceIndicator indicator;
	private ButtonBar buttonBar;
	private ImageButton prevButton;
	private ImageButton nextButton;
	private ImageButton playButton;
	private ImageButton resetButton;
	private ImageButton prefButton;

	//
	private CubeState initialState;
	private String[] symbols;
	private int current;
	private Map<Integer, Integer> sequenceIndexToSymbolsIndex = new HashMap<Integer, Integer>();

	public CubeDemonstrator(Context context, CubeState initialState,
			String[] symbols) {
		super(context);
		init();
		this.symbols = symbols;
		this.initialState = initialState;
		if (this.initialState == null) {
			this.initialState = cubeController.getMagicCube().getCubeState();
		}
		this.cubeController.getMagicCube().setCubeState(this.initialState);
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

		cubeController = new CubeController(getContext(), false, true);
		moveController = new MoveController(cubeController);

		this.moveController.addMoveControllerListener(this);
		this.indicator = new MoveSequenceIndicator(this.getContext());
		this.cubeView = cubeController.getCubeView();

		this.addView(cubeView);
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

		resetButton = new ImageButton(getContext());
		resetButton.setBackgroundResource(R.drawable.play_button);
		resetButton.setImageResource(R.drawable.icon_reset);

		prefButton = new ImageButton(getContext());
		prefButton.setBackgroundResource(R.drawable.play_button);
		prefButton.setImageResource(R.drawable.icon_setting);

		buttonBar = new ButtonBar(getContext());

		buttonBar.addView(resetButton, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		buttonBar.addView(prevButton, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1f));
		buttonBar.addView(playButton, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		buttonBar.addView(nextButton, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		buttonBar.addView(prefButton, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		this.addView(buttonBar);

		OnClickListener l = new ButtonsOnClick();
		nextButton.setOnClickListener(l);
		prevButton.setOnClickListener(l);
		playButton.setOnClickListener(l);
		resetButton.setOnClickListener(l);
		prefButton.setOnClickListener(l);

	}

	public void onDestroy() {

		this.indicator.destroy();
	}

	public MoveController getMoveController() {
		return moveController;
	}

	private int nextIndex() {
		int to = current;
		Move toMove;
		while (to < symbols.length) {
			if ((toMove = SymbolMoveUtil.parseMoveFromSymbol(symbols[to],
					cubeController.getMagicCube().getOrder())) != null) {
				to++;
				break;
			}
			to++;
		}
		while (to < symbols.length) {
			if ((toMove = SymbolMoveUtil.parseMoveFromSymbol(symbols[to],
					cubeController.getMagicCube().getOrder())) != null) {
				return to;
			}
			to++;
		}
		return to;
	}

	private int prevIndex() {
		int to = current;
		for (to = current - 1; to >= 0; to--) {
			if ((SymbolMoveUtil.parseMoveFromSymbol(symbols[to], cubeController
					.getMagicCube().getOrder())) != null) {
				to--;
				break;
			}
		}
		for (; to >= 0; to--) {
			if ((SymbolMoveUtil.parseMoveFromSymbol(symbols[to], cubeController
					.getMagicCube().getOrder())) != null) {
				to++;
				return to;
			}
		}
		return Math.max(0, to);
	}

	private Move nextMove() {
		int to = current;
		Move toMove;
		while (to < symbols.length) {
			if ((toMove = SymbolMoveUtil.parseMoveFromSymbol(symbols[to],
					cubeController.getMagicCube().getOrder())) != null) {
				return toMove;
			}
			to++;
		}
		return null;
	}

	private Move prevMove() {
		int to = current;
		if (to == 0) {
			return null;
		}
		Move toMove = null;
		for (to = current - 1; to >= 0; to--) {
			if ((toMove = SymbolMoveUtil.parseMoveFromSymbol(symbols[to],
					cubeController.getMagicCube().getOrder())) != null) {
				toMove.direction = -toMove.direction;
				break;
			}
		}
		return toMove;
	}

	class ButtonsOnClick implements OnClickListener {

		public void onClick(View view) {
			if (view == prevButton) {
				if (moveController.getState() == MoveController.State.STOPPED) {
					int to = prevIndex();
					Move move = prevMove();

					current = to;
					indicator.moveTo(to);
					if (move != null) {
						moveController.startMove(move);
					}

				}

			} else if (view == nextButton) {
				if (moveController.getState() == MoveController.State.STOPPED) {
					int to = nextIndex();
					Move move = nextMove();
					current = to;
					indicator.moveTo(to);
					if (move != null) {
						moveController.startMove(move);
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
				} else if (moveController.getState() == MoveController.State.RUNNING_MULTPLE_STEP) {
					moveController.stopMove();
				}
			} else if (view == resetButton) {
				if (moveController.getState() == MoveController.State.STOPPED) {
					cubeController.getMagicCube().setCubeState(initialState);
					cubeController.getCubeView().requestRender();
					current = 0;
					indicator.moveTo(0);
				}
			} else if (view == prefButton) {
				Activity activity = (Activity) getContext();
				Intent preferencesActivity = new Intent(activity
						.getBaseContext(), Preferences.class);
				preferencesActivity.putExtra("pref_res",
						R.xml.preferences_demonstrator);
				activity.startActivityForResult(preferencesActivity,
						CubeDemonstratorActivity.PREFERENCE_REQUEST_CODE);
			}
		}
	}

	public void moveSequenceStepped(int index) {
		Log.v("cc", "move controller stepped: " + index);
		int toInt = sequenceIndexToSymbolsIndex.get(index);
		current = toInt;
		final int to = nextIndex();
		current = to;
		Log.v("cc", "correspond to : " + to);
		Context c = this.getContext();
		if (c instanceof Activity) {
			((Activity) c).runOnUiThread(new Runnable() {
				public void run() {
					indicator.moveTo(to);
				}
			});
		}
	}

	public void statusChanged(final State from, final State to) {
		if (this.getContext() instanceof Activity) {
			((Activity) getContext()).runOnUiThread(new Runnable() {
				public void run() {
					statusChangedUIMode(from, to);
				}
			});
		}
	}

	private void statusChangedUIMode(State from, State to) {
		Log.v("cc", "move controller changes state from " + from + " to " + to);
		if (to == MoveController.State.RUNNING_MULTPLE_STEP) {
			playButton.setImageResource(R.drawable.icon_stop);
		} else if (to == MoveController.State.STOPPED) {
			playButton.setImageResource(R.drawable.icon_play);
		}
	}

	public CubeController getCubeController() {
		return cubeController;
	}

	class ButtonBar extends ViewGroup {
		public ButtonBar(Context context) {
			super(context);
		}

		private static final int GAP = 10;

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			if (this.getChildCount() != 0) {
				this.getChildAt(0).measure(widthMeasureSpec, heightMeasureSpec);

				this.setMeasuredDimension(widthMeasureSpec, 2 * GAP
						+ this.getChildAt(0).getMeasuredHeight());
			} else {
				this.setMeasuredDimension(widthMeasureSpec, 2 * GAP);
			}
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			if (!changed) {
				return;
			}
			int height = b - t;
			int width = r - l;
			int n = this.getChildCount();
			if (n > 0) {
				// this.getChildAt(0).measure(LayoutParams.WRAP_CONTENT,
				// LayoutParams.WRAP_CONTENT);
				int h = this.getChildAt(0).getMeasuredHeight();
				int w = (width - (n + 1) * GAP) / n;
				int nt = (height - h) / 2;
				int nb = nt + h;
				for (int i = 0; i < n; i++) {
					View v = this.getChildAt(i);
					int x = (i + 1) * GAP + i * w;
					v.layout(x, nt, x + w, nb);
				}
			}
		}

	}
}
