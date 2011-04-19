package com.heyzap.sdk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class HeyzapAnalytics {
    private static final String LOG_TAG = "HeyzapSDK";
    private static final String HEYZAP_ANALYTICS_ID_PREF = "heyzap_button_analytics_id";
    private static final String HEYZAP_ENDPOINT = "http://android.heyzap.com/mobile/track_sdk_event";

    private static boolean loaded = false;
    private static String deviceId = "unknown";
    private static String packageName = "unknown";
    private static String trackHash = "";
    private static ExecutorService requestThread = Executors.newSingleThreadExecutor();

    public static synchronized void trackEvent(final Context context, final String eventType) {
        Log.d(LOG_TAG, "Tracking " + eventType + " event.");

        // Load the device id and any previous tracking hash
        if(!loaded) {
            init(context);
            loaded = true;
        }

        // Add the analytics request to the thread queue
        requestThread.execute(new Runnable() {
            public void run() {
                try {
                    // Build the post data
                    Uri.Builder builder = new Uri.Builder();
                    builder.appendQueryParameter("game_package", packageName);
                    builder.appendQueryParameter("device_id", deviceId);
                    builder.appendQueryParameter("track_hash", trackHash);
                    builder.appendQueryParameter("type", eventType);
                    String postData = builder.build().getEncodedQuery();

                    // Open the connection
                    URL url = new URL(HEYZAP_ENDPOINT);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    try {
                        // Set up the connection
                        conn.setDoOutput(true);

                        // Write the post data to the stream
                        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                        out.write(postData);
                        out.flush();
                        out.close();

                        // Get the response
                        String response = convertStreamToString(conn.getInputStream()).trim();
                        setTrackHash(context, response);
                    } catch(IOException e) {
                        // Ignore any file stream issues
                        e.printStackTrace();
                    } finally {
                        conn.disconnect();
                    }
                } catch(IOException e) {
                    // Ignore any connection failure when trying to open the connection
                } catch(UnsupportedOperationException e) {
                    // Ignore any url building issues
                    e.printStackTrace();
                }
            }
        });
    }

    public static String getAnalyticsReferrer(Context context) {
        String referrerTrackHash = getTrackHash(context);
        if(referrerTrackHash != null) {
            return URLEncoder.encode("utm_medium=device&utm_source=heyzap_track&utm_campaign=" + referrerTrackHash);
        } else {
            return URLEncoder.encode("utm_medium=device&utm_source=sdk&utm_campaign=" + context.getPackageName());
        }
    }

    private static void init(Context context) {
        // Load up the device id
        String product = Build.PRODUCT;
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(product != null && androidId != null) {
            deviceId = product + "_" + androidId;
        }

        // Load up previous tracking hash
        String tempTrackHash = getTrackHash(context);
        if(tempTrackHash != null) {
            trackHash = tempTrackHash;
        }

        // Load the package name
        packageName = context.getPackageName();
    }

    private static void setTrackHash(Context context, String newTrackHash) {
        if(newTrackHash != null && !newTrackHash.equals("") && !trackHash.equals(newTrackHash)) {
            trackHash = newTrackHash;

            SharedPreferences prefs = context.getSharedPreferences(HEYZAP_ANALYTICS_ID_PREF, Context.MODE_PRIVATE);
            Editor editor = prefs.edit();
            editor.putString(HEYZAP_ANALYTICS_ID_PREF, trackHash);
            editor.commit();
        }
    }

    private static String getTrackHash(Context context) {
        String trackHash = null;
        final SharedPreferences prefs = context.getSharedPreferences(HEYZAP_ANALYTICS_ID_PREF, Context.MODE_PRIVATE);
        if(prefs != null) {
            trackHash = prefs.getString(HEYZAP_ANALYTICS_ID_PREF, null);
        }

        return trackHash;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
