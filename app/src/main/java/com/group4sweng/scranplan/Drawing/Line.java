package com.group4sweng.scranplan.Drawing;

public class Line extends Shape {

    private Integer xStart;
    private Integer yStart;
    private Integer xEnd;
    private Integer yEnd;

    public Line(Integer xStart, Integer yStart, Integer xEnd, Integer yEnd)
    {
        super();
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;

        this.paint.setStrokeWidth(5f);
    }

    public Line(Integer xStart, Integer yStart, Integer xEnd, Integer yEnd, String colour)
    {
        super(colour);
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;

        this.paint.setStrokeWidth(5f);
    }

    public Integer getxStart() {
        return xStart;
    }

    public Integer getyStart() {
        return yStart;
    }

    public Integer getxEnd() {
        return xEnd;
    }

    public Integer getyEnd() {
        return yEnd;
    }
}
