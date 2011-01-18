package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

import com.devinxutal.fmc.cfg.Configuration;
import com.devinxutal.fmc.model.CubeState;
import com.devinxutal.fmc.ui.CubeDemonstrator;
import com.devinxutal.fmc.util.SymbolMoveUtil;

public class CubeDemonstratorActivity extends Activity {

	CubeDemonstrator demostrator;

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

		setContentView(demostrator);
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