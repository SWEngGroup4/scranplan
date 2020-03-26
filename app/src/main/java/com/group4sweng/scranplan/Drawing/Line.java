package com.group4sweng.scranplan.Drawing;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Timer;
import java.util.TimerTask;

public class Line
{
    //Class for Lines
    //It is more of a data store as there isn't much required for a line at this time.
    //This class could be further developed to include more complex lines with further
    //properties...

    private int x_pos_start = 0;
    private int y_pos_start = 0;

    private int x_pos_end = 0;
    private int y_pos_end = 0;

    private Paint linePaint;
    private int length;

    private Timer startTimer;
    private Timer endTimer;
    private String color;

    //Line Constructor
    //Requires:
    //          x_start -> starting X coord val (int)
    //          y_start -> starting Y coord val (int)
    //          x_end -> line end X coord val (int)
    //          x_end -> line end Y coord val (int)
    Line(int x_start, int y_start, int x_end, int y_end, String color)
    {
        this.color = color;
        x_pos_start = x_start;
        y_pos_start = y_start;
        x_pos_end = x_end;
        y_pos_end = y_end;

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor(color));
        linePaint.setStrokeWidth(5f);
    }

    public int getX_pos_start() {
        return x_pos_start;
    }

    public void setX_pos_start(int x_start)
    {
        x_pos_start = x_start;
    }

    public int getY_pos_start() {
        return y_pos_start;
    }

    public void setY_pos_start(int y_pos_start) {
        this.y_pos_start = y_pos_start;
    }

    public int getX_pos_end() {
        return x_pos_end;
    }

    public void setX_pos_end(int x_pos_end) {
        this.x_pos_end = x_pos_end;
    }

    public int getY_pos_end() {
        return y_pos_end;
    }

    public void setY_pos_end(int y_pos_end) {
        this.y_pos_end = y_pos_end;
    }

    void setStartTime(final Activity activity, final Integer startTime) {
        linePaint.setColor(Color.TRANSPARENT);
        startTimer = new Timer();
        startTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                linePaint.setColor(Color.parseColor(color));
                                startTimer = null;
                            }
                        });
                    }
                }, startTime
        );
    }

    public void setEndTime(final Activity activity, Integer endTime) {
        endTimer = new Timer();
        endTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                linePaint.setColor(Color.TRANSPARENT);
                                endTimer = null;
                            }
                        });
                    }
                }, endTime
        );
    }

    public void stopTimers() {
        if (startTimer != null) {
            startTimer.cancel();
            startTimer = null;
        }
        if (endTimer != null) {
            endTimer.cancel();
            endTimer = null;
        }
    }
}
