package com.devinxutal.man20.model;

import java.util.Random;

import android.util.Log;

import com.devinxutal.man20.util.MathUtil;

public class Playground {
	private Defender defender;
	private Attacker[] attackers;
	private int numAttackers;
	private Star[] stars;
	private int width;
	private int height;

	private boolean crashed = false;

	public Playground(int attackerNum) {
		this.numAttackers = attackerNum;
		this.attackers = new Attacker[numAttackers];
		for (int i = 0; i < numAttackers; i++) {
			attackers[i] = new Attacker();
		}
		this.defender = new Defender();
		this.stars = new Star[150];
		for (int i = 0; i < stars.length; i++) {
			stars[i] = new Star();
		}
	}

	public Attacker[] getAttackers() {
		return attackers;
	}

	public Star[] getStars() {
		return stars;
	}

	public Defender getDefender() {
		return defender;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setSize(int w, int h) {
		Log.v("Playground", "1. set size : " + w + " " + h);
		this.width = w;
		this.height = h;
		this.defender.pgHeight = h;
		this.defender.pgWidth = w;

		float defenderRadius = Math.min(w, h);
		defenderRadius = defenderRadius / 20;
		this.defender.setVisibleRadius(defenderRadius);

		for (Attacker at : attackers) {
			at.pgHeight = h;
			at.pgWidth = w;
		}
		for (Star star : stars) {
			star.pgHeight = h;
			star.pgWidth = w;
		}

	}

	public void reset() {
		crashed = false;
		
		Random random = new Random();
		defender.x = this.width / 2;
		defender.y = this.height / 2;
		for (Attacker at : attackers) {
			if (random.nextBoolean()) {// x
				at.x = (float) (random.nextDouble() * width);
				if (random.nextBoolean()) {
					at.y = 0;
				} else {
					at.y = height;
				}
			} else {
				at.y = (float) (random.nextDouble() * height);
				if (random.nextBoolean()) {
					at.x = 0;
				} else {
					at.x = width;
				}
			}
			at.activate(this);
		}

		for (Star star : stars) {
			star.x = random.nextFloat() * this.width;
			star.y = random.nextFloat() * this.height;
			star.vx = 0;
			star.vy = random.nextFloat() / 10;

		}
	}

	public void move(int interval) {
		defender.move(interval);
		for (Attacker at : attackers) {
			at.move(interval);
			if (!at.isActive()) {
				at.activate(this);
			}
			if (checkCollision(defender, at)) {
				crashed = true;
			}
		}
		for (Star at : stars) {
			at.move(interval);
			if (!at.isActive()) {
				at.activate(this);
			}

		}
	}

	private boolean checkCollision(Defender defender, Attacker attacker) {
		if (MathUtil.distanceSquared(defender.x, defender.y, attacker.x,
				attacker.y) < defender.radius * defender.radius) {
			return true;
		}
		return false;
	}

	public boolean isCrashed() {
		return crashed;
	}
}
