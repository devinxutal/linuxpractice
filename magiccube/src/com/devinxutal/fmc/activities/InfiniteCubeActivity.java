package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.devinxutal.fmc.cfg.Configuration;
import com.devinxutal.fmc.control.CubeController;
import com.devinxutal.fmc.control.InfiniteMoveSequence;
import com.devinxutal.fmc.control.MoveController;

public class InfiniteCubeActivity extends Activity {

	CubeController controller;
	MoveController mController;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Configuration.config()
		.setSharedPreferences(
				PreferenceManager
						.getDefaultSharedPreferences(getBaseContext()));
		controller = new CubeController(this);
		mController = new MoveController(controller);
		setContentView(controller.getCubeView());
		mController.startMove(new InfiniteMoveSequence(controller
				.getMagicCube().getOrder()));
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

}