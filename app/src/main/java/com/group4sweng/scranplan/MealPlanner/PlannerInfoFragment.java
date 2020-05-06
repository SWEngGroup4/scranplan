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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class PlannerInfoFragment extends RecipeInfoFragment{

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
        this.ratingMap =  (HashMap<String, Double>) map.get("ratingMap");
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

        initPortionsListeners();
    }


    /**
     * Method that displays the extra information on the recipe information fragment for the users who have a subscription
     */
    @Override
    public void displayInfo(View layout) {
        super.displayInfo(layout);

        mLayoutForPlanner.setVisibility(View.VISIBLE);
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
        //mReheatInformationButton.setVisibility(View.VISIBLE);
    }


    /* ----BEGIN PORTIONS SECTION----
     * Author: JButler
     * Date: 1/5/20
     * (c) CoDev 2020    */

    /** Update the ingredients list for the Mealplanner.
     *  Ingredient list functionality extended for the Mealplanner by:
     *      - Adding warnings if a portion conversion
     *      - Adding icons for a limited set of ingredients.  */
    @Override
    protected void updateIngredientsList(){
        //  Grab the Firebase ingredient HashMap and convert to the 'Ingredient' class format.
        ArrayList<Ingredient> ingredientList = RecipeHelpers.convertToIngredientFormat(ingredientHashMap);
        linearLayoutIngredients.removeAllViews(); //   Clear ingredient list in-case portion amounts change.

        // Cycle through an ingredient list, inflate and add the corresponding layout.
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for(Ingredient ingredient : ingredientList){
            View ingredientView = inflater.inflate(R.layout.ingredient, linearLayoutIngredients, false);

            TextView name = ingredientView.findViewById(R.id.ingredient_name);
            name.setText(ingredient.getName());
            TextView portion = ingredientView.findViewById(R.id.ingredient_portion);
            portion.setText(ingredient.getPortion());

            String warningMessage = Portions.generateWarning(ingredient.getName(), ingredient.getPortion());

            //  If a warning is present (!= null) & we haven't opened the recipe screen for the first time continue to add a warning message.
            if(warningMessage != null && currentServings != -1){
                ImageView warningIcon = ingredientView.findViewById(R.id.ingredient_warning_icon);
                TextView warning = ingredientView.findViewById(R.id.ingredient_warning);

                warning.setVisibility(View.VISIBLE);
                warningIcon.setVisibility(View.VISIBLE);
                warning.setText(warningMessage);
            }

            //  If an ingredient icon is present (!= 1) display it alongside the ingredient name.
            if(ingredient.getIcon() != -1){
                ImageView ingredientIcon = ingredientView.findViewById(R.id.ingredient_icon);

                ingredientIcon.setVisibility(View.VISIBLE);
                ingredientIcon.setImageResource(ingredient.getIcon());
            }

            linearLayoutIngredients.addView(ingredientView); // Add this new generated row to the overall LinearLayout list.
        }
    }

    /* Original serving amount for the recipe. Used to reference to make sure portions can't be
       converted to any value past the portions MAX_SERVINGS_MULTIPLIER amount. */
    private float originalServings;

    private float currentServings = -1; // Initial value = -1 to represent no changes to Portion values.
    private LinearLayout alertLayout;
    private AlertDialog.Builder builder;

    /** Create the layout for the Portions Dialog box **/
    private void buildPortionsDialog() {
        builder = new AlertDialog.Builder(getActivity());

        //  Create a new linear layout which fits the proportions of the screen and descends vertically.
        alertLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        alertLayout.setOrientation(LinearLayout.VERTICAL);
        alertLayout.setLayoutParams(params);

        //  Build the dialog.
        builder.setView(alertLayout)
                .setTitle("Change Portion Amounts")
                .setMessage("Portion amounts are estimated \nand cooking instructions will not update with new portions amounts.")
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialog, which) -> { //Allow the user to cancel the operation.
                    dialog.cancel();
                });
    }

    /** Initiate listeners for a press of the 'Change Portions' button **/
    private void initPortionsListeners() {
        //  Grab the original serving amount for the Recipe.
        originalServings = Float.parseFloat(servingAmount);

        mChangePortions.setOnClickListener(v -> {
            //  Create the portions dialog.
            buildPortionsDialog();
            currentServings = Float.parseFloat(servingAmount);

            //  Get an integer array of values for all possible portion serving amounts.
            ArrayList<Integer> servingAmounts = Portions.getValidServesAmounts(currentServings, originalServings);
            Button[] servesButtons = new Button[servingAmounts.size()];

            //  Generate buttons for each portion serving amount.
            for(int i=0; i < servingAmounts.size(); i++){
                servesButtons[i] = new Button(getContext());
                servesButtons[i].setText(String.format(Locale.ENGLISH, "%d", servingAmounts.get(i)));
                servesButtons[i].setPadding(40,40,40,40);
                servesButtons[i].setGravity(Gravity.CENTER);
                alertLayout.addView(servesButtons[i]);
            }

            //  Create and show the dialog.
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            //  Serving amount counter.
            int sAmountCounter = 0;
            for(Button button : servesButtons) {
                final int sAmountCounterFinal = sAmountCounter;

                button.setOnClickListener(portion -> { // Set on click listeners for all buttons displayed.
                    final float newServings = servingAmounts.get(sAmountCounterFinal);
                    try {
                        //  Convert original portions to new portions amount.
                        final HashMap<String, String> newIngredientsHashMap = Portions.convertPortions(ingredientHashMap, currentServings, newServings);
                        ingredientHashMap = newIngredientsHashMap;

                        updateIngredientsList();

                        DecimalFormat df = new DecimalFormat();
                        df.setMaximumFractionDigits(0);
                        String servesDisplay = "Serves: " + df.format(newServings);
                        mServing.setText(servesDisplay);
                        servingAmount = Float.toString(newServings);

                        alertDialog.cancel(); // Close dialog on selection.
                    } catch (PortionConvertException e) { // Exception thrown when portion conversion error for Portions.convertPortions fails.
                        e.printStackTrace();
                    }
                });
                sAmountCounter++;
            }
        });
    }

}