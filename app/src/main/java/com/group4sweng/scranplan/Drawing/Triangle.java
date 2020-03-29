package com.group4sweng.scranplan.Drawing;

import android.graphics.Paint;
import android.graphics.Path;

public class Triangle extends Shape {

    private Integer xPos1;
    private Integer yPos1;
    private Integer xPos2;
    private Integer yPos2;
    private Integer xPos3;
    private Integer yPos3;

    public Triangle(Integer xPos1, Integer yPos1, Integer xPos2, Integer yPos2,
                    Integer xPos3, Integer yPos3, String colour) {
        super(colour);
        this.xPos1 = xPos1;
        this.yPos1 = yPos1;
        this.xPos2 = xPos2;
        this.yPos2 = yPos2;
        this.xPos3 = xPos3;
        this.yPos3 = yPos3;

        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    Path getPath() {
        Path path = new Path();
        path.moveTo(xPos1, yPos1);
        path.lineTo(xPos2, yPos2);
        path.lineTo(xPos3, yPos3);
        path.close();
        return path;
    }

}