package com.devinxutal.fc.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devinxutal.fc.R;
import com.devinxutal.fc.cfg.Configuration;
import com.devinxutal.fc.cfg.Constants;
import com.devinxutal.fc.control.CubeController;
import com.devinxutal.fc.control.IMoveSequence;
import com.devinxutal.fc.control.InfiniteMoveSequence;
import com.devinxutal.fc.control.Move;
import com.devinxutal.fc.control.MoveController;
import com.devinxutal.fc.control.MoveControllerListener;
import com.devinxutal.fc.control.MoveSequence;
import com.devinxutal.fc.control.CubeController.CubeListener;
import com.devinxutal.fc.model.CubeState;
import com.devinxutal.fc.model.MagicCube;
import com.devinxutal.fc.solver.CfopSolver;
import com.devinxutal.fc.ui.CubeControlView;
import com.devinxutal.fc.ui.CubeControlView.CubeControlListener;
import com.devinxutal.fc.util.AdUtil;
import com.devinxutal.fc.util.DialogUtil;
import com.devinxutal.fc.util.PreferenceUtil;
import com.devinxutal.fc.util.SymbolMoveUtil;
import com.devinxutal.fc.util.VersionUtil;

public class MagicCubeActivity extends Activity {
	public static final String TAG = "MagicCubeActivity";

	public static final int PREFERENCE_REQUEST_CODE = 0x100;

	public enum State {
		SHUFFLING, PAUSED, PLAY, OBSERVE, FREE, WAIT_REPLAY
	}

	private CubeController cubeController;
	private MoveController moveController;
	private CubeControlView controlView;
	private ViewGroup successScreen;
	private Toast toast;
	private Button successScreenBackButton;
	private Button successScreenSubmitButton;

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
		this.setScreenOrientation();

		this.timedMode = getIntent().getBooleanExtra("timedMode", false);
		int order = Configuration.config().getCubeSize();
		cubeController = new CubeController(this, order, true, true);
		moveController = new MoveController(cubeController);
		controlView = new CubeControlView(this, timedMode);
		toast = Toast.makeText(this, "", 5000);
		controlView.addCubeControlListener(new ControlButtonClicked());
		controlView.setCubeController(cubeController);
		moveController.addMoveControllerListener(new MoveListener());
		moveController.setMoveInterval(Configuration.config()
				.getRotationInterval());
		FrameLayout layout = new FrameLayout(this);
		layout.addView(cubeController.getCubeView());
		cubeController.addCubeListener(new CubeSolved());
		layout.addView(controlView);

