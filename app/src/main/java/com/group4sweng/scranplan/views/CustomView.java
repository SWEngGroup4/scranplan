package com.group4sweng.scranplan.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.*;

import androidx.annotation.Nullable;

//Custom View - the class where the main implementation of the features is.
//This could be used as a template to show how features can be implemented.
//Or this could be directly included and have sections called from outside.
// NOT SURE HOW IT IS GOING TO BE USED SO I HAVE LEFT IT AS OPEN AS POSSIBLE.
public class CustomView extends View
{
    private int max_num_shapes = 100;

    //old values of X and Y, so that when drawing lines we know when a move has been made.
    private double old_x = 0;
    private double old_y = 0;

    //3 points that are used to define the path of each triangle.
    private Point point_a = new Point(0,0);
    private Point point_b = new Point(0,0);
    private Point point_c = new Point(0,0);

    //SHAPES are not dynamic (ie they have fixed lengths).
    //Circles, Squares and Triangles have a max of 100 - this can be changed here easily.
    //The shapes are on independent timers so they disappear and get removed from the array after this timer
    //making the array size less important.

    //the shapes also have an array of countdowntimers which are indexed the same as the shapes.
    private triangle[] triangles = new triangle[max_num_shapes];
    private CountDownTimer[] triangle_lives = new CountDownTimer[max_num_shapes]; //counter for a correspondingly indexed triangle.
    private int num_triangles = 0;

    private square[] square_array = new square[max_num_shapes];
    private CountDownTimer[] square_lives = new CountDownTimer[max_num_shapes];
    private int num_squares = 0;

    private circle[] circle_array = new circle[max_num_shapes];
    private CountDownTimer[] circle_lives = new CountDownTimer[max_num_shapes];
    private int num_circles = 0;

    //lines are done in a very much the same way except with 500 as its array size - as it is much faster to draw lines.
    private line[] lines = new line[500];
    private int num_lines = 0;
    private CountDownTimer[] line_lives = new CountDownTimer[500];

    private boolean draw_line = false;

    private Paint line_paint_brush;

    private int shape_selected = 0;

    public CustomView(Context context) {
        super(context);
        init(null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    //Constructor
    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    //draw_line is a mode where lines can be drawn. It is set in the example with a button labelled
    //'/'. When in draw mode it ignores presses on shapes so that you can draw over the top of them.
    public void update_line_stat()
    {
        draw_line = !draw_line;
    }

    //Creating a new circle and adding it to the circle array.
    public void new_circle()
    {
        circle_array[num_circles] = new circle(200, 200, 100, "#000000"); //created a new circle object
        //default colour is green :)
        start_timer(5000,0);
        num_circles++;
        postInvalidate();
    }

    //Creating a new Square and adding it to the square array.
    public void new_square()
    {

        square_array[num_squares] = new square(100, 100, 200, "#000000");
        num_squares++;
        start_timer(5000, 1);

        postInvalidate();
    }

    //Creating a new triagngle and adding it to the triangle array.
    public void new_triangle()
    {
        triangles[num_triangles] = new triangle(200,200,200, "#000000");

        num_triangles ++;
        start_timer(10000, 3);

        Log.d("hint", "here" + num_triangles);
        postInvalidate();

    }

    //timer function, that starts the a timer to the specific timer array (so shapes can be indexed).
    // inputs are int millisInfuture (time to count down from in milliseconds) and flag (that tells it what kind of shape it is).
    public void start_timer(int millisInFuture, int flag) //flag = 0 circle || flag = 1 square || flag = 2 lines || flag = 3 triangle
    {
        if (flag == 0)
        {
            //Circles
            circle_lives[num_circles] = new CountDownTimer(millisInFuture, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {
                    //Left in incase something is needs doing every reqularly as the timer goes down.
                    //on each count
                }

                @Override
                public void onFinish()
                {
                    delete_first_circle();

                    if (num_circles > 0)
                    {
                        num_circles--;
                    }

                    postInvalidate();
                }
            }.start();
        }

        else if (flag == 1)
        {
            //Squares
            square_lives[num_squares] = new CountDownTimer(millisInFuture, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {
                    //Left in incase something is needs doing every reqularly as the timer goes down.
                    //on each count
                }

                @Override
                public void onFinish()
                {
                    //Log.d("COUNTER DONE", "counter finished");
                    delete_first_square();

                    if(num_squares > 0)
                    {
                        num_squares--;
                    }

                    postInvalidate();
                }
            }.start();

        }

        else if (flag == 2) {
            //Lines
            line_lives[num_lines] = new CountDownTimer(millisInFuture, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //Left in incase something is needs doing every reqularly as the timer goes down.
                    //on each count
                }

                @Override
                public void onFinish() {
                    delete_first_line();

                    if (num_lines > 0)
                    {
                        num_lines--;
                    }

                    postInvalidate();
                }
            }.start();
        }

        else if (flag == 3) {
            //Triangles
            triangle_lives[num_triangles] = new CountDownTimer(millisInFuture, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //on each count
                }

                @Override
                public void onFinish() {
                    //Log.d("COUNTER DONE", "counter finished");
                    delete_first_triangle();

                    if (num_triangles > 0)
                    {
                        num_triangles--;
                    }
                    postInvalidate();
                }
            }.start();
        }
    }

