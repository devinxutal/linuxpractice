package com.devinxutal.tetris.model;

import java.io.Serializable;

public class ScoreAndLevel implements Serializable {
	int level;
	int score;
	int totalLines;
	int currentLines;

	public void reset() {
		level = 0;
		score = 0;
		totalLines = 0;
		currentLines = 0;
	}

	public int getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(int totalLines) {
		this.totalLines = totalLines;
	}

	public int getCurrentLines() {
		return currentLines;
	}

	public int addScore(int score) {
		return (this.score += score);
	}

	public int getScore() {
		return score;
	}

	public int getLevel() {
		return level;
	}

	public int getGoal() {
		return (level + 1) * 5;
	}

	public void eliminateLines(int lineNum) {
		int addScore = (int) Math.round(Math.pow(2, lineNum - 1)) * 10;
		addScore(addScore);
		currentLines += lineNum;
		totalLines += lineNum;
		if (currentLines >= getGoal()) {
			level++;
			currentLines = 0;
		}
	}

	public String getScroreString(int digits) {
		String s = "";
		long temp = score;
		while (temp > 0) {
			s = temp % 10 + s;
			temp = temp / 10;
		}
		while (s.length() < digits) {
			s = "0" + s;
		}
		return s;
	}
}
