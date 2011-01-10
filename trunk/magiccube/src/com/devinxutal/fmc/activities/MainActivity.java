package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.devinxutal.fmc.R;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		int ids[] = new int[] { R.id.main_btn_free_play,
				R.id.main_btn_time_play, R.id.main_btn_demonstrator,
				R.id.main_btn_cfop_viewer, R.id.main_btn_cube_solver,
				R.id.main_btn_tester };
		for (int id : ids) {
			try {
				this.findViewById(id).setOnClickListener(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onClick(View view) {
		Intent i = null;
		switch (view.getId()) {
		case R.id.main_btn_free_play:
			i = new Intent(this, MagicCubeActivity.class);
			break;
		case R.id.main_btn_time_play:
			i = new Intent(this, MagicCubeActivity.class);
			break;
		case R.id.main_btn_demonstrator:
			i = new Intent(this, CubeDemostratorActivity.class);
			i.putExtra("formula", "FRUR'(RUR'U)r2yz'");
			break;
		case R.id.main_btn_cfop_viewer:
			i = new Intent(this, CfopViewerActivity.class);
			break;
		case R.id.main_btn_cube_solver:
			i = new Intent(this, CubeSolverActivity.class);
			break;

		case R.id.main_btn_tester:
			i = new Intent(this, CubeCameraActivity.class);
			break;
		}
		startActivity(i);
	}
}
