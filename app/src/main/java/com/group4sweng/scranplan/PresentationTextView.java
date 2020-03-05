package com.group4sweng.scranplan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ViewConstructor")
public class PresentationTextView extends ScrollView {

    private Handler mHandler = null;
    private Timer startTimer = null;
    private Timer endTimer = null;

    LinearLayout.LayoutParams scrollLayoutParams;
    TextView textView;
    Integer slideHeight;
    Integer slideWidth;

    public PresentationTextView(Context context, Integer slideHeight, Integer slideWidth) {
        super(context);

        scrollLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);

        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setGravity(Gravity.CENTER);
//        linearLayout.setLayoutParams(linearLayoutParams);
        linearLayout.addView(textView);

        setFillViewport(true);
        this.slideHeight = slideHeight;
        this.slideWidth = slideWidth;

        setLayoutParams(scrollLayoutParams);
        addView(linearLayout);

        // OPTIONAL - Uncomment code below if object is used within a ScrollView
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    public void setDims(Float width, Float height) {
        scrollLayoutParams.width = Math.round(slideWidth * (width /  100));
        scrollLayoutParams.height = Math.round(slideHeight * (height /  100));
    }

    public void setBackgroundColour(String colour) {
        this.setBackgroundColor(Color.parseColor(colour));
    }

    public void setText(String text) {
        textView.setText(Html.fromHtml(text));
    }

    public void setFont(String font, Integer weight) {

        font = "name=" + font + "&weight=" + weight;

        FontRequest fontRequest = new FontRequest("com.google.android.gms.fonts",
                "com.google.android.gms",
                font,
                R.array.com_google_android_gms_fonts_certs);

        FontsContractCompat.FontRequestCallback callback = new FontsContractCompat.FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                textView.setTypeface(typeface);
            }
            @Override
            public void onTypefaceRequestFailed(int reason) {
                Toast.makeText(getContext(), "Font request failed with exit code: " + reason, Toast.LENGTH_LONG).show();
            }
        };
        FontsContractCompat.requestFont(getContext(), fontRequest, callback, getThreadHandler());
    }

    public void setTextSize(Integer size) {
        textView.setTextSize(size);
    }

    public void setTextColour(String colour) {
        textView.setTextColor(Color.parseColor(colour));
    }

    public void setPos(Float xPos, Float yPos) {
        scrollLayoutParams.setMargins(Math.round(slideWidth* (xPos /  100)), Math.round(slideHeight * (yPos / 100)), 0 ,0);
    }

    public void setStartTime(final Activity activity, Integer startTime) {
        setVisibility(View.INVISIBLE);
        Log.d("Test", "Timer started");
        startTimer = new Timer();
        startTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }, startTime
        );
    }

    public void setEndTime(final Activity activity, Integer endTime) {
        endTimer = new Timer();
        endTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }, endTime
        );
    }

    public void stopTimers() {
        if (startTimer != null) {
            startTimer.cancel();
        }
        if (endTimer != null) {
            endTimer.cancel();
        }
    }

    private Handler getThreadHandler() {
        if (mHandler == null) {
            HandlerThread handlerThread = new HandlerThread("fonts");
            handlerThread.start();
            mHandler = new Handler(handlerThread.getLooper());
        }
        return mHandler;
    }

}
