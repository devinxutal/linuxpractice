package com.devinxutal.fmc.model;

import android.graphics.Color;

public enum CubeColor {
	BLACK, WHITE, YELLOW, RED, ORANGE, BLUE, GREEN, ANY;

	public int getColor() {
		switch (this) {
		case BLACK:
			return Color.rgb(50, 50, 50);
		case WHITE:
			return Color.rgb(255, 255, 255);
		case YELLOW:
			return Color.rgb(255, 255, 0);
		case RED:
			return Color.rgb(255, 0, 0);
		case ORANGE:
			return Color.rgb(255, 120, 0);
		case BLUE:
			return Color.rgb(0, 0, 255);
		case GREEN:
			return Color.rgb(0, 200, 100);
		case ANY:
			return Color.rgb(100, 100, 100);
		}
		return Color.rgb(0, 0, 0);
	}
}
