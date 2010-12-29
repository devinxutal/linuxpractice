package com.devinxutal.fmc.control;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.devinxutal.fmc.model.CubeAnimationInfo;
import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.primitives.Point3I;
import com.devinxutal.fmc.ui.CubeView;

public class CubeController {
	private CubeView cubeView;
	private Activity activity;
	private MagicCube magicCube;

	private boolean inAnimation = false;
	private Timer animationTimer;
	private Timer presentationTimer;

	public CubeController(Activity activity) {
		this.activity = activity;
		this.cubeView = new CubeView(activity);
		this.magicCube = new MagicCube(3);
		this.cubeView.setMagicCube(magicCube);
		this.magicCube.view = cubeView;
		this.cubeView.setOnTouchListener(new OnCubeViewTouched());
		if (t == null) {
			t = Toast.makeText(cubeView.getContext(), "", 1000);
		}
	}

	public CubeView getCubeView() {
		return this.cubeView;
	}

	public MagicCube getMagicCube() {
		return this.magicCube;
	}

	public void turnByGesture(int i, int j, int k, int dx, int dy) {
		boolean succeed = this.magicCube.turnByGesture(i, j, k, dx, dy);
		if (succeed) {
			startAnimation();
		} else {
			t.setText("turn not succeeded");
			t.show();
		}
	}

	public void turnBySymbol(String symbol) {
		boolean succeed = this.magicCube.turnBySymbol(symbol);
		if (succeed) {
			startAnimation();
		} else {
			t.setText("turn not succeeded");
			t.show();
		}
	}

	public void rotate(int dimension, int direction) {
		boolean succeed = this.magicCube.rotate(dimension, direction);
		if (succeed) {
			startAnimation();
		} else {
			t.setText("rotate not succeeded");
			t.show();
		}
	}

	public void startAnimation() {
		if (inAnimation) {
			return;
		}
		this.inAnimation = true;
		this.magicCube.getCubie().prepareAnimation();
		this.magicCube.getAnimationInfo().setAnimation(9);
		this.animationTimer = new Timer();
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

		}, 0, 50);
	}

	public void finishAnimation() {
		if (!inAnimation) {
			return;
		}
		this.magicCube.getCubie().finalizeAnimation();
		inAnimation = false;
		if (animationTimer != null) {
			animationTimer.cancel();
			animationTimer.purge();
			animationTimer = null;
		}
	}

	private MoveSequence sequence;

	public void startPresentation(String symbols) {
		if (inAnimation || sequence != null) {
			return;
		}
		sequence = new MoveSequence(symbols, magicCube.getOrder());
		this.presentationTimer = new Timer();
		this.presentationTimer.scheduleAtFixedRate(new TimerTask() {
			private int waitCount = 0;
			private int waitTotal = 10;

			@Override
			public void run() {
				if (inAnimation || sequence == null
						|| sequence.currentMoveIndex() < 0) {
					return;
				}
				if (waitCount == 0) {

					cubeView.requestRender();
				}
				if (waitCount != waitTotal) {
					waitCount++;
				} else {
					waitCount = 0;
					Move mv = sequence.currentMove();
					boolean suc = magicCube.turn(mv.dimension, mv.layers,
							mv.direction);
					if (suc) {
						startAnimation();
					}
					sequence.step();
					if (sequence.currentMoveIndex() < 0) {
						finishAnimation();
					}
				}

			}

		}, 0, 50);
	}

	public void finishPresentation() {
		if (inAnimation || sequence == null) {
			return;
		}
		sequence = null;
		if (presentationTimer != null) {
			presentationTimer.cancel();
			presentationTimer.purge();
			presentationTimer = null;
		}
	}

	Toast t;

	protected class OnCubeViewTouched implements OnTouchListener {
		int startX, startY;

		public boolean onTouch(View v, MotionEvent evt) {
			switch (evt.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = (int) evt.getX();
				startY = (int) evt.getY();
				if (startY < 30) {
					if (startX < 100) {
						rotate(MagicCube.DIM_X, 1);
					} else if (startX > 200) {
						rotate(MagicCube.DIM_Y, 1);
					} else {
						rotate(MagicCube.DIM_Z, 1);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				processAction(startX, startY, (int) evt.getX() - startX,
						(int) evt.getY() - startY);
				break;
			}
			return true;
		}

		protected void processAction(int x, int y, int deltax, int deltaY) {
			Point3I p = cubeView.mapToCubePosition(x, y);
			if (p.x == 0 && p.y == 0 && p.x == 0) {
				Log.v("motiontest", "all zero, ignore");
				return;
			}
			Log.v("motiontest", "start turn");
			turnByGesture(p.x, p.y, p.z, deltax, deltaY);
		}

	}
}
