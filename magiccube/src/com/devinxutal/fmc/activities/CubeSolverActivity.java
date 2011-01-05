package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.control.CubeController;
import com.devinxutal.fmc.control.IMoveSequence;
import com.devinxutal.fmc.control.InfiniteMoveSequence;
import com.devinxutal.fmc.control.Move;
import com.devinxutal.fmc.control.MoveController;
import com.devinxutal.fmc.control.MoveSequence;
import com.devinxutal.fmc.solver.CfopSolver;

public class CubeSolverActivity extends Activity {

	CubeController controller;
	MoveController mController;
	CfopSolver solver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		controller = new CubeController(this);
		mController = new MoveController(controller);
		setContentView(controller.getCubeView());
		solver = new CfopSolver();
		try {
			solver.init(getAssets().open("algorithm/cfop"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// shuffle();
		t = Toast.makeText(this, "", 500);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.cube_solver, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.start_solve:
			solveCube();
			return true;

		}
		return false;
	}

	Toast t;

	private void solveCube() {
		MoveSequence seq = solver.nextMoves(controller.getMagicCube());
		if (seq != null) {
			t.setText("find moves, length: " + seq.totalMoves());
			t.show();
			mController.startMove(seq);
		}
	}
}