package com.devinxutal.fc.model.basic;

import java.util.List;

import android.util.Log;

import com.devinxutal.fc.model.MagicCube;

public class CubeTurner<T> {
	private int order;

	public CubeTurner(int order) {
		this.order = order;
	}

	public boolean turn(T[][][] from, T[][][] to, int dimension,
			List<Integer> layers, int direction) {
		boolean succeed = true;
		for (int layer : layers) {
			if (layer == 1 || layer == order) {
				int l = layer == 1 ? 0 : order + 1;
				succeed = turnFace(from, to, dimension, l, direction)
						&& succeed;
			}
			Log.v("motiontest", "turn side " + layer);
			succeed = turnSide(from, to, dimension, layer, direction)
					&& succeed;
		}
		return succeed;
	}

	public void copy(T[][][] from, T[][][] to) {
		for (int i = 1; i <= order; i++) {
			for (int j = 1; j <= order; j++) {
				to[i][j][0] = from[i][j][0]; // back
				to[i][j][order + 1] = from[i][j][order + 1]; // front
				to[i][0][j] = from[i][0][j]; // down
				to[i][order + 1][j] = from[i][order + 1][j]; // up
				to[0][i][j] = from[0][i][j]; // left
				to[order + 1][i][j] = from[order + 1][i][j]; // right
			}
		}
	}

	private boolean turnSide(T[][][] from, T[][][] to, int dimension,
			int layer, int direction) {
		Pair pair = new Pair();
		if (dimension == MagicCube.DIM_X) {
			for (int i = 1; i <= order; i++) {
				pair.x = 0;
				pair.y = i;
				applyRotate(pair, direction);
				to[layer][pair.x][pair.y] = from[layer][0][i];

				pair.x = order + 1;
				pair.y = i;
				applyRotate(pair, direction);
				to[layer][pair.x][pair.y] = from[layer][order + 1][i];

				pair.x = i;
				pair.y = 0;
				applyRotate(pair, direction);
				to[layer][pair.x][pair.y] = from[layer][i][0];

				pair.x = i;
				pair.y = order + 1;
				applyRotate(pair, direction);
				to[layer][pair.x][pair.y] = from[layer][i][order + 1];

			}
			return true;
		} else if (dimension == MagicCube.DIM_Y) {
			for (int i = 1; i <= order; i++) {
				pair.x = 0;
				pair.y = i;
				applyRotate(pair, direction);
				to[pair.y][layer][pair.x] = from[i][layer][0];
				pair.x = order + 1;
				pair.y = i;
				applyRotate(pair, direction);
				to[pair.y][layer][pair.x] = from[i][layer][order + 1];

				pair.x = i;
				pair.y = 0;
				applyRotate(pair, direction);
				to[pair.y][layer][pair.x] = from[0][layer][i];

				pair.x = i;
				pair.y = order + 1;
				applyRotate(pair, direction);
				to[pair.y][layer][pair.x] = from[order + 1][layer][i];

			}
			return true;
		} else if (dimension == MagicCube.DIM_Z) {
			for (int i = 1; i <= order; i++) {
				pair.x = 0;
				pair.y = i;
				applyRotate(pair, direction);
				to[pair.x][pair.y][layer] = from[0][i][layer];

				pair.x = order + 1;
				pair.y = i;
				applyRotate(pair, direction);
				to[pair.x][pair.y][layer] = from[order + 1][i][layer];

				pair.x = i;
				pair.y = 0;
				applyRotate(pair, direction);
				to[pair.x][pair.y][layer] = from[i][0][layer];

				pair.x = i;
				pair.y = order + 1;
				applyRotate(pair, direction);
				to[pair.x][pair.y][layer] = from[i][order + 1][layer];

			}
			return true;
		}
		return false;
	}

	private boolean turnFace(T[][][] from, T[][][] to, int dimension,
			int layer, int direction) {
		Pair pair = new Pair();
		if (dimension == MagicCube.DIM_X) {
			for (int i = 1; i <= order; i++) {
				for (int j = 1; j <= order; j++) {
					pair.x = i;
					pair.y = j;
					applyRotate(pair, direction);
					to[layer][pair.x][pair.y] = from[layer][i][j];
				}
			}
			return true;
		} else if (dimension == MagicCube.DIM_Y) {
			for (int i = 1; i <= order; i++) {
				for (int j = 1; j <= order; j++) {
					pair.x = i;
					pair.y = j;
					applyRotate(pair, direction);
					to[pair.y][layer][pair.x] = from[j][layer][i];
				}
			}
			return true;
		} else if (dimension == MagicCube.DIM_Z) {
			for (int i = 1; i <= order; i++) {
				for (int j = 1; j <= order; j++) {
					pair.x = i;
					pair.y = j;
					applyRotate(pair, direction);
					to[pair.x][pair.y][layer] = from[i][j][layer];
				}
			}
			return true;
		}
		return false;
	}

	private void applyRotate(Pair pair, int direction) {
		int tmp;
		if (direction >= 0) { // clock wise
			tmp = pair.x;
			pair.x = pair.y;
			pair.y = order + 1 - tmp;
		} else {
			tmp = pair.x;
			pair.x = order + 1 - pair.y;
			pair.y = tmp;
		}
	}

}
