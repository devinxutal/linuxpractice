package com.devinxutal.fmc.algorithm.patternalgorithm;

import com.devinxutal.fmc.algorithm.pattern.Pattern;
import com.devinxutal.fmc.control.MoveSequence;

public class PatternAlgorithm {
	private Pattern pattern;
	private MoveSequence moves;

	public PatternAlgorithm(Pattern pattern, MoveSequence moves) {
		super();
		this.pattern = pattern;
		this.moves = moves;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public MoveSequence getMoves() {
		return moves;
	}

	public void setMoves(MoveSequence moves) {
		this.moves = moves;
	}
	

}
