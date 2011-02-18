package com.devinxutal.man20.control;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.devinxutal.man20.model.Playground;
import com.devinxutal.man20.ui.PlaygroundView;

public class GameController {
	private static final String TAG = "GameController";

	public interface GameListener {
		public void gameFinishing();

		public void gameFinished();
	}

	private PlaygroundView playgroundView;
	private Playground playground;
	private List<GameListener> listeners = new LinkedList<GameListener>();

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
		this.playground = new Playground(50);
		this.playgroundView = new PlaygroundView(context);
		this.playgroundView.setPlayground(playground);
		this.playgroundView.setGameController(this);

	}

	public void destroy() {
	}

	public void gameFinishedCalledByPlaygroundView() {

		Log.v(TAG, "game finished");
		notifyGameFinished();
	}

	public void gameFinishingCalledByPlaygroundView() {
		notifyGameFinishing();
	}

}
