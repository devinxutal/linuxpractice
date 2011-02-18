package com.devinxutal.man20.model;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Explosion {
	public static int num = 300;
	private float points[] = new float[num * 2];
	private float radius;
	private float scale = 1;
	private float x;
	private float y;

	Paint paint;

	private int colors[] = new int[] { Color.rgb(255, 200, 100),
			Color.rgb(255, 100, 50), Color.rgb(255, 255, 100) };

	public Explosion(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		paint = new Paint();
		paint.setAntiAlias(true);

		Random r = new Random();
		for (int i = 0; i < num; i++) {
			float dx = 1;
			float dy = 1;
			while ((dx * dx + dy * dy) > 0.25f) {
				dx = r.nextFloat() * r.nextFloat();
				dy = r.nextFloat() * r.nextFloat();
				if (r.nextBoolean()) {
					dx = -dx;
				}
				if (r.nextBoolean()) {
					dy = -dy;
				}
			}
			points[i * 2] = dx;
			points[i * 2 + 1] = dy;
		}
		scale = radius/0.8f;
		scaleDelta = (scale - 1) / (totalStep * maxTime);
	}

	private int totalStep = 10;
	private int currentStep = 0;
	private float maxTime = 0.8f;
	private float scaleDelta = (scale - 1) / (totalStep * maxTime);
	private float currentScale;
	private int alpha = 255;
	private int alphaDelta = (int) (alpha / ((1 - maxTime) * totalStep));

	public boolean step() {
		currentStep++;
		if (currentStep < totalStep) {
			return true;
		}
		return false;
	}

	public void reset() {
		currentStep = -1;
		currentScale = 1;
	}

	public void draw(Canvas canvas) {
		// Log.v("Explosion", "draw");
		if (currentStep < maxTime * totalStep) {
			currentScale += scaleDelta;
			alpha = 255;
		} else {
			currentScale -= scaleDelta;
			alpha -= alphaDelta;
		}
		paint.setAlpha(alpha);
		int count = points.length / 2 / colors.length;
		int index = 0;
		paint.setColor(colors[0]);
		for (int i = 0; i < num; i++) {
			if (count-- < 0) {
				count = points.length / 2 / colors.length;
				index++;
				if (index >= colors.length) {
					index = colors.length;
				}
				paint.setColor(colors[index]);
			}
			float dx = points[i * 2] * currentScale;
			float dy = points[i * 2 + 1] * currentScale;
			canvas.drawPoint(x + dx, y + dy, paint);
			// Log.v("Explosion", "draw circle at " + (x + dx) + " " + (y +
			// dy));
		}
	}
}
