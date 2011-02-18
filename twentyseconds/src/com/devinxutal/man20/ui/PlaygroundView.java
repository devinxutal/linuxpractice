package com.devinxutal.man20.ui;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.devinxutal.man20.control.GameController;
import com.devinxutal.man20.model.Attacker;
import com.devinxutal.man20.model.Defender;
import com.devinxutal.man20.model.Explosion;
import com.devinxutal.man20.model.Playground;
import com.devinxutal.man20.model.Star;

public class PlaygroundView extends SurfaceView implements
		SurfaceHolder.Callback {
	public static String TAG = "PlaygroundView";

	private AnimationThread animationThread = null;
	private GameController gameController = null;
	// for drawing
	// int offset = 0;
	private DrawingMetrics dm;

	private Playground playground;
	private Explosion explosion = null;

	/**
	 * Constructor. This version is only needed if you will be instantiating the
	 * object manually (not from a layout XML file).
	 * 
	 * @param context
	 */
	public PlaygroundView(Context context) {
		super(context);

		getHolder().addCallback(this);
		init();
	}

	public void setGameController(GameController controller) {
		this.gameController = controller;
	}

	public Playground getPlayground() {
		return playground;
	}

	public void setPlayground(Playground playground) {
		this.playground = playground;
	}

	public PlaygroundView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PlaygroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private final void init() {
		dm = new DrawingMetrics();

		setPadding(3, 10, 3, 10);

	}

	/**
	 * @see android.view.View#measure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	public void destroy() {
		this.animationThread.flag = false;
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) (-dm.paint.ascent() + dm.paint.descent())
					+ getPaddingTop() + getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private void repaint() {
		Canvas c = null;
		SurfaceHolder surfaceHolder = getHolder();
		try {
			c = surfaceHolder.lockCanvas();
			if (c != null) {
				paint(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				surfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) { // repaint();
		super.onSizeChanged(w, h, oldw, oldh);
		if (this.getPlayground() != null) {
			this.getPlayground().setSize(w, h);
		}
		this.getPlayground().reset();
		dm.resetDefender();
	}

	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
	}

	/**
	 * Render the text
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		paint(canvas);
	}

	public void reset() {
		explosion = null;
		playground.reset();
		finishedAlreadyNotified = false;
		finishingAlreadyNotified = false;
		notifyFinished = false;
	}

	private void paint(Canvas canvas) {
		dm.paint.setAlpha(255);
		canvas.drawRGB(0, 0, 0);

		if (playground != null) {
			Star[] stars = playground.getStars();
			int count = stars.length / dm.STAR_COLORS.length;
			int index = 0;
			dm.paint.setColor(dm.STAR_COLORS[0]);
			for (Star star : stars) {
				if (count-- < 0) {
					count = stars.length / dm.STAR_COLORS.length;
					index += 1;
					if (index >= dm.STAR_COLORS.length) {
						index = dm.STAR_COLORS.length - 1;
					}
					dm.paint.setColor(dm.STAR_COLORS[index]);
				}
				canvas.drawPoint(star.x, star.y, dm.paint);
			}

			dm.paint.setColor(DrawingMetrics.COLOR_ATTACKER);
			for (Attacker at : playground.getAttackers()) {
				canvas.drawCircle(at.x, at.y, DrawingMetrics.ATTACKER_RADIUS,
						dm.paint);
			}

			if (explosion != null) {
				if (explosion.step()) {
					explosion.draw(canvas);
				} else {
					if (!finishedAlreadyNotified) {
						notifyFinished = true;
						finishedAlreadyNotified = true;
					}
				}
			} else {
				Defender def = playground.getDefender();
				dm.paint.setColor(DrawingMetrics.COLOR_DEFENDER);
				// canvas.drawCircle(def.x, def.y, def.radius, dm.paint);
				canvas.drawBitmap(dm.defender, def.x - dm.defender.getWidth()
						/ 2, def.y - dm.defender.getHeight() / 2, dm.paint);
			}
		}

	}

	private boolean finishedAlreadyNotified = false;
	private boolean finishingAlreadyNotified = false;
	private boolean notifyFinished = false;

	public void pause(boolean pause) {
		animationThread.paused = pause;
	}

	public class AnimationThread extends Thread {
		boolean flag = true;
		boolean paused = false;

		public AnimationThread() {
			super();
		}

		@Override
		public void run() {
			while (flag) {
				try {
					if (playground != null) {
						if (!playground.isCrashed() && !paused) {
							playground.move(5);
							if (playground.isCrashed()) {
								if (!finishingAlreadyNotified
										&& gameController != null) {
									gameController
											.gameFinishingCalledByPlaygroundView();
									finishingAlreadyNotified = true;
								}
								Defender defender = playground.getDefender();

								explosion = new Explosion(defender.x,
										defender.y, defender.radius * 2);
								explosion.reset();
							}
						}
						repaint();
						if (notifyFinished && gameController != null) {
							gameController.gameFinishedCalledByPlaygroundView();
							notifyFinished = false;
						}
					}
					sleep(40);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public class DrawingMetrics {
		public static final float ATTACKER_RADIUS = 2;
		public static final int COLOR_DEFENDER = Color.BLUE;
		public static final int COLOR_ATTACKER = Color.YELLOW;

		public final int[] STAR_COLORS = new int[] { Color.rgb(150, 0, 50),
				Color.rgb(0, 150, 50), Color.rgb(150, 150, 0),
				Color.rgb(150, 0, 100), Color.rgb(0, 0, 150) };

		private Paint paint;

		private Bitmap defenderOriginal;
		private Bitmap defender;

		public DrawingMetrics() {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextSize(18);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			Activity activity = (Activity) getContext();
			try {
				defenderOriginal = BitmapFactory.decodeStream(activity
						.getAssets().open("spacecraft.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void resetDefender() {
			defender = Bitmap.createScaledBitmap(defenderOriginal,
					(int) playground.getDefender().getVisibleRadius() * 2,
					(int) playground.getDefender().getVisibleRadius() * 2,
					false);
		}
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// if (this.getPlayground() != null) {
		// this.playground.setSize(this.getWidth(), this.getHeight());
		//
		// this.getPlayground().reset();
		// }
	}

	public void surfaceCreated(SurfaceHolder arg0) {

		this.animationThread = new AnimationThread();

		if (this.getPlayground() != null) {
			this.getPlayground().reset();
		}
		this.animationThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		if (animationThread != null) {
			this.animationThread.flag = false;
		}
	}
}
