package cn.perfectgames.amaze.animation;

import cn.perfectgames.amaze.graphics.Drawable;

public interface Animation extends Drawable {
	public static final int INFINITE = -1;

	public void start();

	public void stop();

	public boolean step();

	public int getFrameCount();

	public int getCurrentFrame();

	public void reset();

	public boolean isFinished();

	public boolean isInfinite();

	public boolean isInAnimation();
	
	public int getLoop();

	public void setLoop(int loop);

	public boolean addAnimationListener(AnimationListener l);

	public boolean removeAnimationListener(AnimationListener l);

	public void clearAnimationListener();
}
