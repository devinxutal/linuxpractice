package com.devinxutal.fc.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class CubeState implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2814107558541847340L;
	
	public List<StateEntry> entries;
	public int order;

	public CubeState() {
		entries = new LinkedList<StateEntry>();
	}

	public void add(int x, int y, int z, CubeColor color) {
		if (color == null) {
			color = CubeColor.ANY;
		}
		entries.add(new StateEntry(x, y, z, color));
	}

	public class StateEntry implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4437137366041521417L;
		public int x;
		public int y;
		public int z;
		public CubeColor color;

		public StateEntry(int x, int y, int z, CubeColor color) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
			this.color = color;
		}

	}

}
