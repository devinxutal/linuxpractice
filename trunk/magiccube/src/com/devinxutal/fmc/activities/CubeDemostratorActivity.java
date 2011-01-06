package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.devinxutal.fmc.model.CubeState;
import com.devinxutal.fmc.model.CubeState.StateEntry;
import com.devinxutal.fmc.ui.CubeDemostrator;
import com.devinxutal.fmc.util.SymbolMoveUtil;

public class CubeDemostratorActivity extends Activity {

	CubeDemostrator demostrator;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String sequence = getIntent().getStringExtra("formula");
		// String sequence =
		// "( R2 R'2 )( u' u2 d'2) R L U D F B R' L' U' D' F' B' r l u d f b r' l' u' d' f' b')";
		demostrator = new CubeDemostrator(this, SymbolMoveUtil
				.parseSymbolSequenceAsArray(sequence));

		CubeState state = (CubeState) getIntent().getSerializableExtra("model");
		if (state != null) {
			for (StateEntry ent : state.entries) {
				Log.v("CubeDemostratorActivity", ent.x + "," + ent.y + ","
						+ ent.z + ": " + ent.color.toString());
			}
			demostrator.getCubeController().getMagicCube().setCubeState(state);
		}
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