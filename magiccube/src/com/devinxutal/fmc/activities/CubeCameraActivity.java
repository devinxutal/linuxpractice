package com.devinxutal.fmc.activities;

import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.ui.CubeCameraPreview;
import com.devinxutal.fmc.util.ImageUtil;

public class CubeCameraActivity extends Activity {
	private static final String TAG = "CameraDemo";
	CubeCameraPreview preview; // <1>
	Button buttonClick; // <2>

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cubecamara);

		preview = new CubeCameraPreview(this); // <3>
		((FrameLayout) findViewById(R.id.preview)).addView(preview); // <4>

		buttonClick = (Button) findViewById(R.id.buttonClick);
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
				// Write to SD Card
				Parameters p = camera.getParameters();
				Log.v(TAG, "picture taken, size: " + p.getPictureSize().width
						+ "," + p.getPictureSize().height + "   bytes: "
						+ data.length);
				Log.v(TAG, "picture format: " + p.getPictureFormat());
				int w = p.getPictureSize().width;
				int h = p.getPictureSize().height;
				int[] rgb = new int[w * h];
				ImageUtil.decodeYUV420SP(rgb, data, w, h);
				Bitmap bitmap = Bitmap.createBitmap(rgb, w, h,
						Bitmap.Config.ARGB_8888);
				// Bitmap bitmap = Bitmap.createBitmap(p.getPictureSize().width,
				// p
				// .getPictureSize().height, Bitmap.Config.ARGB_8888);
				// outStream = new FileOutputStream(String.format(
				// "/sdcard/%d.jpg", System.currentTimeMillis())); // <9>
				// outStream.write(data);
				// outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (Exception e) { // <10>

				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

}