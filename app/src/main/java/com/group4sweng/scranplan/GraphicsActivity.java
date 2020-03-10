package com.group4sweng.scranplan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.group4sweng.scranplan.views.CustomView;

public class GraphicsActivity extends AppCompatActivity {

    private CustomView mCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCustomView = (CustomView) findViewById(R.id.customView);


        findViewById(R.id.btn_add_circle).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCustomView.new_circle();
                //mCustomView.start_timer(10000);
                //function to do something if pressed

            }
        });

        findViewById(R.id.btn_draw).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                mCustomView.update_line_stat();

            }
        });


        findViewById(R.id.btn_add_square).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //function to do something if pressed
                mCustomView.new_square();

            }
        });

        findViewById(R.id.btn_delete_circle).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCustomView.delete_circle();
                //function to do something if pressed

            }
        });

        findViewById(R.id.btn_delete_square).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCustomView.delete_square();
                //function to do something if pressed

            }
        });

        findViewById(R.id.btn_fill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mCustomView.hollow_fill(0);
            }
        });

        findViewById(R.id.btn_hollow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mCustomView.hollow_fill(1);
            }
        });

        findViewById(R.id.btn_bigger).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCustomView.modify_size_selected(0);
            }
        });

        findViewById(R.id.btn_smaller).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCustomView.modify_size_selected(1);
            }
        });

        findViewById(R.id.btn_add_triangle).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCustomView.new_triangle();
            }
        });

        findViewById(R.id.btn_delete_triangle).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCustomView.delete_triangle();
            }
        });



    }
}
