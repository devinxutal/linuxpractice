package com.devinxutal.fmc.ui;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MoveSequenceIndicator extends SurfaceView {
	private static final int LEN_GAP = 10;
	private static final int LEN_IND = 26;
	public static final String SYMBOL_END = "#";

	private Paint paint;
	private Canvas bufferCanvas;
	private Bitmap bufferBitmap;

	private int colorLeft = Color.GREEN;
	private int colorRight = Color.GRAY;
	private int colorIndicator = Color.YELLOW;

	private LinkedList<String> symbols;

	private int current;
	private int previous;
	private Animation animation = new Animation();
	private AnimationThread animationThread = null;
	private int paintAscent;

	// for drawing
	int offset = 0;

	/**
	 * Constructor. This version is only needed if you will be instantiating the
	 * object manually (not from a layout XML file).
	 * 
	 * @param context
	 */
	public MoveSequenceIndicator(Context context) {
		super(context);
		symbols = new LinkedList<String>();
		symbols.addLast(SYMBOL_END);
		init();
		this.setWillNotDraw(false);
		this.animationThread = new AnimationThread();
		this.animationThread.start();
	}

	public void setMoveSymbols(String[] symbols) {
		if (symbols != null) {
			this.symbols.clear();
			for (String str : symbols) {
				this.symbols.addLast(str);
			}
			this.symbols.addLast(SYMBOL_END);
		}
		this.current = 0;
		this.previous = 0;
		this.symbolWidth = -1;
		refreshBuffer();
		invalidate();
	}

	public String[] getMoveSymbols() {
		String[] sym = new String[symbols.size() - 1];
		for (int i = 0; i < sym.length; i++) {
			sym[i] = symbols.get(i);
		}
		return sym;
	}

	public String getCurrentSymbol() {
		if (current != symbols.size() - 1) {
			return symbols.get(current);

		} else {
			return null;
		}
	}

	public boolean moveForward() {
		if (current >= symbols.size() - 1) {
			return false;
		}
		previous = current;
		current++;
		refreshBuffer();
		startAnimation(previous, current);
		invalidate();
		return true;
	}

	public boolean moveBackward() {
		if (current <= 0) {
			return false;
		}
		previous = current;
		current--;
		refreshBuffer();
		startAnimation(previous, current);
		invalidate();
		return true;
	}

	public boolean moveTo(int to) {
		if (to < 0 || to >= symbols.size() || to == current) {
			return false;
		}
		previous = current;
		current = to;

		refreshBuffer();
		startAnimation(previous, current);
		invalidate();
		return true;
	}

	private void startAnimation(int from, int to) {
		Log.v("cc", "start animation");
		Rect rect = new Rect();
		this.getDrawingRect(rect);
		int width = rect.width();
		int symbolWidth = this.measureSymbolsWidth();
		float symbolX = offset + (width - symbolWidth) / 2;

		float indicatorX = symbolX;
		for (int i = 0; i <= from; i++) {
			indicatorX += LEN_GAP;
			float w = paint.measureText(symbols.get(i));
			if (i == from) {
				w = w / 2;
			}
			indicatorX += w;
		}
		indicatorX -= LEN_IND / 2;
		float indicatorXnew = symbolX;
		for (int i = 0; i <= to; i++) {
			indicatorXnew += LEN_GAP;
			float w = paint.measureText(symbols.get(i));
			if (i == to) {
				w = w / 2;
			}
			indicatorXnew += w;
		}

		indicatorXnew -= LEN_IND / 2;
		animation.startAnimation(symbolX, symbolX, indicatorX, indicatorXnew,
				10);

	}

	private final void init() {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(18);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		setPadding(3, 3, 3, 3);
		this.bufferBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		this.bufferCanvas = new Canvas();
	}

	/**
	 * @see android.view.View#measure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
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
			result = measureSymbolsWidth() + getPaddingLeft()
					+ getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	private int symbolWidth = -1;

	private int measureSymbolsWidth() {
		if (symbolWidth > -0)
			return symbolWidth;
		float len = LEN_GAP * (symbols.size() + 1);
		for (String s : symbols) {
			len += paint.measureText(s);
		}
		symbolWidth = (int) (len + 0.1);
		return symbolWidth;
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

		paintAscent = (int) paint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) (-paintAscent + paint.descent()) + getPaddingTop()
					+ getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private void repaint() {
		Canvas c = null;
		SurfaceHolder surfaceHolder = getHolder();
		try {
			c = surfaceHolder.lockCanvas();
			if (c != null) {
				onDraw(c);
			}
		} finally {
			if (c != null) {
				// Log.v("cc", "painting and posting");
				surfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) { // repaint();
		super.onSizeChanged(w, h, oldw, oldh);
		repaint();
	}

	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		repaint();
	}

	private void autoRedraw(final boolean auto) {
		Context c = this.getContext();
		if (c instanceof Activity) {
			((Activity) c).runOnUiThread(new Runnable() {

				public void run() {
					setWillNotDraw(!auto);
				}

			});
		}
	}

	private void refreshBuffer() {
		int height = (int) (paint.descent() - paint.ascent() + 0.1);
		int width = (int) (this.measureSymbolsWidth() + 0.1);
		if (this.bufferBitmap == null || this.bufferBitmap.getWidth() != width
				|| this.bufferBitmap.getHeight() != height) {
			bufferBitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
		}
		bufferCanvas.setBitmap(bufferBitmap);
		Canvas canvas = bufferCanvas;
		canvas.drawRGB(0, 0, 0);
		paint.setColor(colorLeft);
		// prepare metrics
		int symLen = width;
		int index = 0;
		float x;
		float y = -paint.ascent();

		float tmpLen = LEN_GAP;
		// start draw
		for (String sym : symbols) {
			if (index == current) {
				paint.setColor(colorRight);
			}
			x = tmpLen;
			canvas.drawText(sym, x, y, paint);
			//
			index++;
			tmpLen += (LEN_GAP + paint.measureText(sym));
		}
	}

	/**
	 * Render the text
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		paint.setAlpha(255);
		canvas.drawRGB(0, 0, 0);
		int indicatorOffset = 0;
		int symbolOffset = 0;
		if (animation.isInAnimation()) {
			symbolOffset = (int) animation.symbolOffset();
			indicatorOffset = (int) animation.indicatorOffset();
		} else {
			int symLen = measureSymbolsWidth();
			symbolOffset = (getWidth() - symLen) / 2 + offset;

			float indicatorX = symbolOffset;
			for (int i = 0; i <= current; i++) {
				indicatorX += LEN_GAP;
				float w = paint.measureText(symbols.get(i));
				if (i == current) {
					w = w / 2;
				}
				indicatorX += w;
			}
			indicatorX -= LEN_IND / 2;
			indicatorOffset = (int) indicatorX;
		}
		int yOffset = (this.getHeight() - bufferBitmap.getHeight()) / 2;
		canvas.drawBitmap(this.bufferBitmap, symbolOffset, yOffset, paint);
		paint.setColor(colorIndicator);
		//paint.setStyle(Style.STROKE);
		paint.setAlpha(100);
		canvas.drawRect(indicatorOffset, yOffset, indicatorOffset + LEN_IND,
				yOffset + bufferBitmap.getHeight(), paint);
	}

	public class AnimationThread extends Thread {
		boolean flag = true;

		@Override
		public void run() {
			while (flag) {
				try {
					animation.getSemaphore().acquire();

					// setWillNotDraw(true);
					autoRedraw(false);
					// Log.v("cc", "thread running, semaphore aqquired");
					while (animation.step()) {
						repaint();
						sleep(30);
					}
					animation.stopAnimation();

					autoRedraw(true);
					// setWillNotDraw(false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class Animation {
		private float symbolFrom;
		private float symbolTo;
		private float indicatorFrom;
		private float indicatorTo;
		private int totalSteps;
		private int currentStep;
		private boolean inAnimation = false;
		private final Semaphore semaphore = new Semaphore(0);

		public Semaphore getSemaphore() {
			return semaphore;
		}

		public boolean isInAnimation() {
			return inAnimation;
		}

		public void startAnimation(float symbolFrom, float symbolTo,
				float indicatorFrom, float indicatorTo, int totalSteps) {
			if (this.inAnimation) {
				return;
			}
			this.symbolFrom = symbolFrom;
			this.symbolTo = symbolTo;
			this.indicatorFrom = indicatorFrom;
			this.indicatorTo = indicatorTo;
			this.totalSteps = Math.max(3, totalSteps);
			this.currentStep = 0;

			this.inAnimation = true;
			this.getSemaphore().release();
		}

		public void stopAnimation() {
			this.inAnimation = false;
		}

		public boolean step() {
			if (currentStep < totalSteps) {
				currentStep++;
				return true;
			} else {
				return false;
			}
		}

		public float symbolOffset() {
			return (symbolFrom + (symbolTo - symbolFrom) * currentStep
					/ totalSteps);
		}

		public float indicatorOffset() {
			return (indicatorFrom + (indicatorTo - indicatorFrom) * currentStep
					/ totalSteps);
		}

	}
}
