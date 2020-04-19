package com.group4sweng.scranplan.MealPlanner;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import com.group4sweng.scranplan.Exceptions.PortionConvertException;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class PlannerInfoFragment extends RecipeInfoFragment{

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
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
    @SuppressWarnings({"unchecked"})
    private void initPlannerItems(View layout, HashMap <String,Object> map){

        this.recipeID = (String) map.get("recipeID");
        this.recipeName = (String) map.get("recipeTitle");
        this.recipeImage = (String) map.get("imageURL");
        this.recipeDescription = (String) map.get("recipeDescription");
        this.chefName = (String) map.get("chefName");
        this.ingredientArray = (ArrayList<String>) map.get("ingredientList");
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
        this.ingredientHashMap = (HashMap<String, String>) map.get("ingredientListHashMap");

        /*
        for (Map.Entry<String, String> stringStringEntry : ingredientHashMap.entrySet()) {
            String string = ((Map.Entry) stringStringEntry).getKey().toString() + ": " + ((Map.Entry) stringStringEntry).getValue().toString();
            System.out.println("HASHMAP CONTENTS: " +  string);
        }*/
    }

    /*
     *Creates an Alert Dialog to show reheating information to allow the user to see how the meal can be reheated
     */
    protected void initPageListeners(View layout) {
        super.initPageListeners(layout);


        mReheatInformationButton.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(reheat)
                    .setTitle("Reheating Information")
                    .setIcon(R.drawable.reheat);

            AlertDialog dialog = builder.create();

            dialog.show();

        });

        mChangePortions.setOnClickListener(v -> {
           AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
           //  Create a new linear layout which fits the proportions of the screen and descends vertically.

            LinearLayout alertLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            alertLayout.setOrientation(LinearLayout.VERTICAL);
            alertLayout.setLayoutParams(params);

            ArrayList<Integer> servingAmounts = Portions.getValidServesAmounts(Float.parseFloat(servingAmount));
            Button[] servesButtons = new Button[servingAmounts.size()];

            int sAmountCounter = 0;
            for(Button button : servesButtons){
                button = new Button(getContext());
                button.setText(String.format(Locale.ENGLISH, "%d", servingAmounts.get(sAmountCounter)));
                button.setPadding(40,40,40,40);
                button.setGravity(Gravity.CENTER);

                int finalSAmountCounter = sAmountCounter;
                button.setOnClickListener(v12 -> {
                    try {
                        HashMap<String, String> newIngredientsList = Portions.convertPortions(this.ingredientHashMap, Float.parseFloat(servingAmount), servingAmounts.get(finalSAmountCounter));
                    } catch (PortionConvertException e) {
                        e.printStackTrace();
                    }

                    //Getting ingredients array and assigning it to the linear layout view
                    arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ingredientList);
                    arrayAdapter.clear();
                    arrayAdapter.addAll(ingredientArray);

                    final int adapterCount = arrayAdapter.getCount();

                    for (int i = 0; i < adapterCount; i++) {
                        View item = arrayAdapter.getView(i, null, null);
                        listViewIngredients.addView(item);
                    }
                });

                alertLayout.addView(button);
                sAmountCounter++;
            }

            builder.setView(alertLayout)
                    .setTitle("Change Portion Amounts")
                    .setMessage("Portion amounts are estimated")
                    .setCancelable(true)
                    .setNegativeButton("Cancel", (dialog, which) -> { //Allow the user to cancel the operation.
                        dialog.cancel();
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

    }


    /**
     * Method that displays the extra information on the recipe information fragment for the users who have a subscription
     */
    @Override
    public void displayInfo(View layout) {
        super.displayInfo(layout);

        //  Sets the 'Change Portions' button to visible.
        mChangePortions.setVisibility(View.VISIBLE);

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
        mReheatInformationButton.setVisibility(View.VISIBLE);
    }

}