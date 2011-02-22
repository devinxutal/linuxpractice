package com.devinxutal.tetris.model;

public class ScoreAndLevel {
	int level;
	int score;

	public int addScore(int score) {
		return (this.score += score);
	}

	public int getScore() {
		return score;
	}

	public int getLevel() {
		return level;
	}
}
