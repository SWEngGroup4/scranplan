package com.group4sweng.scranplan.Drawing;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import com.group4sweng.scranplan.Presentation.XmlParser;

public class Rectangle extends Shape {

    private Integer centreX;
    private Integer centreY;
    private Float width;
    private Float height;

    public Rectangle(Integer centreX, Integer centreY, Integer width, Integer height, String colour, XmlParser.Shading shading)
    {
        super(colour);
        this.centreX = centreX;
        this.centreY = centreY;
        this.width = Float.valueOf(width);
        this.height = Float.valueOf(height);

        if (shading != null)
            setGradient(shading.x1, shading.y1, shading.color1, shading.x2, shading.y2, shading.color2, shading.cyclic);
    }

    @Override
    void setGradient(Float xPos1, Float yPos1, String colour1, Float xPos2, Float yPos2, String colour2, Boolean cyclic) {
        if (cyclic)
            paint.setShader(new RadialGradient((xPos1 + xPos2) / 2, (yPos1 + yPos2) / 2, Math.max(width, height),
                    Color.parseColor(colour1), Color.parseColor(colour2), Shader.TileMode.CLAMP));
        else
            paint.setShader(new LinearGradient(xPos1, yPos1, xPos2, yPos2, Color.parseColor(colour1), Color.parseColor(colour2), Shader.TileMode.CLAMP));
    }

    public RectF getRectF() {
        return new RectF(centreX - (width / 2),
                centreY - (height / 2),
                centreX + (width / 2),
                centreY + (height / 2));
    }
}
