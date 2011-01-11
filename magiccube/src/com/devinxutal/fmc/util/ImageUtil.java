package com.devinxutal.fmc.util;

import android.graphics.Color;

import com.devinxutal.fmc.model.CubeColor;

public class ImageUtil {
	public static CubeColor getCubeColor(int color, CubeColor[] colors) {
		int distance = Integer.MAX_VALUE;
		CubeColor best = CubeColor.ANY;
		for (CubeColor c : colors) {
			int d = distance(color, c.getColor());
			if (d < distance) {
				best = c;
				distance = d;
			}
		}
		return best;
	}

	public static int distance(int c1, int c2) {
		int r1 = Color.red(c1);
		int g1 = Color.green(c1);
		int b1 = Color.blue(c1);
		int r2 = Color.red(c2);
		int g2 = Color.green(c2);
		int b2 = Color.blue(c2);

		float p = Math.min(255f / r1, Math.min(255f / g1, 255f / b1));
		r1 = (int) (p * r1);
		g1 = (int) (p * g1);
		b1 = (int) (p * b1);

		p = Math.min(255f / r2, Math.min(255f / g2, 255f / b2));
		r2 = (int) (p * r2);
		g2 = (int) (p * g2);
		b2 = (int) (p * b2);
		return (r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2)
				* (b1 - b2);
	}
}
