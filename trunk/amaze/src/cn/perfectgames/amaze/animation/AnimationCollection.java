package cn.perfectgames.amaze.animation;

import java.util.LinkedList;
import java.util.List;

public class AnimationCollection {
	private List<Animation> animations = new LinkedList<Animation>();

	public boolean addAnimation(Animation animation) {
		return this.animations.add(animation);
	}

	public boolean removeAnimation(Animation animation) {
		return this.animations.remove(animation);
	}

	public void step() {
		for (Animation a : animations) {
			a.step();
		}
	}
	

	public boolean addAnimationListener(AnimationListener l){
		for(Animation a: animations){
			a.addAnimationListener(l);
		}
		return true;
	}

	public boolean removeAnimationListener(AnimationListener l){
		for(Animation a: animations){
			a.removeAnimationListener(l);
		}
		return true;
	}

	public void clearAnimationListener(){
		for(Animation a: animations){
			a.clearAnimationListener();
		}
	}
	
	
}
