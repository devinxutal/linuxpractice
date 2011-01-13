package com.devinxutal.fmc.ui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.devinxutal.fmc.model.MagicCube;
import com.devinxutal.fmc.primitives.Color;

public class CubeRenderer implements Renderer {

	private MagicCube magicCube;

	private CubeView cubeView;

	private boolean resetColorPicker = true;
	private int viewWidth = 0;
	private int viewHeight = 0;

	private float rx = 30f;
	private float ry = -45f;
	private float rz = 0f;

	private float translate = -8;

	private float scale = 1;

	public void zoomIn() {
		scale = scale * 1.1f;
		this.resetColorPicker = true;
	}

	public void zoomOut() {
		scale = scale * 0.9f;
		this.resetColorPicker = true;
	}

	public void zoomReset() {
		scale = 1f;
		this.resetColorPicker = true;
	}

	public CubeRenderer() {
	}

	public void setCubeView(CubeView view) {
		this.cubeView = view;
		if (cubeView != null) {
			viewWidth = cubeView.getWidth();
			viewHeight = cubeView.getHeight();
		}
	}

	public void setResetColorPicker(boolean reset) {
		this.resetColorPicker = reset;
	}

	public void setViewWidth(int width) {
		this.viewWidth = width;
	}

	public void setViewHeight(int height) {
		this.viewHeight = height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.
	 * microedition.khronos.opengles.GL10, javax.microedition.khronos.
	 * egl.EGLConfig)
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background color to black ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // OpenGL docs.
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);// OpenGL docs.
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);// OpenGL docs.
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);// OpenGL docs.
		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);// OpenGL docs.
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, // OpenGL docs.
				GL10.GL_NICEST);
		gl.glLoadIdentity();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.
	 * microedition.khronos.opengles.GL10)
	 */

	public void onDrawFrame(GL10 gl) {
		checkColorPicker(gl);
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | // OpenGL docs.
				GL10.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, translate / scale);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);
		if (magicCube != null) {
			magicCube.getCubie().setCubeColors();
			magicCube.getCubie().draw(gl);
		}

	}

	public void checkColorPicker(GL10 gl) {
		if (!this.resetColorPicker) {
			return;
		}
		resetColorPicker = false;
		Log.v("colortest", "[checkColorPicker] is running: viewHeight"
				+ viewHeight + ", viewWidth:" + viewWidth);
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | // OpenGL docs.
				GL10.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, translate / scale);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);

		magicCube.getCubie().drawPickingArea(gl);// TODO should be
		// drawPickingArea
		if (this.cubeView == null) {
			return;
		}
		int len = 4 * viewWidth * viewHeight;
		if (colorPickerBuffer == null || colorPickerBuffer.capacity() < len) {
			colorPickerBuffer = ByteBuffer.allocateDirect(len);
			colorPickerBuffer.order(ByteOrder.nativeOrder());
		}
		if (colorPickerBytes == null || colorPickerBytes.length != len) {
			colorPickerBytes = new byte[len];
		}
		colorPickerBuffer.rewind();
		gl.glReadPixels(0, 0, viewWidth, viewHeight, GL10.GL_RGBA,
				GL10.GL_UNSIGNED_BYTE, colorPickerBuffer);
		colorPickerBuffer.get(colorPickerBytes, 0, len);

	}

	private ByteBuffer colorPickerBuffer = null;
	private byte[] colorPickerBytes = null;

	public Color getColorAt(int x, int y) {
		if (this.cubeView == null || colorPickerBytes == null) {
			return null;
		}
		int len = 4 * viewWidth * viewHeight;
		y = viewHeight - y - 1;
		int offset = (y * viewWidth + x) * 4;
		if (offset + 3 >= colorPickerBytes.length) {
			return null;
		} else {
			return new Color(colorPickerBytes[offset + 0],
					colorPickerBytes[offset + 1], colorPickerBytes[offset + 2]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.
	 * microedition.khronos.opengles.GL10, int, int)
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);// OpenGL
		// docs.
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);// OpenGL docs.
		// Reset the projection matrix
		gl.glLoadIdentity();// OpenGL docs.
		// Calculate the aspect ratio of the window
		float fovy = 0;

		// GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
		// 100.0f);
		GLU.gluPerspective(gl, 45.0f, width / (float) height, 0.1f, 100.0f);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);// OpenGL docs.
		// Reset the modelview matrix
		gl.glLoadIdentity();// OpenGL docs.

		// set translate;
		translate = -8;
		int len = Math.min(width, height);
		translate = translate * height / (float) len;
	}

	public MagicCube getMagicCube() {
		return magicCube;
	}

	public void setMagicCube(MagicCube magicCube) {
		this.magicCube = magicCube;
	}

}