package com.devinxutal.fc.util;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.devinxutal.fc.R;
import com.devinxutal.fc.cfg.Constants;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;

public class AdUtil {
	public static com.google.ads.AdView createAdViewNewSDK(
			final Activity activity) {
		final com.google.ads.AdView adView = new com.google.ads.AdView(
				activity, AdSize.BANNER, Constants.ADMOB_PUBLISHER_ID);

		adView.setId(Constants.ADVIEW_ID);
		return adView;

	}

	public static AdRequest getAdRequest() {
		AdRequest req = new AdRequest();
		req.setTesting(true);
		return req;
	}

	private static View createAdView(Activity activity) {
		View adview = null;
		boolean newsdk = true;
		if (newsdk) {
			adview = createAdViewNewSDK(activity);

		}
		// else {
		// adview = createAdViewOldSDK(activity);
		// }
		return adview;

	}

	public static void determineAd(Activity activity) {
		if (Constants.VERSION == Constants.VERSION_LITE) {
			((LinearLayout) activity.findViewById(R.id.ad_area))
					.addView(createAdView(activity));
		}
	}

	public static void determineAd(Activity activity, int adAreaID) {
		if (Constants.VERSION == Constants.VERSION_LITE) {
			((LinearLayout) activity.findViewById(adAreaID))
					.addView(createAdView(activity));
		}
	}
}
