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
import com.devinxutal.tetris.model.SavablePlayground.SavableBlock;

public class Playground {
	public static final int VERTICAL_BLOCKS = 20;
	public static final int HORIZONTAL_BLOCKS = 10;
	public static final int GAP_LEN = 1;
	private static final String TAG = "Playground";
	private int width;
	private int height;
	private int blockSize;

	private boolean inAnimation = false;
	private boolean eliminating[] = new boolean[VERTICAL_BLOCKS];
	private int eliminatingCurrentStep = 0;
	private int eliminatingTotalSteps = 3;
	private int eliminationLines = 0;
	private boolean finished = false;

	private Block activeBlock = null;
	private int blockOffsetX = 0;
	private int blockOffsetY = 0;
	private int projectedY = 0;
	private int originalOffsetY = 0;

	private int playground[][] = new int[VERTICAL_BLOCKS][HORIZONTAL_BLOCKS];

	private LinkedList<Block> blockQueue = new LinkedList<Block>();
	private int blockQueueLen = 3;

	//
	private ScoreAndLevel scoreLevel = new ScoreAndLevel();

	public Playground() {
		this.reset();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void reset() {
		inAnimation = false;
		finished = false;
		this.scoreLevel.reset();

		for (int i = 0; i < VERTICAL_BLOCKS; i++) {
			for (int j = 0; j < HORIZONTAL_BLOCKS; j++) {
				playground[i][j] = -1;
			}
		}
		for (int i = 0; i < eliminating.length; i++) {
			eliminating[i] = false;
		}
		eliminatingCurrentStep = 0;

		blockQueue.clear();
		for (int i = 0; i < blockQueueLen; i++) {
			blockQueue.addLast(new Block());
		}

	}

	public boolean isFinished() {
		return finished;
	}

	public ScoreAndLevel getScoreAndLevel() {
		return scoreLevel;
	}

	public void moveOn() {
		isFinishingElimination = false;
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
				checkFinish();
				checkElimination();

			}
		}
	}

	public boolean processCommand(Command cmd) {
		Log.v(TAG, "processCommand 1");
		if (this.activeBlock == null) {
			return false;
		}

		Log.v(TAG, "processCommand 2");
		switch (cmd) {
		case TURN:
			return turnBlock();
		case LEFT:
			return moveBlockLeft();
		case RIGHT:
			return moveBlockRight();
		case DOWN:
			return moveBlockDown();
		case DIRECT_DOWN:
			return moveBlockDirectDown();
		}
		return false;
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
		int bs1 = (maxWidth - (HORIZONTAL_BLOCKS + 1) * GAP_LEN)
				/ HORIZONTAL_BLOCKS;
		int bs2 = (maxHeight - (VERTICAL_BLOCKS + 1) * GAP_LEN)
				/ VERTICAL_BLOCKS;
		blockSize = Math.min(bs1, bs2);
		width = HORIZONTAL_BLOCKS * (GAP_LEN + blockSize) + GAP_LEN;
		height = VERTICAL_BLOCKS * (GAP_LEN + blockSize) + GAP_LEN;

		dm.onSizeChanged();
	}

	public void initDrawingMetrics(Context context) {
		this.dm.init(context);
	}

	private DrawingMetrics dm = new DrawingMetrics();

	public void drawPendingBlocks(Canvas canvas, Rect rect1, Rect rect2,
			Rect rect3) {
		Log.v(TAG, "draw pending blocks");
		if (this.blockQueue != null) {
			if (blockQueue.size() >= 1) {
				this.drawBlock(canvas, blockQueue.get(0), rect1);
			}
			if (blockQueue.size() >= 2) {
				this.drawBlock(canvas, blockQueue.get(1), rect2);
			}
			if (blockQueue.size() >= 3) {
				this.drawBlock(canvas, blockQueue.get(2), rect3);
			}
		}
	}

	public void drawBlock(Canvas canvas, Block block, Rect rect) {
		Log.v(TAG, "draw block with rect: " + rect.left + "," + rect.top + ","
				+ rect.width() + "," + rect.height());
		int bs = Math.min(rect.width(), rect.height()) / 4;
		int startX = rect.left + (rect.width() - bs * 4) / 2;
		int startY = rect.top + (rect.height() - bs * 4) / 2;
		int row = block.rowCount();
		int col = block.columnCount();
		int fc = block.firstValidColumn();
		int fr = block.firstValidRow();
		Log.v(TAG, "row col fr fc: " + row + "," + col + "," + fr + "," + fc);
		startX += (4 - col) * bs / 2;
		startY += (4 - row) * bs / 2;
		Rect from = new Rect(0, 0, dm.sized_blocks[0].getWidth(),
				dm.sized_blocks[0].getHeight());
		Rect to = new Rect();
		dm.paint.reset();
		dm.paint.setAntiAlias(true);
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if (block.getMatrix()[fr + i][fc + j]) {
					to.left = j * bs + startX;
					to.top = i * bs + startY;
					to.right = to.left + bs;
					to.bottom = to.top + bs;
					canvas.drawBitmap(dm.sized_blocks[block.getBlockType()
							.ordinal()], from, to, dm.paint);
				}
			}
		}
	}

	public SavablePlayground getSavablePlayground() {
		SavablePlayground sp = new SavablePlayground();
		sp.playground = this.playground;
		sp.activeBlock = sp.create(this.activeBlock);
		sp.offsetX = this.blockOffsetX;
		sp.offsetY = this.blockOffsetY;
		sp.blockQueue = new LinkedList<SavableBlock>();
		for (Block b : this.blockQueue) {
			sp.blockQueue.add(sp.create(b));
		}
		sp.scoreLevel = this.scoreLevel;
		return sp;
	}

	public void restoreSavablePlayground(SavablePlayground sp) {
		this.playground = sp.playground;
		this.activeBlock = null;
		if (sp.activeBlock != null) {
			this.activeBlock = new Block(sp.activeBlock.blockType,
					sp.activeBlock.current);
		}
		this.blockOffsetX = sp.offsetX;
		this.blockOffsetY = sp.offsetY;
		this.blockQueue.clear();
		for (SavableBlock sb : sp.blockQueue) {
			this.blockQueue.add(new Block(sb.blockType, sb.current));
		}
		this.scoreLevel = sp.scoreLevel;
		this.calculateProjectedY();
	}

	public void draw(Canvas canvas, int x, int y) {
		dm.paint.setColor(Color.BLACK);
		// canvas.drawRect(new Rect(x, y, x + width, y + height), dm.paint);
		for (int row = 0; row < VERTICAL_BLOCKS; row++) {
			if (inAnimation && eliminating[row]
					&& (eliminatingCurrentStep % 2 == 0)) {
				continue;
			}
			for (int j = 0; j < HORIZONTAL_BLOCKS; j++) {
				int color = playground[row][j];
				if (color >= 0) {
					canvas.drawBitmap(dm.sized_blocks[color], x + j
							* (blockSize + GAP_LEN) + GAP_LEN, y + row
							* (blockSize + GAP_LEN) + GAP_LEN, dm.paint);
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
						// draw actual;
						if (xx >= 0 && xx < HORIZONTAL_BLOCKS && yy >= 0
								&& yy < VERTICAL_BLOCKS) {

							canvas
									.drawBitmap(dm.sized_blocks[activeBlock
											.getBlockType().ordinal()], x + xx
											* (blockSize + GAP_LEN) + GAP_LEN,
											y + yy * (blockSize + GAP_LEN)
													+ GAP_LEN, dm.paint);
						}
						// draw projected;
						yy = (projectedY + i);
						if (xx >= 0 && xx < HORIZONTAL_BLOCKS && yy >= 0
								&& yy < VERTICAL_BLOCKS) {
							if (blockOffsetY != projectedY) {
								dm.paint.setAlpha(80);
								canvas.drawBitmap(dm.sized_blocks[activeBlock
										.getBlockType().ordinal()], x + xx
										* (blockSize + GAP_LEN) + GAP_LEN, y
										+ (projectedY + i)
										* (blockSize + GAP_LEN) + GAP_LEN,
										dm.paint);
								dm.paint.setAlpha(255);
							}
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

	private boolean moveBlockDirectDown() {
		if (activeBlock == null) {
			return false;
		}
		if (projectedY != blockOffsetY) {
			blockOffsetY = projectedY;
		}
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
		calculateProjectedY();
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
		calculateProjectedY();
		return true;
	}

	private boolean turnBlock() {
		if (activeBlock == null) {
			return false;
		}
		int trials[] = new int[] { 0, -1, 1, -2, 2 };
		for (int offset : trials) {
			if (tryTurn(offset, activeBlock)) {
				activeBlock.turn();
				blockOffsetX += offset;
				calculateProjectedY();
				return true;
			}
		}
		return false;
	}

	private boolean tryTurn(int xOffset, Block block) {
		if (activeBlock == null) {
			return false;
		}
		boolean[][] matrix = activeBlock.getTurnedMatrix();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (matrix[i][j]) {
					int x = blockOffsetX + j + xOffset;
					int y = blockOffsetY + i;
					if (x < 0 || x >= HORIZONTAL_BLOCKS || y >= VERTICAL_BLOCKS) {
						return false;
					}
					if (y >= 0 && playground[y][x] >= 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private void allocateBlock() {
		this.activeBlock = blockQueue.poll();
		blockQueue.addLast(new Block());
		if (this.activeBlock == null) {
			return;
		}
		blockOffsetX = 3;
		blockOffsetY = -4;
		int emptyRows = 0;
		boolean[][] matrix = activeBlock.getMatrix();
		for (int i = 3; i >= 0; i--) {
			boolean empty = true;
			for (int j = 0; j < 4; j++) {
				if (matrix[i][j]) {
					empty = false;
					break;
				}
			}
			if (empty) {
				emptyRows++;
			} else {
				break;
			}
		}
		blockOffsetY = emptyRows - 4 + 1;
		calculateProjectedY();
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
		eliminationLines = 0;
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
				eliminationLines++;
			}
		}
		return inAnimation;
	}

	private boolean isFinishingElimination = false;

	public boolean isFinishingElimination() {
		return isFinishingElimination;
	}

	private void finishElimination() {
		isFinishingElimination = true;
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
		scoreLevel.eliminateLines(eliminationLines);
	}

	private boolean checkFinish() {
		if (originalOffsetY == blockOffsetY) {
			finished = true;
		}
		return finished;
	}

	private void calculateProjectedY() {
		if (activeBlock != null) {
			int Ybackup = this.blockOffsetY;
			while (moveBlockDown())
				;
			projectedY = blockOffsetY;
			blockOffsetY = Ybackup;
		}
	}

}
