package com.group4sweng.scranplan.Drawing;

public class Line extends Shape {

    private Integer xStart;
    private Integer yStart;
    private Integer xEnd;
    private Integer yEnd;

    public Line(Integer xStart, Integer yStart, Integer xEnd, Integer yEnd, String colour)
    {
        super(colour);
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;

        this.paint.setStrokeWidth(5f);
    }

    Integer getxStart() {
        return xStart;
    }

    Integer getyStart() {
        return yStart;
    }

    Integer getxEnd() {
        return xEnd;
    }

    Integer getyEnd() {
        return yEnd;
    }
}
