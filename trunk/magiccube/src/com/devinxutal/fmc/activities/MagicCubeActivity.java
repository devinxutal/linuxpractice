package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.control.CubeController;
import com.devinxutal.fmc.ui.CubeDemostrator;
import com.devinxutal.fmc.ui.MoveSequenceIndicator;

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

		controller = new CubeController(this, false);

		setContentView(controller.getCubeView());
		
		View v = new CubeDemostrator(this, new String[] { "R", "L", "r", "F'",
				"R2'", "R", "D","L", "r", "F'", "R2'" });
setContentView(v);

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