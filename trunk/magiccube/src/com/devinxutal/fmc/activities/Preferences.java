package com.devinxutal.fmc.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.devinxutal.fmc.R;

public class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}