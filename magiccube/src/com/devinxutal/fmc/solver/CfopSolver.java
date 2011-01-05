package com.devinxutal.fmc.solver;

import java.util.List;

import com.devinxutal.fmc.algorithm.model.BasicCubeModel;
import com.devinxutal.fmc.algorithm.pattern.Pattern;
import com.devinxutal.fmc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fmc.control.MoveSequence;
import com.devinxutal.fmc.model.MagicCube;

public class CfopSolver extends AbstractSolver {
	private List<PatternAlgorithm> C;
	private List<PatternAlgorithm> F;
	private List<PatternAlgorithm> O;
	private List<PatternAlgorithm> P;

	private Pattern cPattern;
	private Pattern fPattern;
	private Pattern oPattern;
	private Pattern pPattern;

	public String msg;

	public CfopSolver() {
		init();
	}

	public MoveSequence nextMoves(MagicCube cube) {
		BasicCubeModel model = new BasicCubeModel(cube);
		if (!cPattern.match(model)) { // do C
			return doC(model);
		} else if (!fPattern.match(model)) { // do F
			return doF(model);
		} else if (!oPattern.match(model)) { // do O
			return doO(model);
		} else { // do P
			return doP(model);
		}
	}

	private MoveSequence doC(BasicCubeModel model) {
		return null;
	}

	private MoveSequence doF(BasicCubeModel model) {
		return null;
	}

	private MoveSequence doO(BasicCubeModel model) {
		return null;
	}

	private MoveSequence doP(BasicCubeModel model) {
		return null;
	}

	private void init() {

	}

}
