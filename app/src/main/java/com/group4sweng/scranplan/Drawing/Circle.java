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

    public Circle(int x, int y, int rad, String color)
    {
        this.color = color;
        center_x = x;
        center_y = y;
        radius = rad;
        circle_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circle_paint.setColor(Color.parseColor(color));
    }

    public void make_hollow()
    {
        circle_paint.setStyle(Paint.Style.STROKE);
    }

    public void fill_shape()
    {
        circle_paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public int getSelected_flag()
    {
        return(selected_flag);
    }

    public void set_selected()
    {
        selected_flag = 1;
    }

    public void clear_selected()
    {
        selected_flag = 0;
    }

    //function not used in the example test code, but has been used in the past.
    // has been left in for ease of integration.
    // Colours are labelled by numbers. See color documentation for more.
    // Alternatively color.GREEN (for example) is set to the right value for green.
    //               color.BLACK
    //               color.PINK   etc...
    public void setCircle_colour(int new_color)
    {
        circle_paint.setColor(new_color);
    }

    public Paint get_circ_paint()
    {
        return(this.circle_paint);
    }

    public int getCenter_x()
    {
        return (center_x);
    }

    public  int getCenter_y()
    {
        return(center_y);
    }

    public int getRadius()
    {
        return(radius);
    }

    public void setRadius(int new_val)
    {
        radius = new_val;
    }

    public void setCenter_x(int new_val)
    {
        center_x = new_val;
    }

    public void setCenter_y(int new_val)
    {
        center_y = new_val;
    }
}
