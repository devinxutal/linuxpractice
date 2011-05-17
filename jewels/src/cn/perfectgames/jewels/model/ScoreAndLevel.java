package cn.perfectgames.jewels.model;

import java.io.Serializable;

import cn.perfectgames.jewels.model.Playground.Elimination;

public class ScoreAndLevel implements Serializable {
	public static final int MAX_LEVEL = 15;
	
	private GameMode gameMode = GameMode.Normal;
	
	private int level;
	private int score;
	private int totalEliminations;
	private int currentEliminations;
	private int bonusX = 1;
	
	private int timeElapsed;
	
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
		totalEliminations = 0;
		currentEliminations = 0;
		timeElapsed = 0;
		bonusX = 1;
		
		combo = 0;
		maxChain = 0;
		maxCombo = 0;
	}
	
	public double getProgress(){
		switch(gameMode){
		case Normal:
			return getLevelProgress();
		case Timed:
		case Quick:
			return getTimeProgress();
		case Infinite:
			return 1;
		}
		return 1;
	}
	
	public double getLevelProgress(){
		return Math.min(1, getCurrentEliminations()/(double)getGoal());
	}
	
	public double getTimeProgress(){
		double progress = (getLevelTime() - timeElapsed)/(double) getLevelTime();
		if(progress < 0){
			return 0;
		}
		if(progress >1){
			return 1;
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
		
		// eliminations stat
		currentEliminations ++;
		// time 
		if(gameMode == GameMode.Timed){
			int timeToAdd = 500 + scoreToAdd* 5;
			timeElapsed -= timeToAdd;
			if(timeElapsed< 0){
				timeElapsed = 0;
			}
		}
		return scoreToAdd;
	}
	
	public boolean checkLevelUp(){
		if(currentEliminations > getGoal() && level < MAX_LEVEL){
			level ++ ;
			currentEliminations -= getGoal();
			return true;
		}else{
			return false;
		}
	}
	
	public void resetCombo(){
		this.combo = 0;
	}
	
	public void stepTime(int timeElapsed){
		this.timeElapsed += timeElapsed;
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
	
	public int getTotalEliminations() {
		return totalEliminations;
	}


	public int getCurrentEliminations() {
		return currentEliminations;
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
		return level * 40;
	}
	
	public int getLevelTime(){
		return 90 * 1000;
	}

	public int getGoalRemained() {
		return Math.max(getGoal() - getCurrentEliminations(), 0);
	}

	public float getMaxLevel() {
		return MAX_LEVEL;
	}
	
	public void setGameMode(GameMode mode){
		this.gameMode = mode;
	}
}
