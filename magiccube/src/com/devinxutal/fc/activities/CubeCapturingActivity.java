package com.devinxutal.fc.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.devinxutal.fc.cfg.Configuration;
import com.devinxutal.fc.control.Move;
import com.devinxutal.fc.control.MoveSequence;
import com.devinxutal.fc.model.CubeColor;
import com.devinxutal.fc.model.CubeState;
import com.devinxutal.fc.model.MagicCube;
import com.devinxutal.fc.solver.CfopSolver;
import com.devinxutal.fc.ui.CubeCameraPreview;
import com.devinxutal.fc.util.SymbolMoveUtil;
import com.devinxutal.fmc.R;

public class CubeCapturingActivity extends Activity {
	private static final String TAG = "CameraDemo";
	CubeCameraPreview preview;
	ImageButton buttonClick;

	TextView stepView;
	Button goButton;
	TextView description;
	ProgressDialog progressDialog;

	private int step = 1;
	private CubeState cubeState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Configuration.config()
				.setSharedPreferences(
						PreferenceManager
								.getDefaultSharedPreferences(getBaseContext()));

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		cubeState = new CubeState();
		cubeState.order = 3;

		setContentView(R.layout.capture_cube_from_camera_step1);
		stepView = (TextView) findViewById(R.id.step);
		description = (TextView) findViewById(R.id.description);
		goButton = (Button) findViewById(R.id.goButton);
		goButton.setOnClickListener(new GoButtonListener());

		//
		switchStep(1);
	}

	class GoButtonListener implements OnClickListener {
		public void onClick(View view) {
			if (step == 1) {
				Intent i = new Intent(CubeCapturingActivity.this,
						CubeCameraActivity.class);
				startActivityForResult(i, 1);
			} else if (step == 2) {
				Intent i = new Intent(CubeCapturingActivity.this,
						CubeCameraActivity.class);

				startActivityForResult(i, 2);
			} else if (step == 3) {
				solveCurrentCube();

			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 3 && step == 3) {
			this.setResult(RESULT_OK);
			finish();
		}
		if (resultCode == RESULT_OK) {
			if (requestCode == 1 && step == 1) {
				Log.v(TAG, "step 1 finished with ");
				Log
						.v(TAG, "model null? "
								+ ((CubeState) data
										.getSerializableExtra("model") == null));
				CubeState state = (CubeState) data
						.getSerializableExtra("model");
				this.setState(state, step);
				switchStep(2);
			} else if (requestCode == 2 && step == 2) {
				Log.v(TAG, "step 2 finished with ");
				Log
						.v(TAG, "model null? "
								+ ((CubeState) data
										.getSerializableExtra("model") == null));
				CubeState state = (CubeState) data
						.getSerializableExtra("model");
				this.setState(state, step);
				switchStep(3);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void switchStep(int step) {
		if (step == this.step) {
			return;
		} else {
			this.step = step;
			if (step == 1) {
				stepView.setText(R.string.cube_capture_step1_sign);
				description.setText(R.string.cube_capture_step1_desc);
			} else if (step == 2) {
				stepView.setText(R.string.cube_capture_step2_sign);
				description.setText(R.string.cube_capture_step2_desc);
			} else if (step == 3) {
				stepView.setText(R.string.cube_capture_step3_sign);
				description.setText(R.string.cube_capture_step3_desc);
				this.goButton.setText(R.string.common_solve_now);
			}
		}
	}

	private void setState(CubeState state, int step) {
		int order = cubeState.order;
		if (step == 1) {
			for (int i = 1; i <= order; i++) {
				for (int j = 1; j <= order; j++) {
					this.cubeState.add(i, j, order + 1, getColor(state, i, j,
							order + 1));
					this.cubeState.add(i, order + 1, j, getColor(state, i,
							order + 1, j));
					this.cubeState.add(order + 1, i, j, getColor(state,
							order + 1, i, j));
				}
			}
		} else if (step == 2) {
			for (int i = 1; i <= order; i++) {
				for (int j = 1; j <= order; j++) {
					this.cubeState.add(0, order + 1 - j, order + 1 - i,
							getColor(state, i, j, order + 1));
					this.cubeState.add(order + 1 - j, 0, order + 1 - i,
							getColor(state, i, order + 1, j));
					this.cubeState.add(order + 1 - j, order + 1 - i, 0,
							getColor(state, order + 1, i, j));
				}
			}
		}
	}

	private CubeColor getColor(CubeState state, int x, int y, int z) {
		for (CubeState.StateEntry entry : state.entries) {
			if (entry.x == x && entry.y == y && entry.z == z) {
				return entry.color;
			}
		}
		return CubeColor.ANY;
	}

	private void solveCurrentCube() {
		progressDialog = ProgressDialog.show(this, "", getResources()
				.getString(R.string.progress_solving_cube), true);
		new SolveCubeThread().start();
	}

	class SolveCubeThread extends Thread {

		@Override
		public void run() {
			MagicCube cube = new MagicCube(cubeState.order);
			cube.setCubeState(cubeState);
			CfopSolver solver = CfopSolver
					.getSolver(CubeCapturingActivity.this);
			MoveSequence seq = new MoveSequence();
			while (!cube.solved()) {
				MoveSequence s = solver.nextMoves(cube);
				if (s == null) {
					Log.v(TAG, "No solution found for this cube");
					CubeCapturingActivity.this.runOnUiThread(new Runnable() {

						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									CubeCapturingActivity.this);
							builder
									.setTitle(
											R.string.cube_capture_problem_title)
									.setMessage(
											R.string.cube_capture_problem_content)
									.setCancelable(false)
									.setPositiveButton(
											R.string.common_ok,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													CubeCapturingActivity.this
															.finish();
												}
											});
							AlertDialog alert = builder.create();
							alert.show();
						}
					});

					return;
				}
				Move m = null;
				while ((m = s.step()) != null) {
					cube.turn(m.dimension, m.layers, m.direction, m.doubleTurn);
					seq.addMove(m);
				}
			}
			MoveSequence optimizedSeq = SymbolMoveUtil
					.optimizeMoveSequence(seq);

			final Intent intent = new Intent(getBaseContext(),
					CubeDemonstratorActivity.class);
			intent.putExtra("model", cubeState);
			intent.putExtra("formula",
					SymbolMoveUtil.parseSymbolsFromMoveSequence(optimizedSeq,
							cubeState.order));

			CubeCapturingActivity.this.runOnUiThread(new Runnable() {

				public void run() {
					if (progressDialog != null) {
						progressDialog.cancel();
					}
					startActivityForResult(intent, 3);
				}
			});

		}
	}
}