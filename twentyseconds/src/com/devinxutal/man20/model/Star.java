package com.devinxutal.man20.model;

public class Star {
	public float x;
	public float y;
	public float vx;
	public float vy;

	public float pgWidth;
	public float pgHeight;

	public void move(int interval) {
		x += vx * interval;
		y += vy * interval;

		if (x < 0 || x > pgWidth || y < 0 || y > pgHeight) {
			active = false;
		}
	}

	private boolean active = true;

	public boolean isActive() {
		return active;
	}

	public void activate(Playground playground) {
		this.y = 0;
		this.active = true;
	}

}
