package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.control.CubeController;
import com.devinxutal.fmc.ui.MoveSequenceIndicator;

public class MagicCubeActivity extends Activity {
	private CubeController controller;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		/*
		 * this.requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)
		 */
		super.onCreate(savedInstanceState);

		controller = new CubeController(this);

		setContentView(controller.getCubeView());

		/* for test */
		TableLayout l = new TableLayout(this);
		l.setBackgroundColor(0x00FF0000);
		final MoveSequenceIndicator ind = new MoveSequenceIndicator(this);
		ind.setMoveSymbols(new String[] { "R", "L", "r2", "F'", "R2'" });
		ind.moveTo(0);
		l.addView(ind);
		Button b = new Button(this);
		b.setText("Move Forward");
		l.addView(b);
		b.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				ind.moveForward();
			}

		});
		 b = new Button(this);
			b.setText("Move Backward");
			l.addView(b);
			b.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					ind.moveBackward();
				}

			});

		setContentView(l);
		/* end for test */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.presentation:
			controller.startPresentation("URFDLBU'R'F'D'L'B'");
			return true;
		case R.id.preferences:
			Intent preferencesActivity = new Intent(getBaseContext(),
					Preferences.class);
			startActivity(preferencesActivity);
			return true;
		case R.id.test:
			Intent a = new Intent(getBaseContext(),
					CubeDemostratorActivity.class);
			startActivity(a);
			return true;
		}
		return false;
	}

}