    //Functions to delete the first item from each array type.
    public void delete_first_triangle()
    {
        System.arraycopy(triangles, 1, triangles, 0, triangles.length -1);
    }

    public void delete_first_line()
    {
        System.arraycopy(lines, 1, lines, 0, lines.length -1);
    }


    public void delete_first_square()
    {

        System.arraycopy(square_array, 1, square_array, 0, square_array.length -1);

    }

    public void delete_first_circle()
    {
        System.arraycopy(circle_array, 1, circle_array, 0, circle_array.length -1);
    }

    //functions to delete the last value from the corresponding array.
    //has protection to stop it trying to exceed its bounds.
    public void delete_circle()
    {

        if (num_circles == 0)
        {
            return;
        }

        else if (num_circles == 1)
        {
            num_circles--; //now it knows to automatically overwrite it
        }

        else
        {

            circle_lives[num_circles].cancel(); //cancel relevent timer

            System.arraycopy(circle_array, 0, circle_array, 0, circle_array.length -1);

            if (num_circles > 0)
            {
                num_circles--;
            }
        }
        postInvalidate();
    }



public void delete_triangle()
    {

        if (num_triangles == 0)
        {
            return;
        }

        else if (num_triangles == 1)
        {
            num_triangles--; //now it knows to automatically overwrite it
        }

        else {
            triangle_lives[num_triangles].cancel(); //cancel timer
            System.arraycopy(triangles, 0, triangles, 0, triangles.length -1);

            if (num_triangles > 0)
            {
                num_triangles--;
            }

        }
        postInvalidate();
    }


    public void delete_square()
    {

        if (num_squares == 0)
        {
            return;
        }

        else if (num_squares == 1)
        {
            num_squares--; //now it knows to automatically overwrite it
        }

        else {
            square_lives[num_squares].cancel(); //important to cancel timer when deleting it
            System.arraycopy(square_array, 0, square_array, 0, square_array.length -1);

            if (num_squares > 0)
            {
                num_squares--;
            }
        }
        postInvalidate();
    }




    private void init(@Nullable AttributeSet set)
    {
        //initialises the line Paint. It is the only one that is initialised in this file.
        //if is initialsied here as this is custom view's init function.
        line_paint_brush = new Paint(Paint.ANTI_ALIAS_FLAG);
        line_paint_brush.setColor(Color.BLACK);
        line_paint_brush.setStrokeWidth(5f);


        //iniitialising arrays
        if (set == null)
        {
            //make sure attr is there
            //make sur eno null pointer exceptionn
            return;
        }

        //display text attributes.
//        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.CustomView);

//        ta.recycle(); //so the garbage collector oesnt make a mess
    }

