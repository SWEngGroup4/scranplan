package com.group4sweng.scranplan;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class RecipeFragment extends Fragment {

    public RecipeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        LinearLayout verticalLayout = view.findViewById(R.id.linearLayout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        int numberOfCategories = 10;
        int numberOfRecipes = 10;

        for (int i=0; i < numberOfCategories; i++) {
            TextView mTextView = new TextView(view.getContext());
            mTextView.setTextSize(20);
            mTextView.setText("test");
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(view.getContext());
            horizontalScrollView.setLayoutParams(new ViewGroup.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, height/5));
            LinearLayout horizontalLayout = new LinearLayout(view.getContext());
            horizontalScrollView.addView(horizontalLayout);
            for (int j =0; j < numberOfRecipes; j++) {
                ImageView imageView = new ImageView(view.getContext());
                imageView.setId(j);
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(10,10,10,10);
                imageView.setImageResource(R.drawable.scran);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                horizontalLayout.addView(imageView);
            }
            verticalLayout.addView(mTextView);
            verticalLayout.addView(horizontalScrollView);
        }
        return view;
    }
}