package com.devinxutal.fmc.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;

import com.devinxutal.fmc.ui.MoveSequenceIndicator;

public class CubeDemostratorActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TableLayout l = new TableLayout(this);
		l.setBackgroundColor(0x00FF0000);
		l.addView(new MoveSequenceIndicator(this));
		Button b = new Button(this);
		b.setText("Hello World");
		l.addView(b);
		setContentView(l);
	}
}