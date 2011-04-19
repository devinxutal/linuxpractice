package com.heyzap.sdk;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class HeyzapButton extends ImageButton {
    private Context mContext;

    public HeyzapButton(Context context) {
        super(context);
        init(context);
    }

    public HeyzapButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeyzapButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
        setBackgroundColor(Color.TRANSPARENT);
        setAdjustViewBounds(true);
        setImageResource(R.drawable.heyzap_button);
        drawableStateChanged();
        setOnClickListener(new ButtonOnClickListener());
        HeyzapAnalytics.trackEvent(context, "checkin-button-shown");
        HeyzapLib.broadcastEnableSDK(context);
    }

    private final class ButtonOnClickListener implements OnClickListener {
        public void onClick(View arg0) {
            HeyzapLib.checkin(mContext);
        }
    }
}
