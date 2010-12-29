package com.devinxutal.fmc.control;

import java.util.LinkedList;
import java.util.List;

public class Move {
	public int dimension;
	public int direction;
	public List<Integer> layers = new LinkedList<Integer>();

	public Move cloneMove() {
		Move mv = new Move();
		mv.dimension = this.dimension;
		mv.direction = this.direction;
		mv.layers.addAll(this.layers);
		return mv;
	}
}