		setContentView(R.layout.adframe);
		// determine ad placement
		AdUtil.determineAd(this);
		// add main frame
		((LinearLayout) this.findViewById(R.id.content_area)).addView(layout);
		// add successScreen
		if (timedMode) {
			ViewGroup group = (ViewGroup) this.findViewById(R.id.mask_layer);
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.succeedscreen, group);
			successScreen = (ViewGroup) this.findViewById(R.id.succeed_screen);
			successScreenBackButton = (Button) this
					.findViewById(R.id.back_button);
			successScreenSubmitButton = (Button) this
					.findViewById(R.id.submit_button);
			OnClickListener l = new ButtonsOnClick();
			successScreenBackButton.setOnClickListener(l);
			successScreenSubmitButton.setOnClickListener(l);
			AdUtil.determineAd(this, R.id.ss_ad_area);
			hideSuccessScreen(false);
		}
		//
		switchState(State.FREE);
		if (savedInstanceState == null && timedMode) {
			wrappedShuffle();
		}
	}

	@Override
	protected void onDestroy() {
		if (timedMode) {
			controlView.getCubeTimer().stop();
		}
		if (moveController.getState() == MoveController.State.RUNNING_MULTPLE_STEP) {
			moveController.stopMove();
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
			wrappedShuffle();
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
			wrappedShuffle();
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

	private void setScreenOrientation() {
		String att = Configuration.config().getScreenOrientation();
		if (att.equals("auto")) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		} else if (att.equals("portrait")) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (att.equals("landscape")) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
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
			MagicCubeActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (state == State.PLAY) {
						if (timedMode) {
							controlView.getCubeTimer().stop();
							switchState(State.WAIT_REPLAY);
							showSuccessScreen();
						} else {
							toast
									.setText("Congratulations!\nYou've solve the cube");
							toast.show();
						}
					}
				}
			});

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
						cubeController.resetStepCount();
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

	class ButtonsOnClick implements OnClickListener {
		public void onClick(View view) {
			if (view.getId() == R.id.back_button) {
				MagicCubeActivity.this.hideSuccessScreen(true);
			} else if (view.getId() == R.id.submit_button) {
				if (((Button) view)
						.getText()
						.equals(
								MagicCubeActivity.this
										.getString(R.string.success_screen_submit_score))) {
					MagicCubeActivity.this.submitRecord();
				} else {
					DialogUtil.showRankDialog(MagicCubeActivity.this,
							cubeController.getMagicCube().getOrder());
				}
			}
		}
	}

	public void wrappedShuffle() {
		if (Constants.TEST) {
			shuffle(2);
		} else {
			shuffle(Configuration.config().getShuffleSteps());
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

	private void showSuccessScreen() {
		this.successScreen.setVisibility(View.VISIBLE);
		TextView timeText = ((TextView) this.findViewById(R.id.time_text));
		TextView stepsText = ((TextView) this.findViewById(R.id.steps_text));
		long time = controlView.getCubeTimer().getTime();
		timeText.setText(time / 1000 + "." + time % 1000 + " seconds");
		stepsText.setText("" + cubeController.getStepCount() + " moves");
		this.successScreenSubmitButton
				.setText(R.string.success_screen_submit_score);
		controlView.showPlayButton(false);
	}

	private void hideSuccessScreen(boolean showPlayButton) {
		this.successScreen.setVisibility(View.INVISIBLE);
		if (showPlayButton) {
			controlView.showPlayButton(true);
		}
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
		wrappedShuffle();
	}

	public void pause() {
		switchState(State.PAUSED);
	}

	public void succeed() {

	}

	private boolean restoreCubeState(CubeState state) {
		Log.v("MainCubeActivity", "restore cube state: ");
		boolean result = false;
		if (state.order == cubeController.getMagicCube().getOrder()) {

			Log.v("MainCubeActivity", "restoring cube state");
			cubeController.getMagicCube().setOrder(state.order);
			cubeController.getMagicCube().setCubeState(state);
			result = true;
		}
		cubeController.getCubeView().requestRender();
		return result;
	}

	public void loadCubeState() {
		if (!VersionUtil.checkProVersion(this, true)) {
			return;
		}
		File file = Environment.getExternalStorageDirectory();
		try {
			File dataFile = new File(file, Constants.DATA_DIR + "/"
					+ Constants.CUBE_SAVING_FILE);
			if (!dataFile.exists()) {
				toast.setText("Saved cube not found.");
				toast.show();
			}
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					dataFile));
			CubeState stat = (CubeState) in.readObject();
			in.close();
			if (restoreCubeState(stat)) {
				toast.setText("Load successfully");
			} else {
				toast.setText("Load failed, cube size mismatch");
			}
		} catch (Exception e) {
			toast.setText("Load failed, check the state of media storage");
			e.printStackTrace();
		}
		toast.show();
	}

	public void saveCubeState() {
		if (!VersionUtil.checkProVersion(this, true)) {
			return;
		}
		File file = Environment.getExternalStorageDirectory();
		try {
			File dir = new File(file, Constants.DATA_DIR);
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
			if (!timedMode) {
				cubeController.getMagicCube().setOrder(cubeSize);
			} else {
				toast
						.setText("Cube size will not change in timed mode util next play");
				toast.show();
			}
		}

		cubeController.getCubeView().getCubeRenderer().setRefitCube(true);
		cubeController.getCubeView().requestRender();
		moveController.setMoveInterval(Configuration.config()
				.getRotationInterval());
		this.setScreenOrientation();
	}

	public void submitRecord() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.record_submit_input_name_title);
		final EditText input = new EditText(this);
		input.setText(PreferenceUtil.readPlayerName(this));
		LinearLayout layout = new LinearLayout(this);
		layout.setPadding(20, 10, 20, 10);
		layout.addView(input, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		alert.setView(layout);

		alert.setPositiveButton(R.string.common_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						PreferenceUtil.writePlayerName(MagicCubeActivity.this,
								input.getText().toString());
						submitRecord(input.getText().toString());
					}
				});

		alert.setNegativeButton(R.string.common_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						return;
					}
				});
		alert.show();
	}

	private void submitRecord(String player) {
		progressDialog = ProgressDialog.show(this, "", getResources()
				.getString(R.string.record_submit_submitting_record), true);
		new SubmitRecordThread(player).start();
	}

	public void solveCurrentCube() {
		if (this.cubeController.getMagicCube().getOrder() != 3) {
			DialogUtil.showDialog(this, R.string.cube_solve_problem_title,
					R.string.cube_solve_problem_desc);
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

	class SubmitRecordThread extends Thread {
		private String player;

		public SubmitRecordThread(String player) {
			this.player = player;
		}

		@Override
		public void run() {
			String url = Constants.URL_COMMIT_RECORD;
			Map<String, String> data = new HashMap<String, String>();
			data.put("player", player);
			data.put("time", controlView.getCubeTimer().getTime() + "");
			data.put("steps", cubeController.getStepCount() + "");
			data.put("order", cubeController.getMagicCube().getOrder() + "");
			data.put("shuffles", Configuration.config().getShuffleSteps() + "");
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
			for (Map.Entry<String, String> m : data.entrySet()) {
				postData.add(new BasicNameValuePair(m.getKey(), m.getValue()));
			}
			int statusCode = HttpStatus.SC_ACCEPTED;
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						postData, HTTP.UTF_8);

				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost);
				statusCode = response.getStatusLine().getStatusCode();

			} catch (Exception e) {
				e.printStackTrace();
			}
			final int sc = statusCode;
			MagicCubeActivity.this.runOnUiThread(new Runnable() {

				public void run() {
					if (progressDialog != null) {
						progressDialog.cancel();
						progressDialog = null;
					}
					if (sc == HttpStatus.SC_OK) {
						toast.setText("Your record has been submitted.");
						toast.show();
						successScreenSubmitButton
								.setText(R.string.success_screen_world_rank);
					} else {
						toast
								.setText("Submit failed, please check your network connection.");
						toast.show();
					}
				}

			});
		}
	}

}