package com.appspot.perfectgames.model;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.appspot.perfectgames.util.StringUtility;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class CubeRecord {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private long time;
	@Persistent
	private int steps;
	@Persistent
	private String player;
	@Persistent
	private int shuffleSteps;
	@Persistent
	private int order;
	@Persistent
	private Date commitTime;
	@Persistent
	private String description;

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	public String getDescription() {
		return StringUtility.trim(description);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getPlayer() {
		return StringUtility.trim(player);
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

	public String toTabbedString() {
		String TAB = "\t";
		if (player == null) {
			player = "unknown";
		}
		if (description == null) {
			description = "";
		}

		return getPlayer() + TAB + time + TAB + +steps + TAB + order + TAB
				+ shuffleSteps + TAB + getDescription() + TAB + commitTime;
	}
}
