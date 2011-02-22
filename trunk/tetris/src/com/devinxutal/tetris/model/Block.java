package com.devinxutal.tetris.model;

import java.util.Random;

import com.devinxutal.tetris.util.RandomUtil;

public class Block {
	private boolean matrix[][][] = null;
	private int current = 0;
	private int numStatus = 0;
	private BlockType blockType;

	public Block() {
		this(BlockType.random());
	}

	public Block(BlockType type) {
		this.blockType = type;
		this.matrix = BlockType.getMatrix(type);
		Random r = RandomUtil.r;
		this.numStatus = this.matrix.length;
		this.current = r.nextInt(this.numStatus);
	}

	public void turn() {
		current = (current + 1) % this.numStatus;
	}

	public boolean[][] getMatrix() {
		return matrix[this.current];
	}

	public boolean[][] getTurnedMatrix() {
		return matrix[(this.current + 1) % this.matrix.length];
	}

	public BlockType getBlockType() {
		return blockType;
	}

	public enum BlockType {
		I, J, L, O, S, T, Z;

		public static boolean[][][] getMatrix(BlockType type) {
			return matrix_array[type.ordinal()];
		};

		public static BlockType random() {
			Random r = RandomUtil.r;
			BlockType type = BlockType.I;
			switch (r.nextInt(7)) {
			case 0:
				type = BlockType.I;
				break;
			case 1:
				type = BlockType.J;
				break;
			case 2:
				type = BlockType.L;
				break;
			case 3:
				type = BlockType.O;
				break;
			case 4:
				type = BlockType.S;
				break;
			case 5:
				type = BlockType.T;
				break;
			case 6:
				type = BlockType.Z;
				break;
			}
			;
			return type;
		}

		private static boolean matrix_array[][][][] = new boolean[][][][] {
				{ {//
						{ false, false, false, false },
								{ false, false, false, false },
								{ true, true, true, true },
								{ false, false, false, false } //
						},//
						{//
						{ false, true, false, false },
								{ false, true, false, false },
								{ false, true, false, false },
								{ false, true, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, false, false, false },
								{ true, true, true, true },
								{ false, false, false, false } //
						},//
						{//
						{ false, true, false, false },
								{ false, true, false, false },
								{ false, true, false, false },
								{ false, true, false, false } //
						} //
				},// I
				{//
				{//
						{ false, false, false, false },
								{ true, false, false, false },
								{ true, true, true, false },
								{ false, false, false, false } //
						},//
						{//
						{ true, true, false, false },
								{ true, false, false, false },
								{ true, false, false, false },
								{ false, false, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ true, true, true, false },
								{ false, false, true, false },
								{ false, false, false, false } //
						},//
						{//
						{ false, true, false, false },
								{ false, true, false, false },
								{ true, true, false, false },
								{ false, false, false, false } //
						},//
				},// J
				{//
				{//
						{ false, false, false, false },
								{ false, false, true, false },
								{ true, true, true, false },
								{ false, false, false, false } //
						},//
						{//
						{ true, false, false, false },
								{ true, false, false, false },
								{ true, true, false, false },
								{ false, false, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ true, true, true, false },
								{ true, false, false, false },
								{ false, false, false, false } //
						},//
						{//
						{ true, true, false, false },
								{ false, true, false, false },
								{ false, true, false, false },
								{ false, false, false, false } //
						} //
				},// L
				{//
				{//
						{ false, false, false, false },
								{ false, true, true, false },
								{ false, true, true, false },
								{ false, false, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, true, true, false },
								{ false, true, true, false },
								{ false, false, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, true, true, false },
								{ false, true, true, false },
								{ false, false, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, true, true, false },
								{ false, true, true, false },
								{ false, false, false, false } //
						} //
				},// O
				{//
				{//
						{ false, false, false, false },
								{ false, true, true, false },
								{ true, true, false, false },
								{ false, false, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, true, false, false },
								{ false, true, true, false },
								{ false, false, true, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, true, true, false },
								{ true, true, false, false },
								{ false, false, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, true, false, false },
								{ false, true, true, false },
								{ false, false, true, false } //
						} //
				},// S
				{//
				{//
						{ false, false, false, false },
								{ false, true, false, false },
								{ true, true, true, false },
								{ false, false, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, true, false, false },
								{ false, true, true, false },
								{ false, true, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ true, true, true, false },
								{ false, true, false, false },
								{ false, false, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, true, false, false },
								{ true, true, false, false },
								{ false, true, false, false } //
						},//
				},// T
				{//
				{//
						{ false, false, false, false },
								{ false, true, true, false },
								{ false, false, true, true },
								{ false, false, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, false, true, false },
								{ false, true, true, false },
								{ false, true, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, true, true, false },
								{ false, false, true, true },
								{ false, false, false, false } //
						},//
						{//
						{ false, false, false, false },
								{ false, false, true, false },
								{ false, true, true, false },
								{ false, true, false, false } //
						} //
				},// Z
		};//
	}

}
