package com.devinxutal.fmc.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CopyOfCubeTimer extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "CubeTimer";
	private Timer timer;
	private Paint paint;
	private long ms;

	public CopyOfCubeTimer(Context context) {
		super(context);
		paint = new Paint();
		paint.setColor(Color.rgb(255, 100, 255));
		paint.setAntiAlias(true);
		paint.setTypeface(Typeface.MONOSPACE);
		getHolder().addCallback(this);
		this.setWillNotDraw(false);
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
		Log.v(TAG, "on draw");
		super.onDraw(canvas);
		paint(canvas);
	}

	private String parseTime() {
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
		if (this.timer != null) {
			return;
		}
		this.setWillNotDraw(true);
		this.timer = new Timer();
		timer.scheduleAtFixedRate(new CubeTimerTask(), 0, 100);
	}

	public void stop() {
		if (this.timer == null) {
			return;
		}
		this.setWillNotDraw(false);
		this.timer.cancel();
		this.timer.purge();
		this.timer = null;
	}

	public void reset() {
		if (this.timer != null) {
			return;
		}
		this.ms = 0;
	}

	private void repaint() {
		Canvas c = null;
		SurfaceHolder surfaceHolder = getHolder();
		try {
			c = surfaceHolder.lockCanvas();
			if (c != null) {
				paint(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				surfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}

	protected void paint(Canvas c) {
		c.drawRGB(0, 0, 0);
		int height = this.getHeight();
		int width = this.getWidth();
		String time = parseTime();
		float w = paint.measureText(time);
		float h = paint.descent() - paint.ascent();
		float x = (width - w) / 2;
		float y = (height - h) / 2 - paint.ascent();
		c.drawText(time, x, y, paint);
	}

	class CubeTimerTask extends TimerTask {
		@Override
		public void run() {
			ms += 100;
			repaint();
		}
	}

}
