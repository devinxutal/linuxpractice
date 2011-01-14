package com.devinxutal.fmc.ui;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.primitives.Color;
import com.devinxutal.fmc.primitives.Point3I;

public class CubeView extends GLSurfaceView {

	private MagicCube magicCube;

	private CubeRenderer cubeRenderer;

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
		Log.v("cc", "CubeView init finished");
	}

	@Override
	public void onPause() {
		Log.v("CubeView", "on pause");
		this.setRenderMode(RENDERMODE_CONTINUOUSLY);
		super.onPause();
	}

	@Override
	public void onResume() {
		this.setRenderMode(RENDERMODE_WHEN_DIRTY);
		super.onResume();
	}

	public CubeRenderer getCubeRenderer() {
		return this.cubeRenderer;
	}

	public Point3I mapToCubePosition(int x, int y) {
		Log.v("colortest", "in mapToCubePosition");
		if (cubeRenderer != null) {
			Log.v("colortest", "in mapToCubePosition inner");
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
		Log.v("colortest", "[onSizeChanged]: " + w + ", " + h);
		resetPicker();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		Log.v("colortest", "[onWindowVisibilityChanged]");
		resetPicker();
		super.onWindowVisibilityChanged(visibility);
	}

	protected void resetPicker() {
		final int width = this.getWidth();
		final int height = this.getHeight();

		Log.v("colortest", "[resetPicker] width height: " + width + ", "
				+ height);
		queueEvent(new Runnable() {
			// This method will be called on the rendering
			// thread:
			public void run() {
				cubeRenderer.setViewWidth(width);
				cubeRenderer.setViewHeight(height);
				cubeRenderer.setResetColorPicker(true);
				requestRender();
			}
		});
	}

}
