package com.example.sweng_graphics.views;

import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Path;

//The lower part o

public class triangle
{
    //Triangle Class

    private int start_x;
    private  int start_y;
    private int length;
    private Paint TrianglePaint;

    //Path around the triangle /_\
    public Path tri_draw_path;

    private int selected_flag = 0;

    //Triangle Constructor
    //Requres:
    //          len -> Length of a size of the triangle
    //          x -> the top X coordinate   \   coord of the peak of the triangle
    //          y -> the top Y coordinate    /
    triangle(int len, int x, int y)
    {
        //defining general attributes
        tri_draw_path = new Path();
        start_x = x;
        start_y = y;
        length = len;
        TrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //smoothing the lines
        TrianglePaint.setColor(Color.GREEN); //default colour. Can easily be changed here. Extra functionality can be added by making this accessable if needed.
        TrianglePaint.setStyle(Paint.Style.FILL); //setting it to be a filled shape first.
        TrianglePaint.setStrokeWidth(10);
    }

    public void fill_shape()
    {
        TrianglePaint.setStyle(Paint.Style.FILL);
    }

    public void make_hollow()
    {
        TrianglePaint.setStyle(Paint.Style.STROKE);
    }

    public void resize(int new_len)
    {
        length = new_len;
    }

    public void set_selected()
    {
        selected_flag = 1;
    }

    public int get_selected()
    {
        return(selected_flag);
    }

    public void clear_selected()
    {
        selected_flag = 0;
    }

    public int get_center_x()
    {
        return(start_x);
    }

    public int get_dist_y()
    {
        return((int) Math.sqrt(Math.pow(length, 2) - Math.pow( (int)(length /2), 2)));
    }

    public void set_center(double x, double y) //calculates the top point from the center coords given.
    {
        start_x = (int)x;
        start_y = (int)y - get_dist_to_center_y();
    }

    public int get_dist_to_center_y() // calculates the distance from the top point to the center of the triangle
    {
        int temp = get_dist_y() / 2;
        return (temp);
    }

    public int get_center_y() //returns the center Y coordinate of the triangle
    {
        return ( start_y + get_dist_to_center_y());
    }

    public int getStart_x() {
        return start_x;
    }

    public int getStart_y() {
        return start_y;
    }

    public int getLength() {
        return length;
    }

    public Paint getTrianglePaint() {
        return TrianglePaint;
    }
}
