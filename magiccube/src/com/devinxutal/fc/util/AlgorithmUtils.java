package com.devinxutal.fc.util;

import com.devinxutal.fc.algorithm.pattern.ColorPattern;
import com.devinxutal.fc.algorithm.pattern.Pattern;
import com.devinxutal.fc.algorithm.pattern.ColorPattern.Constraint;
import com.devinxutal.fc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fc.control.MoveSequence;
import com.devinxutal.fc.model.CubeColor;
import com.devinxutal.fc.model.CubeState;
import com.devinxutal.fc.model.MagicCube;

public class AlgorithmUtils {
	public static Integer[][][] getCubeAsIntArray(MagicCube cube) {
		Integer[][][] model = new Integer[cube.getOrder() + 2][cube.getOrder() + 2][cube
				.getOrder() + 2];
		CubeColor[][][] m = cube.getCube();
		for (int i = 0; i < cube.getOrder() + 2; i++) {
			for (int j = 0; j < cube.getOrder() + 2; j++) {
				for (int k = 0; k < cube.getOrder() + 2; k++) {
					model[i][j][k] = m[i][j][k].ordinal();
				}
			}
		}
		return model;
	}

	public static PatternAlgorithm parsePatternAlgorithm(String[] desc,
			int cubeOrder) {
		ColorPattern pattern = new ColorPattern();
		if (desc.length < 3) {
			return null;
		}
		String name = desc[0];
		for (int i = 1; i < desc.length - 1; i++) {
			try {
				String temp = desc[i].replace(" ", "");
				temp = temp.replace("(", "");
				temp = temp.replace(")", "");
				Constraint c = pattern.new Constraint(//
						Integer.valueOf("" + temp.charAt(0)), //
						Integer.valueOf("" + temp.charAt(1)), //
						Integer.valueOf("" + temp.charAt(2)), //
						Integer.valueOf("" + temp.charAt(3)));//
				pattern.addConstraint(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MoveSequence sequence = new MoveSequence(desc[desc.length - 1],
				cubeOrder);
		return new PatternAlgorithm(name, pattern, sequence,
				desc[desc.length - 1]);
	}

	public static PatternAlgorithm parsePatternAlgorithm(String line,
			int cubeOrder) {
		return parsePatternAlgorithm(line.split(","), cubeOrder);
	}

	public static Pattern parsePattern(String[] desc, int cubeOrder) {
		ColorPattern pattern = new ColorPattern();
		if (desc.length < 1) {
			return null;
		}
		for (int i = 0; i < desc.length; i++) {
			try {
				String temp = desc[i].replace(" ", "");
				temp = temp.replace("(", "");
				temp = temp.replace(")", "");
				Constraint c = pattern.new Constraint(//
						Integer.valueOf("" + temp.charAt(0)), //
						Integer.valueOf("" + temp.charAt(1)), //
						Integer.valueOf("" + temp.charAt(2)), //
						Integer.valueOf("" + temp.charAt(3)));//
				pattern.addConstraint(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return pattern;
	}

	public static Pattern parsePattern(String line, int cubeOrder) {
		return parsePattern(line.split(","), cubeOrder);
	}

	public static ColorPattern parseColorPattern(String[] constriants) {
		ColorPattern pattern = new ColorPattern();
		for (int i = 0; i < constriants.length; i++) {
			try {
				String temp = constriants[i].replace(" ", "");
				temp = temp.replace("(", "");
				temp = temp.replace(")", "");
				Constraint c = pattern.new Constraint(//
						Integer.valueOf("" + temp.charAt(0)), //
						Integer.valueOf("" + temp.charAt(1)), //
						Integer.valueOf("" + temp.charAt(2)), //
						Integer.valueOf("" + temp.charAt(3)));//
				pattern.addConstraint(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return pattern;
	}

	public static ColorPattern parseColorPattern(String constraints) {
		return parseColorPattern(constraints.split(","));
	}

	static CubeColor[] colors = new CubeColor[] { CubeColor.ANY,
			CubeColor.WHITE, CubeColor.RED, CubeColor.BLUE, CubeColor.ORANGE,
			CubeColor.GREEN, CubeColor.YELLOW, CubeColor.BLACK };

	public static CubeState PatternToCubeState(ColorPattern pattern, int order) {
		CubeState state = new CubeState();
		state.order = order;
		CubeColor[][][] cube = new CubeColor[order + 2][order + 2][order + 2];
		for (ColorPattern.Constraint c : pattern.getConstraints()) {
			cube[c.getX()][c.getY()][c.getZ()] = colors[c.getColor()];
		}
		for (int i = 1; i <= order; i++) {
			for (int j = 1; j <= order; j++) {
				state.add(0, i, j, cube[0][i][j]);
				state.add(order + 1, i, j, cube[order + 1][i][j]);
				state.add(i, 0, j, cube[i][0][j]);
				state.add(i, order + 1, j, cube[i][order + 1][j]);
				state.add(i, j, 0, cube[i][j][0]);
				state.add(i, j, order + 1, cube[i][j][order + 1]);
			}
		}
		return state;
	}
}
