package com.devinxutal.fc.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

import com.android.vending.licensing.AESObfuscator;
import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.ServerManagedPolicy;
import com.devinxutal.fc.cfg.Configuration;
import com.devinxutal.fc.cfg.Constants;
import com.devinxutal.fc.solver.CfopSolver;
import com.devinxutal.fc.util.VersionUtil;
import com.devinxutal.fmc.R;

public class MainActivity extends Activity implements OnClickListener {
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAit2OZ8OaxRwh8B9Du45ejqWe4XaWVp4RwD4Du0j1S7ZokOAJqYlu4rEQwdz9mGLqLV3I9fJhmgryXjqMpxl5+wODxS7FNvy6uRNzVLTbXdD/vvIt5CucAXRs9xFHj1gT212m59q2dw6iAT4E6dRhnzWIT48v9YYVW4iWymgnQYW7WMxTuk56bBp37VW0qZ9V+D+PrYlRkklSezBe2fVsOVuv09nLkD2sRTQVyvm0fW4P6Q9/7yUx2HJ5p1TQvtHe2ZaRUJuRG5+ZR3/gdGcaEfRXopB59S0DWhNyzMkw2RZRzUtg0N6H9PIbodHab04/qZCM86jTtRUDmQFFnp4/ZQIDAQAB";

	private static final byte[] SALT = new byte[] { -46, 65, 30, -128, -103,
			-57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64,
			89 };

	private Handler mHandler;

	private LicenseCheckerCallback mLicenseCheckerCallback;
	private LicenseChecker mChecker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // (NEW)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW)
		Configuration.config()
				.setSharedPreferences(
						PreferenceManager
								.getDefaultSharedPreferences(getBaseContext()));
		this.setContentView(R.layout.main);
		int ids[] = new int[] { R.id.main_btn_free_play,
				R.id.main_btn_time_play, R.id.main_btn_preference,
				R.id.main_btn_cfop_viewer, R.id.main_btn_cube_solver,
				R.id.main_btn_about };
		for (int id : ids) {
			try {
				this.findViewById(id).setOnClickListener(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		CfopSolver.getSolver(this);

		// for lisencing
		mHandler = new Handler();
		// Try to use more data here. ANDROID_ID is a single point of attack.
		String deviceId = Secure.getString(getContentResolver(),
				Secure.ANDROID_ID);

		// Library calls this when it's done.
		mLicenseCheckerCallback = new MyLicenseCheckerCallback();
		// Construct the LicenseChecker with a policy.
		mChecker = new LicenseChecker(this, new ServerManagedPolicy(this,
				new AESObfuscator(SALT, getPackageName(), deviceId)),
				BASE64_PUBLIC_KEY);
		if (VersionUtil.checkProVersion(this, false)) {
			doCheck();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mChecker.onDestroy();
	}

	public void onClick(View view) {
		Intent i = null;
		switch (view.getId()) {
		case R.id.main_btn_free_play:
			i = new Intent(this, MagicCubeActivity.class);
			i.putExtra("timedMode", false);
			break;
		case R.id.main_btn_time_play:
			i = new Intent(this, MagicCubeActivity.class);
			i.putExtra("timedMode", true);
			break;
		case R.id.main_btn_preference:
			i = new Intent(this, Preferences.class);
			break;
		case R.id.main_btn_cfop_viewer:
			i = new Intent(this, CfopViewerActivity.class);
			break;
		case R.id.main_btn_cube_solver:
			checkCubeSolver();
			return;
		case R.id.main_btn_about:
			i = new Intent(this, AboutActivity.class);
			break;
		}
		startActivity(i);
	}

	private void openCubeSolver() {
		Intent intent = new Intent(this, CubeCapturingActivity.class);
		startActivity(intent);
	}

	private void checkCubeSolver() {
		if (VersionUtil.checkProVersion(this, false)) {
			openCubeSolver();
			return;
		}
		final VersionUtil.VersionInfo info = VersionUtil.readVersionInfo(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.version_title).setNegativeButton(
				R.string.common_cancel, null).setNeutralButton(
				R.string.version_get_pro,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						try {
							Intent goToMarket = null;
							goToMarket = new Intent(
									Intent.ACTION_VIEW,
									Uri
											.parse("market://details?id=com.devinxutal.fmc"));
							startActivity(goToMarket);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		String content = this.getString(R.string.version_desc) + " "
				+ getString(R.string.version_trials_1) + " "
				+ (Constants.TRIAL_TIMES - info.solveCubeTrials) + " "
				+ getString(R.string.version_trials_2);

		if (info.solveCubeTrials < Constants.TRIAL_TIMES) {
			content += " " + getString(R.string.version_continue_hint);
			builder.setPositiveButton(R.string.common_ok,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							info.solveCubeTrials++;
							VersionUtil.writeVersionInfo(MainActivity.this,
									info);
							openCubeSolver();
						}
					});
		}
		builder.setMessage(content);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
		public void allow() {
			Log.v("MainActivity", "allow");
			// DO nothing
		}

		public void dontAllow() {
			Log.v("MainActivity", "not allow");
			if (isFinishing()) {
				return;
			}
			MainActivity.this.showDontAllowDialog();
		}

		public void applicationError(ApplicationErrorCode errorCode) {

			Log.v("MainActivity", "not allow");
			if (isFinishing()) {
				return;
			}
			MainActivity.this.showDontAllowDialog();
		}
	}

	private void doCheck() {
		if (VersionUtil.checkProVersion(this, false)) {
			mChecker.checkAccess(mLicenseCheckerCallback);
		}
	}

	private void showDontAllowDialog() {
		Dialog alert = new AlertDialog.Builder(this).setTitle(
				R.string.unlicensed_dialog_title).setMessage(
				R.string.unlicensed_dialog_body).setPositiveButton(
				R.string.buy_button, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent marketIntent = new Intent(
								Intent.ACTION_VIEW,
								Uri
										.parse("http://market.android.com/details?id="
												+ getPackageName()));
						startActivity(marketIntent);
					}
				}).setNegativeButton(R.string.quit_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).create();

		alert.show();
	}
}
