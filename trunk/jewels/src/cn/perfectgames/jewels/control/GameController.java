package cn.perfectgames.jewels.control;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import cn.perfectgames.jewels.cfg.Configuration;
import cn.perfectgames.jewels.model.GameMode;
import cn.perfectgames.jewels.model.Playground;
import cn.perfectgames.jewels.sound.SoundManager;
import cn.perfectgames.jewels.ui.ControlView;
import cn.perfectgames.jewels.ui.PlaygroundView;


public class GameController {
	public static final int CONTROL_STICK_PERIOD = 100;
	public static final int BASIC_INTERVAL = 100;

	public static final int INTERVAL_CONTROL_INIT = 400;
	public static final int INTERVAL_CONTROL_STICK = 100;
	public static final int INTERVAL_STEP_NORMAL = 700;
	public static final int INTERVAL_STEP_ANIMATION = 150;

	public static final int IC_STEP = 5;
	private static final String TAG = "GameController";
	
	private GameMode mode;

	public interface GameListener {
		public void gameFinishing();

		public void gameFinished();
	}

	private SoundManager soundManager;

	private PlaygroundView playgroundView;
	private ControlView controlView;
	private Playground playground;
	private List<GameListener> listeners = new LinkedList<GameListener>();

	private Handler handler;

	private boolean playing = true;
	
	private Command pendingCommand = null;

	public void start() {
		playing = true;
		
	}

	public void pause() {
		playing = false;
		this.clearPendingCommand();
	}

	public void stop() {
		playing = false;
	}

	public void reset() {
		//TODO
//		playground.reset();
//		this.playgroundView.reset();
//		this.clearPendingCommand();
	}

	public void finishAnimation() {
		//TODO
//		while (this.playground.isInAnimation()) {
//			this.playground.moveOn();
//		}
	}

	public void processCommand(Command cmd) {

	}

	private int getStepDelay() {
		//TODO
//		return (int) (INTERVAL_STEP_NORMAL * playground.getSpeedScale());
		return 400;
	}

	public void clearPendingCommand() {
		this.pendingCommand = null;
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

	public GameController(Context context, GameMode mode) {
		this.playground = new Playground(mode);
		this.playgroundView = new PlaygroundView(context);
		this.playgroundView.setPlayground(playground);
		this.playgroundView.setGameController(this);
		this.controlView = new ControlView(context);
		this.playgroundView.setControlView(this.controlView);
		this.handler = new Handler();
		this.soundManager = SoundManager.get((Activity) context);
	}

	public void destroy() {
		this.playing = false;
	}

	public void configurationChanged(Configuration config) {
		this.controlView.configurationChanged(config);
		this.playgroundView.configurationChanged(config);
		this.playground.configurationChanged(config);
	}


	public ControlView getControlView() {
		return this.controlView;
	}
}
