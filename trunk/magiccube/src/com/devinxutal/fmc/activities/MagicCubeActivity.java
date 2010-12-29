package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.control.CubeController;

public class MagicCubeActivity extends Activity {
	private CubeController controller;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		/*
		 * this.requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)
		 */
		super.onCreate(savedInstanceState);

		controller = new CubeController(this);

		setContentView(controller.getCubeView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.presentation:
			controller.startPresentation("URFDLBU'R'F'D'L'B'");
			return true;
		case R.id.preferences:
			Intent preferencesActivity = new Intent(getBaseContext(),
					Preferences.class);
			startActivity(preferencesActivity);
			return true;
		case R.id.test:
			Intent a = new Intent(getBaseContext(),
					CubeDemostratorActivity.class);
			startActivity(a);
			return true;
		}
		return false;
	}

}