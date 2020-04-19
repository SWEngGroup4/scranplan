package com.group4sweng.scranplan.Drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GraphicsView extends View {

    private Boolean interaction;

    private ArrayList<Line> lines = new ArrayList<>();
    private ArrayList<Rectangle> ovals = new ArrayList<>();
    private ArrayList<Rectangle> rectangles = new ArrayList<>();
    private ArrayList<Triangle> triangles = new ArrayList<>();
    private ArrayList<CountDownTimer> startTimers = new ArrayList<>();
    private ArrayList<CountDownTimer> endTimers = new ArrayList<>();

    public GraphicsView(Context context, Boolean interaction) {
        super(context);
        this.interaction = interaction;
    }

    public GraphicsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //Constructor
    public GraphicsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addLine(Line line, Integer startTime, Integer endTime) {
        lines.add(line);
        setTimers(line, startTime, endTime);
        postInvalidate();
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void addOval(Rectangle shape, Integer startTime, Integer endTime) {
        ovals.add(shape);
        setTimers(shape, startTime, endTime);
        postInvalidate();
    }

    public ArrayList<Rectangle> getOvals() {
        return ovals;
    }

    public void addRectangle(Rectangle shape, Integer startTime, Integer endTime) {
        rectangles.add(shape);
        setTimers(shape, startTime, endTime);
        postInvalidate();
    }

    public ArrayList<Rectangle> getRectangles() {
        return rectangles;
    }

    public ArrayList<Rectangle> getShapes() {
        ArrayList<Rectangle> shapes = ovals;
        shapes.addAll(rectangles);
        return shapes;
    }

    public void addTriangle(Triangle triangle, Integer startTime, Integer endTime) {
        triangles.add(triangle);
        setTimers(triangle, startTime, endTime);
        postInvalidate();
    }

    public ArrayList<Triangle> getTriangles() {
        return triangles;
    }

    private void setTimers(Shape shape, Integer startTime, Integer endTime) {
        if (startTime > 0) {
            shape.setPaintColour(Color.TRANSPARENT);
            startTimers.add(new CountDownTimer(startTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    shape.setPaintColour(Color.TRANSPARENT);
                    postInvalidate();
                }

                @Override
                public void onFinish() {
                    shape.setPaintColour(shape.getColour());
                    postInvalidate();
                }
            });
        }

        if (endTime > 0) {
            endTimers.add(new CountDownTimer(endTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    shape.setPaintColour(Color.TRANSPARENT);
                    postInvalidate();
                }
            });
        }
    }

    public void startTimers() {
        for (CountDownTimer timer : startTimers)
            if (timer != null)
                timer.start();

        for (CountDownTimer timer : endTimers)
            if (timer != null)
                timer.start();
    }

    public void endTimers() {
        for (CountDownTimer timer : startTimers)
            if (timer != null)
                timer.cancel();

        for (CountDownTimer timer : endTimers)
            if (timer != null)
                timer.cancel();
    }

    @Override
    public void onDraw(Canvas canvas) {
        for (Line line : lines)
            canvas.drawLine(line.getxStart(), line.getyStart(),
                    line.getxEnd(), line.getyEnd(), line.getPaint());

        for (Rectangle oval : ovals)
            canvas.drawOval(oval.getRectF(), oval.getPaint());

        for (Rectangle rect : rectangles)
            canvas.drawRect(rect.getRectF(), rect.getPaint());

        for (Triangle triangle : triangles)
            canvas.drawPath(triangle.getPath(), triangle.getPaint());
    }

}
