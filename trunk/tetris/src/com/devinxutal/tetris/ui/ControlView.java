package com.devinxutal.tetris.ui;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

import com.devinxutal.tetris.control.ButtonInfo;
import com.devinxutal.tetris.control.GameController;
import com.devinxutal.tetris.util.MathUtil;

public class ControlView extends LinearLayout implements OnTouchListener {
	private GameController controller;
	private List<GameControlListener> listeners = new LinkedList<GameControlListener>();

	public static final int BTN_PLAY = 3300;
	public static final int BTN_LEFT = 3301;
	public static final int BTN_RIGHT = 3302;
	public static final int BTN_TURN = 3303;
	public static final int BTN_DOWN = 3304;
	public static final int BTN_DIRECT_DOWN = 3305;
	private static final String TAG = "GameControlView";
	public static final float SLIDE_THRESHOLD = 10;
	private List<ButtonInfo> buttons = new LinkedList<ButtonInfo>();

	public ControlView(Context context) {
		super(context);
		init();
	}

	public void setButtons(List<ButtonInfo> buttons) {
		this.buttons.clear();
		this.buttons.addAll(buttons);
	}

	public void setGameController(GameController controller) {
		this.controller = controller;
	}

	private void init() {
		this.setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	private float oldX = -1;
	private float oldY = -1;

	public boolean onTouch(View arg0, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float x = event.getX();
			float y = event.getY();
			boolean notified = false;
			boolean touchOnButton = false;
			for (ButtonInfo button : buttons) {
				if (MathUtil.distance(x, y, button.x, button.y) <= button.radius
						&& !notified) {
					if (!button.pressed) {
						button.pressed = true;
						touchOnButton = true;
						this.notifyButtonPressed(button.buttonID);
					}
					notified = true;
				} else {
					if (button.pressed) {
						button.pressed = false;
						this.notifyButtonReleased(button.buttonID);
					}
				}
			}
			if (touchOnButton) {
				Log.v(TAG, "touch on button");
				oldX = -1;
				oldY = -1;
			} else {
				Log.v(TAG, "not touch on button");
				oldX = x;
				oldY = y;
			}

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			float x = event.getX();
			float y = event.getY();
			for (ButtonInfo button : buttons) {
				if (button.pressed) {
					button.pressed = false;
					if (MathUtil.distance(x, y, button.x, button.y) <= button.radius) {
						this.notifyButtonReleased(button.buttonID);
						// this.notifyButtonClicked(button.buttonID);
					} else {
						this.notifyButtonReleased(button.buttonID);
					}
				}

			}
			if (oldX >= 0 && oldY >= 0) {
				float deltaX = x - oldX;
				float deltaY = y - oldY;
				Log.v(TAG, deltaX + "\t" + deltaY);
				if (Math.abs(deltaX) > SLIDE_THRESHOLD
						|| Math.abs(deltaY) > SLIDE_THRESHOLD) {
					if (Math.abs(deltaX) > Math.abs(deltaY)) {
						if (deltaX > 0) {
							this.notifyButtonClicked(BTN_RIGHT);
						} else {
							this.notifyButtonClicked(BTN_LEFT);
						}
					} else {
						if (deltaY > 0) {
							this.notifyButtonClicked(BTN_DIRECT_DOWN);
						} else {
							this.notifyButtonClicked(BTN_TURN);
						}
					}
				} else {
					this.notifyButtonClicked(BTN_TURN);
				}

			}
			oldX = -1;
			oldY = -1;
		}
		return true;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		default:
			notifyButtonClicked(view.getId());
		}
	}

	public interface GameControlListener {
		void buttonClickced(int id);

		void buttonPressed(int id);

		void buttonReleased(int id);
	}

	public boolean addGameControlListener(GameControlListener l) {
		return listeners.add(l);
	}

	public boolean removeGameControlListener(GameControlListener l) {
		return listeners.remove(l);
	}

	public void clearGameControlListener() {
		listeners.clear();
	}

	public void notifyButtonClicked(int id) {
		for (GameControlListener l : listeners) {
			l.buttonClickced(id);
		}
	}

	public void notifyButtonPressed(int id) {
		for (GameControlListener l : listeners) {
			l.buttonPressed(id);
		}
	}

	public void notifyButtonReleased(int id) {
		for (GameControlListener l : listeners) {
			l.buttonReleased(id);
		}
	}

}
