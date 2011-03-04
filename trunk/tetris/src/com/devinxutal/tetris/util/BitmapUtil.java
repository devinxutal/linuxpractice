package com.devinxutal.tetris.util;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.devinxutal.tetris.R;
import com.devinxutal.tetris.ui.ControlView;

public class BitmapUtil {
	private Context context;
	private Bitmap background;
	private Bitmap aimButton1;
	private Bitmap aimButton2;
	private Bitmap arrowDown;
	private Bitmap arrowUp;
	private Bitmap arrowLeft;
	private Bitmap arrowRight;
	private Paint paint;
	private Drawable hInfoBar;
	private Drawable vInfoBar;
	private static BitmapUtil util;

	public synchronized static BitmapUtil get(Context context) {
		if (util == null) {
			util = new BitmapUtil(context);
		}
		return util;
	}

	private BitmapUtil(Context context) {
		this.context = context;
		try {
			background = BitmapFactory.decodeStream(context.getAssets().open(
					"images/bg.jpg"));
			aimButton1 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.aim_button1);
			aimButton2 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.aim_button2);

			arrowDown = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.arrow_down);
			arrowUp = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.arrow_up);
			arrowLeft = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.arrow_left);
			arrowRight = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.arrow_right);
			hInfoBar = context.getResources()
					.getDrawable(R.drawable.h_info_bar);

			vInfoBar = context.getResources()
					.getDrawable(R.drawable.v_info_bar);

		} catch (IOException e) {
			e.printStackTrace();
		}
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(18);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setStrokeWidth(1f);
		paint.setAlpha(255);
	}

	public Bitmap getAimButtonBitmap1() {
		return aimButton1;
	}

	public Bitmap getAimButtonBitmap2() {
		return aimButton2;
	}

	public Bitmap getAimButtonBitmap1(int size) {
		return Bitmap.createScaledBitmap(aimButton1, size, size, true);
	}

	public Bitmap getAimButtonBitmap2(int size) {
		return Bitmap.createScaledBitmap(aimButton2, size, size, true);
	}

	public Drawable getVerticalInfoBar() {
		return vInfoBar;
	}

	public Drawable getHorizontalInfoBar() {
		return hInfoBar;
	}

	public Bitmap getBackgroundBitmap(int width, int height) {
		int w = background.getWidth();
		int h = background.getWidth();
		float scale = Math.min(w / (float) width, h / (float) height);
		int ww = (int) (width * scale);
		int hh = (int) (height * scale);
		int x = (w - ww) / 2;
		int y = (h - hh) / 2;
		Log.v("BitmapUtil", "width height: " + width + "," + height + ". w h: "
				+ w + "," + h);
		Bitmap bg = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bg);
		paint.reset();
		paint.setAntiAlias(true);
		canvas.drawBitmap(background, new Rect(x, y, x + ww, y + hh), new Rect(
				0, 0, width, height), paint);
		paint.setAntiAlias(false);
		paint.setColor(Color.argb(50, 255, 255, 255));

		for (int i = 0; i < height; i += 2) {
			canvas.drawLine(0, i, width, i, paint);
		}

		return bg;
	}

	public void drawBackgroundBitmap(Canvas canvas, int width, int height,
			Paint paint) {
		int w = background.getWidth();
		int h = background.getWidth();
		float scale = Math.min(w / (float) width, h / (float) height);
		int ww = (int) (width * scale);
		int hh = (int) (height * scale);
		int x = (w - ww) / 2;
		int y = (h - hh) / 2;

		paint.setAntiAlias(false);
		paint.setAlpha(255);
		canvas.drawBitmap(background, new Rect(x, y, x + ww, y + hh), new Rect(
				0, 0, width, height), paint);

		paint.setColor(Color.argb(50, 255, 255, 255));
		for (int i = 0; i < height; i += 2) {
			canvas.drawLine(0, i, width, i, paint);
		}
	}

	public enum Direction {
		UP, DOWN, LEFT, RIGHT;
	}

	public Bitmap getArrowBitmap(int id, int size) {
		Bitmap arrowBitmap = null;
		switch (id) {
		case ControlView.BTN_TURN:
			arrowBitmap = arrowUp;
			break;
		case ControlView.BTN_DOWN:
			arrowBitmap = arrowDown;
			break;
		case ControlView.BTN_LEFT:
			arrowBitmap = arrowLeft;
			break;
		case ControlView.BTN_RIGHT:
			arrowBitmap = arrowRight;
			break;
		default:
			arrowBitmap = arrowDown;
		}
		return Bitmap.createScaledBitmap(arrowBitmap, size, size, true);
	}
}
