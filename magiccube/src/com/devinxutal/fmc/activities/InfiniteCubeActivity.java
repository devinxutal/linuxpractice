package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;

import com.devinxutal.fmc.control.CubeController;
import com.devinxutal.fmc.control.InfiniteMoveSequence;
import com.devinxutal.fmc.control.MoveController;

public class InfiniteCubeActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CubeController controller = new CubeController(this);
		MoveController mController = new MoveController(controller);
		setContentView(controller.getCubeView());
		mController.startMove(new InfiniteMoveSequence(controller
				.getMagicCube().getOrder()));
	}
}