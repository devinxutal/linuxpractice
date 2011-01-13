package com.devinxutal.fmc.control;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.devinxutal.fmc.cfg.Configuration;
import com.devinxutal.fmc.model.CubeAnimationInfo;
import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.primitives.Point3I;
import com.devinxutal.fmc.ui.CubeView;

public class CubeController {
	private CubeView cubeView;
	private MagicCube magicCube;

	private boolean inAnimation = false;
	private boolean touchEnabled = false;

	private Timer animationTimer;
	private Timer presentationTimer;

	private List<AnimationListener> listeners = new LinkedList<AnimationListener>();

	public CubeController(Context context) {
		this(context, true);
	}

	public CubeController(Context context, boolean touchEnabled) {
		this.cubeView = new CubeView(context);
		this.magicCube = new MagicCube(3);
		this.cubeView.setMagicCube(magicCube);
		this.magicCube.view = cubeView;
		this.cubeView.setOnTouchListener(new OnCubeViewTouched());
		this.touchEnabled = touchEnabled;
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
		}
		return succeed;
	}

	public boolean turnByGesture(int i, int j, int k, int dx, int dy) {
		boolean succeed = this.magicCube.turnByGesture(i, j, k, dx, dy);
		if (succeed) {
			startAnimation();
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

	private void startAnimation() {
		if (inAnimation) {
			return;
		}
		this.inAnimation = true;
		this.magicCube.getCubie().prepareAnimation();
		int step = Configuration.config().getAnimationQuality() * 3;
		this.magicCube.getAnimationInfo().setAnimation(step);
		this.animationTimer = new Timer();
		int animationDuration = Configuration.config().getAnimationSpeed();
		Log.v("CubeController", "animationDuration" + animationDuration);
		Log.v("CubeController", "step " + step);
		this.animationTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				CubeAnimationInfo info = magicCube.getAnimationInfo();
				info.step();
				cubeView.requestRender();
				if (info.currentStep() >= info.totalStep()) {
					finishAnimation();
				}
			}

		}, 0, animationDuration / step);
	}

	private void finishAnimation() {
		if (!inAnimation) {
			return;
		}
		this.magicCube.getCubie().finishAnimation();
		inAnimation = false;
		if (animationTimer != null) {
			animationTimer.cancel();
			animationTimer.purge();
			animationTimer = null;
		}
		notifyAnimationFinished();
	}

	public boolean addAnimationListener(AnimationListener l) {
		return listeners.add(l);
	}

	public boolean removeAnimationListener(AnimationListener l) {
		return listeners.remove(l);
	}

	public void clearAnimationListeners(AnimationListener l) {
		listeners.clear();
	}

	protected void notifyAnimationFinished() {
		for (AnimationListener l : listeners) {
			l.animationFinishied();
		}
	}

	protected class OnCubeViewTouched implements OnTouchListener {
		int startX, startY;

		public boolean onTouch(View v, MotionEvent evt) {
			if (!touchEnabled) {
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
			if (p.x == 0 && p.y == 0 && p.x == 0) {
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
				turnByGesture(p.x, p.y, p.z, deltaX, deltaY);
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
}
