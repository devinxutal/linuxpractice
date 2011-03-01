package com.devinxutal.tetris.ui;

import java.io.IOException;
import java.util.LinkedList;

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

import com.devinxutal.tetris.cfg.Constants;
import com.devinxutal.tetris.control.ButtonInfo;
import com.devinxutal.tetris.control.GameController;
import com.devinxutal.tetris.model.Playground;
import com.devinxutal.tetris.util.BitmapUtil;
import com.devinxutal.tetris.util.TextPainter;

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

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(dm.bgBitmap, 0, 0, dm.paint);
		int w = playground.getWidth();
		int h = playground.getHeight();
		int width = this.getWidth();
		int height = this.getHeight();
		dm.resetPaint();
		playground.draw(canvas, dm.playgroundRect.left, dm.playgroundRect.top);
		//
		playground.drawPendingBlocks(canvas, dm.next1BlockRect,
				dm.next2BlockRect, dm.next3BlockRect);

		TextPainter painter = new TextPainter();
		painter.setStrokeColor(Color.DKGRAY);
		painter.setTextColor(Color.WHITE);
		if (getWidth() > getHeight()) {
			painter.setTextSize(dm.scoreSize);
			painter.setTypeface(dm.scoreFont);
			painter.setStrokeWidth(dm.scoreSize * dm.textStrokeScale);
			painter.drawMonoScore(canvas, playground.getScoreAndLevel()
					.getScroreString(dm.scoreDigits), dm.scoreRect.left,
					dm.scoreRect.top, dm.scoreGap);
			painter.setTextSize(dm.labelSize);
			painter.setTypeface(dm.labelFont);
			painter.setStrokeWidth(dm.labelSize * dm.textStrokeScale);
			painter.drawMonoScore(canvas, playground.getScoreAndLevel()
					.getLevel()
					+ "", dm.levelRect.left, dm.levelRect.top, dm.labelGap);
			painter.drawMonoScore(canvas, playground.getScoreAndLevel()
					.getTotalLines()
					+ "", dm.linesRect.left, dm.linesRect.top, dm.labelGap);
		} else {
			painter.setTextSize(dm.scoreSize);
			painter.setTypeface(dm.scoreFont);
			painter.setStrokeWidth(dm.scoreSize * dm.textStrokeScale);
			painter.drawMonoScore(canvas, playground.getScoreAndLevel()
					.getScroreString(dm.scoreDigits), dm.scoreRect.left,
					dm.scoreRect.top, dm.scoreGap);
			painter.setTextSize(dm.statSize);
			painter.setTypeface(dm.statFont);
			painter.setStrokeWidth(dm.statSize * dm.textStrokeScale);
			painter.drawCenteredText(canvas, playground.getScoreAndLevel()
					.getLevel()
					+ "", dm.levelRect);
			painter.drawCenteredText(canvas, playground.getScoreAndLevel()
					.getTotalLines()
					+ "", dm.linesRect);
		}

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
		private Bitmap gridBitmap;
		private Rect playgroundRect;
		private Rect next1BlockRect;
		private Rect next2BlockRect;
		private Rect next3BlockRect;
		private Rect scoreRect;
		private Rect levelRect;
		private Rect linesRect;
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
		private float textStrokeScale = 0.2f;

		public DrawingMetrics() {
			font = Typeface.createFromAsset(getContext().getAssets(),
					Constants.FONT_PATH_COMIC);
			scoreFont = statFont = font;
			labelFont = Typeface.SANS_SERIF;
			labelFont = Typeface.create("Arial", Typeface.BOLD);
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
		}

		public void onSizeChanged(int w, int h) {
			determinePlaygroundLocation();
			if (w > 0 && h > 0) {
				if (w > h) {
					regenerateLandscapeBackground();
				} else {
					regenerateLandscapeBackground();
				}
			}

		}

		public void resetPaint() {
			dm.paint.setStyle(Style.FILL_AND_STROKE);
			paint.setAlpha(255);
			paint.setColor(Color.BLACK);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(1f);
		}

		private void regenerateLandscapeBackground() {
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
			canvas.drawRoundRect(new RectF(playgroundRect.left - 3,
					playgroundRect.top - 3, playgroundRect.right + 3,
					playgroundRect.bottom + 3), 3, 3, dm.paint);
			dm.paint.setStyle(Style.FILL_AND_STROKE);
			dm.paint.setColor(Color.BLACK);
			// draw grid;
			float startX = playgroundRect.left;
			float startY = playgroundRect.top;
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
			determinAndDrawNextBar(canvas);

			// draw score and level
			determinAndDrawStatistics(canvas);
			determinAndDrawControlButtons(canvas);
		}

		private void determinAndDrawNextBar(Canvas canvas) {
			int width = getWidth();
			int height = getHeight();

			if (width > height) {

				Drawable nextBar = bitmapUtil.getHorizontalInfoBar();
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
				int next2BlockRectStartX = (int) (barStartX + barHeight * 1.1 + (barWidth
						- barHeight - 2 * bs2) / 4);
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
			} else {
				Drawable nextBar = bitmapUtil.getVerticalInfoBar();
				int barWidth = nextBar.getIntrinsicWidth();
				int barHeight = (int) (barWidth * 2.5);
				int rightSide = (width - playgroundRect.width() - 6) / 2;
				int barStartX = playgroundRect.right + 3
						+ (rightSide - barWidth) / 2;
				int barStartY = playgroundRect.top;
				int bs1 = (int) (barWidth * 0.6f);
				int bs2 = Math.min((int) (bs1 * 0.8),
						(int) ((barHeight - barWidth) / 2 * 0.8));

				next1BlockRect = new Rect(barStartX + (barWidth - bs1) / 2,
						barStartY + (barWidth - bs1) / 2, barStartX
								+ (barWidth - bs1) / 2 + bs1, barStartY
								+ (barWidth - bs1) / 2 + bs1);
				int next2BlockRectStartX = next1BlockRect.left + (bs1 - bs2)
						/ 2;
				int next2BlockRectStartY = (int) (barStartY + barWidth * 1.1 + (barHeight
						- barWidth - 2 * bs2) / 4);
				next2BlockRect = (new Rect(next2BlockRectStartX,
						next2BlockRectStartY, next2BlockRectStartX + bs2,
						next2BlockRectStartY + bs2));
				int next3BlockRectStartY = next2BlockRectStartY + bs2
						+ (barHeight - barWidth - 2 * bs2) / 4;

				next3BlockRect = (new Rect(next2BlockRectStartX,
						next3BlockRectStartY, next2BlockRectStartX + bs2,
						next3BlockRectStartY + bs2));

				nextBar.setBounds(new Rect(barStartX, barStartY, barStartX
						+ barWidth, barStartY + barHeight));
				nextBar.draw(canvas);

			}

		}

		private void determinAndDrawStatistics(Canvas canvas) {
			int width = getWidth();
			int height = getHeight();
			if (getWidth() > getHeight()) {
				int scorePadding = 10;
				int textGap = 10;
				int scoreWidth = (width - playground.getWidth() - 12) / 2 - 2
						* scorePadding;
				int scoreStartX = scorePadding;
				int scoreStartY = scorePadding;
				determineScoreSize(scoreWidth, scoreWidth);
				determineLabelSize(scoreWidth / 2, scoreWidth);
				scoreRect = new Rect(scoreStartX, scoreStartY, scoreStartX
						+ scoreWidth, (int) (scoreStartY + lineHeight + 1));
				TextPainter painter = new TextPainter();
				painter.setTypeface(labelFont);
				painter.setTextSize(labelSize);
				painter.setStrokeWidth(labelSize * dm.textStrokeScale);
				painter.setStrokeColor(Color.DKGRAY);
				painter.setTextColor(Color.WHITE);
				painter.drawText(canvas, "LEVEL", scoreStartX, scoreStartY
						+ lineHeight + textGap, labelGap);
				painter.drawText(canvas, "LINES", scoreStartX, scoreStartY
						+ lineHeight * 2 + textGap, labelGap);
				levelRect = new Rect(scoreStartX + scoreWidth / 2 + textGap,
						(int) (scoreStartY + lineHeight + textGap), scoreStartX
								+ scoreWidth / 2 + textGap, (int) (scoreStartY
								+ lineHeight + textGap + lineHeight));
				linesRect = new Rect(
						scoreStartX + scoreWidth / 2 + textGap,
						(int) (scoreStartY + 2 * lineHeight + textGap),
						scoreStartX + scoreWidth / 2 + textGap,
						(int) (scoreStartY + 2 * lineHeight + textGap + lineHeight));
			} else {
				int scoreMaxWidth = playground.getWidth();
				int scoreMaxHeight = playgroundRect.top - 2 * 5;
				determineScoreSize(scoreMaxWidth, scoreMaxHeight);
				int scoreStartX = (width - playground.getWidth()) / 2;
				int scoreStartY = (int) (playgroundRect.top - scoreLineHeight) / 2;
				scoreRect = new Rect(scoreStartX, scoreStartY, scoreStartX
						+ playground.getWidth(), (int) (scoreStartY
						+ lineHeight + 1));

				int leftWidth = playgroundRect.left;
				int labelMaxWidth = leftWidth - 2 * 5;
				determineLabelSize(labelMaxWidth, height);
				int labelStartY = playgroundRect.top;
				int labelStartX = 5;

				TextPainter painter = new TextPainter();
				painter.setTypeface(labelFont);
				painter.setTextSize(labelSize);
				painter.setStrokeWidth(labelSize * dm.textStrokeScale);
				painter.setStrokeColor(Color.DKGRAY);
				painter.setTextColor(Color.WHITE);

				painter.drawText(canvas, "LEVEL", labelStartX, labelStartY,
						labelGap);
				labelStartY = (int) (labelStartY + labelLineHeight);
				levelRect = new Rect(labelStartX, labelStartY, labelStartX
						+ labelMaxWidth,
						(int) (labelStartY + labelLineHeight * 1.5));

				labelStartY = (int) (levelRect.bottom + labelLineHeight * 0.5);
				painter.drawText(canvas, "LINES", labelStartX, labelStartY,
						labelGap);
				labelStartY = (int) (labelStartY + labelLineHeight);
				linesRect = new Rect(labelStartX, labelStartY, labelStartX
						+ labelMaxWidth,
						(int) (labelStartY + labelLineHeight * 1.5));
				determineStatSize(levelRect.width(), levelRect.height());
			}

			// draw control buttons;
		}

		private void determinePlaygroundLocation() {
			int height = getHeight();
			int width = getWidth();
			if (width > height) {
				getPlayground().determinSize(width - 10, height - 10);
				playgroundRect = new Rect(
						(width - getPlayground().getWidth()) / 2,
						(height - getPlayground().getHeight()) / 2,
						(width - getPlayground().getWidth()) / 2
								+ getPlayground().getWidth(),
						(height - getPlayground().getHeight()) / 2
								+ getPlayground().getHeight());
			} else {
				getPlayground().determinSize((int) (width * 0.6),
						(int) (height * 0.7));
				int startY = getPlayground().getWidth() / 3;
				int startX = (width - getPlayground().getWidth()) / 2;
				playgroundRect = new Rect(startX, startY, startX
						+ getPlayground().getWidth(), startY
						+ getPlayground().getHeight());
			}
		}

		private void determinAndDrawControlButtons(Canvas canvas) {
			LinkedList<ButtonInfo> buttons = new LinkedList<ButtonInfo>();
			Bitmap button1 = BitmapUtil.get(getContext()).getAimButtonBitmap1();
			Bitmap button2 = BitmapUtil.get(getContext()).getAimButtonBitmap2();
			int button1Radius = button1.getWidth() / 2;
			int button2Radius = button2.getWidth() / 2;
			Pair delta = buttonDelta(button1Radius, button2Radius, 0);
			buttons.clear();
			int buttonY = 0;
			int buttonX = 0;
			if (getWidth() > getHeight()) {
				buttonY = getHeight() - button2Radius * 2 - 5;
				buttonX = getWidth() - button1Radius;
				buttons.add(new ButtonInfo(buttonX, buttonY, button1Radius,
						ControlView.BTN_RIGHT, button1, null));
				buttons.add(new ButtonInfo(buttonX - delta.x,
						buttonY - delta.y, button2Radius, ControlView.BTN_TURN,
						button2, null));
				buttons.add(new ButtonInfo(buttonX - delta.x,
						buttonY + delta.y, button2Radius, ControlView.BTN_DOWN,
						button2, null));

				buttonX = button1Radius;
				buttons.add(new ButtonInfo(buttonX, buttonY, button1Radius,
						ControlView.BTN_LEFT, button1, null));
				buttons.add(new ButtonInfo(buttonX + delta.x,
						buttonY - delta.y, button2Radius, ControlView.BTN_TURN,
						button2, null));
				buttons.add(new ButtonInfo(buttonX + delta.x,
						buttonY + delta.y, button2Radius,
						ControlView.BTN_DIRECT_DOWN, button2, null));
			} else {
				buttonY = getHeight() - button1Radius - 5;
				buttonX = button1Radius + 5;
				buttons.add(new ButtonInfo(buttonX, buttonY, button1Radius,
						ControlView.BTN_LEFT, button1, null));
				buttons.add(new ButtonInfo(getWidth() - buttonX, buttonY,
						button1Radius, ControlView.BTN_RIGHT, button1, null));

				buttonX = getWidth() / 2;
				buttonY = getHeight() - button2Radius - 5;

				buttons.add(new ButtonInfo(buttonX, buttonY, button2Radius,
						ControlView.BTN_DOWN, button2, null));
			}

			paint.setAlpha(255);
			for (ButtonInfo button : buttons) {
				canvas.drawBitmap(button.buttonBG, button.x - button.radius,
						button.y - button.radius, paint);
			}
			if (controlView != null) {
				controlView.setButtons(buttons);
			}
		}

		private Pair buttonDelta(int r1, int r2, int gap) {
			int edge1 = r1 + r2 + gap;
			int edge2 = r2 + gap / 2;
			int edge3 = (int) Math.round(Math.sqrt(edge1 * edge1 - edge2
					* edge2));
			return new Pair(edge3, edge2);
		}

		private void determineScoreSize(float maxWidth, float maxHeight) {
			TextPainter painter = new TextPainter();
			painter.setTypeface(scoreFont);
			scoreSize = 1f;
			String text = "";
			for (int i = 0; i < scoreDigits; i++) {
				text += "0";
			}
			float gapScale = 0.1f;
			for (; scoreSize <= maxHeight; scoreSize += 0.5f) {
				scoreGap = scoreSize * gapScale;
				painter.setTextSize(scoreSize);
				if (painter.measureTextWidth(text, scoreGap) > maxWidth) {
					break;
				}
			}
			scoreSize -= 0.5f;
			scoreGap = scoreSize * gapScale;
			Paint paint = painter.getStrokePaint();
			lineHeight = paint.descent() - paint.ascent() + 2
					* paint.getStrokeWidth();
			scoreLineHeight = paint.descent() - paint.ascent() + 2
					* paint.getStrokeWidth();

		}

		private void determineLabelSize(float maxWidth, float maxHeight) {
			TextPainter painter = new TextPainter();
			painter.setTypeface(labelFont);
			labelSize = 1f;
			String text = "LEVEL";
			float gapScale = 0.1f;
			for (; labelSize <= maxHeight; labelSize += 0.5f) {
				labelGap = labelSize * gapScale;
				painter.setTextSize(labelSize);
				if (painter.measureTextWidth(text, labelGap) > maxWidth) {
					break;
				}
			}
			labelSize -= 0.5f;
			labelGap = labelSize * gapScale;
			Paint paint = painter.getStrokePaint();
			labelLineHeight = paint.descent() - paint.ascent() + 2
					* paint.getStrokeWidth();
		}

		private void determineStatSize(float maxWidth, float maxHeight) {
			TextPainter painter = new TextPainter();
			painter.setTypeface(labelFont);
			statSize = 1f;
			String text = "000";
			float gapScale = 0.1f;
			for (; statSize <= maxHeight; statSize += 0.5f) {
				painter.setTextSize(statSize);
				if (painter.measureTextWidth(text, 0) > maxWidth) {
					break;
				}
			}
			statSize -= 0.5f;
			Paint paint = painter.getStrokePaint();
			statLineHeight = paint.descent() - paint.ascent() + 2
					* paint.getStrokeWidth();
		}
	}

	public void setControlView(ControlView controlView) {
		this.controlView = controlView;
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
