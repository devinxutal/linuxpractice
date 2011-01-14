package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.cfg.Configuration;
import com.devinxutal.fmc.cfg.Constants;
import com.devinxutal.fmc.control.CubeController;
import com.devinxutal.fmc.model.CubeState;
import com.devinxutal.fmc.ui.CubeControlView;

public class MagicCubeActivity extends Activity {
	private CubeController controller;

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
		controller = new CubeController(this, true, true);
		controller.getCubeView().setId(12345676);
		FrameLayout layout = new FrameLayout(this);
		layout.addView(controller.getCubeView());
		CubeControlView controlView = new CubeControlView(this);
		layout.addView(controlView);
		controlView.setCubeController(controller);
		setContentView(layout);

	}

	private void restoreInstanceState(Bundle inState) {
		CubeState state = (CubeState) inState
				.getSerializable(Constants.CUBE_STATE);
		if (state != null) {
			this.controller.getMagicCube().setCubeState(state);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(Constants.CUBE_STATE, controller
				.getMagicCube().getCubeState());
	}

	@Override
	protected void onPause() {
		this.controller.getCubeView().onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		this.controller.getCubeView().onResume();
		super.onResume();
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
		case R.id.preferences:
			Intent preferencesActivity = new Intent(getBaseContext(),
					Preferences.class);
			startActivity(preferencesActivity);
			return true;
		case R.id.demostrator:
			Intent a = new Intent(getBaseContext(),
					CubeDemostratorActivity.class);
			startActivity(a);
			return true;
		case R.id.infinite:
			Intent b = new Intent(this, InfiniteCubeActivity.class);
			startActivity(b);
			return true;
		case R.id.solver:
			Intent c = new Intent(this, CubeSolverActivity.class);
			startActivity(c);
			return true;
		case R.id.cfopviewer:
			Intent d = new Intent(this, CfopViewerActivity.class);
			startActivity(d);
			return true;

		case R.id.test:
			Intent e = new Intent(this, TestActivity.class);
			startActivity(e);
			return true;
		}
		return false;
	}

}