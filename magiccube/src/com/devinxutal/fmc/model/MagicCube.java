package com.devinxutal.fmc.model;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.util.Log;

import com.devinxutal.fmc.control.Move;
import com.devinxutal.fmc.primitives.Color;
import com.devinxutal.fmc.primitives.Cube;
import com.devinxutal.fmc.primitives.ESquare;
import com.devinxutal.fmc.primitives.Group;
import com.devinxutal.fmc.primitives.Point3F;
import com.devinxutal.fmc.primitives.Square;
import com.devinxutal.fmc.ui.CubeView;
import com.devinxutal.fmc.util.SymbolMoveUtil;

public class MagicCube {
	public enum CubeColor {
		BLACK, WHITE, YELLOW, RED, ORANGE, BLUE, GREEN
	}

	public static final int DIM_X = 0x01; // ..0001
	public static final int DIM_Y = 0x02; // ..0010
	public static final int DIM_Z = 0x04; // ..0100

	private static final Color CUBE_COLORS[] = new Color[] { Color.BLACK,
			Color.WHITE, Color.YELLOW, Color.RED, Color.ORANGE, Color.BLUE,
			Color.GREEN };

	private int order;
	private Cubie cubie;
	private CubeColor[][][] currCube;
	private CubeColor[][][] prevCube;
	private CubeColor[][][] tempCube;

	private CubeAnimationInfo animationInfo;

	private boolean noAnimationMode = false;

	public CubeView view;

	public MagicCube() {
		this(3);
	}

	public MagicCube(int order) {
		this.order = order;
		initCube();
		animationInfo = new CubeAnimationInfo();
		cubie = new CubbyWithESquare();
	}

	private void initCube() {
		currCube = new CubeColor[order + 2][order + 2][order + 2];
		tempCube = new CubeColor[order + 2][order + 2][order + 2];
		prevCube = new CubeColor[order + 2][order + 2][order + 2];
		for (int i = 0; i < order + 2; i++) {
			for (int j = 0; j < order + 2; j++) {
				for (int k = 0; k < order + 2; k++) {
					currCube[i][j][k] = CubeColor.BLACK;
					tempCube[i][j][k] = CubeColor.BLACK;
					prevCube[i][j][k] = CubeColor.BLACK;
				}
			}
		}
		for (int i = 1; i <= order; i++) {
			for (int j = 1; j <= order; j++) {
				currCube[i][j][0] = CubeColor.ORANGE; // back
				currCube[i][j][order + 1] = CubeColor.RED; // front
				currCube[i][0][j] = CubeColor.WHITE; // down
				currCube[i][order + 1][j] = CubeColor.YELLOW; // up
				currCube[0][i][j] = CubeColor.BLUE; // left
				currCube[order + 1][i][j] = CubeColor.GREEN; // right
			}
		}
	}

	public int getOrder() {
		return order;
	}

	public CubeColor[][][] getCube() {
		return currCube;
	}

	public CubeAnimationInfo getAnimationInfo() {
		return this.animationInfo;
	}

	public boolean turnByGesture(int i, int j, int k, int dx, int dy) {
		if (i <= 0 || i >= order + 2 || j <= 0 || j >= order + 2 || k <= 0
				|| k >= order + 2 || (Math.abs(dx) < 5 && Math.abs(dy) < 5)) {
			return false;
		}
		int x = -1, y = -1, z = -1;
		int dimension = 0;
		int direction = 1;
		List<Integer> layers = new LinkedList<Integer>();
		if (i == order + 1) { // touched right side
			float slope1 = 1f;
			float slope2 = -1.5f;
			float dxf = dx == 0 ? 0.01f : dx;
			float dyf = dy == 0 ? 0.01f : dy;
			float slope = dyf / dxf;
			if (slope <= slope1 && slope >= slope2) {
				dimension = DIM_Y;
				layers.add(j);
				if (dx > 0) {
					direction = -1;
				} else {
					direction = 1;
				}
			} else {
				dimension = DIM_Z;
				layers.add(k);
				if (dy > 0) {
					direction = 1;
				} else {
					direction = -1;
				}
			}
		} else if (j == order + 1) { // touched up side
			if (dx >= 0 && dy >= 0) { //
				dimension = DIM_Z;
				direction = 1;
				layers.add(k);
			} else if (dx <= 0 && dy <= 0) { //
				dimension = DIM_Z;
				direction = -1;
				layers.add(k);
			} else if (dx >= 0 && dy <= 0) { //
				dimension = DIM_X;
				direction = 1;
				layers.add(i);
			} else if (dx <= 0 && dy >= 0) { //
				dimension = DIM_X;
				direction = -1;
				layers.add(i);
			}
		} else if (k == order + 1) { // touched front side
			float slope1 = 1.5f;
			float slope2 = -1f;
			float dxf = dx == 0 ? 0.01f : dx;
			float dyf = dy == 0 ? 0.01f : dy;
			float slope = dyf / dxf;
			if (slope <= slope1 && slope >= slope2) {
				dimension = DIM_Y;
				layers.add(j);
				if (dx > 0) {
					direction = -1;
				} else {
					direction = 1;
				}
			} else {
				dimension = DIM_X;
				layers.add(i);
				if (dy > 0) {
					direction = -1;
				} else {
					direction = 1;
				}
			}
		}
		if (dimension != 0) {
			return turn(dimension, layers, direction);
		}

		return false;
	}

