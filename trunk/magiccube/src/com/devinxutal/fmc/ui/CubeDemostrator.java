package com.devinxutal.fmc.ui;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

import com.devinxutal.fmc.control.CubeController;

public class CubeDemostrator extends TableLayout {
	CubeController controller;

	private MoveSequenceIndicator indicator;

	public CubeDemostrator(Context context, String[] symbols) {
		super(context);
		controller = new CubeController(context);
		init();

		indicator.setMoveSymbols(symbols);
		indicator.moveTo(0);
	}

	private void init() {
		this.indicator = new MoveSequenceIndicator(this.getContext());
		TableLayout.LayoutParams p = new LayoutParams();
		p.height = 200;
		this.addView(indicator);
		Button b = new Button(getContext());
		b.setText("Move Forward");
		this.addView(b);
		b.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				controller.turnBySymbol(indicator.getCurrentSymbol());
				Toast t = Toast.makeText(getContext(), "turn by gesture: "
						+ indicator.getCurrentSymbol(), 1000);
				t.show();
				indicator.moveForward();
			}

		});
		b = new Button(getContext());
		b.setText("Move Backward");
		this.addView(b);
		b.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				indicator.moveBackward();
			}

		});

		this.addView(controller.getCubeView(), LayoutParams.WRAP_CONTENT);

	}
}
