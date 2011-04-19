package com.heyzap.sdk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

class HeyzapDialog extends Dialog {
    private static final int BUTTON_TYPE_PRIMARY = 1;
    private static final int BUTTON_TYPE_SECONDARY = 2;

    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_TEXT_SIZE = 13;
    private static final int PRIMARY_BUTTON_COLOR = 0xFF7faa35;

    private LinearLayout dialogView;
    private RelativeLayout buttonRow;
    private View contentView;

    public HeyzapDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        dialogView = new LinearLayout(getContext());
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.setBackgroundResource(R.drawable.dialog_background);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        super.onCreate(savedInstanceState);
    }

    public void setView(View contentView) {
        if(this.contentView != null) {
            dialogView.removeView(contentView);
        }

        dialogView.addView(contentView, 0);
        this.contentView = contentView;
    }

    public void addPrimaryButton(String title, View.OnClickListener listener) {
        addButton(title, 0xFFFFFFFF, R.drawable.dialog_button_primary, RelativeLayout.ALIGN_PARENT_RIGHT, listener);
    }

    public void addSecondaryButton(String title, View.OnClickListener listener) {
        addButton(title, 0xFF000000, R.drawable.dialog_button_secondary, RelativeLayout.ALIGN_PARENT_LEFT, listener);
    }

    private void addButton(String title, int textColor, int backgroundResource, int position, View.OnClickListener listener) {
        final float scale = getContext().getResources().getDisplayMetrics().density;

        if(buttonRow == null) {
            buttonRow = new RelativeLayout(getContext());
            buttonRow.setBackgroundResource(R.drawable.dialog_button_background);
            buttonRow.setPadding((int)(scale * 2), (int)(scale * 5), (int)(scale * 2), (int)(scale * 4));
            dialogView.addView(buttonRow, dialogView.getChildCount());
        }

        Button button = new Button(getContext());
        button.setBackgroundResource(backgroundResource);
        button.setTextColor(textColor);
        button.setTextSize(BUTTON_TEXT_SIZE);
        button.setText(title);
        button.setOnClickListener(listener);

        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams((int)(BUTTON_WIDTH*scale), (int)(BUTTON_HEIGHT*scale));
        layout.addRule(position);
        buttonRow.addView(button, layout);
    }
}