package com.devinxutal.fmc.solver;

import com.devinxutal.fmc.control.MoveSequence;
import com.devinxutal.fmc.model.MagicCube;

public interface ISolver {
	public MoveSequence nextMoves(MagicCube cube);

	public boolean solved(MagicCube cube);

	public String getMessage();
}
