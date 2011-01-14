package com.devinxutal.fmc.activities;

import java.io.IOException;
import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.devinxutal.fmc.cfg.Configuration;

public class TestActivity extends Activity {

	Timer timer;
	PictureView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Configuration.config()
				.setSharedPreferences(
						PreferenceManager
								.getDefaultSharedPreferences(getBaseContext()));
		FrameLayout layout = new FrameLayout(this);
		view = new PictureView(this);
		try {
			view.setBitmap(BitmapFactory.decodeStream(getAssets().open(
					"cubedemo.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		layout.addView(view);
		LinearLayout l = new LinearLayout(this);
		Button b = new Button(this);
		l.addView(b, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		layout.addView(l);
		setContentView(layout);

		b.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				for (int i = 0; i < view.getWidth(); i++) {
					view.step();
				}
				// timer = new Timer();
				// timer.scheduleAtFixedRate(new TimerTask() {
				//
				// @Override
				// public void run() {
				// view.step();
				// }
				//
				// }, 0, 100);
			}

		});
	}

	class PictureView extends View implements OnTouchListener {
		private Bitmap bitmap;
		private Bitmap original;
		private Paint paint;

		private int scanX = -1;
		private boolean[] points;

		PointF[] keypoints;

		public PictureView(Context context) {
			super(context);
			paint = new Paint();
			paint.setColor(Color.WHITE);
			this.setOnTouchListener(this);
		}

		public void setBitmap(Bitmap bm) {
			this.original = bm;
			this.bitmap = bm;
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			// TODO Auto-generated method stub
			super.onSizeChanged(w, h, oldw, oldh);
			if (points == null || points.length != (w * h)) {
				points = new boolean[w * h];
			}
			if (original != null) {
				bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
				Canvas c = new Canvas(bitmap);
				c.drawBitmap(original, new Rect(0, 0, original.getWidth(),
						original.getHeight()), new Rect(0, 0,
						bitmap.getWidth(), bitmap.getHeight()), paint);
			}

			if (keypoints == null) {
				keypoints = new PointF[7];
			}
			float len = Math.min(w, h) * 4f / 10f;
			float cx = w / 2f;
			float cy = h / 2f;
			float sr3 = (float) Math.sqrt(3);
			float lensr3 = len * sr3 / 2;
			float lenhalf = len / 2;
			keypoints[0] = new PointF(cx, cy);
			keypoints[1] = new PointF(cx, cy - len * 4 / 5);
			keypoints[2] = new PointF(cx + lensr3, cy - lenhalf);
			keypoints[3] = new PointF(cx + lensr3 * 5 / 6, cy + lenhalf * 7 / 6);
			keypoints[4] = new PointF(cx, cy + len * 6 / 5);
			keypoints[5] = new PointF(cx - lensr3 * 5 / 6, cy + lenhalf * 7 / 6);
			keypoints[6] = new PointF(cx - lensr3, cy - lenhalf);

		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if (bitmap != null) {
				// Rect bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap
				// .getHeight());
				// Rect viewRect = new Rect(0, 0, this.getWidth(), this
				// .getHeight());
				// canvas.drawBitmap(bitmap, bitmapRect, viewRect, paint);
				canvas.drawBitmap(bitmap, 0, 0, paint);
				canvas.drawLine(scanX, 0, scanX, this.getHeight(), paint);
				if (points != null) {
					int w = this.getWidth();
					for (int i = 0; i < points.length; i++) {
						if (points[i]) {
							int x = i % w;
							int y = i / w;
							canvas.drawPoint(x, y, paint);
						}
					}
				}
				if (keypoints != null) {
					for (int i = 2; i <= 6; i += 2) {
						canvas.drawLine(keypoints[0].x, keypoints[0].y,
								keypoints[i].x, keypoints[i].y, paint);
					}
					PointF pp = keypoints[6];

					for (int i = 1; i <= 6; i++) {
						canvas.drawLine(pp.x, pp.y, keypoints[i].x,
								keypoints[i].y, paint);
						pp = keypoints[i];
					}
					for (int i = 0; i < keypoints.length; i++) {
						canvas.drawCircle(keypoints[i].x, keypoints[i].y, 5,
								paint);
					}
				}
			}
		}

		public void step() {
			scanX += 1;
			scan();
		}

		private void scan() {
			for (int i = 0; i < this.getHeight(); i++) {
				scan(scanX, i);
			}
		}

		int ads[][] = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, };

		private void scan(int x, int y) {
			for (int i = 0; i < ads.length; i++) {
				int m = ads[i][0] + x;
				int n = ads[i][1] + y;
				if (differ(x, y, m, n)) {
					points[x + y * this.getWidth()] = true;
				}
			}
		}

		private boolean differ(int x, int y, int m, int n) {
			if (m < 0 || m >= getWidth() || n < 0 || n >= getHeight()) {
				return false;
			}
			int p1 = bitmap.getPixel(x, y);
			int p2 = bitmap.getPixel(m, n);
			for (int i = 0; i < 4; i++) {
				int a1 = p1 & 0xFF;
				int a2 = p2 & 0xFF;
				if (Math.abs(a1 - a2) > 30) {
					return true;
				}
				p1 = p1 >> 8;
				p2 = p2 >> 8;
			}
			return false;
		}

		private int index = -1;

		public boolean onTouch(View view, MotionEvent event) {
			Log.v("TestActivity", "touched");
			float x = event.getX();
			float y = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				for (int i = 0; i < keypoints.length; i++) {
					if (Math.abs(x - keypoints[i].x) < 15
							&& Math.abs(y - keypoints[i].y) < 15) {
						index = i;
						Log.v("TestActivity", "the index found: " + i);
						break;
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				Log.v("TestActivity", "moved: " + index);
				if (index >= 0) {
					keypoints[index].x = x;
					keypoints[index].y = y;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (index >= 0) {
					keypoints[index].x = x;
					keypoints[index].y = y;
				}
				index = -1;
				break;
			}
			this.invalidate();
			return true;
		}
	}
}