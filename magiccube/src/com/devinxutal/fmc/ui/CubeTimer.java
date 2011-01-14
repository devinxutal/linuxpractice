package com.devinxutal.fmc.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

public class CubeTimer extends View {
	private static final String TAG = "CubeTimer";

	private boolean running = false;
	private Paint paint;
	private Handler mHandler = new Handler();
	private long startTime;
	private long accumulatedTime = 0;

	public CubeTimer(Context context) {
		super(context);
		paint = new Paint();
		paint.setColor(Color.rgb(255, 100, 255));
		paint.setAntiAlias(true);
		paint.setTypeface(Typeface.MONOSPACE);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(TAG, "surface created");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v(TAG, "surface changed");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(TAG, "surface destroyed");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = this.getHeight();
		int width = this.getWidth();
		String time = parseTime();
		float w = paint.measureText(time);
		float h = paint.descent() - paint.ascent();
		float x = (width - w) / 2;
		float y = (height - h) / 2 - paint.ascent();
		canvas.drawText(time, x, y, paint);
	}

	private String parseTime() {
		long now = System.currentTimeMillis();
		long ms = now - startTime + accumulatedTime;
		if (!running) {
			ms = 0;
		}
		int fraction = (int) (ms % 1000) / 100;
		int time = (int) ms / 1000;
		int h = (time / 3600) % 100;
		int m = (time / 60) % 60;
		int s = time % 60;
		String str = "";
		str += h < 10 ? "0" + h : "" + h;
		str += ":";
		str += m < 10 ? "0" + m : "" + m;
		str += ":";
		str += s < 10 ? "0" + s : "" + s;
		str += "." + fraction;
		return str;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.v(TAG, "on measure");
		float w = getPaddingLeft() + getPaddingRight()
				+ paint.measureText("00:00:00.0");
		float h = getPaddingTop() + getPaddingBottom() + paint.descent()
				- paint.ascent();
		this.setMeasuredDimension((int) w + 1, (int) h + 1);
	}

	public void start() {
		if (running) {
			return;
		}
		running = true;
		this.startTime = System.currentTimeMillis();
		mHandler.postDelayed(cubeTimerTask, 100);
	}

	public void stop() {
		if (!running) {
			return;
		}
		running = false;
		this.accumulatedTime += System.currentTimeMillis() - startTime;
		this.startTime = -1;
	}

	public void reset() {
		if (running) {
			return;
		}
		this.accumulatedTime = 0;
		this.startTime = -1;
	}

	private Runnable cubeTimerTask = new Runnable() {
		public void run() {
			invalidate();
			if (running) {
				mHandler.postDelayed(this, 100);
			}
		}
	};

}
