package com.group4sweng.scranplan.Drawing;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.Timer;
import java.util.TimerTask;

public class Square extends Shape {

    // Square shape class
    // This could be modified for different functionality relatively easily.


    private int center_x;
    private int center_y;
    private Paint squarePaint; //the paint attributes (thickness, colour...)
    private Rect square; //rect as defined in canvas.
    private int len;
    private int selected_flag = 0;
    private Timer startTimer = null;
    private Timer endTimer = null;
    private String color;


    //Square Constructor:
    //Requres:
    //          Top -> the y value of the top line (int)
    //          Left -> the X value of the furthest left line (int)
    //          length -> the length of the sides to draw the square (int)
    public Square(int top, int left, int length, String color)
    {
        this.color = color;
        square = new Rect(left, top, (left+length), (top+length)); //this is for a square, could be modified for a variable rect by adding different sized inputs
        squarePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        squarePaint.setColor(Color.parseColor(color));
        len = length;

        //calculates the center of the square immediately
        center_x = left + (len/2);
        center_y = top + (len/2); //len for both bc square
    }

    int getlen()
    {
        return(len);
    }

    void make_hollow()
    {
        squarePaint.setStyle(Paint.Style.STROKE);
    }

    void fill_shape()
    {
        squarePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    //resizes the length of each size to the given value (int)
    void resize(int length)
    {
        square.left = center_x - (length/2);
        square.top= center_y - (length/2);
        square.right = square.left + length; //how i've done this doesn't really matter
        square.bottom = square.top + length;
        len = length;
    }

    int getSelected_flag()
    {
        return(selected_flag);
    }

    void set_selected()
    {
        selected_flag = 1;
    }

    void clear_selected()
    {
        selected_flag = 0;
    }

    Paint getSquarePaint()
    {
        return(squarePaint);
    }

    Rect getSquare()
    {
        return(square);
    }

    void update_position(int cen_x, int cen_y)
    {
        center_x = cen_x;
        center_y = cen_y;

        resize(len);

    }

    //function that checks to see if the point provided is inside the square.
    int is_inside_square(double x, double y)
    {

        if ((square.left < x && square.right > x) && (square.top < y && square.bottom > y))
        {
            return(1);
        }
        else
        {
            return(0);
        }
    }

    void setStartTime(final Activity activity, final Integer startTime) {
        squarePaint.setColor(Color.TRANSPARENT);
        startTimer = new Timer();
        startTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                squarePaint.setColor(Color.parseColor(color));
                                startTimer = null;
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
                                squarePaint.setColor(Color.TRANSPARENT);
                                endTimer = null;
                            }
                        });
                    }
                }, endTime
        );
    }

    public void stopTimers() {
        if (startTimer != null) {
            startTimer.cancel();
            startTimer = null;
        }
        if (endTimer != null) {
            endTimer.cancel();
            endTimer = null;
        }
    }

}
