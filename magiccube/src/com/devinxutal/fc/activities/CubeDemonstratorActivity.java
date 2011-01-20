package com.devinxutal.fc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.devinxutal.fc.R;
import com.devinxutal.fc.cfg.Configuration;
import com.devinxutal.fc.model.CubeState;
import com.devinxutal.fc.ui.CubeDemonstrator;
import com.devinxutal.fc.util.SymbolMoveUtil;

public class CubeDemonstratorActivity extends Activity {
	public static final int PREFERENCE_REQUEST_CODE = 0x100;

	private CubeDemonstrator demostrator;

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

		demostrator = new CubeDemonstrator(this, state, SymbolMoveUtil
				.parseSymbolSequenceAsArray(sequence));
		demostrator.getMoveController().setMoveInterval(
				Configuration.config().getRotationInterval());
		setContentView(demostrator);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.demostrator.onDestroy();
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
		if (demostrator.getMoveController().getMoveInterval() != Configuration
				.config().getRotationInterval()) {
			demostrator.getMoveController().setMoveInterval(
					Configuration.config().getRotationInterval());
		}
	}

	@Override
	protected void onPause() {
		this.demostrator.getCubeController().getCubeView().onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		this.demostrator.getCubeController().getCubeView().onResume();
		super.onResume();
	}

}