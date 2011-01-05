package com.devinxutal.fmc.util;

import android.util.Log;

import com.devinxutal.fmc.algorithm.pattern.ColorPattern;
import com.devinxutal.fmc.algorithm.pattern.ColorPattern.Constraint;
import com.devinxutal.fmc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fmc.control.MoveSequence;
import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.model.MagicCube.CubeColor;

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

				Log.v("unitest", "array length: " + temp.length());
				Log.v("unitest", temp);
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
}
