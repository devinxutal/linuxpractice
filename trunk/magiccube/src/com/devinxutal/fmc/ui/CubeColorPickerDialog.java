package com.devinxutal.fmc.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.devinxutal.fmc.model.CubeColor;

public class CubeColorPickerDialog extends Dialog {

	public interface OnColorChangedListener {
		void colorChanged(CubeColor color);
	}

	private OnColorChangedListener mListener;

	private CubeColor[] colors;
	private CubeColor initialColor;

	private class ColorPickerView extends View {
		private Paint mPaint;
		private OnColorChangedListener mListener;

		ColorPickerView(Context c, OnColorChangedListener l,
				CubeColor initialColor) {
			super(c);
			mListener = l;

			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			for (int i = 0; i < colors.length; i++) {
				int x = (i % NUM_PER_LINE) * (PICKER_RADIUS * 2 + PICKER_GAP)
						+ PICKER_RADIUS + PICKER_GAP;
				int y = (i / NUM_PER_LINE) * (PICKER_RADIUS * 2 + PICKER_GAP)
						+ PICKER_RADIUS + PICKER_GAP;
				if (colors[i] == initialColor) {
					Rect r = calcRect(i);
					RectF rf = new RectF(r.left, r.top, r.right, r.bottom);
					mPaint.setColor(Color.rgb(150, 150, 150));
					mPaint.setAlpha(100);
					canvas.drawRoundRect(rf, PICKER_RADIUS / 4,
							PICKER_RADIUS / 4, mPaint);
				}
				mPaint.setColor(colors[i].getColor());
				mPaint.setAlpha(255);
				canvas.drawCircle(x, y, PICKER_RADIUS, mPaint);

			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(PICKER_GAP * (NUM_PER_LINE + 1)
					+ PICKER_RADIUS * NUM_PER_LINE * 2, ((colors.length - 1)
					/ NUM_PER_LINE + 1)
					* PICKER_RADIUS
					* 2
					+ ((colors.length - 1) / NUM_PER_LINE + 1 + 1) * PICKER_GAP);
		}

		private static final int PICKER_RADIUS = 20;
		private static final int PICKER_GAP = 20;
		private static final int NUM_PER_LINE = 3;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			for (int i = 0; i < colors.length; i++) {
				Rect r = calcRect(i);
				if (x >= r.left && x <= r.right && y >= r.top && y <= r.bottom) {
					mListener.colorChanged(colors[i]);
					break;
				}
			}
			return true;
		}

		private Rect calcRect(int index) {
			int x = (index % NUM_PER_LINE) * (PICKER_RADIUS * 2 + PICKER_GAP)
					+ PICKER_RADIUS + PICKER_GAP;
			int y = (index / NUM_PER_LINE) * (PICKER_RADIUS * 2 + PICKER_GAP)
					+ PICKER_RADIUS + PICKER_GAP;
			int delta = PICKER_RADIUS + PICKER_GAP / 2;
			return new Rect(x - delta, y - delta, x + delta, y + delta);
		}
	}

	public CubeColorPickerDialog(Context context,
			OnColorChangedListener listener, CubeColor initialColor) {
		super(context);
		colors = new CubeColor[] { CubeColor.WHITE, CubeColor.YELLOW,
				CubeColor.RED, CubeColor.ORANGE, CubeColor.BLUE,
				CubeColor.GREEN };
		mListener = listener;
		this.initialColor = initialColor;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OnColorChangedListener l = new OnColorChangedListener() {
			public void colorChanged(CubeColor color) {
				mListener.colorChanged(color);
				dismiss();
			}
		};

		setContentView(new ColorPickerView(getContext(), l, initialColor));
		setTitle("Pick a Color");
	}
}