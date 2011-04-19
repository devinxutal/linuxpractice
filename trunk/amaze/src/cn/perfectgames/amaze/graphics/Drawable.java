package cn.perfectgames.amaze.graphics;

import android.graphics.Canvas;

public interface Drawable {
	void draw(Canvas canvas);

	boolean isVisible();

	void setVisible(boolean visible);
}
