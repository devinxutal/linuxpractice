package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;

import com.devinxutal.fmc.control.CubeController;
import com.devinxutal.fmc.control.IMoveSequence;
import com.devinxutal.fmc.control.InfiniteMoveSequence;
import com.devinxutal.fmc.control.Move;
import com.devinxutal.fmc.control.MoveController;
import com.devinxutal.fmc.control.MoveSequence;

public class CubeSolverActivity extends Activity {

	CubeController controller;
	MoveController mController;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		controller = new CubeController(this);
		mController = new MoveController(controller);
		setContentView(controller.getCubeView());

		shuffle();
	}

	private void shuffle() {
		IMoveSequence seq = new InfiniteMoveSequence(controller.getMagicCube()
				.getOrder());
		Move move = null;
		MoveSequence seqq = new MoveSequence();
		while (seq.currentMoveIndex() < 40) {
			move = seq.step();
			seqq.addMove(move);
		}
		mController.startMove(seqq);
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