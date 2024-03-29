package com.devinxutal.fc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.devinxutal.fc.R;
import com.devinxutal.fc.cfg.Configuration;
import com.devinxutal.fc.cfg.Constants;
import com.devinxutal.fc.model.CubeState;
import com.devinxutal.fc.ui.CubeDemonstrator;
import com.devinxutal.fc.util.AdDaemon;
import com.devinxutal.fc.util.AdUtil;
import com.devinxutal.fc.util.SymbolMoveUtil;

public class CubeDemonstratorActivity extends Activity {
	public static final int PREFERENCE_REQUEST_CODE = 0x100;

	private CubeDemonstrator demonstrator;

	// for ad animation
	private Handler adHandler = new Handler();
	private AdDaemon adDaemon;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)
		Configuration.config()
				.setSharedPreferences(
						PreferenceManager
								.getDefaultSharedPreferences(getBaseContext()));

		String sequence = getIntent().getStringExtra("formula");
		CubeState state = (CubeState) getIntent().getSerializableExtra("model");

		demonstrator = new CubeDemonstrator(this, state, SymbolMoveUtil
				.parseSymbolSequenceAsArray(sequence));
		demonstrator.getMoveController().setMoveInterval(
				Configuration.config().getRotationInterval());
		setContentView(R.layout.adframe);
		AdUtil.determineAd(this);
		((LinearLayout) this.findViewById(R.id.content_area))
				.addView(demonstrator);
		//
		View view = (View) this.findViewById(R.id.ad_area);
		View ad = null;
		if (view != null) {
			ad = view.findViewById(Constants.ADVIEW_ID);
		}
		adDaemon = new AdDaemon("cube", this, ad, adHandler);

		adDaemon.run();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		adDaemon.stop();
		this.demonstrator.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.demo_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.preferences:
			Intent preferencesActivity = new Intent(getBaseContext(),
					Preferences.class);
			preferencesActivity.putExtra("pref_res",
					R.xml.preferences_demonstrator);
			startActivityForResult(preferencesActivity, PREFERENCE_REQUEST_CODE);
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PREFERENCE_REQUEST_CODE) {
			this.preferenceChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void preferenceChanged() {
		if (demonstrator.getMoveController().getMoveInterval() != Configuration
				.config().getRotationInterval()) {
			demonstrator.getMoveController().setMoveInterval(
					Configuration.config().getRotationInterval());
		}
	}

	@Override
	protected void onPause() {
		this.demonstrator.getCubeController().getCubeView().onPause();
		this.adDaemon.stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		this.demonstrator.getCubeController().getCubeView().onResume();
		this.adDaemon.run();
		super.onResume();
	}

}