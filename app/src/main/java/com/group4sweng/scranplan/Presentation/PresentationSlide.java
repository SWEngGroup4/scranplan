package com.group4sweng.scranplan.Presentation;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group4sweng.scranplan.Drawing.Line;
import com.group4sweng.scranplan.Drawing.GraphicsView;
import com.group4sweng.scranplan.Drawing.Rectangle;
import com.group4sweng.scranplan.Drawing.Shape;
import com.group4sweng.scranplan.Drawing.Triangle;
import com.group4sweng.scranplan.Presentation.Views.PresentationImageView;
import com.group4sweng.scranplan.Presentation.Views.PresentationPlayerView;
import com.group4sweng.scranplan.Presentation.Views.PresentationTextView;
import com.group4sweng.scranplan.R;

import java.util.ArrayList;

public class PresentationSlide extends RelativeLayout {

    private Integer slideWidth;
    private Integer slideHeight;

    private ArrayList<PresentationTextView> textViews = new ArrayList<>();
    private ArrayList<Shape> shapes = new ArrayList<>();
    private ArrayList<PresentationImageView> imageViews = new ArrayList<>();

    private GraphicsView graphicsView;

    private PresentationPlayerView playerView;

    public PresentationSlide(Context context, Integer slideWidth, Integer slideHeight) {
        super(context);
        this.slideWidth = slideWidth;
        this.slideHeight = slideHeight;

        graphicsView = new GraphicsView(getContext(), false);
        this.addView(graphicsView);

        LayoutParams layoutParams = new LayoutParams(slideWidth, slideHeight);
        layoutParams.addRule(CENTER_IN_PARENT, TRUE);
        this.setLayoutParams(layoutParams);
    }

    //Makes slide visible and starts timers for elements
    public void show() {
        this.setVisibility(VISIBLE);
        for (PresentationTextView textView : textViews)
            textView.startTimers();

        graphicsView.startTimers();

        for (PresentationImageView imageView : imageViews)
            imageView.startTimers();
    }

    //Makes slide invisble
    public void hide() {
        this.setVisibility(GONE);

        for (PresentationTextView textView : textViews)
            textView.stopTimers();

        graphicsView.endTimers();

        for (PresentationImageView imageView : imageViews)
            imageView.stopTimers();
    }

    //Add text element to slide
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

    public void addLine(XmlParser.Line line) {
        Line newLine = new Line(Math.round(slideWidth * (line.xStart / 100)),
                Math.round(slideHeight * (line.yStart /100)),
                Math.round(slideWidth * (line.xEnd / 100)),
                Math.round(slideHeight * (line.yEnd / 100)),
                line.lineColor);
        graphicsView.addLine(newLine, line.startTime, line.endTime);
    }

    //Add shape to file
    public void addShape(XmlParser.Shape shape) {
        XmlParser.Shading shading = null;
        if (shape.shading != null) {
            shading = new XmlParser.Shading(
                    slideWidth * (shape.shading.x1 / 100),
                    slideHeight * (shape.shading.y1 / 100),
                    slideWidth * (shape.shading.x2 / 100),
                    slideHeight * (shape.shading.y2 / 100),
                    shape.shading.color1, shape.shading.color2,
                    shape.shading.cyclic);
        }

        if (shape.type.equals("oval")) {
            Rectangle oval = new Rectangle(Math.round(slideWidth * (shape.xStart / 100)), //xPos
                    Math.round(slideHeight * (shape.yStart / 100)), //yPos
                    Math.round(slideWidth * (shape.width / 100)), //width
                    Math.round(slideHeight * (shape.height / 100)), //height
                    shape.fillColor, shading);
            graphicsView.addOval(oval, shape.startTime, shape.endTime);
        } else if (shape.type.equals("rectangle")) {
            Rectangle rect = new Rectangle(Math.round(slideWidth * (shape.xStart / 100)), //xPos
                    Math.round(slideHeight * (shape.yStart / 100)), //yPos
                    Math.round(slideWidth * (shape.width / 100)), //width
                    Math.round(slideHeight * (shape.height / 100)), //height
                    shape.fillColor, shading);
            graphicsView.addRectangle(rect, shape.startTime, shape.endTime);
        }
    }

    public void addTriangle(XmlParser.Triangle triangle) {
        Triangle newTriangle = new Triangle(Math.round(slideWidth * (triangle.xPos1 / 100)),
                Math.round(slideHeight * (triangle.yPos1 / 100)),
                Math.round(slideWidth * (triangle.xPos2 / 100)),
                Math.round(slideHeight * (triangle.yPos2 / 100)),
                Math.round(slideWidth * (triangle.xPos3 / 100)),
                Math.round(slideHeight * (triangle.yPos3 / 100)),
                triangle.fillColor, triangle.shading);
        graphicsView.addTriangle(newTriangle, triangle.startTime, triangle.endTime);
    }

    //Add image element to file
    public void addImage(XmlParser.Image image) {
        PresentationImageView imageView = new PresentationImageView(getContext(), slideHeight, slideWidth);
        imageView.setImage(image.urlName);
        imageView.setPos(image.xStart, image.yStart);
        imageView.setDims(image.width, image.height);

        if (image.startTime > 0)
            imageView.setStartTime(image.startTime);
        if (image.endTime > 0)
            imageView.setEndTime(image.endTime);

        imageViews.add(imageView);
        this.addView(imageView);
    }

    //TODO - add dimensions for video?
    //Add video element to file
    public void addVideo(XmlParser.Video video) {
        playerView = new PresentationPlayerView(getContext(), slideHeight, slideWidth);
        playerView.initializePlayer(video.urlName, Boolean.TRUE);
        playerView.setPos(video.xStart, video.yStart);

        this.addView(playerView);
    }

    //Add timer to file
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
