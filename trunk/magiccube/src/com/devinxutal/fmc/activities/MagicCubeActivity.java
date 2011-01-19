package com.devinxutal.fmc.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.cfg.Configuration;
import com.devinxutal.fmc.cfg.Constants;
import com.devinxutal.fmc.control.CubeController;
import com.devinxutal.fmc.control.IMoveSequence;
import com.devinxutal.fmc.control.InfiniteMoveSequence;
import com.devinxutal.fmc.control.Move;
import com.devinxutal.fmc.control.MoveController;
import com.devinxutal.fmc.control.MoveControllerListener;
import com.devinxutal.fmc.control.MoveSequence;
import com.devinxutal.fmc.control.CubeController.CubeListener;
import com.devinxutal.fmc.model.CubeState;
import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.solver.CfopSolver;
import com.devinxutal.fmc.ui.CubeControlView;
import com.devinxutal.fmc.ui.CubeControlView.CubeControlListener;
import com.devinxutal.fmc.util.DialogUtil;
import com.devinxutal.fmc.util.SymbolMoveUtil;
import com.devinxutal.fmc.util.VersionUtil;

public class MagicCubeActivity extends Activity {
	public static final String TAG = "MagicCubeActivity";

	public static final int SHUFFLE_STEPS = 3;
	public static final int PREFERENCE_REQUEST_CODE = 0x100;

	public enum State {
		SHUFFLING, PAUSED, PLAY, OBSERVE, FREE, WAIT_REPLAY
	}

	private CubeController cubeController;
	private MoveController moveController;
	private CubeControlView controlView;
	private Toast toast;

	private Dialog progressDialog;

	private boolean timedMode = false;

	private State state = State.FREE;

