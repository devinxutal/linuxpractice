package com.devinxutal.fmc.util;

import android.app.AlertDialog;
import android.content.Context;

import com.devinxutal.fmc.R;

public class DialogUtil {
	public static void showDialog(Context context, int title, int content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title)
				.setMessage(content).setCancelable(false)
				.setPositiveButton(R.string.common_ok, null);
		AlertDialog alert = builder.create();
		alert.show();
	}
}