package cn.perfectgames.amaze.animation;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.util.Log;

public abstract class AbstractAnimation implements Animation {
	private int totalFrames;
	private int currentFrame;

	private int loop = 0;
	private boolean visible = true;

	private boolean inAnimation = false;
	private List<AnimationListener> listeners;

	public boolean step() {
		if (!inAnimation) {
			return false;
		}
		if (currentFrame < totalFrames) {
			currentFrame++;
			return true;
		}
		if (inAnimation) {
			inAnimation = false;
			notifyAnimationFinished();
		}
		return false;
	}

	public AbstractAnimation(int totalFrames) {
		this.totalFrames = totalFrames;
		this.currentFrame = -1;
	}

	public int getFrameCount() {
		return totalFrames;
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public void reset() {
		currentFrame = -1;
		inAnimation = false;
	}

	public void draw(Canvas canvas) {
		Log.v("AbstractAnimation", this.getClass().getName()
				+ " is about to draw: visible-" + visible + ", inAnimation-"
				+ inAnimation);
		if (visible && inAnimation) {
			innerDraw(canvas);
		}
	}

	public void start() {
		reset();
		inAnimation = true;
	}

	public void stop() {
		inAnimation = false;
	}

	public boolean isFinished() {
		return currentFrame == totalFrames;
	}

	public boolean isInfinite() {
		return loop == Animation.INFINITE;
	}

	public boolean isInAnimation() {
		return inAnimation;
	}

	public int getLoop() {
		return loop;
	}

	public void setLoop(int loop) {
		if (loop != Animation.INFINITE && loop < 0) {
			loop = 0;
		}
		this.loop = loop;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean addAnimationListener(AnimationListener l) {
		if (listeners == null) {
			listeners = new LinkedList<AnimationListener>();
		}
		return listeners.add(l);
	}

	public boolean removeAnimationListener(AnimationListener l) {
		if (listeners == null) {
			listeners = new LinkedList<AnimationListener>();
		}
		return listeners.remove(l);
	}

	public void clearAnimationListener() {
		if (listeners != null) {
			listeners.clear();
		}
	}

	protected void notifyAnimationFinished() {

	}

	protected abstract void innerDraw(Canvas canvas);
}
