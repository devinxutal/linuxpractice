package com.devinxutal.fmc.algorithm.model;

import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.util.AlgorithmUtils;

public class BasicCubeModel implements CubeModel {
	private int[][][] original;
	private int[][][] current;
	private int order;

	public BasicCubeModel(MagicCube cube) {
		order = cube.getOrder();
		this.original = AlgorithmUtils.getCubeAsIntArray(cube);
		this.current = AlgorithmUtils.getCubeAsIntArray(cube);
	}

	public int[][][] get() {
		return current;
	}

	public int[][][] getOriginal() {
		return original;
	}

	public void applyRotate(int dim, int direction, boolean half) {
		if (direction == 0) {

		}
		
		if (dim == DIM_X) {

		} else if (dim == DIM_Y) {

		} else if (dim == DIM_Z) {

		}
	}
}
