package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.devinxutal.fmc.ui.MoveSequenceIndicator;

public class CubeDemostratorActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView(new CubeDemostrator(this));
		
		setContentView(new MoveSequenceIndicator(this));
	}
}