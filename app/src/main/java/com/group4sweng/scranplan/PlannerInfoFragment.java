package com.group4sweng.scranplan;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class PlannerInfoFragment extends RecipeInfoFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View layout = inflater.inflate(R.layout.fragment_recipe_info, null);
        HashMap<String, Object> map = (HashMap<String, Object>) getArguments().getSerializable("hashmap");

        this.recipeID = (String) map.get("recipeID");
        this.recipeName = (String) map.get("recipeTitle");
        this.recipeImage = (String) map.get("imageURL");
        this.recipeDescription = (String) map.get("recipeDescription");
        this.chefName = (String) map.get("chefName");
        this.ingredientArray = (ArrayList<String>) map.get("ingredientList");
        this.recipeRating = (String) map.get("rating");
        this.xmlPresentation = (String) map.get("xmlURL");
        this.planner = (Boolean) map.get("planner");

        builder.setView(layout);
        displayInfo(layout);
        initPageListeners(layout);
        tabFragments(layout);

        return layout;
    }
}
