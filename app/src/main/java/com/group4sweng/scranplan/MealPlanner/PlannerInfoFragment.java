package com.group4sweng.scranplan.MealPlanner;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.group4sweng.scranplan.Exceptions.PortionConvertException;
import com.group4sweng.scranplan.Helper.RecipeHelpers;
import com.group4sweng.scranplan.MealPlanner.Ingredients.Ingredient;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class PlannerInfoFragment extends RecipeInfoFragment{

    //  Fragment layout view. Used when refreshing the layout.
    private View layout;
    private float originalServings;
    private float currentServings = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        layout = inflater.inflate(R.layout.fragment_recipe_info, null);
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

    @Override
    public void onResume() {
        super.onResume();

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
        this.ingredientHashMap = (HashMap<String, String>) map.get("ingredientHashMap");
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

        mReheatInformationButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(reheat)
                    .setTitle("Reheating Information")
                    .setIcon(R.drawable.reheat);

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        initPortionsListeners();
    }

    private void initPortionsListeners() {
        originalServings = Float.parseFloat(servingAmount);

        mChangePortions.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            //  Create a new linear layout which fits the proportions of the screen and descends vertically.
            LinearLayout alertLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            alertLayout.setOrientation(LinearLayout.VERTICAL);
            alertLayout.setLayoutParams(params);

            currentServings = Float.parseFloat(servingAmount);
            ArrayList<Integer> servingAmounts = Portions.getValidServesAmounts(currentServings, originalServings);
            Button[] servesButtons = new Button[servingAmounts.size()];

            for(int i=0; i < servingAmounts.size(); i++){
                servesButtons[i] = new Button(getContext());
                servesButtons[i].setText(String.format(Locale.ENGLISH, "%d", servingAmounts.get(i)));
                servesButtons[i].setPadding(40,40,40,40);
                servesButtons[i].setGravity(Gravity.CENTER);

                alertLayout.addView(servesButtons[i]);
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

            int sAmountCounter = 0;
            for(Button button : servesButtons) {
                final int sAmountCounterFinal = sAmountCounter;

                button.setOnClickListener(portion -> {
                    final float newServings = servingAmounts.get(sAmountCounterFinal);
                    try {
                        final HashMap<String, String> newIngredientsHashMap = Portions.convertPortions(ingredientHashMap, currentServings, newServings);

                        final ArrayList<String> newIngredientsArray = RecipeHelpers.convertToIngredientListFormat(newIngredientsHashMap);
                        RecipeHelpers.displayIngredients(newIngredientsArray);

                        ingredientHashMap = newIngredientsHashMap;
                        //ingredientArray = newIngredientsArray;

                        updateIngredientsList();

                        mServing.setText("Serves: " + Float.toString(newServings));
                        servingAmount = Float.toString(newServings);

                        alertDialog.cancel();
                    } catch (PortionConvertException e) {
                        e.printStackTrace();
                    }
                });
                sAmountCounter++;

//            alertDialog.setOnDismissListener(portionAlert -> {
//                updateIngredientsList();
//            });
            }
        });
    }

//    private void resetPortions(){
//        displayInfo(layout);
//    }

    @Override
    protected void updateIngredientsList(){
        ArrayList<Ingredient> ingredientList = RecipeHelpers.convertToIngredientFormat(ingredientHashMap);
        linearLayoutIngredients.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for(Ingredient ingredient : ingredientList){
            View ingredientView = inflater.inflate(R.layout.ingredient, linearLayoutIngredients, false);

            TextView name = ingredientView.findViewById(R.id.ingredient_name);
            name.setText(ingredient.getName());

            TextView portion = ingredientView.findViewById(R.id.ingredient_portion);
            portion.setText(ingredient.getPortion());

            String warningMessage = Portions.generateWarning(ingredient.getName(), ingredient.getPortion());
            if(warningMessage != null && currentServings != -1){
                ImageView warningIcon = ingredientView.findViewById(R.id.ingredient_warning_icon);
                TextView warning = ingredientView.findViewById(R.id.ingredient_warning);

                warning.setVisibility(View.VISIBLE);
                warningIcon.setVisibility(View.VISIBLE);
                warning.setText(warningMessage);
            }

            if(ingredient.getIcon() != -1){
                ImageView ingredientIcon = ingredientView.findViewById(R.id.ingredient_icon);

                ingredientIcon.setVisibility(View.VISIBLE);
                ingredientIcon.setImageResource(ingredient.getIcon());
            }

            linearLayoutIngredients.addView(ingredientView);
        }
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