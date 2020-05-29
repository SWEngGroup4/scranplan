package com.group4sweng.scranplan.Presentation.Views;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import com.squareup.picasso.Picasso;

public class PresentationImageView extends AppCompatImageView {

    private CountDownTimer startTimer;
    private CountDownTimer endTimer;
    private LinearLayout.LayoutParams layoutParams;
    private Integer slideHeight;
    private Integer slideWidth;

    public PresentationImageView(Context context, Integer slideHeight, Integer slideWidth) {
        super(context);

        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        this.slideHeight = slideHeight;
        this.slideWidth = slideWidth;
        setLayoutParams(layoutParams);
    }

    public void setDims(Float width, Float height) {
        layoutParams.width = Math.round(slideWidth * (80f / 100));
        layoutParams.height = Math.round(slideHeight * (40f / 100));
    }

    public void setImage(String url) {
        Picasso.get().load(url).fit().centerCrop().into(this);
    }

    public void setPos(Float xPos, Float yPos) {
        layoutParams.setMargins(Math.round(slideWidth * (xPos / 100)),
                Math.round(slideHeight * (yPos / 100)), 0, 0);
    }

    public void setStartTime(Integer startTime) {
        setVisibility(INVISIBLE);
        startTimer = new CountDownTimer(startTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                setVisibility(VISIBLE);
            }
        };
    }

    public void setEndTime(Integer endTime) {
        endTimer = new CountDownTimer(endTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
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
        try {
            startTimer.cancel();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            endTimer.cancel();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
