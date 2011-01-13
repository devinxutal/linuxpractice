package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.model.CubeState;
import com.devinxutal.fmc.ui.CubeCameraPreview;

public class CubeCapturingActivity extends Activity {
	private static final String TAG = "CameraDemo";
	CubeCameraPreview preview;
	ImageButton buttonClick;

	TextView stepView;
	Button goButton;
	TextView description;

	private int step = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1 && step == 1) {
				Log.v(TAG, "step 1 finished with ");
				Log
						.v(TAG, "model null? "
								+ ((CubeState) data
										.getSerializableExtra("model") == null));
				switchStep(2);
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
			}
		}
	}
}