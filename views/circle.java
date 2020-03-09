package com.example.sweng_graphics.views;

import android.graphics.Paint;
import android.graphics.Color;

public class circle extends shape {

    //Circle Shape Class

    private int center_x;
    private int center_y;
    private int radius;
    private Paint circle_paint;
    private int selected_flag = 0;


    //circle constructor
    //Requires: x -> center x coordinate (int)
    //          y -> center y coordinate (int)
    //          rad -> radius of the circle (int)

    circle(int x, int y, int rad)
    {
        center_x = x;
        center_y = y;
        radius = rad;
        circle_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circle_paint.setColor(Color.RED);


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

}
