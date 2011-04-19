package cn.perfectgames.jewels.model;

import java.util.Random;

public class Block {
	public static final int TYPE_COUNT = 7;
	private int type = 0;

	public Block() {
		this(new Random().nextInt(TYPE_COUNT));
	}

	public Block(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void copy(Block copy) {
		this.type = copy.getType();
	}
}
