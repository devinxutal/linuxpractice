package com.devinxutal.man20.util;

import android.app.Activity;
import android.graphics.Color;
import android.widget.LinearLayout;

import com.admob.android.ads.AdView;
import com.devinxutal.man20.R;
import com.devinxutal.man20.R.id;
import com.devinxutal.man20.cfg.Constants;

public class AdUtil {
	public static AdView createAdView(Activity activity) {
		AdView adView = new AdView(activity);
		adView.setBackgroundColor(Color.rgb(0, 0, 0));
		adView.setPrimaryTextColor(Color.rgb(255, 255, 255));
		adView.setSecondaryTextColor(Color.rgb(180, 180, 180));
		adView.setMinimumHeight(Constants.ADMOB_HEIGHT);
		return adView;

	}

	public static void determineAd(Activity activity) {
		if (Constants.VERSION == Constants.VERSION_LITE) {
			((LinearLayout) activity.findViewById(R.id.ad_area))
					.addView(createAdView(activity));
		}
	}
	public static void determineAd(Activity activity,int adAreaID) {
		if (Constants.VERSION == Constants.VERSION_LITE) {
			((LinearLayout) activity.findViewById(adAreaID))
					.addView(createAdView(activity));
		}
	}
}
