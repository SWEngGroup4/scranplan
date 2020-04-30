package com.group4sweng.scranplan.MealPlanner;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;

public class PlannerInfoFragment extends RecipeInfoFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View layout = inflater.inflate(R.layout.fragment_recipe_info, null);
        HashMap<String, Object> map = (HashMap<String, Object>) getArguments().getSerializable("hashmap");


        initPlannerItems(layout, map);
        initPageItems(layout);
        builder.setView(layout);
        displayInfo(layout);
        allergyDisplay(layout);
        initPageListeners(layout);
        tabFragments(layout);
        addFavourite(layout);

        return layout;
    }

    /**
     * Method that takes all the data out of the map needed for the information screen and assigns it to
     * the relevant variables
     */
    private void initPlannerItems(View layout, HashMap <String,Object> map){

        this.recipeID = (String) map.get("recipeID");
        this.recipeName = (String) map.get("recipeTitle");
        this.recipeImage = (String) map.get("imageURL");
        this.recipeDescription = (String) map.get("recipeDescription");
        this.chefName = (String) map.get("chefName");
        this.ingredientArray = (ArrayList<String>) map.get("ingredientList");
        this.ratingMap =  (HashMap<String, Double>) map.get("ratingMap");
        this.recipeRating = (String) map.get("rating");
        this.xmlPresentation = (String) map.get("xmlURL");
        this.planner = (Boolean) map.get("planner");
        this.favouriteRecipe = (ArrayList<String>) map.get("favourite");
        this.mUser = (UserInfoPrivate) requireActivity().getIntent().getSerializableExtra("user");
        this.isFavourite = (Boolean) map.get("isFav");
        this.servingAmount = (String) map.get("peopleServes");
        this.fridgeTime =  (String) map.get("fridgeDays");
        this.canFreeze = (Boolean) map.get("canFreeze");
        this.reheat = (String) map.get("reheat");
        this.noEggs = (Boolean) map.get("noEggs");
        this.noMilk = (Boolean) map.get("noMilk");
        this.noNuts = (Boolean) map.get("noNuts");
        this.noShellfish = (Boolean) map.get("noShellfish");
        this.noSoy = (Boolean) map.get("noSoy");
        this.noWheat = (Boolean) map.get("noWheat");
        this.mPescatarian = (Boolean) map.get("pescatarian");
        this.mVegan = (Boolean) map.get("vegan");
        this.mVegetarian = (Boolean) map.get("vegetarian");

    }

    /*
     *Creates an Alert Dialog to show reheating information to allow the user to see how the meal can be reheated
     */
    protected void initPageListeners(View layout) {
        super.initPageListeners(layout);


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


    /**
     * Method that displays the extra information on the recipe information fragment for the users who have a subscription
     */
    @Override
    public void displayInfo(View layout) {
        super.displayInfo(layout);

        mLayoutForPlanner.setVisibility(View.VISIBLE);

        //Adds refrigerator information
        mFridge.setText("Keep in Fridge: " + fridgeTime + " days");

        //If canFreeze boolean is set to true then the screen will display that the meal can be frozen
        mFreezer = layout.findViewById(R.id.freezer);
        if (canFreeze){
            mFreezer = layout.findViewById(R.id.freezer);
            mFreezer.setText("Can be frozen");
        }
        else{
            mFreezer.setText("Cannot be frozen");
        }


        //Sets reheat text
        mReheatInformation.setText("Reheat Information");

        //Sets the reheat information button to visible for the paying user
        //mReheatInformationButton.setVisibility(View.VISIBLE);
    }

}