package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.devinxutal.fmc.ui.CubeDemostrator;
import com.devinxutal.fmc.util.SymbolMoveUtil;

public class CubeDemostratorActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String sequence = " R2 R'2 u' u2 d'2 R L U D F B R' L' U' D' F' B' r l u d f b r' l' u' d' f' b')";
		View v = new CubeDemostrator(this, SymbolMoveUtil
				.parseSymbolSequenceAsArray(sequence));
		setContentView(v);
	}
}