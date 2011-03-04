package com.devinxutal.tetris.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TabHost;

import com.devinxutal.tetris.R;
import com.devinxutal.tetris.sound.SoundManager;
import com.devinxutal.tetris.util.AdUtil;

public class HighScoreActivity extends TabActivity implements OnClickListener {

	private static final String TAG = "HighScoreActvity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.highscore);
		AdUtil.determineAd(this, R.id.ad_area);
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, LocalRankActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("localrank").setIndicator("Local",
				res.getDrawable(android.R.drawable.btn_star_big_on))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, WorldRankActivity.class);
		spec = tabHost.newTabSpec("worldrank").setIndicator("Worldwide",
				res.getDrawable(android.R.drawable.btn_star_big_on))
				.setContent(intent);
		tabHost.addTab(spec);
		tabHost.setCurrentTab(0);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SoundManager.release();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected void onResume() {
		super.onResume();
	}

	public void onClick(View view) {

	}

}
