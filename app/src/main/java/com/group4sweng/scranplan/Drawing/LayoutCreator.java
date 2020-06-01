package com.group4sweng.scranplan.Drawing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.group4sweng.scranplan.R;

import static com.group4sweng.scranplan.Drawing.LayoutCreator.DeviceDisplay.HEIGHT;
import static com.group4sweng.scranplan.Drawing.LayoutCreator.DeviceDisplay.WIDTH;

public class LayoutCreator extends AppCompatActivity {

    //  enumerations to define if we should change the devices width or height.
    enum DeviceDisplay {
        WIDTH,
        HEIGHT
    }

    private GraphicsView mGraphicsView;
    private DisplayMetrics mDisplayMetrics;

    private Integer width;
    private Integer height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphics);

        mDisplayMetrics = new DisplayMetrics();
        width = getGraphicsSize(WIDTH);
        height = getGraphicsSize(HEIGHT);

        GraphicsView mGraphicsView = new GraphicsView(this, height, width, true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.gravity = Gravity.CENTER;
        mGraphicsView.setLayoutParams(layoutParams);
        mGraphicsView.setBackgroundColor(Color.WHITE);

        LinearLayout linearLayout = findViewById(R.id.graphicsLayout);
        linearLayout.addView(mGraphicsView);

        findViewById(R.id.graphicsOval).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mGraphicsView.addOval(new Rectangle(width / 2, height / 2, width / 4, height / 4), 0, 0);
            }
        });

        findViewById(R.id.graphicsRect).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //function to do something if pressed
                mGraphicsView.addRectangle(new Rectangle(width / 2, height / 2, width / 4, height / 4), 0, 0);

            }
        });

        findViewById(R.id.graphicsTri).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGraphicsView.addTriangle(new Triangle(width / 2, height / 2, 200f),0 , 0);
            }
        });

        findViewById(R.id.graphicsDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGraphicsView.deleteSelected();
            }
        });

        Button colourButton = findViewById(R.id.graphicsCol);
        colourButton.setOnClickListener(view -> ColorPickerDialogBuilder
                .with(view.getContext())
                .setTitle("Choose color")
                .initialColor(Color.WHITE)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> Toast.makeText(getApplicationContext(),
                        "onColorSelected: 0x" + Integer.toHexString(selectedColor),
                        Toast.LENGTH_SHORT).show())
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                    colourButton.setBackgroundColor(selectedColor);
                    mGraphicsView.setColour(selectedColor); })
                .setNegativeButton("cancel", (dialog, which) -> {})
                .build()
                .show()
        );

        findViewById(R.id.graphicsSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent graphics = new Intent();
                graphics.putExtra("shapes", mGraphicsView.getShapes());
                graphics.putExtra("triangles", mGraphicsView.getTriangles());
                setResult(Activity.RESULT_OK, graphics);
                finish();
            }
        });
    }

    private int getGraphicsSize(DeviceDisplay displayParam) {
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        switch (displayParam) {
            case WIDTH:
                return Math.round(mDisplayMetrics.widthPixels * 0.8f); //Floating point represents the display percentage the presentation should take up.
            case HEIGHT:
                //Shrink the 'Card' presentation display size based upon the height of the device.
                if (mDisplayMetrics.heightPixels < 1750) {
                    return Math.round(mDisplayMetrics.heightPixels * 0.7f);
                } else {
                    return Math.round(mDisplayMetrics.heightPixels * 0.75f);
                }
        }
        return -1;
    }
}