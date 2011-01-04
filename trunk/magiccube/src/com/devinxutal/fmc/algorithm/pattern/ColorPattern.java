package com.devinxutal.fmc.algorithm.pattern;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.devinxutal.fmc.algorithm.model.BasicCubeModel;
import com.devinxutal.fmc.algorithm.model.CubeModel;

public class ColorPattern implements Pattern {
	private List<Constraint> constraints;

	public ColorPattern() {
		constraints = new LinkedList<Constraint>();
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public void addConstraint(int x, int y, int z, int colorType) {
		constraints.add(new Constraint(x, y, z, colorType));
	}

	public boolean match(CubeModel model) {
		if (!(model instanceof BasicCubeModel)) {
			return false;
		}
		BasicCubeModel basicModel = (BasicCubeModel) model;
		Integer[][][] cube = basicModel.get();
		Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
		for (Constraint c : constraints) {
			try {
				int color = cube[c.x][c.y][c.z];
				int colorToBe = -1;
				if (colorMap.containsKey(c.color)) {
					colorToBe = colorMap.get(c.color);
				} else {
					colorToBe = color;
					colorMap.put(c.color, colorToBe);
				}
				if (colorToBe != color) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public class Constraint {

		private int x;
		private int y;
		private int z;
		private int color;

		public Constraint(int x, int y, int z, int color) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
			this.color = color;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}

		public int getColor() {
			return color;
		}

	}

	public void addConstraint(Constraint c) {
		if (c != null) {
			this.constraints.add(c);
		}
	}

}
