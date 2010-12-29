package com.devinxutal.fmc.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class MoveSequenceIndicator extends View {
	private Paint mTextPaint;
	private String mText;
	private int mAscent;

	/**
	 * Constructor. This version is only needed if you will be instantiating the
	 * object manually (not from a layout XML file).
	 * 
	 * @param context
	 */
	public MoveSequenceIndicator(Context context) {
		super(context);
		initLabelView();
		this.setText("Hello World");
		this.setBackgroundColor(0x0000FF00);
	}

	private final void initLabelView() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(16);
		mTextPaint.setColor(0xFF000000);
		setPadding(3, 3, 3, 3);
	}

	/**
	 * Sets the text to display in this label
	 * 
	 * @param text
	 *            The text to display. This will be drawn as one line.
	 */
	public void setText(String text) {
		mText = text;
		requestLayout();
		invalidate();
	}

	/**
	 * Sets the text size for this label
	 * 
	 * @param size
	 *            Font size
	 */
	public void setTextSize(int size) {
		mTextPaint.setTextSize(size);
		requestLayout();
		invalidate();
	}

	/**
	 * Sets the text color for this label.
	 * 
	 * @param color
	 *            ARGB value for the text
	 */
	public void setTextColor(int color) {
		mTextPaint.setColor(color);
		invalidate();
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
			result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
					+ getPaddingRight();
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

		mAscent = (int) mTextPaint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop()
					+ getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * Render the text
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);

		Log.v("motiontest", "ondraw " + canvas.getWidth() + ","
				+ canvas.getHeight());
		canvas.drawText(mText, getPaddingLeft(), getPaddingTop() - mAscent,
				mTextPaint);
		mTextPaint.setColor(0x00FF0000);
		canvas.drawText(mText, 100, 100, mTextPaint);
	}
}
