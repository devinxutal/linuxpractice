package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.devinxutal.fmc.ui.CubeDemostrator;

public class CubeDemostratorActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View v = new CubeDemostrator(this, new String[] { "R", "L", "r2", "F'",
				"R2'" });
		setContentView(v);
	}
}