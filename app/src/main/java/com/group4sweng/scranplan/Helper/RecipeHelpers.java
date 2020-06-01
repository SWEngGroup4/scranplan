package com.group4sweng.scranplan.Helper;

import com.group4sweng.scranplan.MealPlanner.Ingredients.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Helper functions for the Recipe Info & MealPlanner screens.
 *  Author: JButler
 *  (c) CoDev 2020 **/
public class RecipeHelpers {

    /** Convert a Firebase ingredient HashMap entry to the Ingredient format displayed within a Recipe & Mealplanners ingredient list.
     * @param ingredientHashMap - Input Ingredient HashMap, format: (Ingredient, Portion)
     * @return - Converted ingredient list.
     */
    public static ArrayList<Ingredient> convertToIngredientFormat(HashMap<String, String> ingredientHashMap){
        ArrayList<Ingredient> ingredientList = new ArrayList<>();
        String ingredient; String portion;

        //  Cycle through the ingredient HashMap.
        for (Map.Entry<String, String> listElement : ingredientHashMap.entrySet()) {
            ingredient = listElement.getKey();
            portion = listElement.getValue();

            ingredientList.add(new Ingredient(ingredient, portion)); // Create a new ingredient with an ingredient & portion name.
        }
        return ingredientList;
    }
    public static ArrayList<String> convertToIngredientList(HashMap<String, String> ingredientHashMap){
        ArrayList<String> ingredientArray = new ArrayList<>();

        for (Map.Entry<String, String> stringStringEntry : ingredientHashMap.entrySet()) {
            Map.Entry mapElement = stringStringEntry;
            String string = mapElement.getKey().toString() + ": " + mapElement.getValue().toString();
            ingredientArray.add(string);
        }
        return ingredientArray;
    }
}
