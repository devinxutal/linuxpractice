package com.devinxutal.fc.algorithm.model;

import java.util.LinkedList;
import java.util.List;

import com.devinxutal.fc.control.Move;
import com.devinxutal.fc.model.MagicCube;
import com.devinxutal.fc.model.basic.CubeTurner;
import com.devinxutal.fc.util.AlgorithmUtils;

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
			layers.add(i);
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

	public void applyTurn(Move move) {
		turner.copy(current, temp);
		turner.turn(temp, current, move.dimension, move.layers, move.direction);
		if (move.doubleTurn) {
			turner.copy(current, temp);
			turner.turn(temp, current, move.dimension, move.layers,
					move.direction);
		}
	}

	public void reset() {
		turner.copy(original, current);
		turner.copy(original, temp);
	}
}
