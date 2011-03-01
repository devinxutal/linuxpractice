package com.devinxutal.tetris.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;

public class TextPainter {
	Paint textPaint = null;
	Paint strokePaint = null;

	public TextPainter() {
		this.textPaint = new Paint();
		this.strokePaint = new Paint();
		this.textPaint.reset();
		this.strokePaint.reset();
		textPaint.setStrokeWidth(0);
		textPaint.setStyle(Style.FILL);
		strokePaint.setStrokeWidth(1);
		strokePaint.setStyle(Style.STROKE);
		strokePaint.setAntiAlias(true);
		textPaint.setAntiAlias(true);
	}

	public Paint getTextPaint() {
		return textPaint;
	}

	public Paint getStrokePaint() {
		return strokePaint;
	}

	public void setStrokeWidth(float width) {
		strokePaint.setStrokeWidth(width);
	}

	public void setStrokeColor(int color) {
		strokePaint.setColor(color);
	}

	public void setTextColor(int color) {
		textPaint.setColor(color);
	}

	public void setTextSize(float size) {
		textPaint.setTextSize(size);
		strokePaint.setTextSize(size);
	}

	public void setTypeface(Typeface typeface) {
		textPaint.setTypeface(typeface);
		strokePaint.setTypeface(typeface);
	}

	public void drawMonoText(Canvas canvas, String text, float x, float y,
			float gap) {

		float width = textPaint.measureText("A");
		this.drawMonoText(canvas, text, x, y, gap, width);
	}

	public void drawMonoScore(Canvas canvas, String text, float x, float y,
			float gap) {
		float width = textPaint.measureText("0");
		this.drawMonoText(canvas, text, x, y, gap, width);
	}

	public void drawMonoText(Canvas canvas, String text, float x, float y,
			float gap, float width) {
		x += 2;
		for (int i = 0; i < text.length(); i++) {
			String t = text.charAt(i) + "";
			this.drawCharacter(canvas, t, x
					+ (width - textPaint.measureText(t)) / 2, y);
			x += (gap + width);
		}
	}

	public void drawText(Canvas canvas, String text, float x, float y, float gap) {
		x += 2;
		for (int i = 0; i < text.length(); i++) {
			String t = text.charAt(i) + "";
			this.drawCharacter(canvas, t, x, y);
			x += (gap + textPaint.measureText(t));
		}
	}

	public void drawCharacter(Canvas canvas, String text, float x, float y) {
		y = y - textPaint.ascent();
		canvas.drawText(text, x, y, strokePaint);
		canvas.drawText(text, x, y, textPaint);
	}

	public float measureTextWidth(String text, float gap) {
		return textPaint.measureText(text) + (text.length() - 1) * gap + 4;
	}

	public void drawCenteredText(Canvas canvas, String string, Rect rect) {
		float x = rect.left + (rect.width() - textPaint.measureText(string))
				/ 2;
		float y = rect.top
				+ (rect.height() - textPaint.descent() + textPaint.ascent())
				/ 2;
		drawCharacter(canvas, string, x, y);
	}
}
