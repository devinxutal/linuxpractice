package com.devinxutal.fmc.model;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.util.Log;

import com.devinxutal.fmc.primitives.Color;
import com.devinxutal.fmc.primitives.Cube;
import com.devinxutal.fmc.primitives.ESquare;
import com.devinxutal.fmc.primitives.Group;
import com.devinxutal.fmc.primitives.Point3F;
import com.devinxutal.fmc.primitives.Square;
import com.devinxutal.fmc.ui.CubeView;

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
	private CubeColor[][][] cube;
	private CubeColor[][][] backup;

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
		cube = new CubeColor[order + 2][order + 2][order + 2];
		backup = new CubeColor[order + 2][order + 2][order + 2];
		for (int i = 0; i < order + 2; i++) {
			for (int j = 0; j < order + 2; j++) {
				for (int k = 0; k < order + 2; k++) {
					cube[i][j][k] = CubeColor.BLACK;
					backup[i][j][k] = CubeColor.BLACK;
				}
			}
		}
		for (int i = 1; i <= order; i++) {
			for (int j = 1; j <= order; j++) {
				cube[i][j][0] = CubeColor.ORANGE; // back
				cube[i][j][order + 1] = CubeColor.RED; // front
				cube[i][0][j] = CubeColor.WHITE; // down
				cube[i][order + 1][j] = CubeColor.YELLOW; // up
				cube[0][i][j] = CubeColor.BLUE; // left
				cube[order + 1][i][j] = CubeColor.GREEN; // right
			}
		}
	}

	public int getOrder() {
		return order;
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
		if (symbol.length() >= 1) {
			char face = symbol.charAt(0);
			int direction = 1;
			if (symbol.length() >= 2 && symbol.charAt(1) == '\'') {
				direction = -1;
			}
			return turn(face, direction);
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

			Color up = CUBE_COLORS[cube[i][j + 1][k].ordinal()];
			Color down = CUBE_COLORS[cube[i][j - 1][k].ordinal()];
			Color right = CUBE_COLORS[cube[i + 1][j][k].ordinal()];
			Color left = CUBE_COLORS[cube[i - 1][j][k].ordinal()];
			Color front = CUBE_COLORS[cube[i][j][k + 1].ordinal()];
			Color back = CUBE_COLORS[cube[i][j][k - 1].ordinal()];
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
					CubeColor[][][] tmp = cube;
					cube = backup;
					backup = tmp;
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
			CubeColor drawCube[][][] = cube;
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
				float rt = (90f * info.currentStep()) / info.totalStep();
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
			CubeColor drawCube[][][] = cube;
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
				float rt = (90f * info.currentStep()) / info.totalStep();
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

	public boolean turn(int dimension, List<Integer> layers, int direction) {

		Log.v("motiontest", "in actual turn routine: layers:"
				+ layers.toString() + ", dimension: " + dimension
				+ ", direction: " + direction);
		backupModel();
		boolean succeed = true;
		for (int layer : layers) {
			if (layer == 1 || layer == order) {
				int l = layer == 1 ? 0 : order + 1;
				succeed = turnFace(dimension, l, direction) && succeed;
			}
			Log.v("motiontest", "turn side " + layer);
			succeed = turnSide(dimension, layer, direction) && succeed;
		}
		swapModel();

		if (!this.noAnimationMode) {
			this.animationInfo.reset();
			this.animationInfo.cube = backup;
			this.animationInfo.layers.clear();
			this.animationInfo.layers.addAll(layers);
			this.animationInfo.direction = direction;
			this.animationInfo.dimension = dimension;
		}
		/* end for test */
		return succeed;
	}

	private boolean turnSide(int dimension, int layer, int direction) {
		Pair pair = new Pair();
		if (dimension == DIM_X) {
			for (int i = 1; i <= order; i++) {
				pair.x = 0;
				pair.y = i;
				applyRotate(pair, direction);
				backup[layer][pair.x][pair.y] = cube[layer][0][i];

				pair.x = order + 1;
				pair.y = i;
				applyRotate(pair, direction);
				backup[layer][pair.x][pair.y] = cube[layer][order + 1][i];

				pair.x = i;
				pair.y = 0;
				applyRotate(pair, direction);
				backup[layer][pair.x][pair.y] = cube[layer][i][0];

				pair.x = i;
				pair.y = order + 1;
				applyRotate(pair, direction);
				backup[layer][pair.x][pair.y] = cube[layer][i][order + 1];

			}
			return true;
		} else if (dimension == DIM_Y) {
			for (int i = 1; i <= order; i++) {
				pair.x = 0;
				pair.y = i;
				applyRotate(pair, direction);
				backup[pair.y][layer][pair.x] = cube[i][layer][0];
				pair.x = order + 1;
				pair.y = i;
				applyRotate(pair, direction);
				backup[pair.y][layer][pair.x] = cube[i][layer][order + 1];

				pair.x = i;
				pair.y = 0;
				applyRotate(pair, direction);
				backup[pair.y][layer][pair.x] = cube[0][layer][i];

				pair.x = i;
				pair.y = order + 1;
				applyRotate(pair, direction);
				backup[pair.y][layer][pair.x] = cube[order + 1][layer][i];

			}
			return true;
		} else if (dimension == DIM_Z) {
			for (int i = 1; i <= order; i++) {
				pair.x = 0;
				pair.y = i;
				applyRotate(pair, direction);
				backup[pair.x][pair.y][layer] = cube[0][i][layer];

				pair.x = order + 1;
				pair.y = i;
				applyRotate(pair, direction);
				backup[pair.x][pair.y][layer] = cube[order + 1][i][layer];

				pair.x = i;
				pair.y = 0;
				applyRotate(pair, direction);
				backup[pair.x][pair.y][layer] = cube[i][0][layer];

				pair.x = i;
				pair.y = order + 1;
				applyRotate(pair, direction);
				backup[pair.x][pair.y][layer] = cube[i][order + 1][layer];

			}
			return true;
		}
		return false;
	}

	private boolean turnFace(int dimension, int layer, int direction) {
		Pair pair = new Pair();
		if (dimension == DIM_X) {
			for (int i = 1; i <= order; i++) {
				for (int j = 1; j <= order; j++) {
					pair.x = i;
					pair.y = j;
					applyRotate(pair, direction);
					backup[layer][pair.x][pair.y] = cube[layer][i][j];
				}
			}
			return true;
		} else if (dimension == DIM_Y) {
			for (int i = 1; i <= order; i++) {
				for (int j = 1; j <= order; j++) {
					pair.x = i;
					pair.y = j;
					applyRotate(pair, direction);
					backup[pair.y][layer][pair.x] = cube[j][layer][i];
				}
			}
			return true;
		} else if (dimension == DIM_Z) {
			for (int i = 1; i <= order; i++) {
				for (int j = 1; j <= order; j++) {
					pair.x = i;
					pair.y = j;
					applyRotate(pair, direction);
					backup[pair.x][pair.y][layer] = cube[i][j][layer];
				}
			}
			return true;
		}
		return false;
	}

	private boolean turn(char side, int direction) {
		List<Integer> layers = new LinkedList<Integer>();
		int dimension = 0;
		if (side == 'f' || side == 'u' || side == 'r') {
			layers.add(order);
		} else if (side == 'b' || side == 'd' || side == 'l') {
			layers.add(1);
			direction = -direction;
		}

		side = ("" + side).toLowerCase().charAt(0);
		if (side == 'u' || side == 'd') {
			dimension = DIM_Y;
		} else if (side == 'r' || side == 'l') {
			dimension = DIM_X;
		} else if (side == 'f' || side == 'b') {
			dimension = DIM_Z;
		}

		return this.turn(dimension, layers, direction);
	}

	private void swapModel() {
		CubeColor[][][] tmp = backup;
		backup = cube;
		cube = tmp;
	}

	private void backupModel() {
		for (int i = 1; i <= order; i++) {
			for (int j = 1; j <= order; j++) {
				backup[i][j][0] = cube[i][j][0]; // back
				backup[i][j][order + 1] = cube[i][j][order + 1]; // front
				backup[i][0][j] = cube[i][0][j]; // down
				backup[i][order + 1][j] = cube[i][order + 1][j]; // up
				backup[0][i][j] = cube[0][i][j]; // left
				backup[order + 1][i][j] = cube[order + 1][i][j]; // right
			}
		}
	}

}
