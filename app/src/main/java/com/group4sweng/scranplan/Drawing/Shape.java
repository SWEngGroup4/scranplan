package com.group4sweng.scranplan.Drawing;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Shape {

    private Integer centreX;
    private Integer centreY;
    private Float width;
    private Float height;
    protected Paint paint;
    private Integer selectedFlag = 0;
    protected Integer colour;

    //Default constructor for line and triangle class access
    Shape(String colour) {
        this.colour = Color.parseColor(colour);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(this.colour);
    }

    public Shape(Integer centreX, Integer centreY, Integer width, Integer height, String colour)
    {
        this.centreX = centreX;
        this.centreY = centreY;
        this.width = Float.valueOf(width);
        this.height = Float.valueOf(height);
        this.colour = Color.parseColor(colour);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(this.colour);
    }

    void setColour(Integer colour) {
        paint.setColor(colour);
    }

    Integer getColour() {
        return colour;
    }

    Paint getPaint() {
        return paint;
    }

    RectF getRectF() {
        return new RectF(centreX - (width / 2),
                centreY - (height / 2),
                centreX + (width / 2),
                centreY + (height / 2));
    }

}
