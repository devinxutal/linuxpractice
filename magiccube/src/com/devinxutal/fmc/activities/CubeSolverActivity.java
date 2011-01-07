package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
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
import com.devinxutal.fmc.control.MoveControllerListener;
import com.devinxutal.fmc.control.MoveSequence;
import com.devinxutal.fmc.control.MoveController.State;
import com.devinxutal.fmc.solver.CfopSolver;

public class CubeSolverActivity extends Activity implements
		MoveControllerListener {

	CubeController controller;
	MoveController mController;
	CfopSolver solver;

	private boolean continuesly = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		controller = new CubeController(this);
		mController = new MoveController(controller);
		setContentView(controller.getCubeView());
		solver = new CfopSolver();
		try {
			solver.init(getAssets().open("algorithm/cfop_pattern"), getAssets()
					.open("algorithm/cfop_algorithm"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// shuffle();
		t = Toast.makeText(this, "", 10000);
		mController.addMoveControllerListener(this);
	}

	private void shuffle(int step) {
		IMoveSequence seq = new InfiniteMoveSequence(controller.getMagicCube()
				.getOrder());
		Move move = null;
		MoveSequence seqq = new MoveSequence();
		while (seq.currentMoveIndex() < step) {
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
		case R.id.solver_solve_cont:
			if (!controller.getMagicCube().solved()) {
				continuesly = true;
				solveCube();
			}
			return true;
		case R.id.solver_solve:
			continuesly = false;
			solveCube();
			return true;
		case R.id.solver_shuffle:
			shuffle(40);
			return true;

		}
		return false;
	}

	Toast t;

	private void solveCube() {
		MoveSequence seq = solver.nextMoves(controller.getMagicCube());
		if (seq != null) {
			t.setText(solver.getMessage());
			t.setDuration(10000);
			t.setGravity(Gravity.TOP, 0, 0);
			t.show();
			mController.startMove(seq);
		}
	}

	public void moveSequenceStepped(int index) {
		// TODO Auto-generated method stub
	}

	public void statusChanged(State from, State to) {
		if (from == State.RUNNING_MULTPLE_STEP && to == State.STOPPED) {
			if (continuesly) {
				this.runOnUiThread(new Runnable() {
					public void run() {
						if (controller.getMagicCube().solved()) {
							continuesly = false;
						} else {
							solveCube();
						}
					}
				});

			}
		}
	}

}