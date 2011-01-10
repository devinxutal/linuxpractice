package com.devinxutal.fmc.ui;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

public class CubeCameraPreview extends FrameLayout { // <1>
	private static final String TAG = "Preview";

	public Camera camera;
	private PreviewArea area;
	private CubeLocator locator;

	public CubeCameraPreview(Context context) {
		super(context);
		area = new PreviewArea(context);
		this.addView(area);
		locator = new CubeLocator(context);
		this.addView(locator);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!changed) {
			return;
		}
		int w = r - l;
		int h = b - t;
		int d = Math.min(w / 4, h / 3);
		int aw = d * 4;
		int ah = d * 3;
		area.layout((w - aw) / 2, (h - ah) / 2, (w - aw) / 2 + aw, (h - ah) / 2
				+ ah);
		locator.layout((w - aw) / 2, (h - ah) / 2, (w - aw) / 2 + aw, (h - ah)
				/ 2 + ah);
	}

	class PreviewArea extends SurfaceView implements SurfaceHolder.Callback {

		SurfaceHolder mHolder; // <2>

		public PreviewArea(Context context) {
			super(context);
			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			camera.startPreview();
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, acquire the camera and tell it
			// where
			// to draw.
			camera = Camera.open();
			Camera.Parameters p = camera.getParameters();
			List<Size> size = p.getSupportedPictureSizes();
			for (Size sz : size) {
				Log.v(TAG, "supported: " + sz.width + "," + sz.height);
			}
			try {
				camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}

	}

	class CubeLocator extends View {
		Paint paint;
		PointF[] points;

		public CubeLocator(Context context) {
			super(context);
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setAlpha(200);
			paint.setColor(Color.BLUE);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(5f);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if (points != null) {
				for (int i = 2; i <= 6; i += 2) {
					canvas.drawLine(points[0].x, points[0].y, points[i].x,
							points[i].y, paint);
				}
				PointF pp = points[6];

				for (int i = 1; i <= 6; i++) {
					canvas
							.drawLine(pp.x, pp.y, points[i].x, points[i].y,
									paint);
					pp = points[i];
				}
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			// TODO Auto-generated method stub
			super.onSizeChanged(w, h, oldw, oldh);
			if (points == null) {
				points = new PointF[7];
			}
			float len = Math.min(w, h) * 4f / 10f;
			float cx = w / 2f;
			float cy = h / 2f;
			float sr3 = (float) Math.sqrt(3);
			float lensr3 = len * sr3 / 2;
			float lenhalf = len / 2;
			points[0] = new PointF(cx, cy);
			points[1] = new PointF(cx, cy - len * 4 / 5);
			points[2] = new PointF(cx + lensr3, cy - lenhalf);
			points[3] = new PointF(cx + lensr3 * 5/6, cy + lenhalf*7/6);
			points[4] = new PointF(cx, cy + len * 6 / 5);
			points[5] = new PointF(cx - lensr3 * 5/6, cy + lenhalf*7/6);
			points[6] = new PointF(cx - lensr3, cy - lenhalf);

		}
	}

}