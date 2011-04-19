package com.heyzap.sdk;

import java.util.List;

import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class HeyzapLib {
    public static final String HEYZAP_PACKAGE = "com.heyzap.android";
    private static final String HEYZAP_INTENT_CLASS = ".CheckinForm";

    private static String packageName;

    public static void checkin(Context context){
        checkin(context, null);
    }

    public static void checkin(Context context, String prefillMessage){
        packageName = context.getPackageName();

        if(packageIsInstalled(HEYZAP_PACKAGE, context)){
            launchCheckinForm(context, prefillMessage);
        }else{
            try{
                HeyzapAnalytics.trackEvent(context, "checkin-button-clicked");
                new PreMarketDialog(context, packageName).show();
            } catch(ActivityNotFoundException e) {
                // only happens if android market is not installed... in which case, do nothing.
            }
        }
    }

    public static void broadcastEnableSDK(Context context){
        // Tell the heyzap app this is an SDK game, so the popup does not show up
        Intent broadcast = new Intent("com.heyzap.android.enableSDK");
        broadcast.putExtra("packageName", context.getPackageName());
        context.sendBroadcast(broadcast);
    }

    public static void init(Context context){
        broadcastEnableSDK(context);
    }

    private static void launchCheckinForm(Context context, String prefillMessage) {
        String packageName = HEYZAP_PACKAGE;

        Intent popup = new Intent(Intent.ACTION_MAIN);
        popup.putExtra("message", prefillMessage);
        popup.setAction(packageName);
        popup.addCategory(Intent.CATEGORY_LAUNCHER);
        popup.putExtra("packageName", context.getPackageName());
        popup.setComponent(new ComponentName(packageName, HEYZAP_PACKAGE + HEYZAP_INTENT_CLASS));

        context.startActivity(popup);
    }

    private static boolean packageIsInstalled(String packageName, Context context){
        boolean installed = false;

        try{
            PackageManager pm = context.getPackageManager();
            Intent pi = pm.getLaunchIntentForPackage(packageName);
            if(pi != null){
                List<ResolveInfo> list = pm.queryIntentActivities(pi, PackageManager.MATCH_DEFAULT_ONLY);
                if(list.size() > 0) {
                    installed = true;
                }
            }
        } catch (Exception e) {}

        return installed;
    }
}