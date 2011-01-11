package com.devinxutal.fmc.ui;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.MotionEvent;
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

	public CubeLocator getCubeLocator() {
		return this.locator;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!changed) {
			return;
		}
		final int H = 3;
		final int W = 4;
		int w = r - l;
		int h = b - t;
		float d = Math.min(w / (float) W, h / (float) H);
		int aw = (int) d * W;
		int ah = (int) d * H;
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
			camera = Camera.open();
			Parameters params = camera.getParameters();
			List<Size> sizes = params.getSupportedPictureSizes();
			if (sizes != null && sizes.size() > 0) {
				Size min = sizes.get(0);
				for (Size s : sizes) {
					if (s.width < min.width) {
						min = s;
					}
				}
				params.setPictureSize(min.width, min.height);
			}
			try {
				camera.setParameters(params);
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

	public class CubeLocator extends View implements OnTouchListener {
		public static final int VALID_DISTANCE = 50;

		public static final int INDICATE_MODE = 0;
		public static final int MOVE_MODE = 1;

		private Bitmap bitmap;
		private Bitmap original;

		Paint paint;
		PointF[] points;

		PointF[][] face1;
		PointF[][] face2;
		PointF[][] face3;

		int mode = INDICATE_MODE;
		int movingIndex = -1;
		float movingDeltaX = -1;
		float movingDeltaY = -1;

		public CubeLocator(Context context) {
			super(context);
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setAlpha(200);
			paint.setColor(Color.WHITE);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(3f);

			face1 = new PointF[3][3];
			face2 = new PointF[3][3];
			face3 = new PointF[3][3];
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					face1[i][j] = new PointF();
					face2[i][j] = new PointF();
					face3[i][j] = new PointF();
				}

			}

			this.setOnTouchListener(this);
		}

		public void setMode(int mode) {
			if (mode == INDICATE_MODE) {
				this.mode = INDICATE_MODE;
			} else {
				this.mode = MOVE_MODE;
			}
			Log.v("CubeCameraPreview", "Invalidate");
			invalidate();

		}

		public void setBitmap(Bitmap bm) {
			this.original = bm;
			bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
					Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(bitmap);
			c.drawBitmap(original, new Rect(0, 0, original.getWidth(), original
					.getHeight()), new Rect(0, 0, bitmap.getWidth(), bitmap
					.getHeight()), paint);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			Log.v("CubeCameraPreview", "OnDraw: " + mode);
			super.onDraw(canvas);
			if (mode == MOVE_MODE) {
				canvas.drawBitmap(bitmap, 0, 0, paint);
			}
			paint.setStrokeWidth(3);
			paint.setColor(Color.DKGRAY);
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
			paint.setStrokeWidth(1);
			paint.setColor(Color.WHITE);
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
			if (mode == MOVE_MODE) {
				for (int i = 0; i < points.length; i++) {
					paint.setColor(Color.DKGRAY);
					canvas.drawCircle(points[i].x, points[i].y, 7, paint);
					paint.setColor(Color.WHITE);
					canvas.drawCircle(points[i].x, points[i].y, 5, paint);
				}
				recalcPickerPoints();
				paint.setColor(Color.argb(150, 255, 255,255));
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						canvas.drawCircle(face1[i][j].x, face1[i][j].y, 5,
								paint);
						canvas.drawCircle(face2[i][j].x, face2[i][j].y, 5,
								paint);
						canvas.drawCircle(face3[i][j].x, face3[i][j].y, 5,
								paint);
					}
				}
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			// ajust locator points;
			if (points == null) {
				points = new PointF[7];
			}
			float len = Math.min(w, h) * 5f / 10f;
			float cx = w / 2f;
			float cy = h / 2f;
			float sr3 = (float) Math.sqrt(3);
			float lensr3 = len * sr3 / 2;
			float lenhalf = len / 2;
			points[0] = new PointF(cx, cy);
			points[1] = new PointF(cx, cy - len * 4 / 5);
			points[2] = new PointF(cx + lensr3, cy - lenhalf);
			points[3] = new PointF(cx + lensr3 * 5 / 6, cy + lenhalf * 7 / 6);
			points[4] = new PointF(cx, cy + len * 6 / 5);
			points[5] = new PointF(cx - lensr3 * 5 / 6, cy + lenhalf * 7 / 6);
			points[6] = new PointF(cx - lensr3, cy - lenhalf);

			//
			if (original != null) {
				bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
				Canvas c = new Canvas(bitmap);
				c.drawBitmap(original, new Rect(0, 0, original.getWidth(),
						original.getHeight()), new Rect(0, 0,
						bitmap.getWidth(), bitmap.getHeight()), paint);
			}

		}

		private void recalcPickerPoints() {
			float[] props1 = new float[] { 8f / 21, 7f / 21, 6f / 21 };
			float[] props2 = new float[] { 6f / 21, 7f / 21, 8f / 21 };

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					setPointF(face1[i][j], i, j, props2, props2, points[1],
							points[6], points[0], points[2]);
					setPointF(face2[i][j], i, j, props2, props1, points[6],
							points[5], points[4], points[0]);
					setPointF(face3[i][j], i, j, props1, props1, points[0],
							points[4], points[3], points[2]);
				}
			}
		}

		// a d
		//
		// n
		//
		// b c
		// m

		private void setPointF(PointF point, int m, int n, float[] propsM,
				float[] propsN, PointF a, PointF b, PointF c, PointF d) {
			float acumM = 0;
			float acumN = 0;
			for (int i = 0; i <= m; i++) {
				acumM += propsM[i];
			}
			acumM -= propsM[m] / 2;
			for (int i = 0; i <= n; i++) {
				acumN += propsN[i];
			}
			acumN -= propsN[m] / 2;
			float x1 = a.x + (b.x - a.x) * acumN;

			float x2 = d.x + (c.x - d.x) * acumN;
			point.x = x1 + (x2 - x1) * acumM;
			float y1 = a.y + (d.y - a.y) * acumM;
			float y2 = b.y + (c.y - b.y) * acumM;
			point.y = y1 + (y2 - y1) * acumN;
		}

		public boolean onTouch(View view, MotionEvent event) {
			if (mode != MOVE_MODE) {
				return true;
			}
			Log.v("TestActivity", "touched");
			float x = event.getX();
			float y = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				double minDist = Double.MAX_VALUE;
				int index = -1;
				for (int i = 0; i < points.length; i++) {
					float m = points[i].x;
					float n = points[i].y;
					double d = (x - m) * (x - m) + (y - n) * (y - n);
					if (d < minDist) {
						minDist = d;
						index = i;
					}
				}
				if (minDist < VALID_DISTANCE * VALID_DISTANCE) {
					movingIndex = index;
					movingDeltaX = points[index].x - x;
					movingDeltaY = points[index].y - y;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				Log.v("TestActivity", "moved: " + movingIndex);
				if (movingIndex >= 0) {
					points[movingIndex].x = x + movingDeltaX;
					points[movingIndex].y = y + movingDeltaY;
				}
				break;
			case MotionEvent.ACTION_UP:
				movingIndex = -1;
				break;
			}
			this.invalidate();
			return true;
		}
	}

}