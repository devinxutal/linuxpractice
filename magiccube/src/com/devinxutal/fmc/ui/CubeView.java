package com.devinxutal.fmc.ui;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.primitives.Color;
import com.devinxutal.fmc.primitives.Point3I;

public class CubeView extends GLSurfaceView {

	private MagicCube magicCube;

	public MagicCube getMagicCube() {
		return magicCube;
	}

	public void setMagicCube(MagicCube magicCube) {
		this.magicCube = magicCube;
		this.getCubeRenderer().setMagicCube(magicCube);
	}

	public CubeView(Context context) {
		super(context);
		initCubeView();
	}

	public CubeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCubeView();
	}

	private void initCubeView() {

		this.cubeRenderer = new CubeRenderer();
		this.setRenderer(cubeRenderer);

		this.cubeRenderer.setCubeView(this);

		this.setFocusable(true);
		this.setFocusableInTouchMode(true);

		this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.v("cc", "width and height:"+widthMeasureSpec+", "+heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.v("cc", "measured width and height:"+this.getMeasuredWidth()+", "+this.getMeasuredWidth());
		//this.setMeasuredDimension(getMeasuredWidth(), heightMeasureSpec);
	}

	private CubeRenderer cubeRenderer;

	public CubeRenderer getCubeRenderer() {
		return this.cubeRenderer;
	}

	private Toast t = Toast.makeText(this.getContext(), "", 1000);

	public void turn(MotionEvent evt) {
		String faces[] = new String[] { "u", "d", "f", "b", "r", "l" };
		float x = evt.getX();
		float y = evt.getY();

		float W = this.getWidth();
		float H = this.getHeight();
		float direction = y - H / 2;
		String face = faces[(int) ((x - 0.1) * 6 / W) % 6];
		if (direction < 0) {
			face += "'";
		}
		if (magicCube != null) {
			magicCube.turnBySymbol(face);
		}
		t.setText("Operation " + face.toUpperCase());
		t.show();
		this.requestRender();
	}

	public Point3I mapToCubePosition(int x, int y) {
		if (cubeRenderer != null) {
			Color color = null;
			if ((color = cubeRenderer.getColorAt(x, y)) != null) {
				int i = Math.round(color.getRed() / 20f);
				int j = Math.round(color.getGreen() / 20f);
				int k = Math.round(color.getBlue() / 20f);
				return new Point3I(i, j, k);
			}
		}
		return null;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		resetPicker();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		resetPicker();
		super.onWindowVisibilityChanged(visibility);
	}

	protected void resetPicker() {
		final int width = this.getWidth();
		final int height = this.getHeight();
		queueEvent(new Runnable() {
			// This method will be called on the rendering
			// thread:
			public void run() {
				cubeRenderer.setViewWidth(width);
				cubeRenderer.setViewHeight(height);
				cubeRenderer.setResetColorPicker(true);
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent evt) {
		if (keyCode == KeyEvent.KEYCODE_U) {
			magicCube.turnBySymbol("u");
		} else if (keyCode == KeyEvent.KEYCODE_D) {
			magicCube.turnBySymbol("d");
		} else if (keyCode == KeyEvent.KEYCODE_F) {
			magicCube.turnBySymbol("f");
		} else if (keyCode == KeyEvent.KEYCODE_B) {
			magicCube.turnBySymbol("b");
		} else if (keyCode == KeyEvent.KEYCODE_R) {
			magicCube.turnBySymbol("r");
		} else if (keyCode == KeyEvent.KEYCODE_L) {
			magicCube.turnBySymbol("l");
		}
		return false;
	}
}
