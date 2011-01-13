package com.devinxutal.fmc.ui;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.control.CubeController;

public class CubeControlView extends ViewGroup implements OnClickListener {
	CubeController controller;

	private static final int BTN_COLLAPSE = 3301;
	private static final int BTN_ZOOM_IN = 3302;
	private static final int BTN_ZOOM_OUT = 3303;
	private static final int BTN_ROTATE_X = 3304;
	private static final int BTN_ROTATE_Y = 3305;
	private static final int BTN_ROTATE_Z = 3306;

	private List<ImageButton> buttons;
	private boolean collapsed = true;
	private boolean collapseChanged = false;

	public CubeControlView(Context context) {
		super(context);
		init();
	}

	public void setCubeController(CubeController controller) {
		this.controller = controller;
	}

	private void init() {
		buttons = new LinkedList<ImageButton>();

		int[] ids = new int[] { BTN_COLLAPSE, //
				BTN_ZOOM_IN, //
				BTN_ZOOM_OUT, //
				BTN_ROTATE_X, //
				BTN_ROTATE_Y, //
				BTN_ROTATE_Z };//
		int[] res = new int[] { R.drawable.icon_play, //
				R.drawable.icon_zoom_in, //
				R.drawable.icon_zoom_out, //
				R.drawable.icon_rotate_x, //
				R.drawable.icon_rotate_y, //
				R.drawable.icon_rotate_z //
		};//
		for (int i = 0; i < ids.length; i++) {
			ImageButton button = makeButton(ids[i], res[i]);
			buttons.add(button);
		}
		this.addView(buttons.get(0));
	}

	private ImageButton makeButton(int id, int resid) {
		ImageButton b = new ImageButton(getContext());
		b.setId(id);
		b.setBackgroundResource(R.drawable.transparent_button);
		b.setImageResource(resid);
		b.setOnClickListener(this);
		return b;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.v("CubeControlView", "layouting, " + changed);
		if (!changed && !collapseChanged) {
			return;
		}
		collapseChanged = false;
		ImageButton collapseButton = buttons.get(0);
		collapseButton.measure(r - l, b - t);
		int width = r - l;
		int height = b - t;
		int len = width > height ? height : width;
		int btn_w = collapseButton.getMeasuredWidth();
		int btn_h = collapseButton.getMeasuredHeight();
		int btn_len = width > height ? btn_h : btn_w;
		int margin = 10;
		int padding = ((len - 2 * margin) - buttons.size() * btn_len)
				/ (buttons.size() - 1);

		collapseButton.layout(margin, margin, btn_w + margin, btn_h + margin);

		if (!collapsed) {
			int index = -1;
			for (ImageButton btn : buttons) {
				index++;
				if (index == 0) {
					continue;
				}
				if (width < height) {
					btn.layout(margin + index * (padding + btn_w), margin,
							margin + index * (padding + btn_w) + btn_w, margin
									+ btn_h);
				} else {
					btn.layout(margin, margin + index * (padding + btn_h),
							margin + btn_w, margin + index * (padding + btn_h)
									+ btn_h);
				}
			}
		}
	}

	private void resetButtons() {
		if (collapsed) {
			for (int i = 1; i < buttons.size(); i++) {
				this.removeView(buttons.get(i));
			}
		} else {
			for (int i = 1; i < buttons.size(); i++) {
				this.addView(buttons.get(i));
			}
		}
		this.invalidate();
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case BTN_COLLAPSE:
			this.collapsed = !this.collapsed;
			this.collapseChanged = true;
			resetButtons();
			break;
		case BTN_ZOOM_IN:
			zoomIn();
			break;
		case BTN_ZOOM_OUT:
			zoomOut();
			break;
		case BTN_ROTATE_X:
			rotate("x'");
			break;
		case BTN_ROTATE_Y:
			rotate("y");
			break;
		case BTN_ROTATE_Z:
			rotate("z");
			break;
		}
	}

	private void rotate(String symbol) {
		if (controller == null || controller.isInAnimation()) {
			return;
		}
		controller.turnBySymbol(symbol);
	}

	private void zoomIn() {
		this.controller.zoomIn();
	}

	private void zoomOut() {
		this.controller.zoomOut();

	}

	private void zoomReset() {
		this.controller.zoomReset();
	}
}
