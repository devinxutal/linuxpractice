package cn.perfectgames.jewels.activities;

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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.perfectgames.jewels.GoJewelsApplication;
import cn.perfectgames.jewels.R;
import cn.perfectgames.jewels.cfg.Configuration;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.model.GameMode;
import cn.perfectgames.jewels.sound.SoundManager;
import cn.perfectgames.jewels.util.AdDaemon;
import cn.perfectgames.jewels.util.AdUtil;
import cn.perfectgames.jewels.util.BitmapUtil;
import cn.perfectgames.jewels.util.MarketUtil;
import cn.perfectgames.jewels.util.PreferenceUtil;

public class MainActivity extends BaseActivity implements OnClickListener {
	

	private static final String TAG = "MainActvity";

	private Typeface buttonFont;
	private Dialog progressDialog;

	public static final int PLAYGROUND_ACTIVITY_ID = 1151;

	private Handler adHandler;

	private AdDaemon adDaemon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		globalInit();
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)
		Configuration.config()
				.setSharedPreferences(
						PreferenceManager
								.getDefaultSharedPreferences(getBaseContext()));
		this.setContentView(R.layout.main);
		SoundManager.init(this);
		customizeButtons();
		int ids[] = new int[] { R.id.main_btn_play_game,
				R.id.main_btn_preference, R.id.main_btn_rank,
				R.id.main_btn_help };
		for (int id : ids) {
			try {
				this.findViewById(id).setOnClickListener(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		AdUtil.determineAd(this, R.id.ad_area);
		// for lisencing
		// Try to use more data here. ANDROID_ID is a single point of attack.
		String deviceId = Secure.getString(getContentResolver(),
				Secure.ANDROID_ID);
		//
		showNoticeDialog();
		// AdManager.setTestDevices(new String[] { AdManager.TEST_EMULATOR,
		// "7037AF0B100BC4F3CC9F4B5401F96685" });
		adHandler = new Handler();
		this.adDaemon = new AdDaemon("main", this,
				this.findViewById(Constants.ADVIEW_ID), adHandler);
		adDaemon.run();

	}

	public void globalInit() {
		BitmapUtil.get(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		adDaemon.stop();
		SoundManager.release();
	}

	@Override
	protected void onPause() {
		adDaemon.stop();
		super.onPause();
	}

	protected void onResume() {
		adDaemon.run();
		super.onResume();
	}

	public void onClick(View view) {
		Intent i = null;
		int startID = -1;
		Log.v(TAG, "before play effect");
		SoundManager.get(this).playButtonClickEffect();
		Log.v(TAG, "after play effect");
		switch (view.getId()) {
		case R.id.main_btn_play_game:
			this.showModeSelectionDialog();

			return;

		case R.id.main_btn_preference:
			i = new Intent(this, Preferences.class);
			break;

		case R.id.main_btn_rank:
			// i = new Intent(this, HighScoreActivity.class);
			// TODO
			i = new Intent(this, LeaderBoardActivity.class);
			// DialogUtil.showRankDialog(this);
			break;
		case R.id.main_btn_help:
			i = new Intent(this, AnimationTestActivity.class);
			// DialogUtil
			// .showDialogWithView(this, "Go Tetris Help", R.layout.help);
			// return;
			// break;
		}
		if (startID >= 0) {
			this.startActivityForResult(i, startID);
		} else {
			startActivity(i);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				|| event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
			Log.v(TAG, "back key down");
			this.showQuitDialog();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void customizeButtons() {
		if (buttonFont == null) {

			buttonFont = Typeface.createFromAsset(getAssets(),
					Constants.FONT_PATH_COMIC);
		}
		customizeButton((Button) this.findViewById(R.id.main_btn_play_game));
		customizeButton((Button) this.findViewById(R.id.main_btn_preference));
		customizeButton((Button) this.findViewById(R.id.main_btn_rank));
		customizeButton((Button) this.findViewById(R.id.main_btn_help));
	}

	private void customizeButton(Button button) {
		button.setTypeface(buttonFont);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int width = Math.min(display.getWidth(), display.getHeight());
		if (width < 320) {
			button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
		}
		// button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
	}

	private void showModeSelectionDialog() {
		final CharSequence[] items = {
				getResources().getString(R.string.mode_normal),
				getResources().getString(R.string.mode_timed),
				getResources().getString(R.string.mode_quick),
				getResources().getString(R.string.mode_infinite) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(
				getResources().getString(R.string.mode_title));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				GameMode mode = GameMode.Normal;
				
				for(GameMode m: GameMode.values()){
					if(m.ordinal() == item){
						mode = m;
					}
				}
				GoJewelsApplication.setGameMode(mode);
				Intent intent = new Intent(MainActivity.this, PlaygroundActivity.class);
				startActivityForResult(intent, PLAYGROUND_ACTIVITY_ID);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showQuitDialog() {
		final CharSequence[] items = { "Give us feedback", "Rate Go Tetris",
				"More games", "Quit game" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Go Tetris");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					MainActivity.this.submitReport();
				} else if (item == 1) {
					MarketUtil.openMarketForApp(MainActivity.this,
							"com.devinxutal.tetris");

					// TODO check this
					// Intent i = new Intent(Intent.ACTION_VIEW, Uri
					// .parse("market://details?id=com.devinxutal.tetris"));
					// startActivity(i);
				} else if (item == 2) {
					MarketUtil.openMarketForAuthor(MainActivity.this,
							"Yinfei XU");
					// TODO check this
					// Intent i = new Intent(Intent.ACTION_SEARCH);
					// i.setPackage("com.android.vending");
					// i.putExtra("query", "pub:\"Yinfei XU\"");
					// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// startActivity(i);
				} else if (item == 3) {

					MainActivity.this.finish();
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showCommitReportDialog() {

	}

	public void submitReport() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Feedback");
		final EditText input = new EditText(this);
		input.setText("");
		LinearLayout layout = new LinearLayout(this);
		layout.setPadding(20, 10, 20, 10);
		layout.addView(input, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		alert.setView(layout);

		alert.setPositiveButton(R.string.common_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						submitReport(input.getText().toString());
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

	private void submitReport(String report) {
		progressDialog = ProgressDialog.show(this, "", getResources()
				.getString(R.string.record_submit_submitting_record), true);
		new SubmitRecordThread(report).start();
	}

	class SubmitRecordThread extends Thread {
		private String report;

		public SubmitRecordThread(String report) {
			this.report = report;
		}

		private String getInfo() {
			String info = "";
			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();
			int w = display.getWidth();
			int h = display.getHeight();
			info += "SCREEN[" + Math.min(w, h) + "x" + Math.max(w, h) + "]";
			info += ", ANDROID[" + android.os.Build.VERSION.SDK + "]";
			return info;
		}

		@Override
		public void run() {
			String url = Constants.URL_COMMIT_REPORT;

			Map<String, String> data = new HashMap<String, String>();
			data.put("app", "Go Tetris");
			data.put("report", report);
			data.put("info", getInfo());
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
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (progressDialog != null) {
						progressDialog.cancel();
						progressDialog = null;
					}
					if (sc == HttpStatus.SC_OK) {
						Toast.makeText(MainActivity.this,
								"Your feedback has been submitted. Thankyou!",
								1000).show();
					} else {
						Toast.makeText(
								MainActivity.this,
								"Submit failed, please check your network connection.",
								1000).show();
					}
				}

			});
		}
	}

	private void showNoticeDialog() {
		if (PreferenceUtil.canShowUpgradeNotice(MainActivity.this)) {
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout l = new LinearLayout(this);
			inflater.inflate(R.layout.whats_new, l);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("What's New")
					.setCancelable(false)
					.setPositiveButton(R.string.common_ok, null)
					.setPositiveButton("OK", null)
					.setNegativeButton("Never Show Again",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									PreferenceUtil.setShowUpgradeNotice(
											MainActivity.this, false);
								}
							}).setView(l);
			AlertDialog alert = builder.create();

			// alert.setContentView(view_id);
			alert.show();
		}
	}
}
