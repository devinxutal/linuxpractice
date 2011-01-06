package com.devinxutal.fmc.solver;

import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.model.MagicCube.CubeColor;

public abstract class AbstractSolver implements ISolver {
	private String msg;

	public boolean solved(MagicCube mcube) {
		CubeColor[][][] cube = mcube.getCube();
		int order = mcube.getOrder();
		CubeColor u = cube[order / 2 + 1][order + 1][order / 2 + 1];
		CubeColor d = cube[order / 2 + 1][0][order / 2 + 1];
		CubeColor r = cube[order + 1][order / 2 + 1][order / 2 + 1];
		CubeColor l = cube[0][order / 2 + 1][order / 2 + 1];
		CubeColor f = cube[order / 2 + 1][order / 2 + 1][order + 1];
		CubeColor b = cube[order / 2 + 1][order / 2 + 1][0];
		for (int i = 1; i <= order; i++) {
			for (int j = 1; j <= order; j++) {
				if (cube[i][order + 1][j] != u || cube[i][0][j] != d
						|| cube[order + 1][i][j] != r || cube[0][i][j] != l
						|| cube[i][j][order + 1] != f || cube[i][j][0] != b) {
					return false;
				}
			}
		}
		return true;
	}

	public String getMessage() {
		return msg;
	}

	protected void setMessage(String msg) {
		this.msg = msg;
	}
}
