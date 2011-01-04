package com.devinxutal.fmc.algorithm.model;

import java.util.LinkedList;
import java.util.List;

import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.model.basic.CubeTurner;
import com.devinxutal.fmc.util.AlgorithmUtils;

public class BasicCubeModel implements CubeModel {
	private Integer[][][] original;
	private Integer[][][] current;
	private Integer[][][] temp;
	private int order;
	CubeTurner<Integer> turner;
	List<Integer> layers;

	public BasicCubeModel(MagicCube cube) {
		order = cube.getOrder();
		layers = new LinkedList<Integer>();
		for (int i = 1; i <= order; i++) {
			layers.add(1);
		}
		this.turner = new CubeTurner<Integer>(order);
		this.original = AlgorithmUtils.getCubeAsIntArray(cube);
		this.current = new Integer[order + 2][order + 2][order + 2];
		this.temp = new Integer[order + 2][order + 2][order + 2];
		turner.copy(original, current);
		turner.copy(original, temp);
	}

	public Integer[][][] get() {
		return current;
	}

	public Integer[][][] getOriginal() {
		return original;
	}

	public void applyRotate(int dim, int direction, boolean half) {
		if (direction == 0) {
			turner.copy(original, current);
			return;
		}
		turner.copy(original, current);
		turner.turn(original, current, dim, layers, direction);
		if (half) {
			turner.copy(current, temp);
			turner.turn(temp, current, dim, layers, direction);
		}
	}
}
