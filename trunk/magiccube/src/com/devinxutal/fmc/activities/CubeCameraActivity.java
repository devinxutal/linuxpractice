package com.devinxutal.fmc.activities;

import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.ui.CubeCameraPreview;
import com.devinxutal.fmc.ui.CubeCameraPreview.CubeLocator;

public class CubeCameraActivity extends Activity {
	private static final String TAG = "CameraDemo";
	CubeCameraPreview preview; // <1>
	ImageButton buttonClick; // <2>

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.cubecamara);

		FrameLayout frameLayout = ((FrameLayout) findViewById(R.id.preview));
		preview = new CubeCameraPreview(this);
		frameLayout.addView(preview);

		buttonClick = new ImageButton(this);
		buttonClick.setImageResource(R.drawable.cube);
		LinearLayout l = new LinearLayout(this);
		l.setGravity(Gravity.RIGHT | Gravity.TOP);
		l.addView(buttonClick, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		frameLayout.addView(l);

		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) { // <5>
				preview.camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);
			}
		});
		Log.d(TAG, "onCreate'd");
	}

	// Called when shutter is opened
	ShutterCallback shutterCallback = new ShutterCallback() { // <6>
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	// Handles data for raw picture
	PictureCallback rawCallback = new PictureCallback() { // <7>
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	// Handles data for jpeg picture
	PictureCallback jpegCallback = new PictureCallback() { // <8>
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				Parameters p = camera.getParameters();
				Log.v(TAG, "picture taken, size: " + p.getPictureSize().width
						+ "," + p.getPictureSize().height + "   bytes: "
						+ data.length);
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
				Log.v("CubeCameraActivity", "bitmap null? " + (bm == null));
				if (bm != null) {
					preview.getCubeLocator().setBitmap(bm);
					preview.getCubeLocator().setMode(CubeLocator.MOVE_MODE);
					Log.v("CubeCameraActivity", "width:" + bm.getWidth() + ", "
							+ "height:" + bm.getHeight());
				}

				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (Exception e) { // <10>

				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

}