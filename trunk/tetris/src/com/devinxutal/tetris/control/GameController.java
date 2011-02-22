package com.devinxutal.tetris.control;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.devinxutal.tetris.model.Playground;
import com.devinxutal.tetris.ui.PlaygroundView;

public class GameController {
	private static final String TAG = "GameController";

	public interface GameListener {
		public void gameFinishing();

		public void gameFinished();
	}

	private PlaygroundView playgroundView;
	private Playground playground;
	private List<GameListener> listeners = new LinkedList<GameListener>();

	private Handler handler;

	private boolean playing = true;
	private HandlerRunnable runnable = new HandlerRunnable();

	public void start() {
		playing = true;
		handler.postDelayed(runnable, 1000);
	}

	public void pause() {
		playing = false;
	}

	public void stop() {
		playing = false;
	}

	public void processCommand(Command cmd) {
		this.playground.processCommand(cmd);
		this.playgroundView.invalidate();
	}

	public PlaygroundView getPlaygroundView() {
		return playgroundView;
	}

	public boolean addGameListener(GameListener l) {
		return listeners.add(l);
	}

	public boolean removeGameListener(GameListener l) {
		return listeners.remove(l);
	}

	public void clearGameListeners(GameListener l) {
		listeners.clear();
	}

	protected void notifyGameFinished() {
		for (GameListener l : listeners) {
			l.gameFinished();
		}
	}

	protected void notifyGameFinishing() {
		for (GameListener l : listeners) {
			l.gameFinishing();
		}
	}

	public void setPlaygroundView(PlaygroundView playgroundView) {
		this.playgroundView = playgroundView;
	}

	public Playground getPlayground() {
		return playground;
	}

	public void setPlayground(Playground playground) {
		this.playground = playground;
	}

	public GameController(Context context) {
		this.playground = new Playground();
		this.playgroundView = new PlaygroundView(context);
		this.playgroundView.setPlayground(playground);
		this.playgroundView.setGameController(this);
		this.handler = new Handler();
	}

	public void destroy() {
		this.playing = false;
	}

	public void gameFinishedCalledByPlaygroundView() {

		Log.v(TAG, "game finished");
		notifyGameFinished();
	}

	public void gameFinishingCalledByPlaygroundView() {
		notifyGameFinishing();
	}

	class HandlerRunnable implements Runnable {
		@Override
		public void run() {
			Log.v(TAG, "Controller running");
			playground.moveOn();
			playgroundView.invalidate();

			if (playing) {
				if (playground.isInAnimation()) {

					handler.postDelayed(runnable, 300);
				} else {
					handler.postDelayed(runnable, 500);
				}
			}
		}
	}
}
