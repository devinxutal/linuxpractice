package cn.perfectgames.jewels.model;

import java.util.Random;

public class Jewel {
	public static final int TYPE_COUNT = 7;
	private int type = 0;

	public Jewel() {
		this(new Random().nextInt(TYPE_COUNT));
	}

	public Jewel(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void copy(Jewel copy) {
		this.type = copy.getType();
	}
}
