package com.devinxutal.fmc.control;

import java.util.LinkedList;

import com.devinxutal.fmc.util.SymbolMoveUtil;

public class MoveSequence {
	private LinkedList<Move> moves;
	private int current;
	private int cubeOrder;

	public MoveSequence() {
		moves = new LinkedList<Move>();
		current = 0;
	}

	public MoveSequence(String symbols, int cubeOrder) {
		this();
		this.cubeOrder = cubeOrder;
		this.moves.addAll(SymbolMoveUtil.parseMovesFromSymbolSequence(symbols,
				cubeOrder));
	}

	public void addMove(Move mv) {
		if (mv != null) {
			moves.addLast(mv);
		}
	}

	public int totalMoves() {
		return moves.size();
	}

	public int currentMoveIndex() {
		if (current >= moves.size()) {
			return -1;
		}
		return current;
	}

	public Move currentMove() {
		if (current >= moves.size()) {
			return null;
		} else {
			return moves.get(current);
		}
	}

	public boolean step() {
		if (current < moves.size()) {
			current++;
			return true;
		} else {
			return false;
		}
	}

}
