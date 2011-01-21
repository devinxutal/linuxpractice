package com.devinxutal.fc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.devinxutal.fc.R;
import com.devinxutal.fc.cfg.Configuration;
import com.devinxutal.fc.ui.CubeCameraView;

public class CubeCameraActivity extends Activity {
	CubeCameraView preview;
	ImageButton buttonClick;

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

		setContentView(R.layout.cubecamara);

		FrameLayout frameLayout = ((FrameLayout) findViewById(R.id.preview));
		preview = new CubeCameraView(this);
		frameLayout.addView(preview);
	}

}