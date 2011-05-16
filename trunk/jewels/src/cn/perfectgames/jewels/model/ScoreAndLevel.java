package cn.perfectgames.jewels.model;

import java.io.Serializable;

import cn.perfectgames.jewels.cfg.Configuration;
import cn.perfectgames.jewels.model.Playground.Elimination;

public class ScoreAndLevel implements Serializable {
	public static final int MAX_LEVEL = 15;
	private int level;
	private int score;
	private int totalLines;
	private int currentLines;
	private int bonusX = 1;
	
	private int maxCombo;
	private int maxChain;
	
	private int combo = 0;

	public int getMaxCombo() {
		return maxCombo;
	}

	public int getMaxChain() {
		return maxChain;
	}
	
	public int getBonusX(){
		return bonusX;
	}

	public void reset() {
		level = 1;
		score = 0;
		totalLines = 0;
		currentLines = 0;
		
	}
	
	double progress = 0;
	double delta = 0.002;
	public double getProgress(){
		progress +=delta;
		if(progress >1){
			progress = 1;
			delta = -0.002;
		}else if(progress <0){
			progress = 0;
			delta = 0.002;
		}
		return progress;
	}

	public int addScore(Elimination e){
		this.combo++;
		this.maxCombo = Math.max(maxCombo ,combo);
		int chain = e.end - e.start +1;
		this.maxChain = Math.max(chain,maxChain);
		int scoreToAdd = queryScore(e, combo);
		this.score += scoreToAdd;
		return scoreToAdd;
	}
	
	public void resetCombo(){
		this.combo = 0;
	}
	
	public int queryScore(Elimination e, int combo){
		int SCORE_BASE = 10;
		int COMBO_BONUS_BASE = 30;
		int LENGTH_BONUS_BASE = 20;
		int len = e.end - e.start+1;
		int comboBonus = (combo -1) * COMBO_BONUS_BASE;
		int lenBonus = (len -3)* LENGTH_BONUS_BASE;
		return bonusX*(SCORE_BASE + comboBonus +lenBonus);
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
		return level * 5;
	}

	public int getGoalRemained() {
		return Math.max(getGoal() - getCurrentLines(), 0);
	}

	public void eliminateLines(int lineNum, boolean combo) {
		int addScore = 0;
		switch (lineNum) {
		case 1:
			addScore = 20;
			break;
		case 2:
			addScore = 60;
			break;
		case 3:
			addScore = 120;
			break;
		case 4:
			addScore = 200;
			break;
		}
		if (combo) {
			addScore = addScore * 5;
		}
		addScore(addScore);
		currentLines += lineNum;
		totalLines += lineNum;
		if (currentLines >= getGoal() && level < MAX_LEVEL) {
			level++;
			currentLines = 0;
		}
	}

	public void rowsFallDown(int rows) {
		int addScore = (rows / 4);
		addScore(addScore);
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

	public float getMaxLevel() {
		return MAX_LEVEL;
	}
}
