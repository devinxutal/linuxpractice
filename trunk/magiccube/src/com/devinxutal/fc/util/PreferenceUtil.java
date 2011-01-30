package com.devinxutal.fc.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceUtil {

	public static String readPlayerName(Activity activity) {
		SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(activity.getBaseContext());
		return p.getString("player_name", "");
	}

	public static void writePlayerName(Activity activity, String name) {
		SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(activity.getBaseContext());
		if (p == null) {
			return;
		}
		Editor editor = p.edit();
		editor.putString("player_name", name);
		editor.commit();
	}
}
