package cn.perfectgames.amaze.animation;

public interface AnimationListener {
	public void animationFinished(Animation animation);
	public void animationEventHappened(Animation animation, int eventID);
}
