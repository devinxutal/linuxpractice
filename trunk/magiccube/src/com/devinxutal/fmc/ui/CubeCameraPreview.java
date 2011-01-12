package com.devinxutal.fmc.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.devinxutal.fmc.model.CubeColor;
import com.devinxutal.fmc.util.ImageUtil;

public class CubeCameraPreview extends FrameLayout implements OnClickListener { // <1>

	public enum Stage {
		PHOTO, LOCATE, COMFIRM
	}

	private static final String TAG = "CubeCameraPreview";

	public Camera camera;
	private PreviewArea preview;
	private CubeLocator locator;
	private ControlLayer control;
	private CubeValidator validator;

	private Stage stage;

	public CubeCameraPreview(Context context) {
		super(context);
		preview = new PreviewArea(context);

		locator = new CubeLocator(context);

		control = new ControlLayer(context);

		control.setCanBack(true);
		control.setCanNext(true);

		validator = new CubeValidator(context);

		this.control.nextButton.setOnClickListener(this);
		this.control.backButton.setOnClickListener(this);
		switchStage(Stage.PHOTO);
	}

	public Stage getStage() {
		return this.stage;
	}

	public void switchStage(Stage to) {
		if (stage == to) {
			return;
		}
		Log.v("CubeCameraPreview", "switch stage:" + stage + "->" + to);
		stage = to;
		if (to == Stage.PHOTO) {
			this.removeAllViews();
			this.addView(preview);
			this.addView(locator);
			this.addView(control);
			this.locator.setMode(CubeLocator.INDICATE_MODE);
			this.control.setCanBack(false);
			this.control.setCanNext(true);
			this.control.nextButton.setText("OK");
			if (camera != null) {
				this.camera.startPreview();
			}
		} else if (to == Stage.LOCATE) {
			this.removeAllViews();
			this.addView(locator);
			this.addView(control);
			this.locator.setMode(CubeLocator.MOVE_MODE);
			this.control.setCanBack(true);
			this.control.setCanNext(true);
			this.control.nextButton.setText("Next");
			if (camera != null) {
				this.camera.stopPreview();
			}
		} else if (to == Stage.COMFIRM) {
			this.removeAllViews();
			this.addView(validator);
			this.addView(control);
			this.control.setCanBack(true);
			this.control.setCanNext(true);
			this.control.nextButton.setText("Finish");
			if (camera != null) {
				this.camera.stopPreview();
			}
		}
		invalidate();
	}

	public CubeLocator getCubeLocator() {
		return this.locator;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.v(TAG, "on main layout");
		final int H = 3;
		final int W = 4;
		int w = r - l;
		int h = b - t;
		float d = Math.min(w / (float) W, h / (float) H);
		int aw = (int) d * W;
		int ah = (int) d * H;
		preview.layout((w - aw) / 2, (h - ah) / 2, (w - aw) / 2 + aw, (h - ah)
				/ 2 + ah);
		locator.layout((w - aw) / 2, (h - ah) / 2, (w - aw) / 2 + aw, (h - ah)
				/ 2 + ah);
		control.layout(0, 0, r - l, b - t);
		validator.layout(0, 0, r - l, b - t);
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
			Log.v(TAG, "Getting focuse mode");
			if (params.getSupportedFocusModes() != null) {
				for (String mode : params.getSupportedFocusModes()) {
					Log.v(TAG, "FOCUSE MODE :: " + mode);
				}
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
			if (this.getWidth() > 0) {
				bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
						Bitmap.Config.ARGB_8888);
				Canvas c = new Canvas(bitmap);
				c.drawBitmap(original, new Rect(0, 0, original.getWidth(),
						original.getHeight()), new Rect(0, 0,
						bitmap.getWidth(), bitmap.getHeight()), paint);
			}
		}

