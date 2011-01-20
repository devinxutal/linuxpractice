package com.devinxutal.fc.util;

import android.app.AlertDialog;
import android.content.Context;

import com.devinxutal.fmc.R;

public class DialogUtil {
	public static void showDialog(Context context, int title, int content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(content).setCancelable(false)
				.setPositiveButton(R.string.common_ok, null);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void showDialog(Context context, String title, String content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(content).setCancelable(false)
				.setPositiveButton(R.string.common_ok, null);
		AlertDialog alert = builder.create();
		alert.show();

	}
	

}
