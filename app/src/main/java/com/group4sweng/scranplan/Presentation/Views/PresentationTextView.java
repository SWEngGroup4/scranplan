package com.group4sweng.scranplan.Presentation.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Html;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;

import com.group4sweng.scranplan.R;

@SuppressLint("ViewConstructor")
public class PresentationTextView extends ScrollView {

    private Handler mHandler = null;
    private CountDownTimer startTimer = null;
    private CountDownTimer endTimer = null;
    private LinearLayout.LayoutParams scrollLayoutParams;
    private TextView textView;
    private Integer slideHeight;
    private Integer slideWidth;

    public PresentationTextView(Context context, Integer slideHeight, Integer slideWidth) {
        super(context);

        scrollLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER); // Text centered within container

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setGravity(Gravity.CENTER); // Text centered in box here - change gravity to remove
        linearLayout.addView(textView);

        setFillViewport(true);
        this.slideHeight = slideHeight;
        this.slideWidth = slideWidth;

        setLayoutParams(scrollLayoutParams);
        addView(linearLayout);

//        TODO - Uncomment code below if object is used within a ScrollView
//        setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                v.performClick();
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
    }

    // Sets dimensions for the text box as a percentage of the slide width and height
    public void setDims(Float width, Float height) {
        scrollLayoutParams.width = Math.round(slideWidth * (width /  100));
        scrollLayoutParams.height = Math.round(slideHeight * (height /  100));
    }

    // Sets background colour of text box
    public void setBackgroundColour(String colour) {
        textView.setBackgroundColor(Color.parseColor(colour));
    }

    // Sets text in text box (Handles <b> and <i> tags within string)
    public void setText(String text) {
        textView.setText(Html.fromHtml(text));
    }

    // Sets font of text (Refer to https://fonts.google.com/ for list of available fonts)
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
//                Toast.makeText(getContext(), "Font request failed with exit code: " + reason, Toast.LENGTH_SHORT).show();
            }
        };
        FontsContractCompat.requestFont(getContext(), fontRequest, callback, getThreadHandler());
    }

    // Sets size of text
    public void setTextSize(Integer size) {
        textView.setTextSize(size);
    }

    // Sets colour of text
    public void setTextColour(String colour) {
        textView.setTextColor(Color.parseColor(colour));
    }

    // Sets position of text box as percentage of screen size
    public void setPos(Float xPos, Float yPos) {
        scrollLayoutParams.setMargins(Math.round(slideWidth* (xPos /  100)), Math.round(slideHeight * (yPos / 100)), 0 ,0);
    }

    // Sets time in milliseconds for the text box to first appear at
    public void setStartTime(final Integer startTime) {
        setVisibility(INVISIBLE); // Change to View.GONE if maintaining layout is not necessary
        startTimer = new CountDownTimer(startTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Empty
            }

            @Override
            public void onFinish() {
                setVisibility(VISIBLE);
            }
        };
    }

    // Sets time in milliseconds for the text box to disappear at
    public void setEndTime( Integer endTime) {
        endTimer = new CountDownTimer(endTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public  void onFinish() {
                setVisibility(INVISIBLE);
            }
        };
    }

    public void startTimers() {
        if (startTimer != null)
            startTimer.start();
        if (endTimer != null)
            endTimer.start();
    }

    // Stops timers running in case of slide change/transition
    public void stopTimers() {
        if(startTimer != null ){
        try {
            startTimer.cancel();
        }
        catch (Exception e) {
            e.printStackTrace();
        }}
        if(endTimer != null){
        try {
            endTimer.cancel();
        }
        catch (Exception e) {
            e.printStackTrace();
        }}
    }

    // Used for asynchronous retrieval of fonts from google API
    private Handler getThreadHandler() {
        if (mHandler == null) {
            HandlerThread handlerThread = new HandlerThread("fonts");
            handlerThread.start();
            mHandler = new Handler(handlerThread.getLooper());
        }
        return mHandler;
    }

}