package com.group4sweng.scranplan.Drawing;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Color;

import java.util.Timer;
import java.util.TimerTask;

public class Circle extends Shape {

    //Circle Shape Class

    private int center_x;
    private int center_y;
    private int radius;
    private Paint circle_paint;
    private int selected_flag = 0;
    private Timer startTimer = null;
    private Timer endTimer = null;
    private String color;

    //circle constructor
    //Requires: x -> center x coordinate (int)
    //          y -> center y coordinate (int)
    //          rad -> radius of the circle (int)

    Circle(int x, int y, int rad, String color)
    {
        this.color = color;
        center_x = x;
        center_y = y;
        radius = rad;
        circle_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circle_paint.setColor(Color.parseColor(color));
    }

    void make_hollow()
    {
        circle_paint.setStyle(Paint.Style.STROKE);
    }

    void fill_shape()
    {
        circle_paint.setStyle(Paint.Style.FILL_AND_STROKE);
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

    //function not used in the example test code, but has been used in the past.
    // has been left in for ease of integration.
    // Colours are labelled by numbers. See color documentation for more.
    // Alternatively color.GREEN (for example) is set to the right value for green.
    //               color.BLACK
    //               color.PINK   etc...
    void setCircle_colour(int new_color)
    {
        circle_paint.setColor(new_color);
    }

    Paint get_circ_paint()
    {
        return(this.circle_paint);
    }

    int getCenter_x()
    {
        return (center_x);
    }

    int getCenter_y()
    {
        return(center_y);
    }

    int getRadius()
    {
        return(radius);
    }

    void setRadius(int new_val)
    {
        radius = new_val;
    }

    void setCenter_x(int new_val)
    {
        center_x = new_val;
    }

    void setCenter_y(int new_val)
    {
        center_y = new_val;
    }

    void setStartTime(final Activity activity, final Integer startTime) {
        circle_paint.setColor(Color.TRANSPARENT);
        startTimer = new Timer();
        startTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                circle_paint.setColor(Color.parseColor(color));
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
                                circle_paint.setColor(Color.TRANSPARENT);
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
