package com.devinxutal.fc.control;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.devinxutal.fc.cfg.Configuration;
import com.devinxutal.fc.model.CubeAnimationInfo;
import com.devinxutal.fc.model.MagicCube;
import com.devinxutal.fc.primitives.Point3I;
import com.devinxutal.fc.ui.CubeView;

public class CubeController {
	public interface CubeListener {
		public void cubeSolved();
	}

	private CubeView cubeView;
	private MagicCube magicCube;

	private boolean inAnimation = false;
	private boolean turnable = true;
	private boolean rotatable = true;
	private boolean solved = true;

	private int stepCount = 0;

	private List<AnimationListener> animationListeners = new LinkedList<AnimationListener>();
	private List<CubeListener> cubeListeners = new LinkedList<CubeListener>();

	private Handler handler;
	private AnimationThread animationThread;

	public CubeController(Context context) {
		this(context, true, true);
	}

	public CubeController(Context context, boolean turnable, boolean rotatable) {
		this(context, 3, turnable, rotatable);
	}

	public CubeController(Context context, int order, boolean turnable,
			boolean rotatable) {
		this.cubeView = new CubeView(context);
		this.magicCube = new MagicCube(order);
		this.cubeView.setMagicCube(magicCube);
		this.magicCube.view = cubeView;
		this.cubeView.setOnTouchListener(new OnCubeViewTouched());
		this.turnable = turnable;
		this.rotatable = rotatable;
		this.handler = new android.os.Handler();
	}

	public boolean isTurnable() {
		return turnable;
	}

	public void setTurnable(boolean turnable) {
		this.turnable = turnable;
	}

	public boolean isRotatable() {
		return rotatable;
	}

	public void setRotatable(boolean rotatable) {
		this.rotatable = rotatable;
	}

	public boolean isInAnimation() {
		return inAnimation;
	}

	public CubeView getCubeView() {
		return this.cubeView;
	}

	public MagicCube getMagicCube() {
		return this.magicCube;
	}

	public boolean turnByMove(Move move) {
		boolean succeed = this.magicCube.turn(move.dimension, move.layers,
				move.direction, move.doubleTurn);
		if (succeed) {
			startAnimation();
			checkSolved();
		}
		return succeed;
	}

	public boolean turnByGesture(int i, int j, int k, int dx, int dy) {
		boolean succeed = this.magicCube.turnByGesture(i, j, k, dx, dy);
		if (succeed) {
			startAnimation();
			checkSolved();
		} else {
			// t.setText("turn not succeeded");
			// t.show();
		}
		return succeed;
	}

	public boolean turnBySymbol(String symbol) {
		boolean succeed = this.magicCube.turnBySymbol(symbol);
		if (succeed) {
			startAnimation();
			checkSolved();
		} else {
			// t.setText("turn not succeeded");
			// t.show();
		}
		return succeed;
	}

	public void rotate(int dimension, int direction) {
		boolean succeed = this.magicCube.rotate(dimension, direction);
		if (succeed) {
			startAnimation();
		} else {
			// t.setText("rotate not succeeded");
			// t.show();
		}
	}

	private void checkSolved() {
		boolean s = this.magicCube.solved();
		if (s != solved) {
			solved = s;
			if (solved == true) {
				notifyCubeSolved();
			}
		}
	}

	public void resetStepCount() {
		stepCount = 0;
	}

	public int getStepCount() {
		return stepCount;
	}

	private void startAnimation() {
		if (inAnimation) {
			return;
		}
		this.inAnimation = true;
		this.magicCube.getCubie().prepareAnimation();

		int animationDuration = Configuration.config().getAnimationSpeed();
		int stepInterval = 10 + 90 / Configuration.config()
				.getAnimationQuality();

		this.magicCube.getAnimationInfo().setAnimation(
				animationDuration / stepInterval);

		if (animationThread == null) {
			animationThread = new AnimationThread();
		}
		animationThread.delay = stepInterval;

		handler.postDelayed(animationThread, stepInterval);
	}

	private void finishAnimation() {
		if (!inAnimation) {
			return;
		}
		this.magicCube.getCubie().finishAnimation();
		inAnimation = false;
		notifyAnimationFinished();
	}

	public boolean addAnimationListener(AnimationListener l) {
		return animationListeners.add(l);
	}

	public boolean removeAnimationListener(AnimationListener l) {
		return animationListeners.remove(l);
	}

	public void clearAnimationListeners(AnimationListener l) {
		animationListeners.clear();
	}

	protected void notifyAnimationFinished() {
		for (AnimationListener l : animationListeners) {
			l.animationFinishied();
		}
	}

	public boolean addCubeListener(CubeListener l) {
		return cubeListeners.add(l);
	}

	public boolean removeCubeListener(CubeListener l) {
		return cubeListeners.remove(l);
	}

	public void clearCubeListeners(CubeListener l) {
		cubeListeners.clear();
	}

	protected void notifyCubeSolved() {
		for (CubeListener l : cubeListeners) {
			l.cubeSolved();
		}
	}

	protected class OnCubeViewTouched implements OnTouchListener {
		int startX, startY;

		public boolean onTouch(View v, MotionEvent evt) {
			if (!turnable && !rotatable) {
				return true;
			}
			if (inAnimation) {
				return true;
			}
			switch (evt.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = (int) evt.getX();
				startY = (int) evt.getY();
				break;
			case MotionEvent.ACTION_UP:
				processAction(startX, startY, (int) evt.getX() - startX,
						(int) evt.getY() - startY);
				break;
			}
			return true;
		}

		protected void processAction(int x, int y, int deltaX, int deltaY) {
			if (Math.abs(deltaX) < 5 && Math.abs(deltaY) < 5) {
				return;
			}
			Point3I p = cubeView.mapToCubePosition(x, y);
			if ((p.x == 0 && p.y == 0 && p.x == 0) || (rotatable && !turnable)) {
				if (!rotatable) {
					return;
				}
				if (Math.abs(deltaX) > Math.abs(deltaY)) {
					if (deltaX > 0) {
						turnBySymbol("y'");
					} else {
						turnBySymbol("y");
					}
				} else {
					if (x >= cubeView.getWidth() / 2) {
						if (deltaY > 0) {
							turnBySymbol("z");
						} else {
							turnBySymbol("z'");
						}
					} else {
						if (deltaY > 0) {
							turnBySymbol("x'");
						} else {
							turnBySymbol("x");
						}
					}
				}
			} else {
				if (!turnable) {
					return;
				}

				stepCount++;
				if (!turnByGesture(p.x, p.y, p.z, deltaX, deltaY)) {
					stepCount--;
				}
			}
		}

	}

	public void zoomIn() {
		if (this.getCubeView() != null) {
			this.getCubeView().getCubeRenderer().zoomIn();
			this.getCubeView().requestRender();
		}
	}

	public void zoomReset() {
		if (this.getCubeView() != null) {
			this.getCubeView().getCubeRenderer().zoomReset();
			this.getCubeView().requestRender();
		}

	}

	public void zoomOut() {
		Log.v("CubeController", "zoom out");
		if (this.getCubeView() != null) {
			Log.v("CubeController", "zoom out haha ");
			this.getCubeView().getCubeRenderer().zoomOut();
			this.getCubeView().requestRender();
		}

	}

	class AnimationThread implements Runnable {
		private long delay;

		public void run() {
			CubeAnimationInfo info = magicCube.getAnimationInfo();
			info.step();
			if (info.currentStep() >= info.totalStep()) {
				finishAnimation();
			} else {
				handler.postDelayed(animationThread, delay);
			}
			cubeView.requestRender();
		}
	}
}
