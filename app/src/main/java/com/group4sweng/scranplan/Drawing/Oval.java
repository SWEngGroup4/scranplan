package com.group4sweng.scranplan.Drawing;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class Oval extends Shape {

    //Oval Shape Class

    private Integer centreX;
    private Integer centreY;
    private Float width;
    private Float height;
    private Paint ovalPaint;
    private Integer selectedFlag = 0;
    private String color;

    public Oval(Integer centreX, Integer centreY, Integer width, Integer height, String colour) {
        super(centreX, centreY, width, height, colour);
    }

    public void makeHollow()
    {
        ovalPaint.setStyle(Paint.Style.STROKE);
    }

    public void fillShape()
    {
        ovalPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public Integer getselectedFlag()
    {
        return(selectedFlag);
    }

    public void setSelected()
    {
        selectedFlag = 1;
    }

    public void clearSelected()
    {
        selectedFlag = 0;
    }
}
