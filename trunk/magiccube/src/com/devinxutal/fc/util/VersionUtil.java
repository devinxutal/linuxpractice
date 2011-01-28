package com.devinxutal.fc.util;

import java.io.Serializable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.devinxutal.fc.R;
import com.devinxutal.fc.cfg.Constants;

public class VersionUtil {

	public static class VersionInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8387296657408645355L;

		public int solveCubeTrials = 0;
	}

	public static boolean checkProVersion(Context context, boolean showDialog) {
		if (Constants.VERSION == Constants.VERSION_PRO) {
			return true;
		} else {
			if (showDialog) {
				showGetProDialog(context);
			}
			return false;
		}
	}

	public static VersionInfo readVersionInfo(Activity activity) {
		SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(activity.getBaseContext());
		if (p == null) {
			return null;
		}
		VersionInfo info = new VersionInfo();
		info.solveCubeTrials = p
				.getInt(Constants.PREF_KEY_TRIALS_SOLVE_CUBE, 0);
		return info;
	}

	public static void writeVersionInfo(Activity activity, VersionInfo info) {
		SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(activity.getBaseContext());
		if (p == null) {
			return;
		}
		Editor editor = p.edit();
		editor.putInt(Constants.PREF_KEY_TRIALS_SOLVE_CUBE,
				info.solveCubeTrials);
		editor.commit();
	}

	//
	// public static VersionInfo readVersionInfo() {
	// File file = Environment.getExternalStorageDirectory();
	// File dir = new File(file, Constants.DATA_DIR);
	// if (!dir.exists()) {
	// dir.mkdirs();
	// }
	// File dataFile = new File(dir, Constants.VERSION_SAVING_FILE);
	//
	// if (!dataFile.exists()) {
	// return new VersionInfo();
	// }
	// try {
	// ObjectInputStream in = new ObjectInputStream(new FileInputStream(
	// dataFile));
	// VersionInfo info = (VersionInfo) in.readObject();
	// return info;
	// } catch (Exception e) {
	// return new VersionInfo();
	// }
	// }
	//
	// public static void writeVersionInfo(VersionInfo info) {
	// File file = Environment.getExternalStorageDirectory();
	// File dir = new File(file, Constants.DATA_DIR);
	// if (!dir.exists()) {
	// dir.mkdirs();
	// }
	// File dataFile = new File(dir, Constants.VERSION_SAVING_FILE);
	//
	// try {
	// if (!dataFile.exists()) {
	// dataFile.createNewFile();
	// }
	// ObjectOutputStream out = new ObjectOutputStream(
	// new FileOutputStream(dataFile));
	// out.writeObject(info);
	// out.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public static void showGetProDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.version_title).setMessage(
				R.string.version_title).setCancelable(false).setPositiveButton(
				R.string.common_ok, null).setNeutralButton(
				R.string.version_get_pro,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						try {
							Intent goToMarket = null;
							goToMarket = new Intent(
									Intent.ACTION_VIEW,
									Uri
											.parse("market://details?id=com.devinxutal.fc.pro"));
							((Activity) context).startActivity(goToMarket);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		;
		AlertDialog alert = builder.create();
		alert.show();

	}
}
