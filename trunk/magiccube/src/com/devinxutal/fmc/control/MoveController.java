package com.devinxutal.fmc.control;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;

public class MoveController implements AnimationListener {
	public enum State {
		RUNING_SINGLE_STEP, RUNNING_MULTPLE_STEP, STOPPED
	}

	private State state = State.STOPPED;

	private CubeController cubeController;

	private MoveThread moveThread;

	public MoveController(CubeController controller) {
		this.cubeController = controller;
		this.cubeController.addAnimationListener(this);
	}

	public boolean startMove(IMoveSequence sequence) {
		if (this.state != State.STOPPED) {
			return false;
		}

		moveThread = new MoveThread(sequence);
		moveThread.start();

		this.changeState(State.RUNNING_MULTPLE_STEP);
		return true;
	}

	public boolean startMove(Move move) {
		if (this.state != State.STOPPED || move == null) {
			return false;
		}
		boolean succeed = cubeController.turnByMove(move);
		if (succeed) {
			this.state = State.RUNING_SINGLE_STEP;
		}
		return succeed;
	}

	public boolean stopMove() {
		if (this.state != State.RUNNING_MULTPLE_STEP) {
			return false;
		}
		this.moveThread.canceled = true;
		// changeState(State.STOPPED);
		return false;
	}

	public State getState() {
		return state;
	}

	public void animationFinishied() {
		if (this.getState() == State.RUNING_SINGLE_STEP) {
			this.state = State.STOPPED;
			notifyStatusChanged(State.RUNING_SINGLE_STEP, State.STOPPED);
		} else if (this.getState() == State.RUNNING_MULTPLE_STEP) {
			if (moveThread != null) {
				synchronized (moveThread) {
					moveThread.notify();
				}
			}
		}
	}

	// ///////////////////////////

	private List<MoveControllerListener> listeners = new LinkedList<MoveControllerListener>();

	public boolean addMoveControllerListener(MoveControllerListener l) {
		return listeners.add(l);
	}

	public boolean removeMoveControllerListener(MoveControllerListener l) {
		return listeners.remove(l);
	}

	public void clearMoveControllerListeners() {
		listeners.clear();
	}

	protected void notifyStatusChanged(State from, State to) {
		for (MoveControllerListener l : listeners) {
			l.statusChanged(from, to);
		}
	}

	protected void notifyMoveSequenceStepped(int index) {
		for (MoveControllerListener l : listeners) {
			l.moveSequenceStepped(index);
		}
	}

	protected void changeState(State to) {
		if (to != state) {
			State from = state;
			state = to;
			notifyStatusChanged(from, to);
		}
	}

	class MoveThread extends Thread {
		private IMoveSequence sequence;
		private int interval = 200;
		private boolean paused = false;
		private boolean canceled = false;

		public MoveThread(IMoveSequence sequence) {
			this.sequence = sequence;

		}

		@Override
		public void run() {
			sequence.reset();
			Move mv;
			while ((mv = sequence.step()) != null) {
				if (canceled) {
					break;
				}
				Log.v("cc", "before paused");
				while (paused) {
					try {
						sleep(100);
						Log.v("cc", "paused");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					sleep(interval);
				} catch (Exception e) {
					e.printStackTrace();
				}
				cubeController.turnByMove(mv);
				notifyMoveSequenceStepped(sequence.currentMoveIndex());
				long start = System.currentTimeMillis();
				try {
					synchronized (this) {
						this.wait();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				long end = System.currentTimeMillis();
				Log.v("cc", "wait time: " + (end - start));
			}
			changeState(MoveController.State.STOPPED);
		}
	}

}