    //Main Draw function.
    //This displays everything and is called every time there is an invalidated() or postinvalidate() call
    // it requires the canvas, so it knows what is being drawn on.
    @Override
    protected void onDraw(Canvas canvas)
    {
        // for each array of shapes, if there is 1 or more, display them with the corresponding paint.
        // Each shape has an independent shape file so that it can have a different colour if required etc...
        if (num_squares > 0)
        {
            for (int i = 0; i < num_squares; i++) {
                canvas.drawRect(square_array[i].getSquare(), square_array[i].getSquarePaint());
            }
        }

        if (num_circles > 0) //if there are some circles, display them
        {
            for (int i = 0; i < num_circles; i++)
            {
                canvas.drawCircle(circle_array[i].getCenter_x(), circle_array[i].getCenter_y(), circle_array[i].getRadius(), circle_array[i].get_circ_paint());
            }
        }

        if (num_lines > 0)
        {
            for (int i = 0; i < num_lines; i++)
            {
                //lines are slightly different to the other shapes as they require start/end points.
                canvas.drawLine(lines[i].getX_pos_start(), lines[i].getY_pos_start(), lines[i].getX_pos_end(), lines[i].getY_pos_end(), line_paint_brush);
            }
        }

        //Triangles are done differently to squares and circles.
        //They are drawn side at a time using a path, which is made from 3 points.
        if (num_triangles > 0)
        {
            for (int i = 0; i < num_triangles; i++)
            {
                //reset them each individually - without this, it would leave a trail behind it of previous drawings.
                triangles[i].tri_draw_path.reset();

                //take the considered triangle's top point.
                point_a.x = triangles[i].getStart_x();
                point_a.y = triangles[i].getStart_y();

                int y_length = triangles[i].get_dist_y();

                //using the length and some embedded functionality of the triangle class, calcualate the other 2 points.
                point_b.y = point_a.y + y_length;
                point_b.x = point_a.x - (triangles[i].getLength() / 2);

                point_c.y = point_a.y + y_length;
                point_c.x = point_a.x + (triangles[i].getLength() / 2);

                //draw the path between the points on the triangle specific path.
                triangles[i].tri_draw_path.moveTo(point_a.x, point_a.y);
                triangles[i].tri_draw_path.lineTo(point_b.x, point_b.y);
                triangles[i].tri_draw_path.lineTo(point_c.x, point_c.y);
                triangles[i].tri_draw_path.lineTo(point_a.x, point_a.y);

                //each triangle has its own path. Draw the currently considered triangle's path.
                canvas.drawPath(triangles[i].tri_draw_path, triangles[i].getTrianglePaint());
            }
        }

    }

    //Creating a new line and adding it to the array.
    public void new_line(int x_start, int y_start, int x_finish, int y_finish)
    {
        lines[num_lines] = new line(x_start, y_start, x_finish, y_finish, "#000000");
        start_timer(5000, 2);
        num_lines++;
        postInvalidate();

    }

    //Function that takes a flag value and makes the shape the inverse
    //it has to considere every possibility because they are in different arrays.
    public void hollow_fill(int hollow_fill) //hollow = 1, fill = 0;
    {
        for(int i = 0; i < num_circles; i++)
        {
            if( (circle_array[i].getSelected_flag() == 1) && (hollow_fill == 1))
            {
                circle_array[i].make_hollow();
                break;
            }
            else if ( (circle_array[i].getSelected_flag() == 1) && (hollow_fill == 0))
            {
                circle_array[i].fill_shape();
                break;
            }

        }
        for (int i = 0; i < num_squares; i++)
        {
            if ( (square_array[i].getSelected_flag() == 1) && (hollow_fill == 1))
            {
                square_array[i].make_hollow();
                break;
            }
            else if ( (square_array[i].getSelected_flag() == 1) && (hollow_fill == 0))
            {
                square_array[i].fill_shape();
                break;
            }
        }

        for (int i = 0; i < num_triangles; i++)
        {
            if ( (triangles[i].get_selected() == 1) && (hollow_fill == 1))
            {
                triangles[i].make_hollow();
                break;
            }
            else if ( (triangles[i].get_selected() == 1) && (hollow_fill == 0))
            {
                triangles[i].fill_shape();
                break;
            }
        }
        postInvalidate();
    }

    //Array for increasing/decreasing the size of the selected shape.
    public void modify_size_selected(int big_small) //bigger = 0, smaller = 1
    {

        for(int i = 0; i < num_circles; i++)
        {
            if(circle_array[i].getSelected_flag() == 1)
            {
                if (big_small == 1)
                {
                    circle_array[i].setRadius( circle_array[i].getRadius() - 10 );
                }
                else if (big_small == 0)
                {
                    circle_array[i].setRadius( circle_array[i].getRadius() + 10 );
                }
                else
                {
                    Log.d("ERROR", "Error has occured in re-sizing. big_small isn't 1 or 0. Circle.");
                    //ERROR
                }
            }

        }
        for (int i = 0; i < num_squares; i++)
        {
            if(square_array[i].getSelected_flag() == 1)
            {
                if (big_small == 1)
                {
                    square_array[i].resize(square_array[i].getlen() - 10);
                }
                else if (big_small == 0)
                {
                    square_array[i].resize(square_array[i].getlen() + 10);
                }
                else
                {
                    Log.d("ERROR", "Error has occured in re-sizing. big_small isn't 1 or 0. Squares.");
                    //ERROR
                }
            }
        }

        for (int i = 0; i < num_triangles; i++)
        {
            if(triangles[i].get_selected() == 1)
            {
                Log.d("NOTE", "triangle is selected");
                if (big_small == 1)
                {
                    triangles[i].resize(triangles[i].getLength() - 10);
                }
                else if (big_small == 0)
                {
                    triangles[i].resize( triangles[i].getLength() + 10);
                }
                else
                {
                    Log.d("ERROR", "Error has occured in re-sizing. big_small isn't 1 or 0. Triangle.");
                    //error
                }
            }
        }
        postInvalidate();
    }


