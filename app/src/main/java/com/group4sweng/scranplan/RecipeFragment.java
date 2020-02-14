package com.group4sweng.scranplan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class RecipeFragment extends Fragment {

    final String TAG = "Recipe Home";

    Button mRecipeButton;

    public RecipeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "Starting recipe Info activity");

        View view =  inflater.inflate(R.layout.fragment_recipe, container, false);
        mRecipeButton = view.findViewById(R.id.RecipeButton);
        initPageListeners();

        return view;

    }

    //Once recipe is clicked on a new activity is created that hold the recipe information
    private void initPageListeners(){
        mRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), RecipeInfo.class);
                startActivity(in);

            }
        });
    }



}
