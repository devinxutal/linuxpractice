package com.devinxutal.tetris.model;

import java.io.IOException;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.devinxutal.tetris.control.Command;

public class Playground {
	public static final int VERTICAL_BLOCKS = 20;
	public static final int HORIZONTAL_BLOCKS = 10;
	private static final String TAG = "Playground";
	private int width;
	private int height;
	private int blockSize;
	private int gapLen = 1;

	private boolean inAnimation = false;
	private boolean eliminating[] = new boolean[VERTICAL_BLOCKS];
	private int eliminatingCurrentStep = 0;
	private int eliminatingTotalSteps = 3;

	private Block activeBlock = null;
	private int blockOffsetX = 0;
	private int blockOffsetY = 0;

	private int playground[][] = new int[VERTICAL_BLOCKS][HORIZONTAL_BLOCKS];

	private LinkedList<Block> blockQueue = new LinkedList<Block>();
	private int blockQueueLen = 3;

	//
	private ScoreAndLevel scoreLevel = new ScoreAndLevel();

	public Playground() {

	}

	public void reset() {
		for (int i = 0; i < VERTICAL_BLOCKS; i++) {
			for (int j = 0; j < HORIZONTAL_BLOCKS; j++) {
				playground[i][j] = -1;
			}
		}
		inAnimation = false;
		for (int i = 0; i < eliminating.length; i++) {
			eliminating[i] = false;
		}
		eliminatingCurrentStep = 0;

		blockQueue.clear();
		for (int i = 0; i < blockQueueLen; i++) {
			blockQueue.addLast(new Block());
		}
	}

	public ScoreAndLevel getScoreAndLevel() {
		return scoreLevel;
	}

	public void moveOn() {
		if (inAnimation) {
			eliminatingCurrentStep++;
			if (eliminatingCurrentStep > eliminatingTotalSteps) {
				finishElimination();
			}
		} else if (activeBlock == null) {
			allocateBlock();
		} else {
			if (moveBlockDown()) {

			} else {
				settleBlock();
				checkElimination();
			}
		}
	}

	public void processCommand(Command cmd) {
		Log.v(TAG, "processCommand 1");
		if (this.activeBlock == null) {
			return;
		}

		Log.v(TAG, "processCommand 2");
		switch (cmd) {
		case TURN:
			turnBlock();
			break;
		case LEFT:
			moveBlockLeft();
			break;
		case RIGHT:
			moveBlockRight();
			break;
		case DOWN:
			moveBlockDown();
			break;
		}
	}

	public boolean isInAnimation() {
		return inAnimation;
	}

	public Block getNextBlock() {
		if (blockQueue.isEmpty()) {
			return null;
		} else {
			return blockQueue.peek();
		}
	}

	public void determinSize(int maxWidth, int maxHeight) {
		int bs1 = (maxWidth - (HORIZONTAL_BLOCKS - 1) * gapLen)
				/ HORIZONTAL_BLOCKS;
		int bs2 = (maxHeight - (VERTICAL_BLOCKS - 1) * gapLen)
				/ VERTICAL_BLOCKS;
		blockSize = Math.min(bs1, bs2);
		width = HORIZONTAL_BLOCKS * (gapLen + blockSize) - gapLen;
		height = VERTICAL_BLOCKS * (gapLen + blockSize) - gapLen;

		dm.onSizeChanged();
	}

	public void initDrawingMetrics(Context context) {
		this.dm.init(context);
	}

	private DrawingMetrics dm = new DrawingMetrics();

