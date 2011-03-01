package com.devinxutal.tetris.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devinxutal.tetris.R;
import com.devinxutal.tetris.cfg.Configuration;
import com.devinxutal.tetris.cfg.Constants;
import com.devinxutal.tetris.control.Command;
import com.devinxutal.tetris.control.GameController;
import com.devinxutal.tetris.control.GameController.GameListener;
import com.devinxutal.tetris.model.SavablePlayground;
import com.devinxutal.tetris.sound.SoundManager;
import com.devinxutal.tetris.ui.ControlView;
import com.devinxutal.tetris.ui.ControlView.GameControlListener;
import com.devinxutal.tetris.ui.JoyStick.JoyStickListener;
import com.devinxutal.tetris.util.AdUtil;
import com.devinxutal.tetris.util.DialogUtil;
import com.devinxutal.tetris.util.MilitaryRank;
import com.devinxutal.tetris.util.PreferenceUtil;

public class PlaygroundActivity extends Activity {
	public static final String TAG = "PlaygroundActivity";

	public static final int PREFERENCE_REQUEST_CODE = 0x100;

	public enum State {
		PLAY, PAUSED, ENDING, END
	}

	private GameController gameController;
	private ControlView controlView;
	private ViewGroup successScreen;
	private ViewGroup pausedScreen;
	private Toast toast;
	private Button successScreenBackButton;
	private Button successScreenSubmitButton;
	private Button successScreenReplayButton;
	private Button pauseScreenOptionButton;
	private Button pauseScreenQuitButton;
	private Button pauseScreenResumeButton;

	private Dialog progressDialog;

	private State state = State.END;

	// used only when restore state;
	private long elapsedTime = 0;
	private boolean collapsed = true;

	// user interfaces

	private SensorManager sensorManager;
	private Sensor sensor;
	private boolean sensorRunning = false;
	private boolean useJoyStick = true;
	private boolean useAccelerometer = false;
	private AccelerometerListener accelerometerListener = new AccelerometerListener();
	private MyJoyStickListener joyStickListener = new MyJoyStickListener();

	private Typeface buttonFont;

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
		setScreenOrientation();

