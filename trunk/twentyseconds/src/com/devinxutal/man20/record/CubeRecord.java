package com.devinxutal.man20.record;

import java.util.Date;

public class CubeRecord {

	private String player;
	private long time;
	private int steps;
	private int order;
	private int shuffleSteps;
	private Date commitTime;
	private int rank;
	private String description;

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	public long getTime() {
		return time;
	}

	public String getTimeString() {
		return time / 1000 + "." + (time % 1000) / 10;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public int getShuffleSteps() {
		return shuffleSteps;
	}

	public void setShuffleSteps(int shuffleSteps) {
		this.shuffleSteps = shuffleSteps;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Date getCommitTime() {
		return commitTime;
	}

	public void setCommitTime(Date commitTime) {
		this.commitTime = commitTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toTabbedString() {
		String TAB = "\t";
		return rank + TAB + player + TAB + time + TAB + steps + TAB + order
				+ TAB + shuffleSteps + TAB + commitTime;
	}

	public static CubeRecord parse(String str) {
		CubeRecord record = new CubeRecord();
		String[] attrs = str.split("\t");
		try {
			record.setRank(Integer.valueOf(attrs[0]));
			record.setPlayer(attrs[1]);

			record.setTime(Integer.valueOf(attrs[2]));
			record.setSteps(Integer.valueOf(attrs[3]));
			record.setOrder(Integer.valueOf(attrs[4]));
			record.setShuffleSteps(Integer.valueOf(attrs[5]));
			record.setDescription(attrs[6]);
			record.setCommitTime(new Date(Date.parse(attrs[7])));
		} catch (Exception e) {
		}
		return record;
	}
}
