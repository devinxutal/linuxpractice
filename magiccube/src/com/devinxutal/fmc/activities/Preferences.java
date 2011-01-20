package com.devinxutal.fmc.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Window;
import android.view.WindowManager;

import com.devinxutal.fmc.R;

public class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)
		super.onCreate(savedInstanceState);
		int resid = this.getIntent().getIntExtra("pref_res", R.xml.preferences);
		addPreferencesFromResource(resid);
	}
}