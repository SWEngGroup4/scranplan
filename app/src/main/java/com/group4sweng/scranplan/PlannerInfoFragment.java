package com.group4sweng.scranplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class PlannerInfoFragment extends RecipeInfoFragment {

    private TextView mFridge;
    private TextView mFreezer;
    private TextView mReheatInformation;
    private ImageButton mReheatInformationButton;

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
        this.servingAmount = (String) map.get("peopleServes");
        this.fridgeTime =  (String) map.get("fridgeDays");
        this.canFreeze = (Boolean) map.get("canFreeze");
        this.reheat = (String) map.get("reheat");


        builder.setView(layout);
        displayInfo(layout);
        allergyDisplay(layout);
        initPageListeners(layout);
        reheatInformation(layout);
        tabFragments(layout);

        return layout;
    }

    @Override
    public void displayInfo(View layout) {
        super.displayInfo(layout);

        mFridge = layout.findViewById(R.id.fridge);
        mFridge.setText("Keep in Fridge: " + fridgeTime + " days");

        mFreezer = layout.findViewById(R.id.freezer);
        if (canFreeze == true){
            mFreezer = layout.findViewById(R.id.freezer);
            mFreezer.setText("Can be frozen");
        }
        else{
            mFreezer.setText("Cannot be frozen");
        }

        mReheatInformation = layout.findViewById(R.id.reheatInfoText);
        mReheatInformation.setText("Reheat Information");

        mReheatInformationButton = layout.findViewById(R.id.reheatInfoButton);
        mReheatInformationButton.setVisibility(View.VISIBLE);
    }

    /*
    *Creates an Alert Dialog to show reheating information to allow the user to see how the meal can be reheated
     */
    protected void reheatInformation(View layout) {

        mReheatInformationButton = layout.findViewById(R.id.reheatInfoButton);
        mReheatInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage(reheat)
                        .setTitle("Reheating Information")
                        .setIcon(R.drawable.reheat);

                AlertDialog dialog = builder.create();

                dialog.show();

            }
        });

    }

}
