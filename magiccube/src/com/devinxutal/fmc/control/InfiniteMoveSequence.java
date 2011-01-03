package com.devinxutal.fmc.control;

import com.devinxutal.fmc.util.SymbolMoveUtil;

public class InfiniteMoveSequence implements IMoveSequence {
	private Move current;
	private Move next;
	private int index;
	private int cubeOrder;

	public InfiniteMoveSequence(int cubeOrder) {
		this.cubeOrder = cubeOrder;
		reset();
	}

	public int currentMoveIndex() {
		return index;
	}

	public Move currentMove() {
		return current;
	}

	public Move nextMove() {
		return next;
	}

	public int nextMoveIndex() {
		return index + 1;
	}

	public Move step() {
		index++;
		Move tmp = SymbolMoveUtil.randomMove(cubeOrder);
		current = next;
		next = tmp;
		return current;

	}

	public void reset() {
		this.index = -1;
		current = null;
		next = SymbolMoveUtil.randomMove(cubeOrder);
	}
}
