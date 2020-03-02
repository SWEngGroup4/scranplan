package com.group4sweng.scranplan;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;

// TODO - Need to confirm horizontal or vertical scroll box with other group

public class PresentationTextView extends ScrollView {

    LinearLayout.LayoutParams layoutParams;
    TextView textView;
    Integer slideHeight;
    Integer slideWidth;
    Thread startTimer;
    Thread endTimer;

    public PresentationTextView(Context context, Integer slideHeight, Integer slideWidth) {
        super(context);

        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView = new TextView(context);

        this.slideHeight = slideHeight;
        this.slideWidth = slideWidth;

        this.setLayoutParams(layoutParams);
        this.addView(textView);
    }

    public void setText(String text) {
        textView.setText(Html.fromHtml(text));
    }

    public void setFont(String font) {
        String query = font;

        FontRequest fontRequest = new FontRequest("com.google.gms.fonts",
                "com.google.android.gms",
                query,
                R.array.com_google_android_gms_fonts_certs);

        FontsContractCompat.FontRequestCallback callback = new FontsContractCompat.FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                textView.setTypeface(typeface);
            }
        };
    }

    public void setSize(Integer size) {
        textView.setTextSize(size);
    }

    public void setColour(String colour) {
        textView.setTextColor(Color.parseColor(colour));
    }

    public void setPos(Float xPos, Float yPos) {
        layoutParams.setMargins(Math.round(slideWidth* (xPos /  100)), Math.round(slideHeight * (yPos / 100)), 0 ,0);
    }

    // TODO - Need to confirm need for timers with other group

//    public void appearAfter(final Integer startTime) {
//        this.setVisibility(View.GONE);
//        startTimer = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    synchronized (this) {
//                        wait (startTime);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                textView.setVisibility(View.VISIBLE);
//                            }
//                        });
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//    }


}
