package cn.perfectgames.jewels.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;
import cn.perfectgames.amaze.animation.Animation;
import cn.perfectgames.amaze.animation.AnimationCollection;
import cn.perfectgames.amaze.animation.AnimationListener;
import cn.perfectgames.amaze.graphics.Layer;
import cn.perfectgames.amaze.graphics.Layers;
import cn.perfectgames.jewels.GoJewelsApplication;
import cn.perfectgames.jewels.animation.DripJewelsAnimation;
import cn.perfectgames.jewels.animation.EliminationAnimation;
import cn.perfectgames.jewels.animation.HintAnimation;
import cn.perfectgames.jewels.animation.JewelDropAnimation;
import cn.perfectgames.jewels.animation.RefillPlaygroundAnimation;
import cn.perfectgames.jewels.animation.ScoreAnimation;
import cn.perfectgames.jewels.animation.ScoreBoardAnimation;
import cn.perfectgames.jewels.animation.SelectionAnimation;
import cn.perfectgames.jewels.animation.SwapAnimation;
import cn.perfectgames.jewels.cfg.Configuration;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.sound.SoundManager;
import cn.perfectgames.jewels.util.BitmapUtil;

public class Playground {
	public static final String TAG = "Playground";
	public static final int NUM_COLS = 8;
	public static final int NUM_ROWS = 8;

	private int cols = NUM_COLS;
	private int rows = NUM_ROWS;

	private GameMode gameMode = GameMode.Normal;

	private Jewel[][] jewels;
	private boolean[][] visibilities;

	private Jewel tempJewel = new Jewel();

	private Position first = null;
	private Position second = null;

	private DrawingMetrics dm = new DrawingMetrics();
	private Animations animations = new Animations();

	private ScoreAndLevel scoreLevel = new ScoreAndLevel();

	private AnimationState animationState = AnimationState.IDLE;
	private State state = State.INIT;

	private List<Elimination> eliminations;

	private List<Pair<Position, Position>> swapables = new LinkedList<Pair<Position, Position>>();

	private int delayCount = 0;

	private SoundManager soundManager;

	private int HINT_DELAY = 0;

	private boolean inLevelupAnimation;

	public Playground(GameMode mode) {
		this.gameMode = mode;
		this.scoreLevel.setGameMode(mode);

		this.HINT_DELAY = Configuration.config().getHintDelay();

		jewels = new Jewel[rows][cols];
		visibilities = new boolean[rows][cols];

		this.animations = new Animations();

		// generate jewels, if the generated jewels is bad(cannot find
		// swappable), regenerate it.
		reset();
	}

	public void reset() {
		this.state = State.INIT;
		this.scoreLevel.reset();
		this.animations.reset();
		regenerateJewels();
		
		// control
		this.clearCommand();
	}
	public void play(){
		if(this.state == State.INIT || this.state == State.PAUSED){
			this.state = State.PLAYING;
		}
	}

	private void clearCommand(){
		this.touchX = -1
		;
		this.touchY =-1;
		this.flipX = 0;
		this.flipY = 0;
		
	}
	private void regenerateJewels() {
		do {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					jewels[i][j] = new Jewel();
					visibilities[i][j] = true;
				}
			}
			Log.v(TAG, "Check eliminaion on start");

			checkElimination();
			while (eliminations != null) {
				startElimination(false, false);
				finishElimination(false, false);
				checkElimination();
				setVisibilities(true);
			}