		PowerManager pm = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);
		gameController = new GameController(this);
		controlView = gameController.getControlView();
		toast = Toast.makeText(this, "", 5000);
		controlView.addGameControlListener(new ControlButtonClicked());
		controlView.setGameController(gameController);
		FrameLayout layout = new FrameLayout(this);
		layout.addView(gameController.getPlaygroundView());
		gameController.addGameListener(new GameFinished());

		layout.addView(controlView);

		successScreen = new LinearLayout(this);
		layout.addView(successScreen);
		pausedScreen = new LinearLayout(this);
		layout.addView(pausedScreen);
		this.setContentView(layout);

		// add successScreen
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.succeedscreen, successScreen);
		// successScreen = (ViewGroup) this.findViewById(R.id.succeed_screen);
		successScreenBackButton = (Button) this.findViewById(R.id.back_button);
		successScreenSubmitButton = (Button) this
				.findViewById(R.id.submit_button);
		successScreenReplayButton = (Button) this
				.findViewById(R.id.replay_button);
		OnClickListener l = new ButtonsOnClick();
		successScreenBackButton.setOnClickListener(l);
		successScreenSubmitButton.setOnClickListener(l);
		successScreenReplayButton.setOnClickListener(l);
		AdUtil.determineAd(this, R.id.ss_ad_area);
		hideSuccessScreen();

		// add pauseScreen
		inflater.inflate(R.layout.pausescreen, pausedScreen);
		pauseScreenOptionButton = (Button) this
				.findViewById(R.id.options_button);
		pauseScreenQuitButton = (Button) this.findViewById(R.id.quit_button);
		pauseScreenResumeButton = (Button) this
				.findViewById(R.id.resume_button);
		pauseScreenOptionButton.setOnClickListener(l);
		pauseScreenQuitButton.setOnClickListener(l);
		pauseScreenResumeButton.setOnClickListener(l);
		AdUtil.determineAd(this, R.id.ps_ad_area);
		hidePauseScreen();
		//
		switchState(State.END);
		preferenceChanged();
		this.customizeButtons();
		if (savedInstanceState == null) {
			this.play();
		} else {
			this.pause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initByRestoredState() {
		Log.v(TAG, "initByRestoredState: " + state);
		if (state == State.PLAY) {
			state = State.PAUSED;
		}
		switchState(state);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.v(TAG, "key up " + event.getKeyCode());
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_DPAD_UP:
			gameController.processCommand(Command.TURN);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			gameController.processCommand(Command.DOWN);
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			gameController.processCommand(Command.DOWN);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			gameController.processCommand(Command.LEFT);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			gameController.processCommand(Command.RIGHT);
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_MENU:
			Log.v(TAG, "menu key down");
			pause();
			return false;
		case KeyEvent.KEYCODE_BACK:
			Log.v(TAG, "back key down");
			pause();
			return false;
		default:
			return super.onKeyDown(keyCode, event);
		}
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

	private void switchState(State to) {

		if (to == State.PAUSED) {
			SoundManager.get(this).pauseBackgroundMusic();
		} else if (to == State.PLAY) {

			SoundManager.get(this).playBackgroundMusic();
		} else if (to == State.ENDING) {
			SoundManager.get(this).stopBackgroundMusic();
		} else if (to == State.END) {

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
		this.gameController.finishAnimation();
		outState.putSerializable(Constants.PLAYGROUND_STATE, gameController
				.getPlayground().getSavablePlayground());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.v(TAG, "onRestoreInstanceState");
		SavablePlayground sp = (SavablePlayground) (savedInstanceState
				.getSerializable(Constants.PLAYGROUND_STATE));

		if (sp != null) {
			this.gameController.getPlayground().restoreSavablePlayground(sp);
		}
		initByRestoredState();
		super.onRestoreInstanceState(savedInstanceState);

	}

	@Override
	protected void onPause() {
		Log.v(TAG, "pause");
		pause();
		SoundManager.get(this).stopBackgroundMusic();
		this.stopListening();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PREFERENCE_REQUEST_CODE) {
			this.preferenceChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	class ControlButtonClicked implements GameControlListener {
		public void buttonClickced(int id) {
			switch (id) {
			case ControlView.BTN_LEFT:
				gameController.processCommand(Command.LEFT);
				break;
			case ControlView.BTN_RIGHT:
				gameController.processCommand(Command.RIGHT);
				break;
			case ControlView.BTN_TURN:
				gameController.processCommand(Command.TURN);
				break;
			case ControlView.BTN_DOWN:
				gameController.processCommand(Command.DOWN);
				break;
			case ControlView.BTN_DIRECT_DOWN:
				gameController.processCommand(Command.DIRECT_DOWN);
				break;
			}
		}

		public void buttonPressed(int id) {
			Log.v(TAG, "button down : " + id);
			switch (id) {
			case ControlView.BTN_LEFT:
				gameController.processCommand(Command.LEFT_DOWN);
				break;
			case ControlView.BTN_RIGHT:
				gameController.processCommand(Command.RIGHT_DOWN);
				break;
			case ControlView.BTN_TURN:
				gameController.processCommand(Command.TURN_DOWN);
				break;
			case ControlView.BTN_DOWN:
				gameController.processCommand(Command.DOWN_DOWN);
				break;
			case ControlView.BTN_DIRECT_DOWN:
				gameController.processCommand(Command.DIRECT_DOWN);
				break;
			}
		}

		public void buttonReleased(int id) {

			Log.v(TAG, "button up : " + id);
			switch (id) {
			case ControlView.BTN_LEFT:
				gameController.processCommand(Command.LEFT_UP);
				break;
			case ControlView.BTN_RIGHT:
				gameController.processCommand(Command.RIGHT_UP);
				break;
			case ControlView.BTN_TURN:
				gameController.processCommand(Command.TURN_UP);
				break;
			case ControlView.BTN_DOWN:
				gameController.processCommand(Command.DOWN_UP);
				break;
			}
		}
	}

	class GameFinished implements GameListener {

		public void gameFinishing() {

			PlaygroundActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					stop();
				}
			});

		}

		public void gameFinished() {

			PlaygroundActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					switchState(State.END);
					showSuccessScreen();
				}
			});

		}
	}

	class ButtonsOnClick implements OnClickListener {
		public void onClick(View view) {
			SoundManager.get(PlaygroundActivity.this).playButtonClickEffect();
			if (view.getId() == R.id.replay_button) {
				hideSuccessScreen();
				replay();
			} else if (view.getId() == R.id.submit_button) {
				if (((Button) view)
						.getText()
						.equals(
								PlaygroundActivity.this
										.getString(R.string.success_screen_submit_score))) {
					if (Configuration.config().getDifficulty() == Configuration.DIFFICULTY_STANDARD) {
						PlaygroundActivity.this.submitRecord();
					} else {
						DialogUtil
								.showDialog(
										PlaygroundActivity.this,
										"Record Submission Denied",
										"The current game difficulty setting is not STANDARD. Only records of STANDARD mode can be submitted.");
					}
				} else {
					DialogUtil.showRankDialog(PlaygroundActivity.this);
				}
			} else if (view.getId() == R.id.back_button
					|| view.getId() == R.id.quit_button) {
				PlaygroundActivity.this.finish();
			} else if (view.getId() == R.id.resume_button) {
				resume();
			} else if (view.getId() == R.id.options_button) {
				Intent intent = new Intent(PlaygroundActivity.this,
						Preferences.class);
				startActivity(intent);
			}
		}
	}

	private void showSuccessScreen() {
		Log.v(TAG, "showing screen");
		long time = 10000;
		int title = R.string.prompt_title_failed;
		int desc = R.string.prompt_faied;
		if (time / 1000 >= 20) {
			title = R.string.prompt_title_succeed;
			desc = R.string.prompt_succeed;
		}
		this.successScreen.setVisibility(View.VISIBLE);
		((TextView) this.findViewById(R.id.success_screen_title))
				.setText(title);
		((TextView) this.findViewById(R.id.success_screen_desc)).setText(desc);

		TextView timeText = ((TextView) this.findViewById(R.id.time_text));
		timeText.setText(time / 1000 + "." + time % 1000 + " seconds");

		MilitaryRank rank = MilitaryRank.getRank(time, 0);

		TextView rankText = ((TextView) this.findViewById(R.id.rank_text));
		rankText.setText(rank.getStringID());
		ImageView rankImage = ((ImageView) this.findViewById(R.id.rank_image));
		rankImage.setImageResource(rank.getDrawableID());

		this.successScreenSubmitButton
				.setText(R.string.success_screen_submit_score);
	}

	private void hideSuccessScreen() {
		Log.v(TAG, "hiding screen");
		this.successScreen.setVisibility(View.INVISIBLE);
	}

	private void showPauseScreen() {
		Log.v(TAG, "showing pause screen");

		this.pausedScreen.setVisibility(View.VISIBLE);
	}

	private void hidePauseScreen() {
		Log.v(TAG, "hiding pause screen");
		this.pausedScreen.setVisibility(View.INVISIBLE);
	}

	public void play() {
		if (this.state != State.PAUSED && this.state != State.END) {
			return;
		}
		gameController.start();
		switchState(State.PLAY);
	}

	public void pause() {
		gameController.pause();
		switchState(State.PAUSED);
		showPauseScreen();
	}

	public void resume() {
		gameController.start();
		switchState(State.PLAY);
		hidePauseScreen();
	}

	public void replay() {
		gameController.getPlaygroundView().reset();
		play();
	}

	public void stop() {
		switchState(State.ENDING);
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
		int control = Configuration.config().getControl();
		if (control == Configuration.CONTROL_JOY_STICK) {
			this.useAccelerometer = false;
			this.useJoyStick = true;
		} else if (control == Configuration.CONTROL_SENSOR) {
			this.useAccelerometer = true;
			this.useJoyStick = false;
		} else {
			this.useAccelerometer = true;
			this.useJoyStick = true;
		}
		if (this.useAccelerometer && !this.sensorRunning) {
			this.startListening();
		} else if (!this.useAccelerometer && this.sensorRunning) {
			this.stopListening();
		}
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
						PreferenceUtil.writePlayerName(PlaygroundActivity.this,
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
			PlaygroundActivity.this.runOnUiThread(new Runnable() {

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

	// accelerometer

	public void startListening() {
		sensorManager = (SensorManager) gameController.getPlaygroundView()
				.getContext().getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> sensors = sensorManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			sensor = sensors.get(0);
			sensorRunning = sensorManager.registerListener(
					accelerometerListener, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	/**
	 * Unregisters listeners
	 */
	public void stopListening() {
		sensorRunning = false;
		try {
			if (sensorManager != null && accelerometerListener != null) {
				sensorManager.unregisterListener(accelerometerListener);
			}
		} catch (Exception e) {
		}
	}

	class AccelerometerListener implements SensorEventListener {

		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		public void onSensorChanged(SensorEvent event) {
			if (!useAccelerometer) {
				return;
			}
			float x = event.values[0];
			float y = event.values[1];
		}
	}

	class MyJoyStickListener implements JoyStickListener {

		public void joyStickMoved(float dx, float dy) {
			Log.v(TAG, "joy stick moved: dx=" + dx + ", dy=" + dy);
			if (!useJoyStick) {
				return;
			}
			if (Math.abs(dx) > Math.abs(dy)) {
				if (dx > 0.5f) {
					gameController.processCommand(Command.RIGHT);
				} else if (dx < -0.5f) {
					gameController.processCommand(Command.LEFT);
				}
			} else {
				if (dy < 0 - .5f) {
					gameController.processCommand(Command.TURN);
				}
			}
			gameController.getPlaygroundView().invalidate();

		}

	}

	public void customizeButtons() {
		if (buttonFont == null) {

			buttonFont = Typeface.createFromAsset(getAssets(),
					Constants.FONT_PATH_SCRIPT);
		}
		customizeButton((Button) this.findViewById(R.id.submit_button));
		customizeButton((Button) this.findViewById(R.id.replay_button));
		customizeButton((Button) this.findViewById(R.id.back_button));
	}

	private void customizeButton(Button button) {
		button.setTypeface(buttonFont);
		button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
	}

}