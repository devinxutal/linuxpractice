package com.devinxutal.fc.model;

import javax.microedition.khronos.opengles.GL10;

public interface Cubie {

	public void setCubeColors();

	public void draw(GL10 gl);

	public void drawPickingArea(GL10 gl);

	public void finishAnimation();

	public void prepareAnimation();
}
