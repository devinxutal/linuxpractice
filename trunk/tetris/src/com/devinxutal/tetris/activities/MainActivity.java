package com.devinxutal.tetris.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.devinxutal.tetris.R;
import com.devinxutal.tetris.cfg.Configuration;
import com.devinxutal.tetris.cfg.Constants;
import com.devinxutal.tetris.sound.SoundManager;
import com.devinxutal.tetris.util.AdUtil;
import com.devinxutal.tetris.util.DialogUtil;

public class MainActivity extends Activity implements OnClickListener {
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAit2OZ8OaxRwh8B9Du45ejqWe4XaWVp4RwD4Du0j1S7ZokOAJqYlu4rEQwdz9mGLqLV3I9fJhmgryXjqMpxl5+wODxS7FNvy6uRNzVLTbXdD/vvIt5CucAXRs9xFHj1gT212m59q2dw6iAT4E6dRhnzWIT48v9YYVW4iWymgnQYW7WMxTuk56bBp37VW0qZ9V+D+PrYlRkklSezBe2fVsOVuv09nLkD2sRTQVyvm0fW4P6Q9/7yUx2HJ5p1TQvtHe2ZaRUJuRG5+ZR3/gdGcaEfRXopB59S0DWhNyzMkw2RZRzUtg0N6H9PIbodHab04/qZCM86jTtRUDmQFFnp4/ZQIDAQAB";

	private static final byte[] SALT = new byte[] { -46, 65, 30, -128, -103,
			-57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64,
			89 };

	private static final String TAG = "MainActvity";

	private Typeface buttonFont;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
				R.id.main_btn_preference, R.id.main_btn_more,
				R.id.main_btn_rank, R.id.main_btn_help };
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SoundManager.release();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected void onResume() {
		super.onResume();
	}

	public void onClick(View view) {
		Intent i = null;
		Log.v(TAG, "before play effect");
		SoundManager.get(this).playButtonClickEffect();
		Log.v(TAG, "after play effect");
		switch (view.getId()) {
		case R.id.main_btn_play_game:
			i = new Intent(this, PlaygroundActivity.class);
			break;

		case R.id.main_btn_preference:
			i = new Intent(this, Preferences.class);
			break;

		case R.id.main_btn_more:
			i = new Intent(Intent.ACTION_SEARCH);
			i.setPackage("com.android.vending");
			i.putExtra("query", "pub:\"Yinfei XU\"");
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			break;
		case R.id.main_btn_rank:
			i = new Intent(this, HighScoreActivity.class);
			// DialogUtil.showRankDialog(this);
			break;
		case R.id.main_btn_help:
			DialogUtil
					.showDialogWithView(this, "Go Tetris Help", R.layout.help);
			return;
		}
		startActivity(i);
	}

	public void customizeButtons() {
		if (buttonFont == null) {

			buttonFont = Typeface.createFromAsset(getAssets(),
					Constants.FONT_PATH_COMIC);
		}
		customizeButton((Button) this.findViewById(R.id.main_btn_more));
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
}
