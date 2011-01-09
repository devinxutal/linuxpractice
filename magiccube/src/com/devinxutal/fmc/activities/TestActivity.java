package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;

import com.devinxutal.fmc.control.CubeController;
import com.devinxutal.fmc.ui.CubeControlView;

public class TestActivity extends Activity {
	CubeController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CubeControlView view = new CubeControlView(this);
		setContentView(view);
	}
}