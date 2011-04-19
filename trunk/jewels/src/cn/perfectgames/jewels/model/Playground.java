package cn.perfectgames.jewels.model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import cn.perfectgames.amaze.animation.AnimationCollection;
import cn.perfectgames.amaze.graphics.Layer;
import cn.perfectgames.amaze.graphics.Layers;
import cn.perfectgames.jewels.animation.EliminationAnimation;
import cn.perfectgames.jewels.animation.ScoreAnimation;
import cn.perfectgames.jewels.animation.SelectionAnimation;
import cn.perfectgames.jewels.animation.SwapAnimation;
import cn.perfectgames.jewels.cfg.Configuration;

public class Playground {
	public static final String TAG = "Playground";
	public static final int NUM_COLS = 8;
	public static final int NUM_ROWS = 8;

	private int cols = NUM_COLS;
	private int rows = NUM_ROWS;

	private Block[][] jewels;
	private boolean[][] visibilities;

	private Block tempJewel;

	private DrawingMetrics dm = new DrawingMetrics();
	private Animations animations = new Animations();

	private ScoreAndLevel scoreLevel = new ScoreAndLevel();

	public Playground() {
		jewels = new Block[rows][cols];
		visibilities = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				jewels[i][j] = new Block();
				visibilities[i][j] = false;
			}
		}
	}

	public void draw(Canvas canvas) {
		dm.draw(canvas);
		if (animations != null) {
			animations.getAnimationLayers().draw(canvas);
		}
	}

	public void swap(Position p1, Position p2) {
		if (!(checkPosition(p1) && checkPosition(p2))) {
			return;
		} else {
			// try swap
			tempJewel.copy(j(p1));
			j(p1).copy(j(p2));
			j(p2).copy(tempJewel);
			if (checkElimination(p1, p2)) {

			} else {
				// swap back
				tempJewel.copy(j(p1));
				j(p1).copy(j(p2));
				j(p2).copy(tempJewel);
			}
		}
	}

	public boolean isFinished() {
		return true;
		// TODO;
	}

	private boolean checkElimination(Position p1, Position p2) {
		LinkedList<Elimination> elis = new LinkedList<Elimination>();
		elis.addAll(checkElimination(p1));
		elis.addAll(checkElimination(p2));
		return !elis.isEmpty();
	}

	private List<Elimination> checkElimination(Position p) {
		List<Elimination> elis = new LinkedList<Elimination>();
		int start = 0;
		int end = 0;
		Block block = j(p);
		// horizontal
		start = p.col;
		end = p.col;
		while ((start - 1) >= 0
				&& j(p.row, start - 1).getType() == block.getType()) {
			start--;
		}

		while ((end + 1) < cols
				&& j(p.row, end + 1).getType() == block.getType()) {
			end++;
		}
		if (end - start >= 2) {
			elis.add(new Elimination(false, p.row, start, end));
		}
		// vertical
		start = p.row;
		end = p.row;
		while ((start - 1) >= 0
				&& j(start - 1, p.col).getType() == block.getType()) {
			start--;
		}

		while ((end + 1) < rows
				&& j(end + 1, p.col).getType() == block.getType()) {
			end++;
		}
		if (end - start >= 2) {
			elis.add(new Elimination(true, p.col, start, end));
		}

		// return ;
		return elis;
	}

	public void step() {
		animations.getAsCollection().step();
	}

	public void touch(float x, float y) {
		Log.v(TAG, "touch : " + x + "," + y);

		animations.selectionAnimation.setLocation(dm.pg.left, dm.pg.top);
		animations.selectionAnimation.start();
	}

	public ScoreAndLevel getScoreAndLevel() {
		return scoreLevel;
	}

	public DrawingMetrics getDM() {
		return dm;
	}

	public void configurationChanged(Configuration config) {

	}

	private Block j(Position p) {
		return jewels[p.row][p.col];
	}

	private Block j(int row, int col) {
		return jewels[row][col];
	}

	private boolean checkPosition(Position p) {
		return (p.row >= 0 && p.col >= 0 && p.row < rows && p.col < cols);
	}

	public class Elimination {
		public boolean vertical;
		public int position;
		public int start;
		public int end;

		public Elimination(boolean vertical, int position, int start, int end) {
			super();
			this.vertical = vertical;
			this.position = position;
			this.start = start;
			this.end = end;
		}
	}

	public class Animations {
		public ScoreAnimation scoreAnimation = new ScoreAnimation();
		public SwapAnimation swapAnimation = new SwapAnimation();
		public EliminationAnimation eliminationAnimation = new EliminationAnimation();
		public SelectionAnimation selectionAnimation = new SelectionAnimation();

		private AnimationCollection collection;

		private Layers animationLayers;

		public Animations() {
			animationLayers = new Layers();
			Layer layer = new Layer();
			layer.addDrawable(swapAnimation);
			layer.addDrawable(selectionAnimation);
			animationLayers.addLayer(layer, Layers.TOP);
			layer = new Layer();
			layer.addDrawable(eliminationAnimation);
			animationLayers.addLayer(layer, Layers.TOP);
			layer = new Layer();
			layer.addDrawable(scoreAnimation);
			animationLayers.addLayer(layer, Layers.TOP);
		}

		public Layers getAnimationLayers() {
			return animationLayers;
		}

		public AnimationCollection getAsCollection() {
			if (collection == null) {
				collection = new AnimationCollection();
				collection.addAnimation(scoreAnimation);
				collection.addAnimation(swapAnimation);
				collection.addAnimation(eliminationAnimation);
				collection.addAnimation(selectionAnimation);
			}
			return collection;
		}
	}

	public SavablePlayground getSavablePlayground() {
		return null;
	}

	public void restoreSavablePlayground(SavablePlayground sp) {

	}

	public class DrawingMetrics {
		// playground size
		private RectF pg = new RectF(0, 0, 1, 1);
		private float size = 0f;
		private float bsize = 0f;

		// drawing bitmaps
		public Bitmap[] original_blocks = new Bitmap[7];
		public Bitmap[] sized_blocks = new Bitmap[7];

		// paint
		private Paint paint;
		private String blockStyle = "animal";

		public void setRectF(RectF rect) {
			Log.v(TAG, "setting RectF: " + rect);
			size = Math.min(rect.width(), rect.height());
			pg = new RectF(rect.left + (rect.width() - size) / 2, rect.top
					+ (rect.height() - size) / 2, rect.left
					+ (rect.width() - size) / 2 + size, rect.top
					+ (rect.height() - size) / 2 + size);
			bsize = size / NUM_COLS;
			onSizeChanged(false);
		}

		public void draw(Canvas canvas) {
			Log.v(TAG, "playground rect: " + pg);
			Log.v(TAG, "playground bsize: " + bsize);
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					canvas.drawBitmap(sized_blocks[j(r, c).getType()], pg.left
							+ r * bsize, pg.top + c * bsize, paint);
				}
			}
		}

		public DrawingMetrics() {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(18);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
		}

		private Context context;

		public void init(Context context) {

			Log.v(TAG, "playground dm - init");
			this.context = context;
			// TODO blockStyle = Configuration.config().getBlockStyle();
			for (int i = 0; i < 7; i++) {
				try {

					original_blocks[i] = BitmapFactory.decodeStream(context
							.getAssets().open(
									"images/jewels/" + blockStyle + "/" + i
											+ ".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void onSizeChanged(boolean mustChange) {
			Log.v(TAG, "playground dm - on size changed");
			if (sized_blocks[0] == null || sized_blocks[0].getWidth() != bsize
					|| mustChange) {
				for (int i = 0; i < 7; i++) {

					Log.v(TAG, "playground dm - on size changed in for");
					if (original_blocks[i] != null) {

						Log
								.v(TAG,
										"playground dm - on size changed, creating image");
						sized_blocks[i] = Bitmap.createScaledBitmap(
								original_blocks[i], Math.round(bsize), Math
										.round(bsize), false);
					}
				}
			}
			animations.selectionAnimation.setSize(Math.round(bsize));
		}

		public void configurationChanged(Configuration config) {
			String bs = config.getBlockStyle();
			if (!bs.equals(blockStyle) && context != null) {
				blockStyle = bs;
				for (int i = 0; i < 7; i++) {
					try {
						original_blocks[i] = BitmapFactory.decodeStream(context
								.getAssets().open(
										"blocks/" + blockStyle + "/" + i
												+ ".png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				onSizeChanged(true);
			}

		}
	}

}
