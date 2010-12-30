package com.devinxutal.fmc.ui;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.control.CubeController;

public class CubeDemostrator extends TableLayout {
	CubeController controller;

	private MoveSequenceIndicator indicator;

	public CubeDemostrator(Context context, String[] symbols) {
		super(context);
		controller = new CubeController(context, true);
		init();

		indicator.setMoveSymbols(symbols);
	}

	private void init() {
		this.indicator = new MoveSequenceIndicator(this.getContext());
		TableLayout.LayoutParams p = new LayoutParams();
		p.height = 200;
		this.addView(indicator);
		ImageButton prevButton = new ImageButton(getContext());
		prevButton.setBackgroundResource(R.drawable.play_button);
		prevButton.setImageResource(R.drawable.icon_prev);

		ImageButton nextButton = new ImageButton(getContext());
		nextButton.setBackgroundResource(R.drawable.play_button);
		nextButton.setImageResource(R.drawable.icon_next);

		ImageButton playButton = new ImageButton(getContext());
		playButton.setBackgroundResource(R.drawable.play_button);
		playButton.setImageResource(R.drawable.icon_play);

		TableLayout tl = new TableLayout(getContext());
		TableRow tr = new TableRow(getContext());
		tr.addView(prevButton);
		tr.addView(playButton);
		tr.addView(nextButton);
		tl.addView(tr);
		this.addView(tl);

		ImageButton b = new ImageButton(getContext());
		b.setImageResource(android.R.drawable.arrow_up_float);
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
		b = new ImageButton(getContext());
		b.setBackgroundResource(R.drawable.play_button);
		b.setImageResource(R.drawable.icon_play);
		this.addView(b);
		b.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				indicator.moveBackward();
			}
		});
		ImageButton bb = new ImageButton(getContext());
		bb.setImageResource(com.devinxutal.fmc.R.drawable.play);
		this.addView(bb);

		this.addView(controller.getCubeView(), LayoutParams.WRAP_CONTENT);

	}
}
