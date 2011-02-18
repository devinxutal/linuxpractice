package com.devinxutal.man20.model;

import java.util.Random;

import com.devinxutal.man20.cfg.Configuration;
import com.devinxutal.man20.util.RandomUtil;

public class Attacker extends Movable {
	private float standardSpeed = 1;

	public Attacker() {
		this.standardSpeed = 0.7f + 0.3f * (Configuration.config()
				.getDifficulty() - 1);
	}

	public void move(int interval) {
		x += this.standardizedVX() * interval;
		y += this.standardizedVY() * interval;

		if (x < 0 || x > pgWidth || y < 0 || y > pgHeight) {
			active = false;
		}
	}

	private boolean active = false;

	public boolean isActive() {
		return active;
	}

	public void activate(Playground playground) {
		Defender def = playground.getDefender();
		reset();
		Random random = RandomUtil.r;
		float v = standardSpeed;
		v = (float) (v * RandomUtil.r.nextDouble() / 10 + v);
		double dx = def.x - x;
		double dy = def.y - y;
		dx = dx * (random.nextDouble() / 2 + 0.75f);
		dy = dy * (random.nextDouble() / 2 + 0.75f);
		double scale = v / Math.sqrt(dx * dx + dy * dy);
		vx = (float) (scale * dx);
		vy = (float) (scale * dy);
		this.active = true;
	}

	public void reset() {
		Random random = RandomUtil.r;

		int delta = random.nextInt(Math.max(1, (int) pgWidth / 10));
		if (random.nextBoolean()) {// x
			this.x = (float) (random.nextDouble() * pgWidth);
			if (random.nextBoolean()) {
				this.y = 0 - delta;
			} else {
				this.y = pgHeight + delta;
			}
		} else {
			this.y = (float) (random.nextDouble() * pgHeight);
			if (random.nextBoolean()) {
				this.x = 0 - delta;
			} else {
				this.x = pgWidth + delta;
			}
		}
	}
}
