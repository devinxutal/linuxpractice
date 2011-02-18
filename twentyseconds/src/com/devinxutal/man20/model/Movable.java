package com.devinxutal.man20.model;

public abstract class Movable {
	public float x;
	public float y;
	public float vx;
	public float vy;

	public static final float STANDARD_HEIGHT = 320f;

	public float pgWidth;
	public float pgHeight;

	public abstract void move(int interval);

	public float standardizedVX() {
		return pgHeight / STANDARD_HEIGHT * vx;
	}

	public float standardizedVY() {
		return pgHeight / STANDARD_HEIGHT * vy;
	}
}
