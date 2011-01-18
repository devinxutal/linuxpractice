package com.devinxutal.fmc.util;

import android.content.Context;

import com.devinxutal.fmc.R;
import com.devinxutal.fmc.cfg.Constants;

public class VersionUtil {
	public static boolean checkProVersion(Context context) {
		if (Constants.VERSION == Constants.VERSION_LITE) {
			DialogUtil.showDialog(context, R.string.version_title,
					R.string.version_title);
			return false;
		} else if (Constants.VERSION == Constants.VERSION_PRO) {
			return true;
		}
		return false;
	}
}