		public Bitmap getCubeAsBitmap() {
			if (bitmap == null) {
				return null;
			}
			int minY = bitmap.getHeight();
			int maxY = 0;
			int minX = bitmap.getWidth();
			int maxX = 0;
			for (int i = 0; i < points.length; i++) {
				int x = (int) points[i].x;
				int y = (int) points[i].y;
				minY = Math.max(Math.min(minY, y - 10), 0);
				maxY = Math.min(Math.max(maxY, y + 10), bitmap.getHeight());
				minX = Math.max(Math.min(minX, x - 10), 0);
				maxX = Math.min(Math.max(maxX, x + 10), bitmap.getWidth());
			}
			int len = Math.max(maxY - minY, maxX - minX);
			Log.v(TAG, "step 1 : " + len);
			len = Math
					.min(len, Math.min(bitmap.getHeight(), bitmap.getWidth()));

			Log.v(TAG, "step 2" + " : " + len);
			int l = minX - (len - (maxX - minX)) / 2;
			int r = maxX + (len - (maxX - minX)) / 2;
			int t = minY - (len - (maxY - minY)) / 2;
			int b = maxY + (len - (maxY - minY)) / 2;
			if (l < 0) {
				r = r - l;
				l = 0;
			} else if (r > bitmap.getWidth()) {
				l = l - (r - bitmap.getWidth());
				r = bitmap.getWidth();
			}
			if (t < 0) {
				b = b - t;
				t = 0;
			} else if (b > bitmap.getHeight()) {
				t = l - (r - bitmap.getHeight());
				b = bitmap.getHeight();
			}
			Log.v(TAG, "create bitmap : " + len);
			Bitmap figure = Bitmap.createBitmap(len, len,
					Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(figure);
			c.drawBitmap(bitmap, new Rect(l, t, r, b), new Rect(0, 0, figure
					.getWidth(), figure.getHeight()), new Paint());
			return figure;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			Log.v(TAG, "Locator is redrawing");
			paint.setAlpha(255);
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
				paint.setColor(Color.argb(150, 255, 255, 255));
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
					setPointF(face2[i][j], i, j, props1, props2, points[6],
							points[5], points[4], points[0]);
					setPointF(face3[i][j], i, j, props1, props1, points[0],
							points[4], points[3], points[2]);
				}
			}
		}

		// a d
		// n
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
			acumN -= propsN[n] / 2;

			float x1 = a.x + (b.x - a.x) * acumM;
			float x2 = d.x + (c.x - d.x) * acumM;
			point.x = x1 + (x2 - x1) * acumN;

			float y1 = a.y + (d.y - a.y) * acumN;
			float y2 = b.y + (c.y - b.y) * acumN;
			point.y = y1 + (y2 - y1) * acumM;
		}

		public boolean onTouch(View view, MotionEvent event) {
			if (mode != MOVE_MODE) {
				return true;
			}
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

			// for test
			if (event.getX() < 20) {
				getColors();
			}
			return true;
		}

		public int[] getColors() {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					discoverColor(face1[i][j]);
				}
			}
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					discoverColor(face2[i][j]);
				}
			}
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					discoverColor(face3[i][j]);
				}
			}
			return null;
		}

		private void discoverColor(PointF p) {
			int step = 5;
			int gap = 2;
			int tolerance = 30;

			int avgR = 0;
			int avgG = 0;
			int avgB = 0;
			for (int i = 0; i < step; i++) {
				for (int j = 0; j < step; j++) {
					float x = (i - step / 2f) * gap + p.x;
					float y = (j - step / 2f) * gap + p.y;
					int pixel = bitmap.getPixel((int) x, (int) y);
					avgR += Color.red(pixel);
					avgG += Color.green(pixel);
					avgB += Color.blue(pixel);
				}
			}
			avgR = avgR / step / step;
			avgG = avgG / step / step;
			avgB = avgB / step / step;
			Log.v("CubeCameraPreview", "first round: r:g:b   " + avgR + ":"
					+ avgG + ":" + avgB);
			int aR = 0, aG = 0, aB = 0;
			int totalPoints = 0;
			for (int i = 0; i < step; i++) {
				for (int j = 0; j < step; j++) {
					float x = (i - step / 2f) * gap + p.x;
					float y = (j - step / 2f) * gap + p.y;
					int pixel = bitmap.getPixel((int) x, (int) y);
					if (Math.abs(avgR - Color.red(pixel)) > tolerance
							|| Math.abs(avgG - Color.green(pixel)) > tolerance
							|| Math.abs(avgB - Color.blue(pixel)) > tolerance) {
						Log.v("CubeCameraPreview", "omit color at [" + i + ","
								+ j + "] r:g:b   " + Color.red(pixel) + ":"
								+ Color.green(pixel) + ":" + Color.blue(pixel));
					} else {
						totalPoints++;
						aR += Color.red(pixel);
						aG += Color.green(pixel);
						aB += Color.blue(pixel);
					}
				}
			}
			aR = aR / step / step;
			aG = aG / step / step;
			aB = aB / step / step;
			Log.v("CubeCameraPreview", "second round: r:g:b   " + aR + ":" + aG
					+ ":" + aB);
			CubeColor colors[] = new CubeColor[] { CubeColor.RED,
					CubeColor.ORANGE, CubeColor.WHITE, CubeColor.YELLOW,
					CubeColor.BLUE, CubeColor.GREEN };
			Log.v("CubeCameraPreview", "best fit color:"
					+ ImageUtil.getCubeColor(Color.rgb(aR, aG, aB), colors));
		}
	}

	public class ControlLayer extends ViewGroup {
		private Button backButton;
		private Button nextButton;

		private boolean canBack = false;
		private boolean canNext = false;

		public boolean canBack() {
			return canBack;
		}

		public void setCanBack(boolean canBack) {
			this.canBack = canBack;

			backButton.setVisibility(canBack ? VISIBLE : INVISIBLE);
		}

		public boolean canNext() {
			return canNext;
		}

		public void setCanNext(boolean canNext) {
			this.canNext = canNext;

			nextButton.setVisibility(canNext ? VISIBLE : INVISIBLE);
		}

		public ControlLayer(Context context) {
			super(context);
			backButton = new Button(context);
			nextButton = new Button(context);
			backButton.setText("Back");
			nextButton.setText("Next");
			this.addView(backButton);
			this.addView(nextButton);
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			int padding = 10;
			Log.v("CubeCameraPreview", "control view layout ing...");
			if (canBack()) {
				backButton.measure(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);

				Log.v("CubeCameraPreview", "layout back button ..."
						+ backButton.getMeasuredHeight());
				backButton.layout(padding, b - t - padding
						- backButton.getMeasuredHeight(), padding
						+ backButton.getMeasuredWidth(), b - t - padding);
			}
			if (canNext()) {
				nextButton.measure(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				nextButton.layout(r - l - padding
						- nextButton.getMeasuredWidth(), b - t - padding
						- nextButton.getMeasuredHeight(), r - l - padding, b
						- t - padding);
			}
		}
	}

	public class CubeValidator extends ViewGroup {
		private ImageView imageView;
		private PlaneCubeView cubeView;

		public CubeValidator(Context context) {
			super(context);
			imageView = new ImageView(context);

			this.cubeView = new PlaneCubeView(context);
			this.addView(imageView);
			this.addView(cubeView);
			// test
			this.imageView.setImageResource(com.devinxutal.fmc.R.drawable.cube);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);

		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			Log.v("CubeCameraPreview", "validator is layouting");
			imageView.layout(0, 0, (r - l) / 2, b - t);
			cubeView.layout((r - l) / 2, 0, r - l, b - t);
		}
	}

	public void onClick(View view) {
		Log.v("CubeCameraPreview", "clicked");
		if (view == control.nextButton) {
			switch (stage) {
			case PHOTO:
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				break;
			case LOCATE:
				this.validator.imageView.setImageBitmap(locator
						.getCubeAsBitmap());
				switchStage(Stage.COMFIRM);
				break;
			case COMFIRM:
				// TODO
				break;
			}
		} else if (view == control.backButton) {
			switch (stage) {
			case LOCATE:
				switchStage(Stage.PHOTO);
				break;
			case COMFIRM:
				switchStage(Stage.LOCATE);
				break;
			}
		}

	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() { // <8>
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				Parameters p = camera.getParameters();
				Log.v(TAG, "picture taken, size: " + p.getPictureSize().width
						+ "," + p.getPictureSize().height + "   bytes: "
						+ data.length);
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
				Log.v(TAG, "bitmap null? " + (bm == null));
				if (bm != null) {
					locator.setBitmap(bm);
					locator.setMode(CubeLocator.MOVE_MODE);
					Log.v("CubeCameraPreview", "width:" + bm.getWidth() + ", "
							+ "height:" + bm.getHeight());
					switchStage(Stage.LOCATE);
				}

				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (Exception e) { // <10>

				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};
}