package cn.perfectgames.amaze.graphics;

import java.util.LinkedList;

import android.graphics.Canvas;

public class Layers implements Drawable {
	public static final int TOP = 1;
	public static final int BOTTOM = 0;
	private boolean visible = true;

	private LinkedList<Layer> layers = new LinkedList<Layer>();

	public void draw(Canvas canvas) {
		for (Layer l : layers) {
			if (l.isVisible()) {
				l.draw(canvas);
			}
		}
	}

	public boolean addLayer(Layer layer, int position) {
		if (layer == null) {
			return false;
		}
		if (position == TOP) {
			layers.addLast(layer);
		} else if (position == BOTTOM) {
			layers.addFirst(layer);
		} else {
			return false;
		}
		return true;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
