package com.group4sweng.scranplan.RecipeInfo;

import android.util.Log;

import com.group4sweng.scranplan.Exceptions.PortionConvertException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Portions {

    private final static String[] commonAlcohol = {"pinot grigio","pinot gris","sauvignon blanc", "chardonnay", "sherry", "madeira", "vermouth", "vinsanto",
    "riesling", "cabernet sauvignon", "pinot noir", "syrah", "zinfandel", "pilsener", "witbier", "pale ale", "bitter ale", "brown ale", "cask ale",
    "mild ale", "old ale", "stock ale", "fruit beer", "scotch ale", "cider", "mead", "red wine", "white wine", "rose wine", "vodka", "whiskey", "white rum", "dark rum",
    "spiced rum", };

    private final static String[] commonSpices  = {"allspice", "anise", "cardamom", "cayenne", "five spice", "coriander", "cumin", "curry powder", "fennel", "garam masala",
            "nutmeg", "paprika", "turmeric", "black pepper",  "caraway", "cinnamon", "jalapeno", "mustard seeds", "nutmeg", "saffron", "vanilla essence", "wasabi"};

    private final static String[] commonSpicesExact = {"ginger", "salt", "cinnamon", "vanilla", "mace"};

    private final static float MAX_SERVINGS_MULTIPLIER = 4f;

    private final static String TAG = "portions";
    // TODO - Force quanties to be integers. With the exception of

    static float retrieveQuantity(String portionDisplayed){
        boolean quantityFound = false;
        boolean decimalFound = false;
        float quantity;
        String quantityString = "";

        Pattern quantityPattern = Pattern.compile("[0-9.]+");
        Matcher portionMatcher = quantityPattern.matcher(portionDisplayed);

        while(portionMatcher.find()){
            String match = portionMatcher.group();

            if(match.equals(".") && decimalFound){
                Log.e(TAG, "Multiple fullstops detected. in quantity");
            } else {
                if(match.equals(".")){
                    decimalFound = true;
                } else {
                    quantityFound = true;
                }

                quantityString = quantityString.concat(match);
            }
        }

        if(quantityFound) {
            try { //  Catch an invalid input and instead of throwing an exception return that a quantity cannot be found.
                quantity = Float.parseFloat(quantityString);
            } catch (NumberFormatException nfe){
                return -1;
            }

            if (quantity > 0) {
                return quantity;
            } else if (quantity < 0) {
                return Math.abs(quantity);
            }
        }

        return -1;
    }

    static String retrieveMeasurement(String portionDisplayed) throws IllegalArgumentException{
        float quantity = retrieveQuantity(portionDisplayed);

        if(portionDisplayed.equals("")){
            return null;
        } else if (quantity == -1){
            return portionDisplayed;
        }

        return portionDisplayed.replaceAll("[0-9.]+", "");
    }

    static HashMap<String , String> convertPortions(HashMap<String, String> ingredients, float prevServes, float newServes) throws PortionConvertException {
        float currentMultiplierScalar;
        float overallMultiplier = newServes/prevServes;
        boolean increasePortions;

        if(overallMultiplier > 1 && overallMultiplier < MAX_SERVINGS_MULTIPLIER){
            overallMultiplier = overallMultiplier - 1;
            increasePortions = true;
        } else if (overallMultiplier < 1 && overallMultiplier > 1/MAX_SERVINGS_MULTIPLIER) {
            increasePortions = false;
        } else if (overallMultiplier == 1) {
            throw new PortionConvertException("Unable to convert portions of food. New and previous serve amounts cannot be the same");
        } else {
            throw new PortionConvertException("Unable to convert portions of food from " + prevServes + " to " + newServes + ". Amount is outside the range in which an acceptable estimate can be calculated.");
        }

        System.out.println("Overall Multiplier = " + overallMultiplier);
        HashMap<String, String> scaledIngredients = new HashMap<>();

            //  Construct an iterator for the HashMap.
            Iterator portionsIterator = ingredients.entrySet().iterator();

            while (portionsIterator.hasNext()) {
                Map.Entry ingredient = (Map.Entry) portionsIterator.next(); // Increment value.

                String value = (String) ingredient.getKey();
                String portion = (String) ingredient.getValue();

                currentMultiplierScalar = checkMultiplier(value);
                String quantityFloat = Float.toString(retrieveQuantity(portion));
                String quantityInt = Integer.toString((int) retrieveQuantity(portion));

                float newQuantity;

                if(increasePortions){
                    newQuantity = retrieveQuantity(portion) + (retrieveQuantity(portion) * currentMultiplierScalar * overallMultiplier);
                } else {
                    newQuantity = retrieveQuantity(portion) - (retrieveQuantity(portion) * (currentMultiplierScalar * (1-overallMultiplier)));
                }

                String newQuantityString = Float.toString(newQuantity);
                String scaledPortion;

                if(Math.ceil(newQuantity) == newQuantity && !portion.contains(".")) {
                    scaledPortion = portion.replaceAll(quantityInt, newQuantityString);
                } else if (!portion.contains(".")){
                    scaledPortion = portion.replaceAll(quantityInt, newQuantityString);
                } else {
                    scaledPortion = portion.replaceAll(quantityFloat, newQuantityString);
                }
                scaledIngredients.put(value, scaledPortion);
            }

            return scaledIngredients;
    }

    static float checkMultiplier(String ingredient){
        for(String spice : commonSpices){
            if (ingredient.toLowerCase().contains(spice)){
                return 0.5f;
            }
        }

        for(String alcohol : commonAlcohol){
            if(ingredient.toLowerCase().contains(alcohol)){
                return 0.5f;
            }
        }

        String ingredientSpacesRemoved = ingredient.replaceAll(" ", "");
        for(String spice : commonSpicesExact){
            if (ingredientSpacesRemoved.toLowerCase().equals(spice) ){
                return 0.5f;
            }
        }
        return 1.0f;
    }

    // TODO - Use to convert measurements. Could use this later maybe.
    static String convertPortions(String measurementType, float value){
        return null;
    }
}
