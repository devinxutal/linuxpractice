package com.devinxutal.fc.solver;

import com.devinxutal.fc.control.MoveSequence;
import com.devinxutal.fc.model.MagicCube;

public interface ISolver {
	public MoveSequence nextMoves(MagicCube cube);

	public boolean solved(MagicCube cube);

	public String getMessage();
}
