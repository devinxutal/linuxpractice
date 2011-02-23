package com.devinxutal.tetris.ui;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

import com.devinxutal.tetris.control.GameController;
import com.devinxutal.tetris.util.BitmapUtil;
import com.devinxutal.tetris.util.MathUtil;

public class GameControlView extends LinearLayout implements OnTouchListener {
	private GameController controller;
	private List<GameControlListener> listeners = new LinkedList<GameControlListener>();

	public static final int BTN_PLAY = 3300;
	public static final int BTN_LEFT = 3301;
	public static final int BTN_RIGHT = 3302;
	public static final int BTN_TURN = 3303;
	public static final int BTN_DOWN = 3304;
	private static final String TAG = "GameControlView";

	private DrawingMetrics dm = new DrawingMetrics();

	private List<ButtonInfo> buttons = new LinkedList<ButtonInfo>();

	public GameControlView(Context context) {
		super(context);
		this.setBackgroundColor(Color.argb(1, 255, 255, 255));
		init();
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
		dm.recalc();

		dm.paint.setAntiAlias(true);
		dm.paint.setAlpha(255);
		for (ButtonInfo button : buttons) {
			canvas.drawBitmap(button.buttonBG, button.x - button.radius,
					button.y - button.radius, dm.paint);
		}
	}

	public boolean onTouch(View arg0, MotionEvent event) {
		Log.v(TAG, "touched");
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float x = event.getX();
			float y = event.getY();
			boolean notified = false;
			for (ButtonInfo button : buttons) {
				if (MathUtil.distance(x, y, button.x, button.y) <= button.radius
						&& !notified) {
					if (!button.pressed) {
						button.pressed = true;
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
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.v(TAG, "touched up");
			float x = event.getX();
			float y = event.getY();
			for (ButtonInfo button : buttons) {
				if (button.pressed) {
					button.pressed = false;
					if (MathUtil.distance(x, y, button.x, button.y) <= button.radius) {
						this.notifyButtonReleased(button.buttonID);
						this.notifyButtonClicked(button.buttonID);
					} else {
						this.notifyButtonReleased(button.buttonID);
					}
				}

			}
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

	public class DrawingMetrics {

		public final int[] STAR_COLORS = new int[] { Color.rgb(150, 0, 50),
				Color.rgb(0, 150, 50), Color.rgb(150, 150, 0),
				Color.rgb(150, 0, 100), Color.rgb(0, 0, 150) };

		private Paint paint;
		private BitmapUtil bitmapUtil;

		Bitmap buttonBG1 = null;
		Bitmap buttonBG2 = null;
		int playgroundWidth;
		int playgroundHeight;
		int width;
		int height;
		int sideWidth;
		int button1Radius;
		int button2Radius;

		public DrawingMetrics() {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(18);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			bitmapUtil = BitmapUtil.get(getContext());
		}

		public void recalc() {
			if (width != getWidth()) {
				playgroundWidth = controller.getPlayground().getWidth();
				playgroundHeight = controller.getPlayground().getHeight();
				width = getWidth();
				height = getHeight();
				sideWidth = (width - playgroundWidth) / 2;
				recalcButtons();

			}
		}

		private void recalcButtons() {
			Bitmap button1 = BitmapUtil.get(getContext()).getAimButtonBitmap1();
			Bitmap button2 = BitmapUtil.get(getContext()).getAimButtonBitmap2();
			button1Radius = button1.getWidth() / 2;
			button2Radius = button2.getWidth() / 2;
			Pair delta = delta(button1Radius, button2Radius, 0);
			buttons.clear();
			int buttonY = height - button2Radius * 2 - 5;
			int buttonX = width - button1Radius;
			buttons.add(new ButtonInfo(buttonX, buttonY, button1Radius,
					BTN_RIGHT, button1, null));
			buttons.add(new ButtonInfo(buttonX - delta.x, buttonY - delta.y,
					button2Radius, BTN_TURN, button2, null));
			buttons.add(new ButtonInfo(buttonX - delta.x, buttonY + delta.y,
					button2Radius, BTN_DOWN, button2, null));

			buttonX = button1Radius;
			buttons.add(new ButtonInfo(buttonX, buttonY, button1Radius,
					BTN_LEFT, button1, null));
			buttons.add(new ButtonInfo(buttonX + delta.x, buttonY - delta.y,
					button2Radius, BTN_TURN, button2, null));
			buttons.add(new ButtonInfo(buttonX + delta.x, buttonY + delta.y,
					button2Radius, BTN_DOWN, button2, null));

		}

		private Pair delta(int r1, int r2, int gap) {
			int edge1 = r1 + r2 + gap;
			int edge2 = r2 + gap / 2;
			int edge3 = (int) Math.round(Math.sqrt(edge1 * edge1 - edge2
					* edge2));
			return new Pair(edge3, edge2);
		}
	}
}

class Pair {
	int x;
	int y;

	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

class ButtonInfo {
	int x;
	int y;
	int radius;
	int buttonID;
	Bitmap buttonBG;
	Bitmap buttonIcon;
	boolean pressed = false;

	public ButtonInfo(int x, int y, int radius, int buttonID, Bitmap buttonBG,
			Bitmap buttonIcon) {
		super();
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.buttonID = buttonID;
		this.buttonBG = buttonBG;
		this.buttonIcon = buttonIcon;

	}

}
