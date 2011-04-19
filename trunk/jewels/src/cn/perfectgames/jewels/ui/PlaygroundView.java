package cn.perfectgames.jewels.ui;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import cn.perfectgames.jewels.cfg.Configuration;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.control.ButtonInfo;
import cn.perfectgames.jewels.control.GameController;
import cn.perfectgames.jewels.model.Playground;
import cn.perfectgames.jewels.util.BitmapUtil;

public class PlaygroundView extends SurfaceView implements
		SurfaceHolder.Callback {
	public static String TAG = "PlaygroundView";

	private AnimationThread animationThread = null;

	private GameController gameController = null;
	// for drawing
	// int offset = 0;
	private DrawingMetrics dm;

	private Playground playground;

	/**
	 * Constructor. This version is only needed if you will be instantiating the
	 * object manually (not from a layout XML file).
	 * 
	 * @param context
	 */
	public PlaygroundView(Context context) {
		super(context);
		getHolder().addCallback(this);
		init();
		this.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent arg1) {
				Log.v(TAG, "i'm on touch");
				if (playground != null) {
					playground.touch(arg1.getX(), arg1.getY());
				}
				return true;
			}
		});
	}

	public void setGameController(GameController controller) {
		this.gameController = controller;
	}

	public Playground getPlayground() {
		return playground;
	}

	public void setPlayground(Playground playground) {
		this.playground = playground;
		this.playground.getDM().init(this.getContext());
	}

	public PlaygroundView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v(TAG, "i'm on touch");
		if (this.playground != null) {
			this.playground.touch(event.getX(), event.getY());
		}
		return super.onTouchEvent(event);
	}

	/**
	 * Render the text
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		paint(canvas);
	}

	private void paint(Canvas canvas) {
		dm.paint.setAlpha(255);
		canvas.drawRGB(0, 0, 0);

		if (playground != null) {
			canvas.drawBitmap(dm.bgBitmap, 0, 0, dm.paint);
			playground.draw(canvas);
		}

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

	public PlaygroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private final void init() {
		dm = new DrawingMetrics();

		setPadding(3, 10, 3, 10);

	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	public void surfaceCreated(SurfaceHolder arg0) {

		this.animationThread = new AnimationThread();
		Log.v(TAG, "thread started");
		this.animationThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		if (animationThread != null) {
			this.animationThread.flag = false;
		}
	}

	/**
	 * @see android.view.View#measure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	public void destroy() {
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) (-dm.paint.ascent() + dm.paint.descent())
					+ getPaddingTop() + getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) { // repaint();
		super.onSizeChanged(w, h, oldw, oldh);
		if (this.getPlayground() != null) {
			dm.onSizeChanged(w, h);
		}
	}

	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
	}

	public void reset() {
		finishedAlreadyNotified = false;
		finishingAlreadyNotified = false;
		notifyFinished = false;
	}

	private boolean finishedAlreadyNotified = false;
	private boolean finishingAlreadyNotified = false;
	private boolean notifyFinished = false;

	private ControlView controlView;

	public void pause(boolean pause) {

	}

	public class DrawingMetrics {

		public final int[] STAR_COLORS = new int[] { Color.rgb(150, 0, 50),
				Color.rgb(0, 150, 50), Color.rgb(150, 150, 0),
				Color.rgb(150, 0, 100), Color.rgb(0, 0, 150) };

		private Paint paint;
		private BitmapUtil bitmapUtil;
		private Bitmap bgBitmap;
		private Canvas canvas;
		private Bitmap gridBitmap;
		private Rect playgroundRect;
		private Rect next1BlockRect;
		private Rect next2BlockRect;
		private Rect next3BlockRect;
		private Rect holdBlockRect;
		private ButtonInfo holdButtonInfo;
		private RectF scoreRect;
		private RectF levelRect;
		private RectF goalRect;
		private Typeface font;
		private Typeface scoreFont;
		private Typeface labelFont;
		private Typeface statFont;
		private float lineHeight;
		private float scoreLineHeight;
		private float labelLineHeight;
		private float statLineHeight;

		private float scoreSize;
		private int scoreDigits = 6;
		private float scoreGap;
		private float labelSize;
		private float labelGap;
		private float statSize;
		private int statDigits = 3;
		private float textStrokeScale = 0.15f;

		public DrawingMetrics() {
			font = Typeface.createFromAsset(getContext().getAssets(),
					Constants.FONT_PATH_MONO);
			scoreFont = statFont = font;
			labelFont = Typeface.create("Arial Black", Typeface.NORMAL);
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(18);
			bitmapUtil = BitmapUtil.get(getContext());
			try {
				gridBitmap = BitmapFactory.decodeStream(getContext()
						.getAssets().open("images/block_bg.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			bgBitmap = bitmapUtil.getScreenBitmap(getContext());
			canvas = new Canvas(bgBitmap);
		}

		public void onSizeChanged(int w, int h) {
			determinePlaygroundLocation();
			if (w > 0 && h > 0) {
				regenerateBackground();
			}
		}

		private void setProperBackground(int w, int h) {
			if (w > h) {// horizontal

			}
		}

		public void resetPaint() {
			dm.paint.setStyle(Style.FILL_AND_STROKE);
			paint.setAlpha(255);
			paint.setColor(Color.BLACK);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(1f);
		}

		private void regenerateBackground() {
			BitmapUtil util = BitmapUtil.get(getContext());

			// this.bgBitmap = bitmapUtil.getBackgroundBitmap(getWidth(),
			// getHeight());
			bitmapUtil.drawBackgroundBitmap(canvas, getWidth(), getHeight(),
					paint);

		}

		private void determinePlaygroundLocation() {
			int height = getHeight();
			int width = getWidth();
			int size = Math.min(height, width);
			size = Playground.NUM_COLS * ((int) (size / Playground.NUM_COLS));
			RectF rect = new RectF((width - size) / 2, (height - size) / 2,
					(width - size) / 2 + size, (height - size) / 2 + size);
			playground.getDM().setRectF(rect);

		}
	}

	public void setControlView(ControlView controlView) {
		this.controlView = controlView;
	}

	public void configurationChanged(Configuration config) {
		if (dm != null && dm.playgroundRect != null) {
			this.dm.regenerateBackground();
		}
	}

	public class AnimationThread extends Thread {
		boolean flag = true;
		boolean paused = false;

		public AnimationThread() {
			super();
		}

		@Override
		public void run() {
			while (flag) {
				try {
					if (playground != null) {
						playground.step();
					}
					repaint();
					sleep(40);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
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
