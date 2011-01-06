package com.devinxutal.fmc.model;

import java.util.LinkedList;
import java.util.List;

public class CubeAnimationInfo {

	public int dimension;
	public int direction;
	public List<Integer> layers = new LinkedList<Integer>();
	public boolean doubleTurn = false;
	public CubeColor[][][] cube;

	public void reset() {
		dimension = 0;
		direction = 0;
		layers.clear();
		cube = null;
		totalStep = 0;
		currentStep = 0;
		doubleTurn = false;
	}

	private int totalStep;
	private int currentStep;

	public void setAnimation(int totalStep) {
		currentStep = 0;
		this.totalStep = totalStep;
	}

	public int totalStep() {
		if (doubleTurn) {
			return totalStep * 2;
		} else {
			return totalStep;
		}
	}

	public int currentStep() {
		return currentStep;
	}

	public int step() {
		if (currentStep < totalStep()) {
			currentStep++;
		}
		return currentStep;
	}
}
