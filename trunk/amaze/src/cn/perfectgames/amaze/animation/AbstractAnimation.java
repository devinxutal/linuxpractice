package cn.perfectgames.amaze.animation;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;

public abstract class AbstractAnimation implements Animation {
	private int totalFrames;
	private int currentFrame;

	private int loop = 0;
	private boolean visible = true;

	private boolean inAnimation = false;
	private List<AnimationListener> listeners;
	
	private boolean alwaysShow = false;

	private int loopCounter = 0;

	public boolean step() {
		if (!inAnimation) {
			return false;
		}
		if (currentFrame < totalFrames) {
			currentFrame++;
			onStep(currentFrame, totalFrames);
			if (currentFrame == totalFrames) {
				loopCounter++;
				if (loop == Animation.INFINITE || loop > loopCounter) {
					currentFrame = 0;
				}
			}
			if (currentFrame != totalFrames) {
				return true;
			}
		}
		if (inAnimation) {
			inAnimation = false;
			notifyAnimationFinished();
		}
		return false;
	}

	public AbstractAnimation(int totalFrames) {
		this(totalFrames, false);
	}
	public AbstractAnimation(int totalFrames, boolean alwaysShow){
		this.totalFrames = totalFrames;
		this.alwaysShow = alwaysShow;
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
		loopCounter = 0;
	}

	public void draw(Canvas canvas) {
		
		if (visible && inAnimation && currentFrame >= 0) {
			innerDraw(canvas, currentFrame, totalFrames);
		}else if (alwaysShow){
			innerDraw(canvas, -1, totalFrames);
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
		if (listeners != null) {
			for (AnimationListener l : listeners) {
				l.animationFinished(this);
			}
		}
	}

	protected void notifyAnimationEventHappened(int eventID) {
		if (listeners != null) {
			for (AnimationListener l : listeners) {
				l.animationEventHappened(this, eventID);
			}
		}
	}

	protected void changeTotalFrames(int frames) {
		if (!inAnimation) {
			this.totalFrames = frames;
			this.currentFrame = -1;
		}
	}

	protected abstract void innerDraw(Canvas canvas, int current, int total);

	protected void onStep(int current, int total) {

	}
}
