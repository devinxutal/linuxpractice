package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.cfg.Configuration;
import com.devinxutal.fmc.solver.CfopSolver;

public class MainActivity extends Activity implements OnClickListener {

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
		int ids[] = new int[] { R.id.main_btn_free_play,
				R.id.main_btn_time_play, R.id.main_btn_preference,
				R.id.main_btn_cfop_viewer, R.id.main_btn_cube_solver,
				R.id.main_btn_about };
		for (int id : ids) {
			try {
				this.findViewById(id).setOnClickListener(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		CfopSolver.getSolver(this);
	}

	public void onClick(View view) {
		Intent i = null;
		switch (view.getId()) {
		case R.id.main_btn_free_play:
			i = new Intent(this, MagicCubeActivity.class);
			i.putExtra("timedMode", false);
			break;
		case R.id.main_btn_time_play:
			i = new Intent(this, MagicCubeActivity.class);
			i.putExtra("timedMode", true);
			break;
		case R.id.main_btn_preference:
			i = new Intent(this, Preferences.class);
			break;
		case R.id.main_btn_cfop_viewer:
			i = new Intent(this, CfopViewerActivity.class);
			break;
		case R.id.main_btn_cube_solver:
			i = new Intent(this, CubeCapturingActivity.class);
			break;
		case R.id.main_btn_about:
			i = new Intent(this, AboutActivity.class);
			break;
		}
		startActivity(i);
	}
}