	public boolean turnBySymbol(String symbol) {
		Move move = SymbolMoveUtil.parseMoveFromSymbol(symbol, order);
		if (move != null) {
			if (move.doubleTurn) {
				return turnTwice(move.dimension, move.layers, move.direction);
			} else {

				return turn(move.dimension, move.layers, move.direction);
			}
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

	public class Cubbie implements Cubie {

		private static final float CUBE_LEN = 1f;
		private static final float GAP_LEN = 0.2f;

		private Cube[][][] blocks;

		public Cubbie() {
			blocks = new Cube[order][order][order];
			for (int i = 0; i < order; i++) {
				for (int j = 0; j < order; j++) {
					for (int k = 0; k < order; k++) {
						blocks[i][j][k] = new Cube(1, 1, 1);
						blocks[i][j][k].setSideColors(Color.BLACK, Color.BLACK,
								Color.BLACK, Color.BLACK, Color.BLACK,
								Color.BLACK);
						blocks[i][j][k].x = (i - order / 2)
								* (CUBE_LEN + GAP_LEN);

						blocks[i][j][k].y = (j - order / 2)
								* (CUBE_LEN + GAP_LEN);

						blocks[i][j][k].z = (k - order / 2)
								* (CUBE_LEN + GAP_LEN);
					}
				}
			}
		}

		public void setCubeColors() {
			Log.v("3dtest", "in Cubbie's setCubeColors");
			for (int i = 0; i < order; i++) {
				for (int j = 0; j < order; j++) {
					for (int k = 0; k < order; k++) {
						setCubeColor(i, j, k);
					}
				}
			}
		}

		protected void setCubeColor(int i, int j, int k) {
			i++;
			j++;
			k++;
			if (i != 1 && i != order && j != 1 && j != order && k != 1
					&& k != order) {
				return;
			}

			Color up = CUBE_COLORS[currCube[i][j + 1][k].ordinal()];
			Color down = CUBE_COLORS[currCube[i][j - 1][k].ordinal()];
			Color right = CUBE_COLORS[currCube[i + 1][j][k].ordinal()];
			Color left = CUBE_COLORS[currCube[i - 1][j][k].ordinal()];
			Color front = CUBE_COLORS[currCube[i][j][k + 1].ordinal()];
			Color back = CUBE_COLORS[currCube[i][j][k - 1].ordinal()];
			blocks[i - 1][j - 1][k - 1].setSideColors(up, down, right, left,
					front, back);
		}

		public void drawPickingArea(GL10 gl) {
			// TODO Auto-generated method stub

		}

		public void finalizeAnimation() {
			// TODO Auto-generated method stub
		}

		public void prepareAnimation() {
			// TODO Auto-generated method stub

		}

		public void draw(GL10 gl) {
			if (inAnimation) {
				if (direction > 0) {
					rotationRate += 10;
				} else {
					rotationRate -= 10;
				}
				if (rotationX > 0) {
					moveGroup.rx = rotationRate;
				} else if (rotationY > 0) {
					moveGroup.ry = rotationRate;
				} else if (rotationZ > 0) {
					moveGroup.rz = rotationRate;
				}
				moveGroup.draw(gl);
				staticGroup.draw(gl);

				if (rotationRate >= 90 || rotationRate <= -90) {
					view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
					inAnimation = false;
					CubeColor[][][] tmp = currCube;
					currCube = tempCube;
					tempCube = tmp;
				}
			} else {
				Log.v("3dtest", "is drawing");
				for (int i = 0; i < order; i++) {
					for (int j = 0; j < order; j++) {
						for (int k = 0; k < order; k++) {
							blocks[i][j][k].draw(gl);
						}
					}
				}
			}
		}

		private boolean inAnimation = false;
		private float rotationRate;
		private int rotationX;
		private int rotationY;
		private int rotationZ;
		private int direction;
		private Group moveGroup = new Group();
		private Group staticGroup = new Group();

		public void startTurn(int x, int y, int z, int direction) {
			inAnimation = true;
			rotationRate = 0;
			rotationX = x < 0 ? 0 : 1;
			rotationY = y < 0 ? 0 : 1;
			rotationZ = z < 0 ? 0 : 1;
			moveGroup.clear();
			staticGroup.clear();
			moveGroup = new Group();
			staticGroup = new Group();
			for (int i = 0; i < order; i++) {
				for (int j = 0; j < order; j++) {
					for (int k = 0; k < order; k++) {
						if (x == i || y == j || z == k) {
							moveGroup.add(blocks[i][j][k]);
						} else {
							staticGroup.add(blocks[i][j][k]);
						}
					}
				}
			}
			this.direction = direction;
			view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}
	}

	public Cubie getCubie() {
		return cubie;
	}

	public class Cubby implements Cubie {

		private static final float BLOCK_LEN = 1.2f;
		private static final float PASTE_LEN = 1f;
		private static final float MARGIN_LEN = (BLOCK_LEN - PASTE_LEN) / 2;

		private Square[][][] cube_pieces;
		private List<Square> draw_pieces;
		private List<Square> pick_pieces;

		public Cubby() {
			cube_pieces = new Square[order + 2][order + 2][order + 2];
			draw_pieces = new LinkedList<Square>();
			pick_pieces = new LinkedList<Square>();
			int i, j, k;
			float x, y, z;
			float cube_len = BLOCK_LEN * order;
			float half_cube_len = cube_len / 2;
			// up & down
			for (i = 1; i <= order; i++) {
				for (k = 1; k <= order; k++) {
					x = (i - 1) * BLOCK_LEN - half_cube_len;
					z = (k - 1) * BLOCK_LEN - half_cube_len;
					// up
					j = order + 1;
					y = half_cube_len;
					Square drawPiece = new Square( //
							new Point3F(x + MARGIN_LEN, y, z + MARGIN_LEN), //
							new Point3F(x + MARGIN_LEN + PASTE_LEN, y, z
									+ MARGIN_LEN), //
							new Point3F(x + MARGIN_LEN + PASTE_LEN, y, z
									+ MARGIN_LEN + PASTE_LEN), //
							new Point3F(x + MARGIN_LEN, y, z + MARGIN_LEN
									+ PASTE_LEN) //
					);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);

					Square pickPiece = new Square( //
							new Point3F(x, y, z), //
							new Point3F(x + BLOCK_LEN, y, z), //
							new Point3F(x + BLOCK_LEN, y, z + BLOCK_LEN), //
							new Point3F(x, y, z + BLOCK_LEN) //
					);
					pickPiece.setColor(new Color(i * 20, j * 20, k * 20));
					pick_pieces.add(pickPiece);
					// down
					j = 0;
					y = -half_cube_len;
					drawPiece = new Square( //
							new Point3F(x + MARGIN_LEN, y, z + MARGIN_LEN), //
							new Point3F(x + MARGIN_LEN, y, z + MARGIN_LEN
									+ PASTE_LEN), //
							new Point3F(x + MARGIN_LEN + PASTE_LEN, y, z
									+ MARGIN_LEN + PASTE_LEN), //
							new Point3F(x + MARGIN_LEN + PASTE_LEN, y, z
									+ MARGIN_LEN) //
					);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);
				}
			}
			// right & left
			for (j = 1; j <= order; j++) {
				for (k = 1; k <= order; k++) {
					y = (j - 1) * BLOCK_LEN - half_cube_len;
					z = (k - 1) * BLOCK_LEN - half_cube_len;
					// up
					i = order + 1;
					x = half_cube_len;
					Square drawPiece = new Square( //
							new Point3F(x, y + MARGIN_LEN, z + MARGIN_LEN), //
							new Point3F(x, y + MARGIN_LEN, z + MARGIN_LEN
									+ PASTE_LEN), //
							new Point3F(x, y + MARGIN_LEN + PASTE_LEN, z
									+ MARGIN_LEN + PASTE_LEN), //
							new Point3F(x, y + MARGIN_LEN + PASTE_LEN, z
									+ MARGIN_LEN) //
					);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);

					Square pickPiece = new Square( //
							new Point3F(x, y, z), //
							new Point3F(x, y, z + BLOCK_LEN), //
							new Point3F(x, y + BLOCK_LEN, z + BLOCK_LEN), //
							new Point3F(x, y + BLOCK_LEN, z) //
					);

					pickPiece.setColor(new Color(i * 20, j * 20, k * 20));
					pick_pieces.add(pickPiece);
					// down
					i = 0;
					x = -half_cube_len;
					drawPiece = new Square( //
							new Point3F(x, y + MARGIN_LEN, z + MARGIN_LEN), //
							new Point3F(x, y + MARGIN_LEN + PASTE_LEN, z
									+ MARGIN_LEN), //
							new Point3F(x, y + MARGIN_LEN + PASTE_LEN, z
									+ MARGIN_LEN + PASTE_LEN), //
							new Point3F(x, y + MARGIN_LEN, z + MARGIN_LEN
									+ PASTE_LEN) //
					);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);
				}
			}
			// front & back
			for (i = 1; i <= order; i++) {
				for (j = 1; j <= order; j++) {
					x = (i - 1) * BLOCK_LEN - half_cube_len;
					y = (j - 1) * BLOCK_LEN - half_cube_len;
					// up
					k = order + 1;
					z = half_cube_len;
					Square drawPiece = new Square( //
							new Point3F(x + MARGIN_LEN, y + MARGIN_LEN, z), //
							new Point3F(x + MARGIN_LEN, y + MARGIN_LEN
									+ PASTE_LEN, z), //
							new Point3F(x + MARGIN_LEN + PASTE_LEN, y
									+ MARGIN_LEN + PASTE_LEN, z), //
							new Point3F(x + MARGIN_LEN + PASTE_LEN, y
									+ MARGIN_LEN, z) //
					);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);

					Square pickPiece = new Square( //
							new Point3F(x, y, z), //
							new Point3F(x, y + BLOCK_LEN, z), //
							new Point3F(x + BLOCK_LEN, y + BLOCK_LEN, z), //
							new Point3F(x + BLOCK_LEN, y, z) //
					);
					pickPiece.setColor(new Color(i * 20, j * 20, k * 20));
					pick_pieces.add(pickPiece);
					// down
					k = 0;
					z = -half_cube_len;
					drawPiece = new Square( //
							new Point3F(x + MARGIN_LEN, y + MARGIN_LEN, z), //
							new Point3F(x + MARGIN_LEN + PASTE_LEN, y
									+ MARGIN_LEN, z), //
							new Point3F(x + MARGIN_LEN + PASTE_LEN, y
									+ MARGIN_LEN + PASTE_LEN, z), //
							new Point3F(x + MARGIN_LEN, y + MARGIN_LEN
									+ PASTE_LEN, z) //
					);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);
				}
			}
		}

		public void setCubeColors() {
			CubeColor drawCube[][][] = currCube;
			if (inAnimation && animationInfo != null
					&& animationInfo.cube != null) {
				drawCube = animationInfo.cube;
			}
			int i, j, k;
			for (i = 1; i <= order; i++) {
				for (k = 1; k <= order; k++) {
					if (cube_pieces[i][0][k] != null) {
						cube_pieces[i][0][k]
								.setColor(CUBE_COLORS[drawCube[i][0][k]
										.ordinal()]);
					}
					if (cube_pieces[i][order + 1][k] != null) {
						cube_pieces[i][order + 1][k]
								.setColor(CUBE_COLORS[drawCube[i][order + 1][k]
										.ordinal()]);
					}

				}
			}
			for (i = 1; i <= order; i++) {
				for (j = 1; j <= order; j++) {
					if (cube_pieces[i][j][0] != null) {
						cube_pieces[i][j][0]
								.setColor(CUBE_COLORS[drawCube[i][j][0]
										.ordinal()]);
					}
					if (cube_pieces[i][j][order + 1] != null) {
						cube_pieces[i][j][order + 1]
								.setColor(CUBE_COLORS[drawCube[i][j][order + 1]
										.ordinal()]);
					}

				}
			}
			for (j = 1; j <= order; j++) {
				for (k = 1; k <= order; k++) {
					if (cube_pieces[0][j][k] != null) {
						cube_pieces[0][j][k]
								.setColor(CUBE_COLORS[drawCube[0][j][k]
										.ordinal()]);
					}
					if (cube_pieces[order + 1][j][k] != null) {
						cube_pieces[order + 1][j][k]
								.setColor(CUBE_COLORS[drawCube[order + 1][j][k]
										.ordinal()]);
					}

				}
			}
		}

		public void draw(GL10 gl) {
			if (inAnimation) {
				CubeAnimationInfo info = getAnimationInfo();
				float angle = info.doubleTurn ? 180f : 90f;
				float rt = (angle * info.currentStep()) / info.totalStep();
				if (info.direction > 0) {
					rt = -rt;
				}
				if (info.dimension == MagicCube.DIM_X) {
					moveGroup.rx = rt;
				} else if (info.dimension == MagicCube.DIM_Y) {
					moveGroup.ry = rt;
				} else if (info.dimension == MagicCube.DIM_Z) {
					moveGroup.rz = rt;
				}
				moveGroup.draw(gl);
				staticGroup.draw(gl);

			} else {
				for (Square s : draw_pieces) {
					s.draw(gl);
				}
			}
		}

		public void drawPickingArea(GL10 gl) {
			for (Square s : pick_pieces) {
				s.draw(gl);
			}
		}

		private boolean inAnimation = false;
		private Group moveGroup = new Group();
		private Group staticGroup = new Group();

		public void finalizeAnimation() {
			inAnimation = false;
		}

		public void prepareAnimation() {
			CubeAnimationInfo info = getAnimationInfo();
			inAnimation = true;

			moveGroup.clear();
			staticGroup.clear();
			moveGroup.x = staticGroup.x = 0;
			moveGroup.y = staticGroup.y = 0;
			moveGroup.z = staticGroup.z = 0;
			moveGroup.rx = staticGroup.rx = 0;
			moveGroup.ry = staticGroup.ry = 0;
			moveGroup.rz = staticGroup.rz = 0;

			for (Square tmp : this.draw_pieces) {
				staticGroup.add(tmp);
			}
			for (int layer : info.layers) {
				for (int i = 0; i <= order + 1; i++) {
					for (int j = 0; j <= order + 1; j++) {
						Square tmp = null;
						if (info.dimension == MagicCube.DIM_X) {
							tmp = cube_pieces[layer][i][j];
						} else if (info.dimension == MagicCube.DIM_Y) {
							tmp = cube_pieces[i][layer][j];
						} else if (info.dimension == MagicCube.DIM_Z) {
							tmp = cube_pieces[i][j][layer];
						}
						if (tmp != null) {
							moveGroup.add(tmp);
							staticGroup.remove(tmp);
						}
					}
				}
				if (layer == 1 || layer == order) {
					int lyr = layer == 1 ? 0 : order + 1;
					for (int i = 1; i <= order; i++) {
						for (int j = 1; j <= order; j++) {
							Square tmp = null;
							if (info.dimension == MagicCube.DIM_X) {
								tmp = cube_pieces[lyr][i][j];
							} else if (info.dimension == MagicCube.DIM_Y) {
								tmp = cube_pieces[i][lyr][j];
							} else if (info.dimension == MagicCube.DIM_Z) {
								tmp = cube_pieces[i][j][lyr];
							}
							if (tmp != null) {
								moveGroup.add(tmp);
								staticGroup.remove(tmp);
							}
						}
					}
				}
			}
		}
	}

	public class CubbyWithESquare implements Cubie {

		private static final float BLOCK_LEN = 1.2f;
		private static final float PASTE_LEN = 1f;
		private static final float MARGIN_LEN = (BLOCK_LEN - PASTE_LEN) / 2;

		private ESquare[][][] cube_pieces;
		private List<ESquare> draw_pieces;
		private List<Square> pick_pieces;

		public CubbyWithESquare() {
			cube_pieces = new ESquare[order + 2][order + 2][order + 2];
			draw_pieces = new LinkedList<ESquare>();
			pick_pieces = new LinkedList<Square>();
			int i, j, k;
			float x, y, z;
			float cube_len = BLOCK_LEN * order;
			float half_cube_len = cube_len / 2;
			float mr = MARGIN_LEN / BLOCK_LEN;
			// up & down
			for (i = 1; i <= order; i++) {
				for (k = 1; k <= order; k++) {
					x = (i - 1) * BLOCK_LEN - half_cube_len;
					z = (k - 1) * BLOCK_LEN - half_cube_len;
					// up
					j = order + 1;
					y = half_cube_len;
					ESquare drawPiece = new ESquare( //
							new Point3F(x, y, z), //
							new Point3F(x + BLOCK_LEN, y, z), //
							new Point3F(x + BLOCK_LEN, y, z + BLOCK_LEN), //
							new Point3F(x, y, z + BLOCK_LEN), //
							mr);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);

					Square pickPiece = new Square( //
							new Point3F(x, y, z), //
							new Point3F(x + BLOCK_LEN, y, z), //
							new Point3F(x + BLOCK_LEN, y, z + BLOCK_LEN), //
							new Point3F(x, y, z + BLOCK_LEN) //
					);
					pickPiece.setColor(new Color(i * 20, j * 20, k * 20));
					pick_pieces.add(pickPiece);
					// down
					j = 0;
					y = -half_cube_len;
					drawPiece = new ESquare( //
							new Point3F(x, y, z), //
							new Point3F(x, y, z + BLOCK_LEN), //
							new Point3F(x + BLOCK_LEN, y, z + BLOCK_LEN), //
							new Point3F(x + BLOCK_LEN, y, z), //
							mr);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);
				}
			}
			// right & left
			for (j = 1; j <= order; j++) {
				for (k = 1; k <= order; k++) {
					y = (j - 1) * BLOCK_LEN - half_cube_len;
					z = (k - 1) * BLOCK_LEN - half_cube_len;
					// up
					i = order + 1;
					x = half_cube_len;
					ESquare drawPiece = new ESquare( //
							new Point3F(x, y, z), //
							new Point3F(x, y, z + BLOCK_LEN), //
							new Point3F(x, y + BLOCK_LEN, z + BLOCK_LEN), //
							new Point3F(x, y + BLOCK_LEN, z), //
							mr);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);

					Square pickPiece = new Square( //
							new Point3F(x, y, z), //
							new Point3F(x, y, z + BLOCK_LEN), //
							new Point3F(x, y + BLOCK_LEN, z + BLOCK_LEN), //
							new Point3F(x, y + BLOCK_LEN, z) //
					);

					pickPiece.setColor(new Color(i * 20, j * 20, k * 20));
					pick_pieces.add(pickPiece);
					// down
					i = 0;
					x = -half_cube_len;
					drawPiece = new ESquare( //
							new Point3F(x, y, z), //
							new Point3F(x, y + BLOCK_LEN, z), //
							new Point3F(x, y + BLOCK_LEN, z + BLOCK_LEN), //
							new Point3F(x, y, z + BLOCK_LEN), //
							mr);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);
				}
			}
			// front & back
			for (i = 1; i <= order; i++) {
				for (j = 1; j <= order; j++) {
					x = (i - 1) * BLOCK_LEN - half_cube_len;
					y = (j - 1) * BLOCK_LEN - half_cube_len;
					// up
					k = order + 1;
					z = half_cube_len;
					ESquare drawPiece = new ESquare( //
							new Point3F(x, y, z), //
							new Point3F(x, y + BLOCK_LEN, z), //
							new Point3F(x + BLOCK_LEN, y + BLOCK_LEN, z), //
							new Point3F(x + BLOCK_LEN, y, z), //
							mr);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);

					Square pickPiece = new Square( //
							new Point3F(x, y, z), //
							new Point3F(x, y + BLOCK_LEN, z), //
							new Point3F(x + BLOCK_LEN, y + BLOCK_LEN, z), //
							new Point3F(x + BLOCK_LEN, y, z) //
					);
					pickPiece.setColor(new Color(i * 20, j * 20, k * 20));
					pick_pieces.add(pickPiece);
					// down
					k = 0;
					z = -half_cube_len;
					drawPiece = new ESquare( //
							new Point3F(x, y, z), //
							new Point3F(x + BLOCK_LEN, y, z), //
							new Point3F(x + BLOCK_LEN, y + BLOCK_LEN, z), //
							new Point3F(x, y + BLOCK_LEN, z), //
							mr);
					cube_pieces[i][j][k] = drawPiece;
					draw_pieces.add(drawPiece);
				}
			}
		}

		public void setCubeColors() {
			CubeColor drawCube[][][] = currCube;
			if (inAnimation && animationInfo != null
					&& animationInfo.cube != null) {
				drawCube = animationInfo.cube;
			}
			int i, j, k;
			for (i = 1; i <= order; i++) {
				for (k = 1; k <= order; k++) {
					if (cube_pieces[i][0][k] != null) {
						cube_pieces[i][0][k]
								.setPasteColor(CUBE_COLORS[drawCube[i][0][k]
										.ordinal()]);
					}
					if (cube_pieces[i][order + 1][k] != null) {
						cube_pieces[i][order + 1][k]
								.setPasteColor(CUBE_COLORS[drawCube[i][order + 1][k]
										.ordinal()]);
					}

				}
			}
			for (i = 1; i <= order; i++) {
				for (j = 1; j <= order; j++) {
					if (cube_pieces[i][j][0] != null) {
						cube_pieces[i][j][0]
								.setPasteColor(CUBE_COLORS[drawCube[i][j][0]
										.ordinal()]);
					}
					if (cube_pieces[i][j][order + 1] != null) {
						cube_pieces[i][j][order + 1]
								.setPasteColor(CUBE_COLORS[drawCube[i][j][order + 1]
										.ordinal()]);
					}

				}
			}
			for (j = 1; j <= order; j++) {
				for (k = 1; k <= order; k++) {
					if (cube_pieces[0][j][k] != null) {
						cube_pieces[0][j][k]
								.setPasteColor(CUBE_COLORS[drawCube[0][j][k]
										.ordinal()]);
					}
					if (cube_pieces[order + 1][j][k] != null) {
						cube_pieces[order + 1][j][k]
								.setPasteColor(CUBE_COLORS[drawCube[order + 1][j][k]
										.ordinal()]);
					}

				}
			}
		}

		public void draw(GL10 gl) {
			if (inAnimation) {
				CubeAnimationInfo info = getAnimationInfo();
				float angle = info.doubleTurn ? 180f : 90f;
				float rt = (angle * info.currentStep()) / info.totalStep();
				if (info.direction > 0) {
					rt = -rt;
				}
				if (info.dimension == MagicCube.DIM_X) {
					moveGroup.rx = rt;
				} else if (info.dimension == MagicCube.DIM_Y) {
					moveGroup.ry = rt;
				} else if (info.dimension == MagicCube.DIM_Z) {
					moveGroup.rz = rt;
				}
				moveGroup.draw(gl);
				staticGroup.draw(gl);

			} else {
				for (ESquare s : draw_pieces) {
					s.draw(gl);
				}
			}
		}

		public void drawPickingArea(GL10 gl) {
			for (Square s : pick_pieces) {
				s.draw(gl);
			}
		}

		private boolean inAnimation = false;
		private Group moveGroup = new Group();
		private Group staticGroup = new Group();

		public void finalizeAnimation() {
			inAnimation = false;
		}

		public void prepareAnimation() {
			CubeAnimationInfo info = getAnimationInfo();
			inAnimation = true;

			moveGroup.clear();
			staticGroup.clear();
			moveGroup.x = staticGroup.x = 0;
			moveGroup.y = staticGroup.y = 0;
			moveGroup.z = staticGroup.z = 0;
			moveGroup.rx = staticGroup.rx = 0;
			moveGroup.ry = staticGroup.ry = 0;
			moveGroup.rz = staticGroup.rz = 0;

			for (ESquare tmp : this.draw_pieces) {
				staticGroup.add(tmp);
			}
			for (int layer : info.layers) {
				for (int i = 0; i <= order + 1; i++) {
					for (int j = 0; j <= order + 1; j++) {
						ESquare tmp = null;
						if (info.dimension == MagicCube.DIM_X) {
							tmp = cube_pieces[layer][i][j];
						} else if (info.dimension == MagicCube.DIM_Y) {
							tmp = cube_pieces[i][layer][j];
						} else if (info.dimension == MagicCube.DIM_Z) {
							tmp = cube_pieces[i][j][layer];
						}
						if (tmp != null) {
							moveGroup.add(tmp);
							staticGroup.remove(tmp);
						}
					}
				}
				if (layer == 1 || layer == order) {
					int lyr = layer == 1 ? 0 : order + 1;
					for (int i = 1; i <= order; i++) {
						for (int j = 1; j <= order; j++) {
							ESquare tmp = null;
							if (info.dimension == MagicCube.DIM_X) {
								tmp = cube_pieces[lyr][i][j];
							} else if (info.dimension == MagicCube.DIM_Y) {
								tmp = cube_pieces[i][lyr][j];
							} else if (info.dimension == MagicCube.DIM_Z) {
								tmp = cube_pieces[i][j][lyr];
							}
							if (tmp != null) {
								moveGroup.add(tmp);
								staticGroup.remove(tmp);
							}
						}
					}
				}
			}
		}
	}

	public boolean rotate(int dim, int direction) {
		List<Integer> layers = new LinkedList<Integer>();
		for (int i = 1; i <= order; i++) {
			layers.add(i);
		}
		return turn(dim, layers, direction);
	}

	public boolean turnTwice(int dimension, List<Integer> layers, int direction) {
		return this.turn(dimension, layers, direction, true);
	}

	public boolean turn(int dimension, List<Integer> layers, int direction,
			boolean twice) {
		boolean succeed = true;
		copyModel(currCube, tempCube);
		succeed = turnInternal(currCube, tempCube, dimension, layers, direction)
				&& succeed;
		if (twice) {

			copyModel(tempCube, prevCube);
			succeed = turnInternal(tempCube, prevCube, dimension, layers,
					direction)
					&& succeed;
			CubeColor[][][] tmp = tempCube;
			tempCube = prevCube;
			prevCube = tmp;
		}
		if (succeed) {
			CubeColor[][][] tmp = currCube;
			currCube = tempCube;
			tempCube = prevCube;
			prevCube = tmp;
		}

		if (succeed && !this.noAnimationMode) {
			this.animationInfo.reset();
			this.animationInfo.cube = prevCube;
			this.animationInfo.layers.clear();
			this.animationInfo.layers.addAll(layers);
			this.animationInfo.direction = direction;
			this.animationInfo.dimension = dimension;
			this.animationInfo.doubleTurn = twice;
		}
		return succeed;
	}

	private boolean turnInternal(CubeColor[][][] from, CubeColor[][][] to,
			int dimension, List<Integer> layers, int direction) {
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

	public boolean turn(int dimension, List<Integer> layers, int direction) {
		return this.turn(dimension, layers, direction, false);
	}

	private boolean turnSide(CubeColor[][][] from, CubeColor[][][] to,
			int dimension, int layer, int direction) {
		Pair pair = new Pair();
		if (dimension == DIM_X) {
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
		} else if (dimension == DIM_Y) {
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
		} else if (dimension == DIM_Z) {
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

	private boolean turnFace(CubeColor[][][] from, CubeColor[][][] to,
			int dimension, int layer, int direction) {
		Pair pair = new Pair();
		if (dimension == DIM_X) {
			for (int i = 1; i <= order; i++) {
				for (int j = 1; j <= order; j++) {
					pair.x = i;
					pair.y = j;
					applyRotate(pair, direction);
					to[layer][pair.x][pair.y] = from[layer][i][j];
				}
			}
			return true;
		} else if (dimension == DIM_Y) {
			for (int i = 1; i <= order; i++) {
				for (int j = 1; j <= order; j++) {
					pair.x = i;
					pair.y = j;
					applyRotate(pair, direction);
					to[pair.y][layer][pair.x] = from[j][layer][i];
				}
			}
			return true;
		} else if (dimension == DIM_Z) {
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

	private void copyModel(CubeColor[][][] from, CubeColor[][][] to) {
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

}
