package com.group4sweng.scranplan.Drawing;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;

public class Shape {

    Paint paint;
    private Integer colour;

    Shape(String colour) {
        this.colour = Color.parseColor(colour);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(this.colour);
    }

    void setPaintColour(Integer colour) {
        paint.setColor(colour);
    }

    Integer getPaintColour() {
        return paint.getColor();
    }

    public Integer getColour() {
        return colour;
    }

    void setGradient(Float xPos1, Float yPos1, String colour1, Float xPos2, Float yPos2, String colour2, Boolean cyclic) {
        if (cyclic)
            paint.setShader(new RadialGradient((xPos1 + xPos2) / 2, (yPos1 + yPos2) / 2,
                    (float) Math.sqrt(Math.pow(Math.abs(xPos2-xPos1), 2) * Math.pow(Math.abs(yPos2-yPos1), 2)),
                    Color.parseColor(colour1), Color.parseColor(colour2), Shader.TileMode.CLAMP));
        else
            paint.setShader(new LinearGradient(xPos1, yPos1, xPos2, yPos2, Color.parseColor(colour1), Color.parseColor(colour2), Shader.TileMode.CLAMP));
    }

    Object getGradient() {
        return paint.getShader();
    }

    Paint getPaint() {
        return paint;
    }
}
