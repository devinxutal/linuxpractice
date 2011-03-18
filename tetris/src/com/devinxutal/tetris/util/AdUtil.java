package com.devinxutal.tetris.util;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.LinearLayout;

import com.admob.android.ads.AdListener;
import com.admob.android.ads.AdView;
import com.devinxutal.tetris.R;
import com.devinxutal.tetris.cfg.Constants;

public class AdUtil {
	public static AdView createAdView(Activity activity) {
		AdView adView = new AdView(activity);
		adView.setBackgroundColor(Color.rgb(0, 0, 0));
		// adView.setRequestInterval(12);
		// for test
		// AdRequest request = new AdRequest();
		// AdManager manager = new AdManager();

		// end for test
		Log.v("AdUtil", "Request Interval :" + adView.getRequestInterval());
		adView.setAdListener(new AdListener() {

			public void onFailedToReceiveAd(AdView view) {

				Log.v("AdUtil", "Failed To Receive Ad");
			}

			public void onFailedToReceiveRefreshedAd(AdView view) {

				Log.v("AdUtil", "Failed To Receive Refresh Ad");
			}

			public void onReceiveAd(AdView view) {

				Log.v("AdUtil", "Receive Ad");
			}

			public void onReceiveRefreshedAd(AdView view) {

				Log.v("AdUtil", "Receive Refresh Ad");
			}
		});
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

	public static void determineAd(Activity activity, int adAreaID) {
		if (Constants.VERSION == Constants.VERSION_LITE) {
			((LinearLayout) activity.findViewById(adAreaID))
					.addView(createAdView(activity));
		}
	}
}
