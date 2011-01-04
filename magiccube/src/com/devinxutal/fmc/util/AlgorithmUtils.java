package com.devinxutal.fmc.util;

import java.util.Arrays;

import android.util.Log;

import com.devinxutal.fmc.algorithm.pattern.ColorPattern;
import com.devinxutal.fmc.algorithm.pattern.ColorPattern.Constraint;
import com.devinxutal.fmc.algorithm.patternalgorithm.PatternAlgorithm;
import com.devinxutal.fmc.control.MoveSequence;
import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.model.MagicCube.CubeColor;

public class AlgorithmUtils {
	public static int[][][] getCubeAsIntArray(MagicCube cube) {
		int[][][] model = new int[cube.getOrder()][cube.getOrder()][cube
				.getOrder()];
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
		for (int i = 0; i < desc.length - 1; i++) {
			try {
				String s[] = desc[i].split(" ");
				Log.v("unitest", "array length: " + s.length);
				Log.v("unitest", Arrays.toString(s));
				Log.v("unitest", "get integer of " + s[0] + ": "
						+ Integer.valueOf(s[0]));
				Constraint c = pattern.new Constraint(//
						Integer.valueOf(s[0]), //
						Integer.valueOf(s[1]),//
						Integer.valueOf(s[2]), //
						Integer.valueOf(s[3]));//
				pattern.addConstraint(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MoveSequence sequence = new MoveSequence(desc[desc.length - 1],
				cubeOrder);
		return new PatternAlgorithm(pattern, sequence);
	}
}
