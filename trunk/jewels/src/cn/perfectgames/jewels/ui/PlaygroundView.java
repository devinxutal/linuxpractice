package cn.perfectgames.jewels.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import cn.perfectgames.jewels.GoJewelsApplication;
import cn.perfectgames.jewels.R;
import cn.perfectgames.jewels.cfg.Configuration;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.control.GameController;
import cn.perfectgames.jewels.model.Playground;
import cn.perfectgames.jewels.util.BitmapUtil;
import cn.perfectgames.jewels.util.TextPainter;

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
	}

	public void setGameController(GameController controller) {
		this.gameController = controller;
		setPlayground(controller.getPlayground());
	}

	public Playground getPlayground() {
		return playground;
	}

	private void setPlayground(Playground playground) {
		this.playground = playground;
		this.playground.getDM().init(this.getContext());
	}

	public PlaygroundView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
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
			// draw progress indicator
			int len = (int)(playground.getScoreAndLevel().getProgress()*(dm.pgRect.width()-dm.pgind.getIntrinsicHeight()))+dm.pgind.getIntrinsicHeight();
			Rect indRect = new Rect(dm.pgRect.left, dm.pgRect.top, dm.pgRect.left+len,dm.pgRect.bottom );
			dm.pgind.setBounds(indRect);
			dm.pgind.draw(canvas);
			
			
			//canvas.clipRect(new Rect(100, 100, 200, 200));
			
			playground.draw(canvas);
			
			

			// draw stats
			dm.painter.setTypeface(dm.font);
			dm.painter.setTextColor(Color.GREEN);
			dm.painter.setStrokeColor(Color.GRAY);
			dm.painter.setTextSize(dm.statDigitSize);
			dm.painter.setStrokeWidth(dm.statDigitSize * dm.textStrokeScale);
			dm.painter.drawText(canvas, ""+playground.getScoreAndLevel().getMaxCombo(), new RectF(dm.comboRect), TextPainter.Align.Left);
			dm.painter.drawText(canvas, ""+playground.getScoreAndLevel().getMaxChain(), new RectF(dm.chainRect), TextPainter.Align.Left);
			dm.painter.drawText(canvas, ""+playground.getScoreAndLevel().getBonusX()+"x", new RectF(dm.bonusRect), TextPainter.Align.Left);
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


	public class DrawingMetrics {

		public final int[] STAR_COLORS = new int[] { Color.rgb(150, 0, 50),
				Color.rgb(0, 150, 50), Color.rgb(150, 150, 0),
				Color.rgb(150, 0, 100), Color.rgb(0, 0, 150) };

		private Paint paint;
		private TextPainter painter = new TextPainter();
		private BitmapUtil bitmapUtil;
		private Bitmap bgBitmap;
		
		private Drawable pgbar;
		private Drawable pgind;
		private Rect pgRect;
		
		private Canvas canvas;
		private Rect playgroundRect;
		private Rect infoAreaRect;
		private Rect controlBarRect;
		private Rect bottomBarRect;
		
		private Rect comboRect;
		private Rect chainRect;
		private Rect bonusRect;
		private Rect comboLabelRect;
		private Rect chainLabelRect;
		private Rect bonusLabelRect;
		private Rect scoreRect;
		private Typeface font;

		private float statLabelSize;
		private float statDigitSize;
		private float textStrokeScale = 0.15f;

		public DrawingMetrics() {
			font = Typeface.createFromAsset(getContext().getAssets(),
					Constants.FONT_PATH_COMIC);
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(18);
			bitmapUtil = GoJewelsApplication.getBitmapUtil();
			

			bgBitmap = bitmapUtil.getScreenBitmap(getContext());
			canvas = new Canvas(bgBitmap);
			// progress bar
			pgbar = getResources().getDrawable(R.drawable.pg_bar);
			pgind = getResources().getDrawable(R.drawable.pg_ind);
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
			paint.setStyle(Style.FILL_AND_STROKE);
			paint.setAlpha(255);
			paint.setColor(Color.BLACK);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(1f);
		}

		private void regenerateBackground() {
			GoJewelsApplication.getBitmapUtil();

			resetPaint();
			bitmapUtil.drawBackgroundBitmap(canvas, getWidth(), getHeight(),
					paint);
			resetPaint();
			playground.getDM().drawPlayground(canvas);
			resetPaint();
			//draw control bar
			paint.setColor(Color.BLACK);
			paint.setAlpha(80);
			canvas.drawRect(controlBarRect, paint);
			canvas.drawRect(bottomBarRect, paint);
			//draw progress bar
			this.pgbar.setBounds(pgRect);
			pgbar.draw(canvas);
			//draw combo and chain label
			
			painter.setTypeface(font);
			painter.setTextColor(Color.rgb(255, 200, 100));
			painter.setStrokeColor(Color.GRAY);
			painter.setTextSize(statLabelSize);
			dm.painter.setStrokeWidth(statLabelSize * dm.textStrokeScale);
			painter.drawText(canvas, "CHAIN", new RectF(chainLabelRect), TextPainter.Align.Left);
			painter.drawText(canvas, "COMBO", new RectF(comboLabelRect), TextPainter.Align.Left);
			painter.drawText(canvas, "BONUS", new RectF(bonusLabelRect), TextPainter.Align.Left);
		
		}

		private void determinePlaygroundLocation() {
			int height = getHeight();
			int width = getWidth();
			int size = Math.min(height, width);
			int l, t, w, h;
			// control bar
			this.controlBarRect = new Rect(0,0,width,getContext().getResources().getDrawable(R.drawable.icon_pause).getIntrinsicHeight());
			// info area
			int heightOfInfoArea = Math.min(height - size - controlBarRect.height(), width/3);
			this.playgroundRect = new Rect((width - size)/2, heightOfInfoArea+controlBarRect.height(), (width+size)/2, heightOfInfoArea+size+controlBarRect.height());
			RectF rect = new RectF(playgroundRect.left, playgroundRect.top, playgroundRect.right, playgroundRect.bottom);
			playground.getDM().setRectF(rect);
			
			
			
			this.infoAreaRect = new Rect(0,controlBarRect.height(),width, heightOfInfoArea + controlBarRect.height());
			// bottom bar
			this.bottomBarRect = new Rect(0, this.playgroundRect.bottom, width, height);
			
			// progress bar

			int pgbarWidth = width;
			int pgbarHeight = this.pgbar.getIntrinsicHeight();
			this.pgRect = new Rect((width - pgbarWidth)/2, infoAreaRect.bottom - pgbarHeight - 5, (width+pgbarWidth)/2, infoAreaRect.bottom - 5);
			
			// score bar 
			w = width/2; h = Math.min(infoAreaRect.height() - pgRect.height(),w/2);
			l = width/2; t = infoAreaRect.top+5;
			this.scoreRect = new Rect(width/2, infoAreaRect.top, width, pgRect.top);
			getPlayground().getAnimations().scoreBoardAnimation.setRect(scoreRect);
			
			// combo and chain and bonus
			l = 5; t = infoAreaRect.top+5; w = width/2 - 10; h = Math.min(w/2, pgRect.top-infoAreaRect.top) - 5;
			int labelW = w*6/10; int numW = w - labelW;
			int H = h/3;
			h = H*7/10;
			
			comboLabelRect = new Rect(l, t,l+ labelW,  t+h);
			comboRect = new Rect(l+labelW + 5, t,l+ labelW+numW,  t+h);

			t+=H;
			chainLabelRect =  new Rect(l, t,l+ labelW,  t+h);
			chainRect = new Rect(l+labelW + 5, t,l+ labelW+numW,  t+h);
			
			t+=H;
			bonusLabelRect =  new Rect(l, t,l+ labelW,  t+h);
			bonusRect = new Rect(l+labelW + 5, t,l+ labelW+numW,  t+h);
			
			this.painter.setTypeface(font);
			this.statLabelSize = Math.min(this.painter.determineTextSize("COMBO", new RectF(comboLabelRect)),this.painter.determineTextSize("BONUS", new RectF(comboLabelRect))) ;
			this.statDigitSize = 1.1f* statLabelSize;
			
			
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
		
		int interval = 1000/Constants.FPS;
		int sleep = interval/ 5;
		
		long timeElapsed;
		long lastTime;

		public AnimationThread() {
			super();
		}

		@Override
		public void run() {
			lastTime = System.currentTimeMillis();
			timeElapsed = 0;
			
			while (flag) {
				try 
				{
					long currentTime = System.currentTimeMillis();
					timeElapsed += currentTime - lastTime;
					lastTime = currentTime;
					
					//Log.v(TAG, "time elapsed: "+ timeElapsed);
					boolean stepped = false;
					while( timeElapsed>= interval){
						//Log.v(TAG, "need for stepping");
						timeElapsed -= interval;
						stepped = true;
						if(gameController != null){
							gameController.step();
						}
					}
					if(stepped == true){
						repaint();
					}
					sleep(5);

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
