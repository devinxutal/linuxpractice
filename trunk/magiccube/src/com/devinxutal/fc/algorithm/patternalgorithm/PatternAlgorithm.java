package com.devinxutal.fc.algorithm.patternalgorithm;

import com.devinxutal.fc.algorithm.pattern.Pattern;
import com.devinxutal.fc.control.MoveSequence;

public class PatternAlgorithm {
	private Pattern pattern;
	private MoveSequence moves;
	private String formula;
	private String name;

	public PatternAlgorithm(Pattern pattern, MoveSequence moves) {
		this("No Name", pattern, moves, "");
	}

	public PatternAlgorithm(String name, Pattern pattern, MoveSequence moves,
			String formula) {
		super();
		this.name = name;
		this.pattern = pattern;
		this.moves = moves;
		this.formula = formula;

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

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
