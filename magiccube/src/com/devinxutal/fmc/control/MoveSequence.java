package com.devinxutal.fmc.control;

import java.util.LinkedList;

import com.devinxutal.fmc.util.SymbolMoveUtil;

public class MoveSequence implements IMoveSequence {
	private LinkedList<Move> moves;
	private int current;
	private int cubeOrder;

	public MoveSequence() {
		moves = new LinkedList<Move>();
		current = -1;
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
		if (current >= 0 && current < moves.size()) {
			return current;
		} else {
			return -1;
		}
	}

	public Move currentMove() {
		if (current >= 0 && current < moves.size()) {
			return moves.get(current);
		} else {
			return null;
		}
	}

	public Move nextMove() {
		if (current >= -1 && current < moves.size() - 1) {
			return moves.get(current + 1);
		} else {
			return null;
		}
	}

	public int nextMoveIndex() {
		if (current < moves.size()) {
			return current + 1;
		} else {
			return -1;
		}
	}

	public Move step() {
		if (current >= -1 && current < moves.size()) {
			current++;
			if (current == moves.size()) {
				return null;
			} else {
				return moves.get(current);
			}
		} else {
			return null;
		}
	}

	public void reset() {
		this.current = -1;
	}

}
