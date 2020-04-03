package com.group4sweng.scranplan.RecipeCreation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.group4sweng.scranplan.R;

import java.util.HashMap;

public class RecipeSteps extends Fragment {

    private String xmlURL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_recipe_steps, container, false);

        buildXML();

        return  view;
    }

    private void buildXML() {
        Bundle bundle = new Bundle();
        bundle.putString("xmlURL", xmlURL);

        ((RecipeCreation) requireActivity()).stepComplete(2, bundle);
    }

}
