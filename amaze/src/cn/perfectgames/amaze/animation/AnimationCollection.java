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
}
