package com.devinxutal.fmc.activities;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.devinxutal.fmc.primitives.Plane;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		GLSurfaceView view = new GLSurfaceView(this);
		view.setRenderer(new Renderer() {

			public void onDrawFrame(GL10 gl) {
				Plane p = new Plane();
				p.draw(gl);
				Log.v("haha", "onDrawFrame");
			}

			public void onSurfaceChanged(GL10 arg0, int arg1, int arg2) {
				Log.v("haha", "onSurfaceChanged");
			}

			public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {

				Log.v("haha", "onSurfaceCreated");
			}

		});
		layout.addView(view);
		setContentView(layout);
	}
}