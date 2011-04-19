package com.heyzap.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

class PreMarketDialog extends HeyzapDialog {
    private static final String LOG_TAG = "HeyzapSDK";

    protected String packageName;
    protected String gameName;
    protected Drawable gameIcon;

    public PreMarketDialog(Context context, String packageName) {
        super(context);

        this.packageName = packageName;

        try {
            this.gameIcon = context.getPackageManager().getApplicationIcon(packageName);
            this.gameName = context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
        } catch (NameNotFoundException e) {
            // Package manager failure
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add view to dialog
        setView(buildDialogContentView());

        // Set up buttons
        addPrimaryButton("Install Heyzap", new View.OnClickListener() {
  
            public void onClick(View v) {
                String uri = "market://details?id="+HeyzapLib.HEYZAP_PACKAGE +"&referrer=" + HeyzapAnalytics.getAnalyticsReferrer(getContext());

                HeyzapAnalytics.trackEvent(getContext(), "install-button-clicked");

                Log.d(LOG_TAG, "Sending player to market, uri: " + uri);
                Intent popup = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getContext().startActivity(popup);

                dismiss();
            }
        });

        addSecondaryButton("Skip", new View.OnClickListener() {
           
            public void onClick(View v) {
                HeyzapAnalytics.trackEvent(getContext(), "skip-button-clicked");

                dismiss();
            }
        });
    }

    private View buildDialogContentView() {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        final int padding = (int)(10*scale);

        // Layouts
        LinearLayout.LayoutParams blockLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Create dialog view
        LinearLayout dialogContents = new LinearLayout(getContext());
        dialogContents.setOrientation(LinearLayout.VERTICAL);

        // Dialog header
        LinearLayout header = new LinearLayout(getContext());
        header.setPadding(padding, padding, padding, padding);
        header.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams gameIconLayout = new LinearLayout.LayoutParams((int)(scale*50), (int)(scale*50));
        gameIconLayout.setMargins(0, 0, padding, 0);
        ImageView gameIconView = new ImageView(getContext());
        gameIconView.setImageDrawable(gameIcon);
        header.addView(gameIconView, gameIconLayout);

        LinearLayout.LayoutParams gameTitleLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        TextView gameTitle = new TextView(getContext());
        gameTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        gameTitle.setText("Check in to " + gameName);
        gameTitle.setTypeface(Typeface.DEFAULT_BOLD);
        gameTitle.setGravity(Gravity.CENTER_VERTICAL);
        gameTitle.setTextColor(0xFFFFFFFF);
        header.addView(gameTitle, gameTitleLayout);

        dialogContents.addView(header, blockLayout);

        // Seperator
        ImageView separator = new ImageView(getContext());
        separator.setImageResource(R.drawable.dialog_separator);
        dialogContents.addView(separator, blockLayout);

        // Info about heyzap
        LinearLayout heyzapInfo = new LinearLayout(getContext());
        heyzapInfo.setPadding(padding, padding, padding, padding*2);
        heyzapInfo.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams heyzapIconLayout = new LinearLayout.LayoutParams((int)(scale*70), (int)(scale*70));
        heyzapIconLayout.setMargins(0, 0, padding, 0);
        ImageView heyzapIcon = new ImageView(getContext());
        heyzapIcon.setImageResource(R.drawable.heyzap_circle);
        heyzapInfo.addView(heyzapIcon, heyzapIconLayout);

        TextView heyzapSell = new TextView(getContext());
        heyzapSell.setLineSpacing(0f, 1.2f);
        heyzapSell.setTextColor(0xFFFFFFFF);
        heyzapSell.setText("Install Heyzap to unlock cool features in " + gameName + ". Share games with friends, collect badges, and more! It's free and easy!");
        heyzapInfo.addView(heyzapSell, blockLayout);

        dialogContents.addView(heyzapInfo, blockLayout);

        return dialogContents;
    }
}