package com.devinxutal.man20.model;

public class Defender extends Movable {

	public float radius = 5;

	public void move(int interval) {
		x += this.standardizedVX() * interval;
		y += this.standardizedVY() * interval;

		x = Math.min(x, pgWidth - radius);
		x = Math.max(x, radius);
		y = Math.min(y, pgHeight - radius);
		y = Math.max(y, radius);
	}

	private float radiusScale = 0.65f;

	public void setVisibleRadius(float radius) {
		this.radius = radius * radiusScale;
	}

	public float getVisibleRadius() {
		return radius / radiusScale;
	}
}
