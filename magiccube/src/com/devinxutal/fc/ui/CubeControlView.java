package com.devinxutal.fc.ui;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.devinxutal.fc.control.CubeController;
import com.devinxutal.fmc.R;

public class CubeControlView extends ViewGroup implements OnClickListener {
	private CubeController controller;
	private List<CubeControlListener> listeners = new LinkedList<CubeControlListener>();

	public static final int BTN_PLAY = 3300;
	public static final int BTN_COLLAPSE = 3301;
	public static final int BTN_ZOOM_IN = 3302;
	public static final int BTN_ZOOM_OUT = 3303;
	public static final int BTN_ROTATE_X = 3304;
	public static final int BTN_ROTATE_Y = 3305;
	public static final int BTN_ROTATE_Z = 3306;
	public static final int BTN_HELP = 3307;
	public static final int BTN_SETTING = 3308;
	public static final int BTN_MENU = 3309;

	private List<ImageButton> buttons1;
	private List<ImageButton> buttons2;
	private ImageButton playButton;
	private CubeTimer cubeTimer;
	private boolean collapsed = true;
	private boolean collapseChanged = false;
	private boolean useTimer = false;
	private boolean showPlayButton = true;

	public CubeControlView(Context context, boolean useTimer) {
		super(context);
		this.useTimer = useTimer;
		init();
	}

	public void setCubeController(CubeController controller) {
		this.controller = controller;
	}

	public CubeTimer getCubeTimer() {
		return cubeTimer;
	}

	public void setCollapsed(boolean collapsed) {
		if (this.collapsed != collapsed) {
			this.collapsed = collapsed;
			this.collapseChanged = true;
			resetButtons();
			invalidate();
		}
	}

	private void init() {

		if (useTimer) {
			cubeTimer = new CubeTimer(getContext());
			this.addView(cubeTimer);
		}
		playButton = new ImageButton(getContext());
		playButton.setId(BTN_PLAY);
		playButton.setBackgroundResource(R.drawable.transparent_button);
		playButton.setImageResource(R.drawable.icon_play_large);
		playButton.setOnClickListener(this);
		if (showPlayButton) {
			this.addView(playButton);
		}
		buttons1 = new LinkedList<ImageButton>();

		int[] ids = new int[] { BTN_COLLAPSE, //
				BTN_MENU, //
				BTN_SETTING, //
				BTN_HELP };//
		int[] res = new int[] { R.drawable.icon_collapse, //
				R.drawable.icon_menu, //
				R.drawable.icon_setting, //
				R.drawable.icon_help //
		};//
		for (int i = 0; i < ids.length; i++) {
			ImageButton button = makeButton(ids[i], res[i]);
			buttons1.add(button);
		}

		buttons2 = new LinkedList<ImageButton>();
		ids = new int[] { BTN_ZOOM_IN, //
				BTN_ZOOM_OUT, //
				BTN_ROTATE_X, //
				BTN_ROTATE_Y, //
				BTN_ROTATE_Z };//
		res = new int[] { R.drawable.icon_zoom_in, //
				R.drawable.icon_zoom_out, //
				R.drawable.icon_rotate_x, //
				R.drawable.icon_rotate_y, //
				R.drawable.icon_rotate_z //
		};//
		for (int i = 0; i < ids.length; i++) {
			ImageButton button = makeButton(ids[i], res[i]);
			buttons2.add(button);
		}
		this.addView(buttons1.get(0));
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
		if (!changed && !collapseChanged) {
			return;
		}
		collapseChanged = false;
		ImageButton collapseButton = buttons1.get(0);
		collapseButton.measure(r - l, b - t);
		int width = r - l;
		int height = b - t;
		int len = width > height ? height : width;
		int btn_w = collapseButton.getMeasuredWidth();
		int btn_h = collapseButton.getMeasuredHeight();
		int btn_len = width > height ? btn_h : btn_w;
		int margin = 10;
		int padding = ((len - 2 * margin) - buttons1.size() * btn_len)
				/ (buttons1.size() - 1);

		collapseButton.layout(margin, margin, btn_w + margin, btn_h + margin);

		if (!collapsed) {
			int index = -1;
			for (ImageButton btn : buttons1) {
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

		padding = ((len - 2 * margin) - buttons2.size() * btn_len)
				/ (buttons2.size() - 1);

		if (!collapsed) {
			int index = -1;
			for (ImageButton btn : buttons2) {
				index++;
				if (width < height) {
					btn.layout(margin + index * (padding + btn_w), height
							- margin - btn_h, margin + index
							* (padding + btn_w) + btn_w, height - margin);
				} else {
					btn.layout(width - margin - btn_h, margin + index
							* (padding + btn_h), width - margin, margin + index
							* (padding + btn_h) + btn_h);
				}
			}
		}

		if (this.useTimer) {
			cubeTimer.measure(-1, -1);
			int h = cubeTimer.getMeasuredHeight();
			int w = cubeTimer.getMeasuredWidth();
			int left = width - w - margin;
			int top = 0 + margin;
			if (!collapsed) {
				if (width < height) {
					top += margin + btn_h;
				} else {
					left -= margin + btn_w;
				}
			}

			Log.v("CubeControlView", "layout  timer: " + left + ", " + top);
			cubeTimer.layout(left, top, left + w, top + h);
		}

		if (showPlayButton) {
			playButton.measure(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			int h = playButton.getMeasuredHeight();
			int w = playButton.getMeasuredWidth();
			int left = (width - w) / 2;
			int top = (height - h) / 2;
			playButton.layout(left, top, left + w, top + h);
		}
	}

	public void showPlayButton(boolean show) {
		if (showPlayButton != show) {
			showPlayButton = show;
			collapseChanged = true;
			resetButtons();
		}
	}

	private void resetButtons() {
		this.removeAllViews();
		if (useTimer) {
			this.addView(cubeTimer);
		}
		if (showPlayButton) {
			this.addView(playButton);
		}
		this.addView(buttons1.get(0));
		if (!collapsed) {
			for (int i = 1; i < buttons1.size(); i++) {
				this.addView(buttons1.get(i));
			}
			for (ImageButton b : buttons2) {
				this.addView(b);
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
		default:
			notifyButtonClicked(view.getId());
		}
	}

	private void rotate(String symbol) {
		if (controller == null || controller.isInAnimation()) {
			return;
		}
		controller.turnBySymbol(symbol);
	}

	public void setPlayButtonImage(int resid) {
		this.playButton.setImageResource(resid);
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

	public interface CubeControlListener {
		void buttonClickced(int id);
	}

	public boolean addCubeControlListener(CubeControlListener l) {
		return listeners.add(l);
	}

	public boolean removeCubeControlListener(CubeControlListener l) {
		return listeners.remove(l);
	}

	public void clearCubeControlListener() {
		listeners.clear();
	}

	public void notifyButtonClicked(int id) {
		for (CubeControlListener l : listeners) {
			l.buttonClickced(id);
		}
	}

	public boolean isCollapsed() {
		return collapsed;
	}
}