	// used only when restore state;
	private long elapsedTime = 0;
	private boolean collapsed = true;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Configuration.config()
				.setSharedPreferences(
						PreferenceManager
								.getDefaultSharedPreferences(getBaseContext()));
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)

		this.timedMode = getIntent().getBooleanExtra("timedMode", false);
		int order = Configuration.config().getCubeSize();
		cubeController = new CubeController(this, order, true, true);
		moveController = new MoveController(cubeController);
		controlView = new CubeControlView(this, timedMode);
		toast = Toast.makeText(this, "", 5000);
		controlView.addCubeControlListener(new ControlButtonClicked());
		controlView.setCubeController(cubeController);
		moveController.addMoveControllerListener(new MoveListener());
		FrameLayout layout = new FrameLayout(this);
		layout.addView(cubeController.getCubeView());
		cubeController.addCubeListener(new CubeSolved());
		layout.addView(controlView);
		setContentView(layout);
		switchState(State.FREE);
		if (savedInstanceState == null && timedMode) {

			shuffle(SHUFFLE_STEPS);
		}
	}

	@Override
	protected void onDestroy() {
		if (timedMode) {
			controlView.getCubeTimer().stop();
		}
		super.onDestroy();
	}

	private void initByRestoredState() {
		if (timedMode) {
			this.controlView.getCubeTimer().setTime(elapsedTime);
		}
		Log.v(TAG, "initByRestoredState: " + state);
		if (state == State.PLAY) {
			state = State.PAUSED;
		}
		if (state == State.SHUFFLING) {
			shuffle(SHUFFLE_STEPS);
		} else {
			switchState(state);
		}
		this.controlView.setCollapsed(collapsed);
	}

	private void switchState(State to) {
		if (to == State.SHUFFLING) {
			this.cubeController.setRotatable(false);
			this.cubeController.setTurnable(false);
			this.controlView.showPlayButton(false);
			if (timedMode) {
				this.controlView.getCubeTimer().reset();
			}
		} else if (to == State.OBSERVE) {
			this.cubeController.setRotatable(true);
			this.cubeController.setTurnable(false);
			this.controlView.setPlayButtonImage(R.drawable.icon_play_large);
			this.controlView.showPlayButton(true);
		} else if (to == State.PAUSED) {
			this.cubeController.setRotatable(false);
			this.cubeController.setTurnable(false);
			this.controlView.setPlayButtonImage(R.drawable.icon_play_large);
			this.controlView.showPlayButton(true);
		} else if (to == State.PLAY) {
			this.cubeController.setRotatable(true);
			this.cubeController.setTurnable(true);
			this.controlView.showPlayButton(false);
			if (timedMode) {
				this.controlView.getCubeTimer().start();
			}
		} else if (to == State.FREE) {
			this.cubeController.setRotatable(true);
			this.cubeController.setTurnable(true);
			this.controlView.showPlayButton(false);
		} else if (to == State.WAIT_REPLAY) {
			this.cubeController.setRotatable(false);
			this.cubeController.setTurnable(false);
			this.controlView.setPlayButtonImage(R.drawable.icon_replay_large);
			this.controlView.showPlayButton(true);
		}
		this.state = to;

		this.controlView.invalidate();
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		Log.v(TAG, "option menu closed");
		super.onOptionsMenuClosed(menu);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.v(TAG, "onSaveInstanceState");
		outState.putSerializable(Constants.CUBE_STATE, cubeController
				.getMagicCube().getCubeState());
		outState.putBoolean("timedMode", timedMode);
		if (timedMode) {
			outState.putLong("elapsedTime", controlView.getCubeTimer()
					.getTime());
		}
		outState.putSerializable("state", state);
		outState.putBoolean("padCollapsed", controlView.isCollapsed());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.v(TAG, "onRestoreInstanceState");
		CubeState cubeState = (CubeState) (savedInstanceState
				.getSerializable(Constants.CUBE_STATE));
		if (cubeState != null) {
			this.cubeController.getMagicCube().setCubeState(cubeState);
		}
		timedMode = savedInstanceState.getBoolean("timedMode", false);
		elapsedTime = savedInstanceState.getLong("elapsedTime", 0);
		collapsed = savedInstanceState.getBoolean("padCollapsed", true);
		this.state = (State) savedInstanceState.getSerializable("state");
		initByRestoredState();
		super.onRestoreInstanceState(savedInstanceState);

	}

	@Override
	protected void onPause() {
		Log.v(TAG, "pause");
		this.cubeController.getCubeView().onPause();
		if (this.timedMode) {
			this.controlView.getCubeTimer().stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.v(TAG, "resume");
		this.cubeController.getCubeView().onResume();
		if (this.timedMode && this.state == State.PLAY) {

			this.controlView.getCubeTimer().start();
		}
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PREFERENCE_REQUEST_CODE) {
			this.preferenceChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.cube_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.solve_it:
			this.solveCurrentCube();
			return true;
		case R.id.shuffle_it:
			shuffle(SHUFFLE_STEPS);
			return true;
		case R.id.save:
			saveCubeState();
			return true;
		case R.id.load:
			loadCubeState();
			return true;
		case R.id.preferences:
			Intent preferencesActivity = new Intent(getBaseContext(),
					Preferences.class);
			startActivityForResult(preferencesActivity, PREFERENCE_REQUEST_CODE);
			return true;
		case R.id.help:
			openHelpDialog();
			return true;
		}
		return false;
	}

	class ControlButtonClicked implements CubeControlListener {
		public void buttonClickced(int id) {
			switch (id) {
			case CubeControlView.BTN_HELP:
				MagicCubeActivity.this.openHelpDialog();
				break;
			case CubeControlView.BTN_MENU:
				getWindow().openPanel(
						Window.FEATURE_OPTIONS_PANEL,
						new KeyEvent(KeyEvent.ACTION_DOWN,
								KeyEvent.KEYCODE_MENU));
				break;
			case CubeControlView.BTN_SETTING:
				Intent preferencesActivity = new Intent(getBaseContext(),
						Preferences.class);
				startActivity(preferencesActivity);
				break;
			case CubeControlView.BTN_PLAY:
				Log.v(TAG, "play button clicked");
				if (state == State.PAUSED || state == State.OBSERVE) {
					play();
				} else if (state == State.WAIT_REPLAY) {
					replay();
				}
			}
		}
	}

	class CubeSolved implements CubeListener {

		public void cubeSolved() {
			if (timedMode) {
				controlView.getCubeTimer().stop();
				switchState(State.WAIT_REPLAY);

				toast
						.setText("Congratulations!\nYou've solve the cube\nTime cost: "
								+ controlView.getCubeTimer().getTime()
								/ 1000
								+ " seconds");
				toast.show();
			} else {
				toast.setText("Congratulations!\nYou've solve the cube");
				toast.show();
			}
		}
	}

	class MoveListener implements MoveControllerListener {

		public void moveSequenceStepped(int index) {

		}

		public void statusChanged(final MoveController.State from,
				final MoveController.State to) {
			MagicCubeActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (from == MoveController.State.RUNNING_MULTPLE_STEP
							&& to == MoveController.State.STOPPED) {
						if (timedMode) {
							switchState(State.OBSERVE);
						} else {
							switchState(State.FREE);
						}
					}
				}
			});
		}
	}

	public void shuffle(int step) {
		if (this.state != State.FREE && this.state != State.WAIT_REPLAY) {
			return;
		}
		if (moveController.getState() != MoveController.State.STOPPED) {
			return;
		}
		IMoveSequence seq = new InfiniteMoveSequence(cubeController
				.getMagicCube().getOrder());
		Move move = null;
		MoveSequence seqq = new MoveSequence();
		toast
				.setText("Shuffling cube with " + step
						+ " steps, please wait ...");
		toast.show();
		while (step-- > 0) {
			move = seq.step();
			seqq.addMove(move);
		}

		moveController.startMove(seqq);
		switchState(State.SHUFFLING);
	}

	public void play() {
		Log.v(TAG, "in play , state: " + state + ", move controller: "
				+ moveController.getState());
		if (this.state != State.PAUSED && this.state != State.OBSERVE) {
			return;
		}
		if (moveController.getState() != MoveController.State.STOPPED) {
			return;
		}
		switchState(State.PLAY);
	}

	public void replay() {
		Log.v(TAG, "replay()");
		if (moveController.getState() != MoveController.State.STOPPED) {
			return;
		}
		shuffle(SHUFFLE_STEPS);
	}

	public void pause() {
		switchState(State.PAUSED);
	}

	public void succeed() {

	}

	private void restoreCubeState(CubeState state) {
		if (state.order != cubeController.getMagicCube().getOrder()) {
			cubeController.getMagicCube().setOrder(state.order);
			cubeController.getMagicCube().setCubeState(state);
		}
		cubeController.getCubeView().requestRender();
	}

	public void loadCubeState() {
		if (!VersionUtil.checkProVersion(this)) {
			return;
		}
		File file = Environment.getExternalStorageDirectory();
		try {
			File dataFile = new File(file, Constants.CUBE_SAVING_DIR + "/"
					+ Constants.CUBE_SAVING_FILE);
			if (!dataFile.exists()) {
				toast.setText("Saved cube not found.");
				toast.show();
			}
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					dataFile));
			CubeState stat = (CubeState) in.readObject();
			in.close();
			restoreCubeState(stat);
			toast.setText("Load successfully");
		} catch (Exception e) {
			toast.setText("Load failed, check the state of media storage");
			e.printStackTrace();
		}
		toast.show();
	}

	public void saveCubeState() {
		if (!VersionUtil.checkProVersion(this)) {
			return;
		}
		File file = Environment.getExternalStorageDirectory();
		try {
			File dir = new File(file, Constants.CUBE_SAVING_DIR);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File dataFile = new File(dir, Constants.CUBE_SAVING_FILE);
			if (!dataFile.exists()) {
				dataFile.createNewFile();
			}
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(dataFile));
			out.writeObject(this.cubeController.getMagicCube().getCubeState());
			out.close();
			toast.setText("Save successfully");
		} catch (Exception e) {
			toast.setText("Save failed, check the state of media storage");
			e.printStackTrace();
		}
		toast.show();
	}

	public void openHelpDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.help_info)
				.setMessage(R.string.help_info_cube).setCancelable(false)
				.setPositiveButton(R.string.common_ok, null);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void preferenceChanged() {
		Log.v(TAG, "preference changed");
		int cubeSize = Configuration.config().getCubeSize();
		if (cubeSize != cubeController.getMagicCube().getOrder()) {
			cubeController.getMagicCube().setOrder(cubeSize);
			cubeController.getCubeView().requestRender();
		}
	}

	public void solveCurrentCube() {
		if (this.cubeController.getMagicCube().getOrder() != 3) {
			DialogUtil.showDialog(this, R.string.cube_not_support_title,
					R.string.cube_not_support_content);
			return;
		}
		progressDialog = ProgressDialog.show(this, "", getResources()
				.getString(R.string.progress_solving_cube), true);
		new SolveCubeThread().start();

		return;
	}

	class SolveCubeThread extends Thread {

		@Override
		public void run() {
			MagicCube cube = new MagicCube(cubeController.getMagicCube()
					.getOrder());
			cube.setCubeState(cubeController.getMagicCube().getCubeState());
			CfopSolver solver = CfopSolver.getSolver(MagicCubeActivity.this);
			MoveSequence seq = new MoveSequence();
			while (!cube.solved()) {
				MoveSequence s = solver.nextMoves(cube);
				Move m = null;
				while ((m = s.step()) != null) {
					cube.turn(m.dimension, m.layers, m.direction, m.doubleTurn);
					seq.addMove(m);
				}
			}
			MoveSequence optimizedSeq = SymbolMoveUtil
					.optimizeMoveSequence(seq);
			final Intent intent = new Intent(getBaseContext(),
					CubeDemonstratorActivity.class);
			intent.putExtra("model", cubeController.getMagicCube()
					.getCubeState());
			intent.putExtra("formula", SymbolMoveUtil
					.parseSymbolsFromMoveSequence(optimizedSeq, cubeController
							.getMagicCube().getOrder()));
			MagicCubeActivity.this.runOnUiThread(new Runnable() {

				public void run() {
					if (progressDialog != null) {
						progressDialog.cancel();
					}
					startActivity(intent);
				}
			});

		}
	}
}