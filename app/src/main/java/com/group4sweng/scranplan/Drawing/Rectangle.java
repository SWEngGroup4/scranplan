package com.group4sweng.scranplan.Drawing;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import com.group4sweng.scranplan.Xml.XmlParser;

import java.io.Serializable;

public class Rectangle extends Shape {

    private Integer centreX;
    private Integer centreY;
    private Float width;
    private Float height;

    private Rectangle mOutline;

    public Rectangle(Integer centreX, Integer centreY, Integer width, Integer height)
    {
        super();
        this.centreX = centreX;
        this.centreY = centreY;
        this.width = Float.valueOf(width);
        this.height = Float.valueOf(height);
    }

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

    Float getCentreX() {
        return (float) centreX;
    }

    Float getCentreY() {
        return (float) centreY;
    }

    public Float getXStart() {
        return centreX - (width / 2);
    }

    public Float getYStart() {
        return centreY - (height / 2);
    }

    public Float getWidth() {
        return width;
    }

    public Float getHeight() {
        return height;
    }

    boolean isInsideSquare(Float x, Float y) {
        return (centreX - (width / 2) < x && centreX + (width / 2) > x) &&
                (centreY - (height / 2) < y && centreY + (height / 2) > y);
    }

    boolean isInsideOval(Float x, Float y) {
        return (Math.pow(x - centreX, 2)) + (Math.pow(y - centreY, 2)) <= (width / 2) * (height / 2);
    }

    void updatePosition(Integer x, Integer y) {
        if (selected) {
            centreX = x;
            centreY = y;
        }
    }

    void scale(Float lastSpanX, Float lastSpanY, Float spanX, Float spanY) {
        float newWidth = width + (spanX - lastSpanX);
        if (newWidth > 100 && newWidth < 800)
            width = newWidth;

        float newHeight = height + (spanY - lastSpanY);
        if (newHeight > 100 && newHeight < 800)
            height = newHeight;
    }
}