	public void draw(Canvas canvas, int x, int y) {
		dm.paint.setColor(Color.BLACK);
		canvas.drawRect(new Rect(x, y, x + width, y + height), dm.paint);
		for (int row = 0; row < VERTICAL_BLOCKS; row++) {
			if (inAnimation && eliminating[row]
					&& (eliminatingCurrentStep % 2 == 0)) {
				continue;
			}
			for (int j = 0; j < HORIZONTAL_BLOCKS; j++) {
				int color = playground[row][j];
				if (color >= 0) {
					canvas.drawBitmap(dm.sized_blocks[color], x + j
							* (blockSize + gapLen), y + row
							* (blockSize + gapLen), dm.paint);
				}
			}
		}
		if (activeBlock != null) {
			boolean[][] matrix = activeBlock.getMatrix();
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					if (matrix[i][j]) {
						int xx = blockOffsetX + j;
						int yy = blockOffsetY + i;
						if (xx >= 0 && xx < HORIZONTAL_BLOCKS && yy >= 0
								&& yy < VERTICAL_BLOCKS) {
							canvas.drawBitmap(dm.sized_blocks[activeBlock
									.getBlockType().ordinal()], x + xx
									* (blockSize + gapLen), y + yy
									* (blockSize + gapLen), dm.paint);
						}
					}
				}
			}
		}
	}

	public class DrawingMetrics {

		public final int[] COLORS = new int[] { Color.rgb(150, 0, 50),
				Color.rgb(0, 150, 50), Color.rgb(150, 150, 0),
				Color.rgb(150, 0, 100), Color.rgb(0, 0, 150),
				Color.rgb(150, 150, 150), Color.rgb(0, 150, 150) };

		public Bitmap[] original_blocks = new Bitmap[7];
		public Bitmap[] sized_blocks = new Bitmap[7];
		private Paint paint;

		public DrawingMetrics() {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(18);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
		}

		public void init(Context context) {
			for (int i = 0; i < 7; i++) {
				try {
					original_blocks[i] = BitmapFactory.decodeStream(context
							.getAssets().open("blocks/default/" + i + ".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void onSizeChanged() {
			if (sized_blocks[0] == null
					|| sized_blocks[0].getWidth() != blockSize) {
				for (int i = 0; i < 7; i++) {
					sized_blocks[i] = Bitmap.createScaledBitmap(
							original_blocks[i], blockSize, blockSize, false);
				}
			}
		}
	}

	// //////////////////////////////////////////////////////////////////
	// private methods //
	// //////////////////////////////////////////////////////////////////
	private boolean moveBlockDown() {
		if (activeBlock == null) {
			return false;
		}
		boolean[][] matrix = activeBlock.getMatrix();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (matrix[i][j]) {
					int x = blockOffsetX + j;
					int y = blockOffsetY + i + 1;
					if (y >= VERTICAL_BLOCKS) {
						return false;
					}
					if (x >= 0 && x < HORIZONTAL_BLOCKS && y >= 0
							&& y < VERTICAL_BLOCKS && playground[y][x] >= 0) {
						return false;
					}
				}
			}
		}
		blockOffsetY++;
		return true;
	}

	private boolean moveBlockLeft() {
		if (activeBlock == null) {
			return false;
		}
		boolean[][] matrix = activeBlock.getMatrix();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (matrix[i][j]) {
					int x = blockOffsetX + j - 1;
					int y = blockOffsetY + i;
					if (x < 0) {
						return false;
					}
					if (x >= 0 && x < HORIZONTAL_BLOCKS && y >= 0
							&& y < VERTICAL_BLOCKS && playground[y][x] >= 0) {
						return false;
					}
				}
			}
		}
		blockOffsetX--;
		return true;
	}

	private boolean moveBlockRight() {
		if (activeBlock == null) {
			return false;
		}
		boolean[][] matrix = activeBlock.getMatrix();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (matrix[i][j]) {
					int x = blockOffsetX + j + 1;
					int y = blockOffsetY + i;
					if (x >= HORIZONTAL_BLOCKS) {
						return false;
					}
					if (x >= 0 && x < HORIZONTAL_BLOCKS && y >= 0
							&& y < VERTICAL_BLOCKS && playground[y][x] >= 0) {
						return false;
					}
				}
			}
		}
		blockOffsetX++;
		return true;
	}

	private boolean turnBlock() {
		if (activeBlock == null) {
			return false;
		}
		boolean[][] matrix = activeBlock.getTurnedMatrix();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (matrix[i][j]) {
					int x = blockOffsetX + j;
					int y = blockOffsetY + i;
					if (x < 0 || x >= HORIZONTAL_BLOCKS || y < 0
							|| y >= VERTICAL_BLOCKS || playground[y][x] >= 0) {
						return false;
					}
				}
			}
		}
		activeBlock.turn();
		return true;
	}

	private void allocateBlock() {
		this.activeBlock = blockQueue.poll();
		blockQueue.addLast(new Block());

		blockOffsetX = 2;
		blockOffsetY = 0;
	}

	private void settleBlock() {
		if (this.activeBlock != null) {
			boolean[][] matrix = activeBlock.getMatrix();
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					if (matrix[i][j]) {
						int x = blockOffsetX + j;
						int y = blockOffsetY + i;
						if (x >= 0 && x < HORIZONTAL_BLOCKS && y >= 0
								&& y < VERTICAL_BLOCKS) {
							playground[y][x] = activeBlock.getBlockType()
									.ordinal();
						}
					}
				}
			}
		}
		this.activeBlock = null;
	}

	private boolean checkElimination() {
		inAnimation = false;
		for (int row = VERTICAL_BLOCKS - 1; row >= 0; row--) {
			boolean eli = true;
			for (int i = 0; i < HORIZONTAL_BLOCKS; i++) {
				if (playground[row][i] < 0) {
					eli = false;
					break;
				}
			}
			if (eli) {
				if (!inAnimation) {
					inAnimation = true;
					for (int i = 0; i < VERTICAL_BLOCKS; i++) {
						eliminating[i] = false;
					}
					eliminatingCurrentStep = -1;
				}
				eliminating[row] = true;
			}
		}
		return inAnimation;
	}

	private void finishElimination() {
		int to = VERTICAL_BLOCKS - 1;
		int from = VERTICAL_BLOCKS - 1;
		for (; from >= 0; from--, to--) {
			if (eliminating[from]) {
				to++;
				continue;
			}
			if (from == to) {
				continue;
			}
			if (from >= 0) {
				for (int i = 0; i < HORIZONTAL_BLOCKS; i++) {
					playground[to][i] = playground[from][i];
				}
			}
		}

		inAnimation = false;
	}
}
