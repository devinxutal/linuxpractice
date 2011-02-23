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

public class BitmapUtil {
	private Context context;
	private Bitmap background;
	private Bitmap aimButton1;
	private Bitmap aimButton2;
	private Paint paint;
	private Drawable infoBar;
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
					"images/bg2.jpg"));
			aimButton1 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.aim_button_1);
			aimButton2 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.aim_button_2);
			infoBar = context.getResources().getDrawable(R.drawable.info_bar);
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

	public Drawable getInfoBar() {
		return infoBar;
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
		canvas.drawBitmap(background, new Rect(x, y, x + ww, y + hh), new Rect(
				0, 0, width, height), paint);
		paint.setAntiAlias(false);
		paint.setColor(Color.argb(50, 255, 255, 255));

		for (int i = 0; i < height; i += 2) {
			canvas.drawLine(0, i, width, i, paint);
		}

		return bg;
	}
}
