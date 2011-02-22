package com.devinxutal.tetris.ui;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.devinxutal.tetris.R;
import com.devinxutal.tetris.cfg.Configuration;
import com.devinxutal.tetris.control.GameController;

public class GameControlView extends ViewGroup implements OnClickListener {
	private GameController controller;
	private List<GameControlListener> listeners = new LinkedList<GameControlListener>();

	public static final int BTN_PLAY = 3300;

	private ImageButton playButton;
	private List<ImageButton> buttons1;
	private JoyStick joyStick;
	private GameTimer gameTimer;
	private boolean collapsed = true;
	private boolean showJoyStick = true;
	private boolean widgetChanged = true;

	public GameControlView(Context context) {
		super(context);
		init();
	}

	public void setGameController(GameController controller) {
		this.controller = controller;
	}

	public GameTimer getGameTimer() {
		return gameTimer;
	}

	public JoyStick getJoyStick() {
		return joyStick;
	}

	private void init() {

		gameTimer = new GameTimer(getContext());
		this.addView(gameTimer);

		buttons1 = new LinkedList<ImageButton>();

		int[] ids = new int[] { BTN_PLAY };//
		int[] res = new int[] { R.drawable.icon_pause, //

		};//
		for (int i = 0; i < ids.length; i++) {
			ImageButton button = makeButton(ids[i], res[i]);
			buttons1.add(button);
		}
		playButton = buttons1.get(0);
		this.addView(playButton);
		joyStick = new JoyStick(this.getContext());
		this.addView(joyStick);
	}

	private ImageButton makeButton(int id, int resid) {
		ImageButton b = new ImageButton(getContext());
		b.setId(id);
		b.setBackgroundResource(R.drawable.transparent_button);
		b.setImageResource(resid);
		b.setOnClickListener(this);
		return b;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!changed && !widgetChanged) {
			return;
		}
		widgetChanged = false;
		ImageButton collapseButton = buttons1.get(0);
		collapseButton.measure(r - l, b - t);
		int width = r - l;
		int height = b - t;
		int len = width > height ? height : width;
		int btn_w = collapseButton.getMeasuredWidth();
		int btn_h = collapseButton.getMeasuredHeight();
		int btn_len = width > height ? btn_h : btn_w;
		int margin = 10;
		int padding = 0;
		if (buttons1.size() > 1) {
			padding = ((len - 2 * margin) - buttons1.size() * btn_len)
					/ (buttons1.size() - 1);
		}
		collapseButton.layout(margin, margin, btn_w + margin, btn_h + margin);

		if (!collapsed) {
			int index = -1;
			for (ImageButton btn : buttons1) {
				index++;
				if (index == 0) {
					continue;
				}
				if (width < height) {
					btn.layout(margin + index * (padding + btn_w), margin,
							margin + index * (padding + btn_w) + btn_w, margin
									+ btn_h);
				} else {
					btn.layout(margin, margin + index * (padding + btn_h),
							margin + btn_w, margin + index * (padding + btn_h)
									+ btn_h);
				}
			}
		}

		gameTimer.measure(-1, -1);
		int h = gameTimer.getMeasuredHeight();
		int w = gameTimer.getMeasuredWidth();
		int left = width - w - margin;
		int top = 0 + margin;
		if (!collapsed) {
			if (width < height) {
				top += margin + btn_h;
			} else {
				left -= margin + btn_w;
			}
		}

		Log.v("CubeControlView", "layout  timer: " + left + ", " + top);
		gameTimer.layout(left, top, left + w, top + h);

		if (showJoyStick) {

			joyStick.measure(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			len = Math.min(height, width) / 3;
			if (Configuration.config().getJoyStickPosition() == Configuration.JOT_STICK_POSITION_LEFT) {

				joyStick.layout(margin, height - margin - len, margin + len,
						height - margin);
			} else {

				joyStick.layout(width - margin - len, height - margin - len,
						width - margin, height - margin);
			}
		}
	}

	public static final int PLAY_BUTTON_STATUS_PLAY = 1;
	public static final int PLAY_BUTTON_STATUS_PAUSE = 2;
	public static final int PLAY_BUTTON_STATUS_NONE = 3;

	public void setPlayButtonStatus(int status) {
		switch (status) {
		case PLAY_BUTTON_STATUS_PLAY:
			playButton.setImageResource(R.drawable.icon_play);
			playButton.setVisibility(VISIBLE);
			break;
		case PLAY_BUTTON_STATUS_PAUSE:
			playButton.setImageResource(R.drawable.icon_pause);
			playButton.setVisibility(VISIBLE);
			break;
		case PLAY_BUTTON_STATUS_NONE:
			playButton.setImageResource(R.drawable.icon_play);
			playButton.setVisibility(INVISIBLE);
			break;
		}
	}

	public void showJoyStick(boolean show) {
		if (showJoyStick != show) {
			showJoyStick = show;
			widgetChanged = true;
			if (show) {
				this.joyStick.setVisibility(VISIBLE);
			} else {
				this.joyStick.setVisibility(INVISIBLE);
			}
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
		default:
			notifyButtonClicked(view.getId());
		}
	}

	public interface GameControlListener {
		void buttonClickced(int id);
	}

	public boolean addCubeControlListener(GameControlListener l) {
		return listeners.add(l);
	}

	public boolean removeCubeControlListener(GameControlListener l) {
		return listeners.remove(l);
	}

	public void clearCubeControlListener() {
		listeners.clear();
	}

	public void notifyButtonClicked(int id) {
		for (GameControlListener l : listeners) {
			l.buttonClickced(id);
		}
	}

	public boolean isCollapsed() {
		return collapsed;
	}
}
