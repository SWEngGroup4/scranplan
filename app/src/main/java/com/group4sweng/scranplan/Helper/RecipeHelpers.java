package com.group4sweng.scranplan.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeHelpers {

    /** Convert original HashMap <String, String> format from Firebase into a readable ArrayList for displaying to the User
     * @param ingredientHashMap - The ingredientList data from Firebase. A HashMap of two strings.
     * @return - The ArrayList equivalent to be displayed from the recipe info screen or Mealplanner screen.
     */
    public static ArrayList<String> convertToIngredientListFormat(HashMap<String, String> ingredientHashMap){
        ArrayList<String> ingredientArray = new ArrayList<>();

        for (Map.Entry<String, String> stringStringEntry : ingredientHashMap.entrySet()) {
            Map.Entry mapElement = stringStringEntry;
            String string = mapElement.getKey().toString() + ": " + mapElement.getValue().toString();
            ingredientArray.add(string);
        }
        return ingredientArray;
    }
}