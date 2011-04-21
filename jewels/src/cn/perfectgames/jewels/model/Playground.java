package cn.perfectgames.jewels.model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import cn.perfectgames.amaze.animation.Animation;
import cn.perfectgames.amaze.animation.AnimationCollection;
import cn.perfectgames.amaze.animation.AnimationListener;
import cn.perfectgames.amaze.graphics.Layer;
import cn.perfectgames.amaze.graphics.Layers;
import cn.perfectgames.jewels.animation.EliminationAnimation;
import cn.perfectgames.jewels.animation.JewelDropAnimation;
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

	private Jewel[][] jewels;
	private boolean[][] visibilities;

	private Jewel tempJewel = new Jewel();

	private Position first = null;
	private Position second = null;

	private DrawingMetrics dm = new DrawingMetrics();
	private Animations animations = new Animations();

	private ScoreAndLevel scoreLevel = new ScoreAndLevel();

	private AnimationState animationState = AnimationState.IDLE;

	private List<Elimination> eliminations;
	private int eliminationCombo = 0;;
	
	public Playground() {
		jewels = new Jewel[rows][cols];
		visibilities = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				jewels[i][j] = new Jewel();
				visibilities[i][j] = true;
			}
		}
		checkElimination();
		if (eliminations != null) {
			startElimination(false);
			finishElimination(false);
			totallyFinishElimination(false);
		}
	}

	public void draw(Canvas canvas) {
		dm.draw(canvas);
		if (animations != null) {
			animations.getAnimationLayers().draw(canvas);
		}
	}

	public void swap(Position p1, Position p2) {
		if (animationState != AnimationState.IDLE) {
			return;
		}

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

	private void finishElimination(boolean animation) {
		LinkedList<Jewel> jewels = new LinkedList<Jewel>();
		LinkedList<PointF> initPos = new LinkedList<PointF>();
		LinkedList<PointF> destPos = new LinkedList<PointF>();
		for (int c = 0; c < cols; c++) {
			int destIndex = rows - 1;
			boolean firstEliminationOccurred = false;
			for (int r = destIndex = rows - 1; r >= 0; r--, destIndex--) {
				if (visibilities[r][c]) {
					if (firstEliminationOccurred) {
						j(destIndex, c).copy(j(r, c));
						if (animation) {
							jewels.add(j(destIndex, c));
							initPos.add(mapToPoint(r, c));
							destPos.add(mapToPoint(destIndex, c));
						}
						if (firstEliminationOccurred) {
							visibilities[r][c] = false;
						}
					}
				} else {
					firstEliminationOccurred = true;
					destIndex++;
				}
			}
			float dropLen = (destIndex + 1) * dm.bsize;
			while (destIndex >= 0) {
				j(destIndex, c).copy(new Jewel());
				if (animation) {
					jewels.add(j(destIndex, c));
					PointF point = mapToPoint(destIndex, c);

					initPos.add(new PointF(point.x, point.y - dropLen));
					destPos.add(point);
				}
				destIndex--;
			}
		}

		if (animation) {
			animations.jewelDropAnimation.setDropJewels(jewels, initPos,
					destPos);
			animations.jewelDropAnimation.start();
		}

	}

	private void totallyFinishElimination(boolean animation) {
		setVisibilities(true);
		checkElimination();
		if (eliminations == null) {
			animationState = AnimationState.IDLE;
			eliminationCombo = 0;
		} else {
			animationState = AnimationState.ELIMINATING;
			if(animation){
				startElimination(animation);
			}
		}
	}

	private void setVisibilities(boolean v) {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				visibilities[r][c] = v;
			}
		}
	}

	public void startElimination(boolean animation) {
		Log.v(TAG, "startElimination, animation="+animation);
		animations.scoreAnimation.clearScores();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				visibilities[r][c] = true;
			}
		}
		if (eliminations != null) {
			for (Elimination e : eliminations) {
				if (e.vertical) {
					for (int i = e.start; i <= e.end; i++) {
						visibilities[i][e.position] = false;
						
					}
				} else {
					for (int i = e.start; i <= e.end; i++) {
						visibilities[e.position][i] = false;
					}
				}
				
				//calculate score
				eliminationCombo ++;
				int score = this.scoreLevel.addScore(e, eliminationCombo);
				if(animation){
					PointF p1 = null, p2  = null;
					if(e.vertical){
						p1 = mapToPoint(e.start, e.position);
						p2 = mapToPoint(e.end, e.position);
					}else{
						p1 = mapToPoint(e.position, e.start);
						p2 = mapToPoint(e.position, e.end);
					}
					animations.scoreAnimation.addScore(score+"", new PointF((p1.x+p2.x +dm.bsize)/2, (p2.y + p2.y+dm.bsize)/2), dm.score_colors[(score/10) % dm.score_colors.length]);
				}
			}
		}

		if (animation) {
			List<Jewel> jewels = new LinkedList<Jewel>();
			List<PointF> pos = new LinkedList<PointF>();
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					if(!visibilities[r][c]){
						jewels.add(j(r,c));
						pos.add(mapToPoint(r,c));
					}
				}
			}
			animations.eliminationAnimation.setJewels(jewels,pos);
			animations.eliminationAnimation.start();
			
			animations.scoreAnimation.start();
		}
	}

	private boolean checkElimination() {
		LinkedList<Elimination> elis = new LinkedList<Elimination>();

		for (int i = 0; i < NUM_COLS; i++) {
			int start = 0;
			for (int j = 1; j < NUM_ROWS; j++) {
				if (j(j, i).getType() != j(j - 1, i).getType()) {
					int len = j - start;
					if (len >= 3) {
						elis.add(new Elimination(true, i, start, j - 1));
					}
					start = j;
				}
			}
			if (NUM_COLS - start >= 3) {
				elis.add(new Elimination(true, i, start, NUM_COLS - 1));
			}
		}

		for (int i = 0; i < NUM_ROWS; i++) {
			int start = 0;
			for (int j = 1; j < NUM_COLS; j++) {
				if (j(i, j).getType() != j(i, j - 1).getType()) {
					int len = j - start;
					if (len >= 3) {
						elis.add(new Elimination(false, i, start, j - 1));
					}
					start = j;
				}
			}
			if (NUM_COLS - start >= 3) {
				elis.add(new Elimination(false, i, start, NUM_ROWS - 1));
			}
		}
		if (elis.isEmpty()) {
			eliminations = null;
			return false;
		} else {
			eliminations = elis;
			return true;
		}
	}

	private boolean checkElimination(Position p1, Position p2) {
		LinkedList<Elimination> elis = new LinkedList<Elimination>();
		elis.addAll(checkElimination(p1));
		elis.addAll(checkElimination(p2));

		if (elis.isEmpty()) {
			eliminations = null;
			return false;
		} else {
			eliminations = elis;
			return true;
		}
	}

	private List<Elimination> checkElimination(Position p) {
		List<Elimination> elis = new LinkedList<Elimination>();
		int start = 0;
		int end = 0;
		Jewel block = j(p);
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

	private void startSwap() {
		Log.v(TAG, "startSwap");
		swap(first, second);
		animationState = AnimationState.SWAPPING;

		visibilities[first.row][first.col] = false;
		visibilities[second.row][second.col] = false;
		animations.selectionAnimation.stop();
		animations.swapAnimation.stop();
		if (eliminations != null) {
			animations.swapAnimation.setSwap(j(second), mapToPoint(first),
					j(first), mapToPoint(second), false);
		} else {
			animations.swapAnimation.setSwap(j(first), mapToPoint(first),
					j(second), mapToPoint(second), true);
		}
		animations.swapAnimation.start();

	}

	private void finishSwap() {
		Log.v(TAG, "finishSwap");
		visibilities[first.row][first.col] = true;
		visibilities[second.row][second.col] = true;
		first = null;
		second = null;
	}

	public void step() {
		checkTouch();
		checkFlip();
		animations.getAsCollection().step();
	}
	
	
	private float touchX = -1;
	private float touchY = -1;
	private int flipX = 0;
	private int flipY = 0;
	
	public void touch(float x, float y) {
		this.touchX = x;
		this.touchY = y;
	}

	public void flip(float dx, float dy) {
		this.flipX = (int)dx;
		this.flipY = (int)dy;
	}
	
	private void checkTouch(){
		if(touchX >=0 && touchY>=0){
			doTouch(touchX, touchY);
			touchX = -1;
			touchY = -1;
		}
	}
	private void checkFlip(){
		if(flipX !=0 || flipY != 0){
			doFlip(flipX, flipY);
			flipX = 0;
			flipY = 0;
		}
	}

	private void doTouch(float x, float y) {
		if (animationState != AnimationState.IDLE) {
			return;
		}
		Position p = map(x, y);
		if (p != null) {
			if (first == null) {
				first = p;
				second = null;
			} else if (p.conjuncted(first)) {
				second = p;
				startSwap();
			} else {
				first = p;
				second = null;
			}
			if (first != null && second == null) {

				animations.selectionAnimation.setLocation(mapToPoint(first));
				animations.selectionAnimation.start();
			}
		}
	}

	private void doFlip(float dx, float dy) {
		if (animationState != AnimationState.IDLE || first == null || second!= null) {
			return;
		}
		Position p = null;
		boolean flipped = false;
		if (Math.abs(dx) > dm.bsize / 2 || Math.abs(dy) > dm.bsize / 2) {
			flipped = true;
			if (Math.abs(dx) > Math.abs(dy)) {
				if (dx < 0) {
					p = new Position(first.row, first.col - 1);
				} else {
					p = new Position(first.row, first.col + 1);
				}
			} else {
				if (dy < 0) {
					p = new Position(first.row - 1, first.col);
				} else {
					p = new Position(first.row + 1, first.col);
				}
			}
		}
		if (p != null && checkPosition(p)) {
			second = p;
			startSwap();
		} else {
			if(flipped){
				animations.selectionAnimation.stop();
				first = second = null;
			}
		}
	}

	public ScoreAndLevel getScoreAndLevel() {
		return scoreLevel;
	}

	public DrawingMetrics getDM() {
		return dm;
	}

	public void configurationChanged(Configuration config) {

	}

	private Position map(float x, float y) {
		int r = (int) ((y - dm.pg.top) / dm.bsize);
		int c = (int) ((x - dm.pg.left) / dm.bsize);
		Position p = new Position(r, c);
		if (checkPosition(p)) {
			return p;
		} else {
			return null;
		}
	}

	private PointF mapToPoint(int row, int col) {
		return new PointF(dm.pg.left + col * dm.bsize, dm.pg.top + row
				* dm.bsize);
	}

	private PointF mapToPoint(Position p) {
		return new PointF(dm.pg.left + p.col * dm.bsize, dm.pg.top + p.row
				* dm.bsize);
	}

	private Jewel j(Position p) {
		return jewels[p.row][p.col];
	}

	private Jewel j(int row, int col) {
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

	public class Animations implements AnimationListener {
		public ScoreAnimation scoreAnimation = new ScoreAnimation(20);
		public SwapAnimation swapAnimation = new SwapAnimation();
		public EliminationAnimation eliminationAnimation = new EliminationAnimation();
		public SelectionAnimation selectionAnimation = new SelectionAnimation();
		public JewelDropAnimation jewelDropAnimation = new JewelDropAnimation();

		private AnimationCollection collection;

		private Layers animationLayers;

		public Animations() {
			
			
			animationLayers = new Layers();
			
			
			Layer layer = new Layer();
			layer.addDrawable(swapAnimation);
			layer.addDrawable(selectionAnimation);
			layer.addDrawable(jewelDropAnimation);
			animationLayers.addLayer(layer, Layers.TOP);
			
			
			layer = new Layer();
			layer.addDrawable(eliminationAnimation);
			animationLayers.addLayer(layer, Layers.TOP);
			
			
			layer = new Layer();
			layer.addDrawable(scoreAnimation);
			animationLayers.addLayer(layer, Layers.TOP);

			getAsCollection().addAnimationListener(this);
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
				collection.addAnimation(jewelDropAnimation);
			}
			return collection;
		}

		public void animationFinished(Animation animation) {
			Log.v(TAG, "animation finished: " + animation.getClass().getName());
			if (animation == swapAnimation
					&& animationState == AnimationState.SWAPPING) {
				finishSwap();
				if (eliminations != null) {
					animationState = AnimationState.ELIMINATING;
					startElimination(true);
				} else {
					animationState = AnimationState.IDLE;
				}
			} else if (animation == jewelDropAnimation
					&& animationState == AnimationState.ELIMINATION_DROPPING) {
				totallyFinishElimination(true);
			}
		}

		public void animationEventHappened(Animation animation, int eventID) {
			if(animation == eliminationAnimation && eventID == EliminationAnimation.EVENT_HALF_PASSED && animationState == AnimationState.ELIMINATING)
			animationState = AnimationState.ELIMINATION_DROPPING;
			finishElimination(true);
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
		
		//score color
		private int score_colors[] = new int[]{
				Color.rgb(255, 255, 255),//white
				Color.rgb(255, 255, 255),//white
				Color.rgb(255, 200, 150),//orange
				Color.rgb(255, 200, 150),//orange
				Color.rgb(200, 0, 0),//red
				Color.rgb(200, 0, 0),//red
				Color.rgb(255, 150, 255),//purple
				Color.rgb(255, 150, 255),//purple
				Color.rgb(0, 200, 0),//green
				Color.rgb(0, 200, 0),//green
		};

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
			paint.setAlpha(255);
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					if (visibilities[r][c]) {
						canvas.drawBitmap(sized_blocks[j(r, c).getType()],
								pg.left + c * bsize, pg.top + r * bsize, paint);
					}
				}
			}
		}
		
		public void drawPlayground(Canvas canvas){

			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			for(int r = 0; r<rows; r++)
			{
				for(int c = 0; c<cols; c++){
					if((r+c)%2 == 0){
						paint.setAlpha(160);
					}else{
						paint.setAlpha(80);
					}
					PointF p = mapToPoint(r, c);
					canvas.drawRect(new RectF(p.x, p.y, p.x+bsize, p.y+bsize), paint);
				}
			}
			paint.setAlpha(255);
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

						Log.v(TAG,
								"playground dm - on size changed, creating image");
						sized_blocks[i] = Bitmap.createScaledBitmap(
								original_blocks[i], Math.round(bsize),
								Math.round(bsize), false);
					}
				}
			}
			animations.selectionAnimation.setSize(Math.round(bsize));
			animations.swapAnimation.setJewelBitmap(sized_blocks);
			animations.jewelDropAnimation.setJewelBitmap(sized_blocks);
			animations.jewelDropAnimation.setUpperBound(pg.top);
			animations.eliminationAnimation.setJewelBitmap(sized_blocks);
			animations.scoreAnimation.setJewelSize(Math.round(bsize));

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

	public enum State {
		INIT, PLAYING, PAUSED, FINISHED,
	}

	public enum AnimationState {
		IDLE, SWAPPING, ELIMINATING, BLASTING, REFILLING, ELIMINATION_DROPPING,

	}

}