    //overridden onTouchEvent function - that acts when the screen receives an input of some kind.
    //the functionality of everything written at this time only requires the ACTION_MOVE consideration,
    //however, I have left an alternative case for further modifications (makes it easier to add a feature as an example).
    // Can be modified further if needed.
    @Override
    public boolean onTouchEvent(MotionEvent event) //this function allows movement of shapes
    {
        boolean value = super.onTouchEvent(event);

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN: //MAYBE ROOM FOR HAVING TAP TO CHANGE COLOUR?
            {
                //At the moment does nothing.
                // Switch Case implemented and left for easier implementation of further features.
            }

            case MotionEvent.ACTION_MOVE: //if touch and move
            {
                double x = event.getX();
                double y = event.getY(); //position of touch x or y

                if (!draw_line) {

                    shape_selected = 0;
                    square_check(x, y, 0); //square has priority as it was put first

                    if (shape_selected == 0)
                    {
                        circle_check(x, y, 0);    //if square hasn't been selected check circle
                        if (shape_selected == 0)
                        {
                            triangle_check(x, y, 0); //if circle hasn't been selected check triangle
                        }
                        else
                        {
                            triangle_check(x,y,1);
                        }

                    }

                    else
                        {
                        circle_check(x, y, 1); //because a square has been selected
                        triangle_check(x, y, 1);
                    }


                    postInvalidate();
                    return true;
                }
                else if (draw_line & old_x == 0) //if in draw_line mode, it will consider these 2 options.
                {
                    old_y = y; //remember the old tap location, so next time the difference can be found.
                    old_x = x;
                }
                else if (draw_line & (old_x != 0))
                {
                    //draw a line between the new and last points
                    new_line((int)old_x, (int)old_y, (int)x, (int)y);
                    postInvalidate();

                    old_x = 0;//reset the old points so it can start the process again.
                    old_y = 0;

                    return(true);
                }
            }
        }
     return value;
    }

    //Functions that each check if 2 given coordinates are inside any of the shapes.
    //The shape classes take into account the maths so the functions are comparators.
    //They update if a shape has been selected (so different operators can be used on that shape).
    private void square_check(double x, double y, int flag) //flag = 1 to clear all selected of this type.
    {
        for (int i = 0; i < num_squares; i++) //considering every rectangle drawn
        {
            //to move positions for the rectangle being considered

            if (square_array[i].is_inside_square(x, y) == 1) //returns 1 if its inside square area
            {
                square_array[i].update_position((int)x,(int)y);

                square_array[i].set_selected();
                shape_selected = 1;
            }
            else
            {
                square_array[i].clear_selected();
            }
        }
    }

    private void triangle_check(double x, double y, int flag) //flag = 1 to clear all selected of this type.
    {

        for (int i = 0; i< num_triangles; i++)
        {
            double dx = Math.pow(triangles[i].get_center_x() - x , 2);
            double dy = Math.pow(triangles[i].get_center_y() - y, 2);

            if ((dx + dy < Math.pow((triangles[i].get_dist_y()  ), 2)) && (flag == 0)) {
                //touch event bc inside the triangle (circle rad)

                triangles[i].set_center(x, y);
                triangles[i].set_selected();
                shape_selected = 1;
                triangles[i].set_selected();
            }
            else
            {
                triangles[i].clear_selected();
            }


        }
    }

    private void circle_check(double x, double y, int flag) //flag = 0 is run normally, flag =1 is clear all selected of this type.
    {
        for (int i = 0; i < num_circles; i++)
        {
            double dx = Math.pow(x - circle_array[i].getCenter_x(), 2);
            double dy = Math.pow(y - circle_array[i].getCenter_y(), 2);

            if ((dx + dy < Math.pow(circle_array[i].getRadius(), 2)) && (flag == 0)) {
                //touch event bc inside the circle
                circle_array[i].setCenter_x((int)x);
                circle_array[i].setCenter_y((int)y);
                circle_array[i].set_selected();
                shape_selected = 1;
            }
            else
            {
                circle_array[i].clear_selected();
            }
        }

    }
}
