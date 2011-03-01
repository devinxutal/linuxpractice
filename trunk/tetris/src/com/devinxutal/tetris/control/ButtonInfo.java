package com.devinxutal.tetris.control;

import android.graphics.Bitmap;

public class ButtonInfo {
	public int x;
	public int y;
	public int radius;
	public int buttonID;
	public Bitmap buttonBG;
	public Bitmap buttonIcon;
	public boolean pressed = false;

	public ButtonInfo(int x, int y, int radius, int buttonID, Bitmap buttonBG,
			Bitmap buttonIcon) {
		super();
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.buttonID = buttonID;
		this.buttonBG = buttonBG;
		this.buttonIcon = buttonIcon;

	}

}