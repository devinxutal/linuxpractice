package com.devinxutal.fmc.primitives;

public class Color {
	private int r, g, b, a;

	public Color() {
		this(255, 255, 255);
	}

	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}
	

	public Color(int r, int g, int b, int a) {
		if (r < 0 && r >= -128) {
			r = 256 + r;
		}
		if (g < 0 && g >= -128) {
			g = 256 + g;
		}
		if (b < 0 && b >= -128) {
			b = 256 + b;
		}
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public int getRed() {
		return r;
	}

	public void setRed(int r) {
		this.r = r;
	}

	public int getGreen() {
		return g;
	}

	public void setGreen(int g) {
		this.g = g;
	}

	public int getBlue() {
		return b;
	}

	public void setBlue(int b) {
		this.b = b;
	}

	public int getAlpha() {
		return a;
	}

	public void setAlpha(int a) {
		this.a = a;
	}

	// pre-defined colors
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color RED = new Color(255, 0, 0);
	public static final Color GREEN = new Color(0, 255, 150);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color ORANGE = new Color(255, 120, 0);
	public static final Color ANY = new Color(100, 100, 100);
}
