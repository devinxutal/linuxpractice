package com.devinxutal.tetris.ui;

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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.devinxutal.tetris.control.GameController;
import com.devinxutal.tetris.model.Playground;
import com.devinxutal.tetris.util.BitmapUtil;

public class PlaygroundView extends View {
	public static String TAG = "PlaygroundView";

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
		init();
	}

	public void setGameController(GameController controller) {
		this.gameController = controller;
	}

	public Playground getPlayground() {
		return playground;
	}

	public void setPlayground(Playground playground) {
		this.playground = playground;
		this.playground.initDrawingMetrics(this.getContext());
	}

	public PlaygroundView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PlaygroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private final void init() {
		dm = new DrawingMetrics();

		setPadding(3, 10, 3, 10);

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
			this.getPlayground().determinSize(w - 10, h - 10);
			this.getPlayground().reset();
			dm.onSizeChanged(w, h);
		}
	}

	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
	}

	public void reset() {
		playground.reset();
		finishedAlreadyNotified = false;
		finishingAlreadyNotified = false;
		notifyFinished = false;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(dm.bgBitmap, 0, 0, dm.paint);
		int w = playground.getWidth();
		int h = playground.getHeight();
		int width = this.getWidth();
		int height = this.getHeight();
		dm.resetPaint();
		playground.draw(canvas, (width - w) / 2, (height - h) / 2);
		//
		playground.drawPendingBlocks(canvas, dm.next1BlockRect,
				dm.next2BlockRect, dm.next3BlockRect);

	}

	private boolean finishedAlreadyNotified = false;
	private boolean finishingAlreadyNotified = false;
	private boolean notifyFinished = false;

	public void pause(boolean pause) {

	}

	public class DrawingMetrics {

		public final int[] STAR_COLORS = new int[] { Color.rgb(150, 0, 50),
				Color.rgb(0, 150, 50), Color.rgb(150, 150, 0),
				Color.rgb(150, 0, 100), Color.rgb(0, 0, 150) };

		private Paint paint;
		private BitmapUtil bitmapUtil;
		private Bitmap bgBitmap;
		private Bitmap gridBitmap;
		private Rect next1BlockRect;
		private Rect next2BlockRect;
		private Rect next3BlockRect;

		public DrawingMetrics() {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(18);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			bitmapUtil = BitmapUtil.get(getContext());
			try {
				gridBitmap = BitmapFactory.decodeStream(getContext()
						.getAssets().open("images/block_bg.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void onSizeChanged(int w, int h) {
			if (w > 0 && h > 0) {
				this.bgBitmap = bitmapUtil.getBackgroundBitmap(w, h);

				regenerateBackground();
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
			this.bgBitmap = bitmapUtil.getBackgroundBitmap(getWidth(),
					getHeight());
			Canvas canvas = new Canvas(bgBitmap);
			int w = playground.getWidth();
			int h = playground.getHeight();
			int gap = playground.GAP_LEN;
			int bs = playground.getBlockSize();
			int gridSize = bs + gap;

			int width = getWidth();
			int height = getHeight();
			dm.resetPaint();
			dm.paint.setStyle(Style.STROKE);
			dm.paint.setColor(Color.rgb(255, 255, 255));
			dm.paint.setStrokeWidth(0.7f);
			canvas.drawRoundRect(new RectF((width - w) / 2 - 3,
					(height - h) / 2 - 3, (width - w) / 2 + w + 3, (height - h)
							/ 2 + h + 3), 3, 3, dm.paint);
			dm.paint.setStyle(Style.FILL_AND_STROKE);
			dm.paint.setColor(Color.BLACK);
			// draw grid;

			float startX = (width - w) / 2;
			float startY = (height - h) / 2;
			paint.reset();
			paint.setAlpha(150);
			paint.setAntiAlias(false);
			paint.setStrokeWidth(1);
			for (int i = 0; i <= Playground.HORIZONTAL_BLOCKS; i++) {
				canvas.drawLine(startX + i * gridSize, startY, startX + i
						* gridSize, startY + h, paint);
			}
			for (int j = 0; j <= Playground.VERTICAL_BLOCKS; j++) {

				canvas.drawLine(startX, startY + j * gridSize, startX + w,
						startY + j * gridSize, paint);
			}
			paint.setAntiAlias(true);
			Bitmap bg = Bitmap.createScaledBitmap(gridBitmap, bs, bs, false);
			for (int i = 0; i < Playground.HORIZONTAL_BLOCKS; i++) {
				for (int j = 0; j < Playground.VERTICAL_BLOCKS; j++) {
					canvas.drawBitmap(bg, startX + gridSize * i + gap, startY
							+ gridSize * j + gap, paint);
				}
			}
			bg.recycle();

			// draw nextBlock bar
			Drawable nextBar = util.getInfoBar();
			int barHeight = nextBar.getIntrinsicHeight();
			int barWidth = (width - playground.getWidth() - 13) / 2;
			int barStartX = width - barWidth - 4;
			int barStartY = 5;
			int bs1 = (int) (barHeight * 0.6f);
			int bs2 = Math.min((int) (bs1 * 0.8),
					(int) ((barWidth - barHeight) / 2 * 0.8));

			next1BlockRect = new Rect(barStartX + (barHeight - bs1) / 2,
					barStartY + (barHeight - bs1) / 2, barStartX
							+ (barHeight - bs1) / 2 + bs1, barStartY
							+ (barHeight - bs1) / 2 + bs1);
			int next2BlockRectStartX = barStartX + barHeight
					+ (barWidth - barHeight - 2 * bs2) / 4;
			int next2BlockRectStartY = next1BlockRect.top + (bs1 - bs2) / 2;
			next2BlockRect = (new Rect(next2BlockRectStartX,
					next2BlockRectStartY, next2BlockRectStartX + bs2,
					next2BlockRectStartY + bs2));
			int next3BlockRectStartX = next2BlockRectStartX + bs2
					+ (barWidth - barHeight - 2 * bs2) / 4;

			next3BlockRect = (new Rect(next3BlockRectStartX,
					next2BlockRectStartY, next3BlockRectStartX + bs2,
					next2BlockRectStartY + bs2));

			nextBar.setBounds(new Rect(barStartX, barStartY, barStartX
					+ barWidth, barStartY + barHeight));
			nextBar.draw(canvas);
			// 
		}
	}

}
