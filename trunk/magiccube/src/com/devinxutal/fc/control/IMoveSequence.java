package com.devinxutal.fc.control;

public interface IMoveSequence {
	public int currentMoveIndex();

	public Move currentMove();

	public Move nextMove();

	public int nextMoveIndex();

	public Move step();

	public void reset();

}