			findSwappable();
		} while (swapables.isEmpty());
		Log.v(TAG, "Finish Check elimination on start");
		Log.v(TAG, "Animation state after elimination : " + animationState);
	}

	public void setSoundManager(SoundManager manager) {
		this.soundManager = manager;
	}

	public void draw(Canvas canvas) {
		dm.draw(canvas);
		if (animations != null) {
			animations.getAnimationLayers().draw(canvas);
		}
	}

	public void swap(Position p1, Position p2) {
		this.delayCount = 0;
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

				// play sound
				soundManager.playIllegalSwapEffect();
			}
		}
	}

	public void refillPlayground(boolean animation, boolean mainRoutine) {
		if (animation && mainRoutine) {
			this.animations.refillAnimation.setOriginalJewels(this.jewels);
		}
		this.regenerateJewels();
		if (animation && mainRoutine) {
			this.animations.refillAnimation.setRefilledJewels(this.jewels);
			this.animationState = AnimationState.REFILLING;

			this.setVisibilities(false);
			this.animations.refillAnimation.start();
			this.soundManager.playRegenerateEffect();
		}
	}

	public GameMode getGameMode() {
		return this.gameMode;
	}

	public boolean isFinished() {
		return (this.state == State.FINISHED);
	}

	private boolean shouldFinish() {
		if (gameMode == GameMode.Infinite) {
			return false;
		} else if (gameMode == GameMode.Timed || gameMode == GameMode.Quick) {
			return scoreLevel.getTimeProgress() <= 0;
		} else if (gameMode == GameMode.Normal) {
			return this.swapables.isEmpty();
		}
		return true;
	}

	private void finishElimination(boolean animation, boolean mainRoutine) {
		Log.v(TAG, "finish elimination: " + animation);
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

	private void totallyFinishElimination(boolean animation, boolean mainRoutine) {
		setVisibilities(true);

		Log.v(TAG, "totally finish eliminattion, check level up");
		if (!this.inLevelupAnimation && scoreLevel.checkLevelUp()) {
			// level up;

			Log.v(TAG, "totally finish eliminattion, level up");
			this.startLevelUp();
		} else {
			if (this.inLevelupAnimation) {
				this.finishLevelUp();
			}
			checkElimination();
			if (this.state != State.FINISHING  && this.state != State.FINISHED){
				if (eliminations == null) { // no more eliminations
					animationState = AnimationState.IDLE;
					this.scoreLevel.resetCombo();
					this.delayCount = 0;
					if (animation) {
						animations.scoreBoardAnimation.setNewScore(scoreLevel
								.getScore());
						animations.scoreBoardAnimation.start();
					}
					// recalculate the swap hint;
					findSwappable();

					if (swapables.isEmpty()) { // no swappables, need to refill
						if (gameMode != GameMode.Normal) {
							refillPlayground(animation, mainRoutine);
						}
					}

				} else {
					if (animation) {
						animationState = AnimationState.ELIMINATING;
						startElimination(animation, mainRoutine);
					}
				}
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

	public void startElimination(boolean animation, boolean mainRoutine) {

		Log.v(TAG, "start elimination: " + animation + ", " + mainRoutine);
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

				// calculate score
				if (animation && mainRoutine) {
					int score = this.scoreLevel.addScore(e);
					PointF p1 = null, p2 = null;
					if (e.vertical) {
						p1 = mapToPoint(e.start, e.position);
						p2 = mapToPoint(e.end, e.position);
					} else {
						p1 = mapToPoint(e.position, e.start);
						p2 = mapToPoint(e.position, e.end);
					}
					animations.scoreAnimation.addScore(score + "", new PointF(
							(p1.x + p2.x + dm.bsize) / 2,
							(p2.y + p2.y + dm.bsize) / 2),
							dm.score_colors[(score / 10)
									% dm.score_colors.length]);
				}
			}

		}

		if (animation) {
			List<Jewel> jewels = new LinkedList<Jewel>();
			List<PointF> pos = new LinkedList<PointF>();
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					if (!visibilities[r][c]) {
						jewels.add(j(r, c));
						pos.add(mapToPoint(r, c));
					}
				}
			}
			if(this.inLevelupAnimation){
				Log.v(TAG, "Elimination Animation for level up");
				animations.eliminationAnimation.setJewels(jewels, pos, EliminationAnimation.Usage.Levelup);
			}else{

				Log.v(TAG, "Elimination Animation for normal");
				animations.eliminationAnimation.setJewels(jewels, pos, EliminationAnimation.Usage.Elimination);
			}
			animations.eliminationAnimation.start();
			if (mainRoutine) {
				animations.scoreAnimation.start();
			}

			// play sound
			int firstEliminationType = 0;
			Elimination e = eliminations.get(0);
			if (e.vertical) {
				firstEliminationType = j(e.start, e.position).getType();
			} else {
				firstEliminationType = j(e.position, e.start).getType();
			}
			this.soundManager.playEliminationEffect(firstEliminationType);
		}
	}

	private void playFinishingAnimation() {
		this.setVisibilities(false);

		List<Jewel> jewels = new LinkedList<Jewel>();
		List<PointF> pos = new LinkedList<PointF>();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				jewels.add(j(r, c));
				pos.add(mapToPoint(r, c));
			}
		}
		
		{ // reset all animations before play drip animation
			animations.swapAnimation.reset();
			animations.jewelDropAnimation.reset();
			animations.eliminationAnimation.reset();
		}
		animations.dripJewelsAnimation.setJewels(jewels, pos);
		animations.dripJewelsAnimation.start();
		
		// play sound
		this.soundManager.playFinishEffect();
		//this.soundManager.playEliminationEffect(firstEliminationType);

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

	private void startLevelUp() {
		Log.v(TAG, "start level up");
		this.inLevelupAnimation = true;
		this.eliminations.clear();
		int eliminateType1 = new Jewel().getType();
		int eliminateType2 = (eliminateType1+4)%7;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (j(r, c).getType() == eliminateType1 ||j(r,c).getType() == eliminateType2) {
					eliminations.add(new Elimination(false, r, c, c));

				}
			}
		}

		animationState = AnimationState.ELIMINATING;
		this.startElimination(true, false);
		soundManager.playLevelUpffect();
	}

	private void finishLevelUp() {
		this.inLevelupAnimation = false;
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

	private void findSwappable() {
		this.swapables.clear();
		for (int c = 0; c < NUM_COLS; c++) {
			for (int r = 1; r < NUM_ROWS; r++) {
				if (j(r, c).getType() == j(r - 1, c).getType()) {
					findSwappable(swapables, new Position(r - 2, c), j(r, c)
							.getType(), 2); // up

					findSwappable(swapables, new Position(r + 1, c), j(r, c)
							.getType(), 3); // down
				} else if (r > 1 && j(r, c).getType() == j(r - 2, c).getType()) {
					findSwappable(swapables, new Position(r - 1, c), j(r, c)
							.getType(), 5); // ignore vertical
				}
			}

		}

		for (int r = 0; r < NUM_ROWS; r++) {
			for (int c = 1; c < NUM_COLS; c++) {
				if (j(r, c).getType() == j(r, c - 1).getType()) {
					findSwappable(swapables, new Position(r, c - 2), j(r, c)
							.getType(), 0);// up
					findSwappable(swapables, new Position(r, c + 1), j(r, c)
							.getType(), 1); // right

				} else if (c > 1 && j(r, c).getType() == j(r, c - 2).getType()) {
					findSwappable(swapables, new Position(r, c - 1), j(r, c)
							.getType(), 4); // ignore horizontal
				}
			}
		}
		return;
	}

	/**
	 * @param swapables
	 * @param p
	 * @param type
	 * @param ignore_side
	 *            0: right, 1: left, 2: down, 3: up, 4: horizontal, 5: vertical
	 */
	private void findSwappable(List<Pair<Position, Position>> swapables,
			Position p, int type, int ignore_side) {
		if (!checkPosition(p)) {
			return;
		}
		int offsets[][] = new int[][] { { 0, 1 }, { 0, -1 }, { 1, 0 },
				{ -1, 0 } };
		Position pp = new Position(p.row, p.col);
		for (int i = 0; i < offsets.length; i++) {
			if (i == ignore_side) {
				continue;
			}
			if (ignore_side == 4 && (i == 0 || i == 1)) {
				continue;
			}
			if (ignore_side == 5 && (i == 2 || i == 3)) {
				continue;
			}
			pp.row = p.row + offsets[i][0];
			pp.col = p.col + offsets[i][1];
			if (checkPosition(pp)) {
				Jewel j = j(pp);
				if (j != null && j.getType() == type) {
					swapables.add(new Pair<Position, Position>(new Position(
							p.row, p.col), new Position(pp.row, pp.col)));
				}
			}
		}
		return;
	}

	public void step() {
		if (this.state == State.PLAYING) {
			checkTouch();
			checkFlip();
		}
		if(this.state != State.INIT && this.state != State.FINISHING && this.state != State.FINISHED){
			scoreLevel.stepTime(1000 / Constants.FPS);
		}
		animations.getAsCollection().step();

		delayCount += 1000 / Constants.FPS;
		if (HINT_DELAY > 0 && delayCount > HINT_DELAY && swapables.size() > 0) {
			Random r = new Random();
			animations.hintAnimation.setLocation(this.mapToPoint(swapables
					.get(r.nextInt(swapables.size())).first));
			animations.hintAnimation.start();
			delayCount = 0;
		}
		
		if(this.state == State.PLAYING && this.shouldFinish()){
			this.state = State.FINISHING;
			this.playFinishingAnimation();
		}
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
		this.flipX = (int) dx;
		this.flipY = (int) dy;

	}

	private void checkTouch() {

		if (touchX >= 0 && touchY >= 0) {
			doTouch(touchX, touchY);
			touchX = -1;
			touchY = -1;
		}
	}

	private void checkFlip() {
		if (flipX != 0 || flipY != 0) {
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
		if (animationState != AnimationState.IDLE || first == null
				|| second != null) {
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
			if (flipped) {
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

	public Animations getAnimations() {
		return animations;
	}

	public void configurationChanged(Configuration config) {
		dm.configurationChanged(config);
		this.HINT_DELAY = config.getHintDelay();
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
		public ScoreAnimation scoreAnimation;
		public SwapAnimation swapAnimation;
		public EliminationAnimation eliminationAnimation;
		public DripJewelsAnimation dripJewelsAnimation;
		public SelectionAnimation selectionAnimation;
		public HintAnimation hintAnimation;
		public JewelDropAnimation jewelDropAnimation;
		public ScoreBoardAnimation scoreBoardAnimation;
		public RefillPlaygroundAnimation refillAnimation;

		private AnimationCollection collection;

		private Layers animationLayers;

		public Animations() {

			scoreAnimation = new ScoreAnimation(20);
			swapAnimation = new SwapAnimation();
			eliminationAnimation = new EliminationAnimation();
			dripJewelsAnimation = new DripJewelsAnimation();
			selectionAnimation = new SelectionAnimation();
			hintAnimation = new HintAnimation();
			jewelDropAnimation = new JewelDropAnimation();
			scoreBoardAnimation = new ScoreBoardAnimation(7);
			refillAnimation = new RefillPlaygroundAnimation(rows, cols);

			// //////////////////
			animationLayers = new Layers();

			Layer layer = new Layer();
			layer.addDrawable(swapAnimation);
			layer.addDrawable(selectionAnimation);
			layer.addDrawable(jewelDropAnimation);
			layer.addDrawable(scoreBoardAnimation);
			layer.addDrawable(hintAnimation);
			animationLayers.addLayer(layer, Layers.TOP);

			layer = new Layer();
			layer.addDrawable(eliminationAnimation);
			layer.addDrawable(dripJewelsAnimation);
			layer.addDrawable(refillAnimation);
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
				collection.addAnimation(dripJewelsAnimation);
				collection.addAnimation(selectionAnimation);
				collection.addAnimation(hintAnimation);
				collection.addAnimation(jewelDropAnimation);
				collection.addAnimation(scoreBoardAnimation);
				collection.addAnimation(refillAnimation);
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
					startElimination(true, true);
				} else {
					animationState = AnimationState.IDLE;
				}
			} else if (animation == jewelDropAnimation
					&& animationState == AnimationState.ELIMINATION_DROPPING) {

				// play sound
				soundManager.playDropEffect();
				
				totallyFinishElimination(true, true);
			} else if (animation == refillAnimation
					&& animationState == AnimationState.REFILLING) {
				animationState = AnimationState.IDLE;
				setVisibilities(true);
			} else if (animation == dripJewelsAnimation
					&& state == State.FINISHING) {
				state = State.FINISHED;
			}
		}

		public void animationEventHappened(Animation animation, int eventID) {
			Log.v(TAG,
					"animation Event Happened, animation:"
							+ animation.getClass() + ", eventID:" + eventID
							+ ", animationState:" + animationState);
			if (((animation == eliminationAnimation && eventID == EliminationAnimation.EVENT_HALF_PASSED) || (animation == dripJewelsAnimation && eventID == DripJewelsAnimation.EVENT_HALF_PASSED))
					&& animationState == AnimationState.ELIMINATING) {
				Log.v(TAG, "eliminationAnimationHalfPassed");
				animationState = AnimationState.ELIMINATION_DROPPING;
				if (inLevelupAnimation) {
					finishElimination(true, false);
				} else {
					finishElimination(true, true);
				}
			}
		}
		public void reset(){
			this.scoreBoardAnimation.setNewScore(0);
		}
	}

	public SavablePlayground getSavablePlayground() {
		SavablePlayground pg = new SavablePlayground();
		pg.playground = new Jewel[rows][cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				pg.playground[r][c] = new Jewel();
				pg.playground[r][c].copy(jewels[r][c]);
			}
		}
		pg.scoreLevel = this.scoreLevel;
		return pg;
	}

	public void restoreSavablePlayground(SavablePlayground sp) {
		if (sp != null) {
			if (sp.playground != null) {
				for (int r = 0; r < rows; r++) {
					for (int c = 0; c < cols; c++) {
						jewels[r][c].copy(sp.playground[r][c]);
					}
				}
			}
			if (sp.scoreLevel != null) {
				this.scoreLevel = sp.scoreLevel;
				this.animations.scoreBoardAnimation.setNewScore(scoreLevel.getScore());
			}
		}
	}

	public class DrawingMetrics {
		// playground size
		private RectF pg = new RectF(0, 0, 1, 1);
		private float size = 0f;
		private float bsize = 0f;

		// drawing bitmaps
		public Bitmap[] sized_blocks = new Bitmap[7];

		// paint
		private Paint paint;
		private String jewelStyle = "jewels";

		// score color
		private int score_colors[] = new int[] { Color.rgb(255, 255, 255),// white
				Color.rgb(255, 255, 255),// white
				Color.rgb(255, 200, 150),// orange
				Color.rgb(255, 200, 150),// orange
				Color.rgb(200, 0, 0),// red
				Color.rgb(200, 0, 0),// red
				Color.rgb(255, 150, 255),// purple
				Color.rgb(255, 150, 255),// purple
				Color.rgb(0, 200, 0),// green
				Color.rgb(0, 200, 0),// green
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
			if(state != State.INIT){
				for (int r = 0; r < rows; r++) {
					for (int c = 0; c < cols; c++) {
						if (visibilities[r][c]) {
							canvas.drawBitmap(sized_blocks[j(r, c).getType()],
									pg.left + c * bsize, pg.top + r * bsize, paint);
						}
					}
				}
			}
		}

		public void drawPlayground(Canvas canvas) {

			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					if ((r + c) % 2 == 0) {
						paint.setAlpha(180);
					} else {
						paint.setAlpha(100);
					}
					PointF p = mapToPoint(r, c);
					canvas.drawRect(new RectF(p.x, p.y, p.x + bsize, p.y
							+ bsize), paint);
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
			jewelStyle = Configuration.config().getJewelStyle();
		}

		public void onSizeChanged(boolean mustChange) {

			reloadJewelBitmap(Configuration.config().getJewelStyle(),
					mustChange);
			animations.selectionAnimation.setSize(Math.round(bsize));
			animations.hintAnimation.setSize(Math.round(bsize));
			animations.swapAnimation.setJewelBitmap(sized_blocks);
			animations.jewelDropAnimation.setJewelBitmap(sized_blocks);
			animations.jewelDropAnimation.setUpperBound(pg.top);
			animations.eliminationAnimation.setJewelBitmap(sized_blocks);
			animations.dripJewelsAnimation.setJewelBitmap(sized_blocks);
			animations.scoreAnimation.setJewelSize(Math.round(bsize));
			animations.refillAnimation.setSize(Math.round(bsize));
			animations.refillAnimation.setJewelBitmap(sized_blocks);
			animations.refillAnimation.setTopLeftPosition(new PointF(pg.left,
					pg.top));
		}

		public void configurationChanged(Configuration config) {
			String bs = config.getJewelStyle();
			reloadJewelBitmap(bs, false);

		}

		private void reloadJewelBitmap(String jewelStyle, boolean mustReload) {
			if (this.sized_blocks[0] == null || jewelStyle != this.jewelStyle
					|| Math.round(bsize) != sized_blocks[0].getWidth()
					|| mustReload) {
				this.jewelStyle = jewelStyle;
				Log.v(TAG, "reloading bitmap , bitmap size: " + bsize);
				for (int i = 0; i < 7; i++) {
					try {
						if (sized_blocks[i] != null) {
							sized_blocks[i].recycle();
						}
						sized_blocks[i] = GoJewelsApplication.getBitmapUtil()
								.getBitmapOfPreferedSize(
										"images/jewels/" + jewelStyle, i + "",
										"png", Math.round(bsize));
						if (sized_blocks[i] == null) {
							throw new Exception("Bitmap is null");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public enum State {
		INIT, PLAYING, PLAYING_ANIMATING, PAUSED, FINISHING, FINISHED,
	}

	public enum AnimationState {
		IDLE, SWAPPING, ELIMINATING, BLASTING, REFILLING, ELIMINATION_DROPPING,

	}

}
