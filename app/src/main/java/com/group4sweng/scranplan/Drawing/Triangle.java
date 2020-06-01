package com.group4sweng.scranplan.Drawing;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.group4sweng.scranplan.Xml.XmlParser;

import java.io.Serializable;

public class Triangle extends Shape {

    private Integer centreX;
    private Integer centreY;
    private Float size;

    public Triangle(Integer centreX, Integer centreY, Float size) {
        super();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.centreX = centreX;
        this.centreY = centreY;
        this.size = size;
    }

    public Triangle(Integer centreX, Integer centreY, Float size, String colour, XmlParser.Shading shading) {
        super(colour);
        this.centreX = centreX;
        this.centreY = centreY;
        this.size = size;
    }

    Path getPath() {
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(centreX - (size / 2), centreY - (getY() / 2));
        path.lineTo(centreX, centreY + (getY() / 2));
        path.lineTo(centreX + (size / 2), centreY - (getY() / 2));
        path.close();
        return path;
    }

    private Float getY() {
        return (float) Math.sqrt(Math.pow(size, 2) - Math.pow(size /2, 2));
    }

    boolean isInside(Float x, Float y) {
        Path temp = new Path();
        temp.moveTo(x, y);
        RectF rect = new RectF(x-1, y-1, x+1, y+1);
        temp.addRect(rect, Path.Direction.CW);
        temp.op(getPath(), Path.Op.DIFFERENCE);
        return temp.isEmpty();
    }

    void updatePosition(Integer x, Integer y) {
        if (selected) {
            centreX = x;
            centreY = y;
        }
    }

    Integer getCentreX() {
        return centreX;
    }

    Integer getCentreY() {
        return centreY;
    }

    Float getSize() {
        return size;
    }
}