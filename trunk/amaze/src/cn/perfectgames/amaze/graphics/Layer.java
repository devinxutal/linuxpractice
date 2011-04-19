package cn.perfectgames.amaze.graphics;

import java.util.LinkedList;

import android.graphics.Canvas;
import android.util.Log;

public class Layer implements Drawable {
	private boolean visible = true;
	private LinkedList<Drawable> drawables;

	public void draw(Canvas canvas) {

		if (drawables != null && visible) {
			for (Drawable d : drawables) {
				if (d.isVisible()) {
					d.draw(canvas);
				}
			}
		}
	}

	public void addDrawable(Drawable drawable) {
		if (this.drawables == null) {
			this.drawables = new LinkedList<Drawable>();
		}
		if (!this.drawables.contains(drawable)) {
			drawables.add(drawable);
		}
	}

	public void removeDrawable(Drawable drawable) {
		if (this.drawables == null) {
			return;
		}
		this.drawables.remove(drawable);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
