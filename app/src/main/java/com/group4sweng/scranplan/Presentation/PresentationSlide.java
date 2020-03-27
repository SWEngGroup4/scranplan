package com.group4sweng.scranplan.Presentation;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group4sweng.scranplan.Presentation.Views.PresentationImageView;
import com.group4sweng.scranplan.Presentation.Views.PresentationPlayerView;
import com.group4sweng.scranplan.Presentation.Views.PresentationTextView;
import com.group4sweng.scranplan.R;

import java.util.ArrayList;

public class PresentationSlide extends RelativeLayout {

    private Integer slideWidth;
    private Integer slideHeight;

    private ArrayList<PresentationTextView> textViews = new ArrayList<>();
    private ArrayList<PresentationImageView> imageViews = new ArrayList<>();

    private PresentationPlayerView playerView;

    public PresentationSlide(Context context, Integer slideWidth, Integer slideHeight) {
        super(context);
        this.slideWidth = slideWidth;
        this.slideHeight = slideHeight;

        LayoutParams layoutParams = new LayoutParams(slideWidth, slideHeight);
        layoutParams.addRule(CENTER_IN_PARENT, TRUE);
        this.setLayoutParams(layoutParams);
    }

    public void show() {
        this.setVisibility(VISIBLE);
        for (PresentationTextView textView : textViews)
            textView.startTimers();

        for (PresentationImageView imageView : imageViews)
            imageView.startTimers();
    }

    public void hide() {
        this.setVisibility(GONE);
    }

    public void addText(XmlParser.Text text) {
        PresentationTextView textView = new PresentationTextView(getContext(), slideHeight, slideWidth);
        textView.setDims(text.width, text.height);
        textView.setBackgroundColor(getResources().getColor(R.color.colorWindow));
        textView.setText(text.text);
        textView.setFont(text.font, text.fontWeight);
        textView.setTextSize(text.fontSize);
        textView.setTextColour(text.fontColor);
        textView.setPos(text.xPos, text.yPos);

        if (text.startTime > 0)
            textView.setStartTime(text.startTime);
        if (text.endTime > text.startTime)
            textView.setEndTime(text.endTime);

        textViews.add(textView);
        this.addView(textView);
    }

    public void addImage(XmlParser.Image image) {
        PresentationImageView imageView = new PresentationImageView(getContext(), slideHeight, slideWidth);
        imageView.setImage(image.urlName);
        imageView.setPos(image.xStart, image.yStart);
        imageView.setDims(image.width, image.height);

        if (image.startTime > 0)
            imageView.setStartTime(image.startTime);
        if (image.endTime > 0)
            imageView.setEndTimer(image.endTime);

        imageViews.add(imageView);
        this.addView(imageView);
    }

    //TODO - add dimensions for video?
    public void addVideo(XmlParser.Video video) {
        playerView = new PresentationPlayerView(getContext(), slideHeight, slideWidth);
        playerView.initializePlayer(video.urlName, Boolean.TRUE);
        playerView.setPos(video.xStart, video.yStart);

        this.addView(playerView);
    }

    public void addTimer(Float timer) {
        TextView timerView = new TextView(getContext());
        timerView.setText(String.format("Timer: %s", timer));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        timerView.setLayoutParams(layoutParams);

        this.addView(timerView);
    }
}
