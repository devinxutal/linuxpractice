package com.devinxutal.fmc.ui;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;

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

		LinearLayout ll = new LinearLayout(getContext());
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.addView(prevButton);
		ll.addView(playButton);
		ll.addView(nextButton);
		this.addView(ll);

		nextButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				controller.turnBySymbol(indicator.getCurrentSymbol());
				//Toast t = Toast.makeText(getContext(), "turn by gesture: "
				//		+ indicator.getCurrentSymbol(), 1000);
				//t.show();
				indicator.moveForward();
			}

		});
		prevButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				indicator.moveBackward();
			}
		});
		this.addView(controller.getCubeView(), LayoutParams.WRAP_CONTENT);

	}
}
