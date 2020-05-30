package com.group4sweng.scranplan.Drawing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.shapes.OvalShape;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;

import com.group4sweng.scranplan.Xml.XmlParser;

import java.util.ArrayList;

public class GraphicsView extends View {

    private Boolean interaction;

    private ArrayList<Line> lines = new ArrayList<>();
    private ArrayList<Rectangle> ovals = new ArrayList<>();
    private ArrayList<Rectangle> rectangles = new ArrayList<>();
    private ArrayList<Triangle> triangles = new ArrayList<>();
    private ArrayList<CountDownTimer> startTimers = new ArrayList<>();
    private ArrayList<CountDownTimer> endTimers = new ArrayList<>();

    private Integer mColour = Color.BLACK;

    private Integer mHeight;
    private Integer mWidth;

    //Interaction variables
    private Integer INVALID_POINTER_ID = -1;
    private Integer mActivePointerId = INVALID_POINTER_ID;
    private Rectangle selectedRect = null;
    private Triangle selectedTri = null;

    private ScaleGestureDetector mScaleDetector;
    private Float mScaleFactor = 1.f;

    public GraphicsView(Context context, Integer height, Integer width, Boolean interaction) {
        super(context);
        mHeight = height;
        mWidth = width;
        this.interaction = interaction;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public GraphicsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //Constructor
    public GraphicsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setColour(Integer colour) {
        mColour = colour;
        if (selectedRect != null)
            selectedRect.setPaintColour(colour);
        postInvalidate();
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
        if (interaction)
            shape.setPaintColour(mColour);
        ovals.add(shape);
        setTimers(shape, startTime, endTime);
        postInvalidate();
    }

    public ArrayList<Rectangle> getOvals() {
        return ovals;
    }

    public void addRectangle(Rectangle shape, Integer startTime, Integer endTime) {
        if (interaction)
            shape.setPaintColour(mColour);
        rectangles.add(shape);
        setTimers(shape, startTime, endTime);
        postInvalidate();
    }

    public ArrayList<Rectangle> getRectangles() {
        return rectangles;
    }

    public ArrayList<XmlParser.Shape> getShapes() {
        ArrayList<XmlParser.Shape> shapes = new ArrayList<>();
        Log.d("Test", this.getMeasuredWidth() + " x " + this.getMeasuredHeight());
        for (Rectangle rect : ovals) {
            shapes.add(new XmlParser.Shape("oval", (rect.getXStart()/this.getMeasuredWidth())*100, (rect.getYStart()/this.getMeasuredHeight())*100,
                    (rect.getWidth()/this.getMeasuredWidth())*100, (rect.getHeight()/this.getMeasuredHeight())*100,
                    "#" + Integer.toHexString(rect.getColour()), 0, 0, null));
        }
        for (Rectangle rect : rectangles) {
            shapes.add(new XmlParser.Shape("rectangle", (rect.getXStart()/this.getMeasuredWidth())*100, (rect.getYStart()/this.getMeasuredHeight())*100,
                    (rect.getWidth()/this.getMeasuredWidth())*100, (rect.getHeight()/this.getMeasuredHeight())*100,
                    "#" + Integer.toHexString(rect.getColour()), 0, 0, null));
        }
        return shapes;
    }

    public void addTriangle(Triangle triangle, Integer startTime, Integer endTime) {
        if (interaction)
            triangle.setPaintColour(mColour);
        triangles.add(triangle);
        setTimers(triangle, startTime, endTime);
        postInvalidate();
    }

    public ArrayList<XmlParser.Triangle> getTriangles() {
        ArrayList<XmlParser.Triangle> tri = new ArrayList<>();
        for (Triangle triangle : triangles) {
            tri.add(new XmlParser.Triangle((triangle.getCentreX().floatValue()/this.getMeasuredWidth())*100,
                    (triangle.getCentreY().floatValue()/this.getMeasuredHeight())*100, (triangle.getSize()/this.getMeasuredWidth())*100,
                    "#" + Integer.toHexString(triangle.getColour()), 0, 0, null));
        }
        return tri;
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
        super.onDraw(canvas);
        canvas.save();

        for (Line line : lines)
            canvas.drawLine(line.getxStart(), line.getyStart(),
                    line.getxEnd(), line.getyEnd(), line.getPaint());

        for (Rectangle oval : ovals)
            canvas.drawOval(oval.getRectF(), oval.getPaint());

        for (Rectangle rect : rectangles)
            canvas.drawRect(rect.getRectF(), rect.getPaint());

        for (Triangle triangle : triangles) {
            canvas.drawPath(triangle.getPath(), triangle.getPaint());
        }

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) //this function allows movement of shapes
    {
        mScaleDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = event.getActionIndex();
                final Float x = event.getX(pointerIndex);
                final Float y = event.getY(pointerIndex);

                squareCheck(x, y, 1);

                mActivePointerId = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                final Float x = event.getX(pointerIndex);
                final Float y = event.getY(pointerIndex);

                if (!squareCheck(x, y, 2)) {
                    if (!ovalCheck(x, y)) {
                        selectedRect = null;
                        if (!triangleCheck(x, y)) {
                            selectedTri = null;
                        }
                    }
                }

                postInvalidate();
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = event.getActionIndex();
                final Integer pointerId = event.getPointerId(pointerIndex);

                if (pointerId.equals(mActivePointerId)) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    private boolean squareCheck(Float x, Float y, Integer flag) {
        boolean found = false;

        for (Rectangle rect : rectangles) {
            if (rect.isInsideSquare(x, y)) {
                if (selectedRect == null || rect == selectedRect) {
                    rect.updatePosition(Math.round(x), Math.round(y));
                    rect.setSelected();
                    selectedRect = rect;
                    found = true;
                }
            } else
                rect.clearSelected();
        }
        return found;
    }

    private boolean ovalCheck(Float x, Float y) {
        boolean found = false;

        for (Rectangle rect : ovals) {
            if (rect.isInsideOval(x, y)) {
                if (selectedRect == null || rect == selectedRect) {
                    rect.updatePosition(Math.round(x), Math.round(y));
                    rect.setSelected();
                    selectedRect = rect;
                    found = true;
                }
            } else
                rect.clearSelected();
        }
        return found;
    }

    private boolean triangleCheck(Float x, Float y) {
        boolean found = false;

        for (Triangle triangle : triangles) {
            if (triangle.isInside(x, y)) {
                triangle.updatePosition(Math.round(x), Math.round(y));
                triangle.setSelected();
                selectedRect = null;
                selectedTri = triangle;
                found = true;
            } else
                triangle.clearSelected();
        }

        return found;
    }

    public void deleteSelected() {
        if (selectedRect != null) {
            rectangles.remove(selectedRect);
            ovals.remove(selectedRect);
            selectedRect = null;
        }
        if (selectedTri != null) {
            triangles.remove(selectedTri);
            selectedTri = null;
        }

        postInvalidate();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private Float lastSpanX;
        private Float lastSpanY;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastSpanX = detector.getCurrentSpanX();
            lastSpanY = detector.getCurrentSpanY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            if (selectedRect != null) {
                selectedRect.scale(lastSpanX, lastSpanY, detector.getCurrentSpanX(), detector.getCurrentSpanY());
            }
            postInvalidate();
            return true;
        }
    }

}
