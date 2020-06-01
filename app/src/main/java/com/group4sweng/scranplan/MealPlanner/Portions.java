package com.group4sweng.scranplan.MealPlanner;

import android.util.Log;

import com.group4sweng.scranplan.Exceptions.PortionConvertException;
import com.group4sweng.scranplan.MealPlanner.Ingredients.Warning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Class for handling portion calculations when switching between different serving amounts for a recipe.
 *    Author(s): JButler
 *    (c) CoDev 2020
 **/
class Portions implements Warning {
    private final static String TAG = "portions";
    private final static int[] servesAmounts = {1,2,4,6,10,20}; // All possible variants of 'serves' amounts for a Recipe.
    private final static float MAX_SERVINGS_MULTIPLIER = 4f; // Limit to how much we can scale a recipes portions before it is considered in-accurate.

    //  Common Alcohol text matchers. If an ingredient is matched with the following in it's text then the amounts of this ingredient are scaled differently.
    private final static String[] commonAlcohol = {"pinot grigio","pinot gris","sauvignon blanc", "chardonnay", "sherry", "madeira", "vermouth", "vinsanto",
    "riesling", "cabernet sauvignon", "pinot noir", "syrah", "zinfandel", "pilsener", "witbier", "pale ale", "bitter ale", "brown ale", "cask ale",
    "mild ale", "old ale", "stock ale", "fruit beer", "scotch ale", "cider", "mead", "red wine", "white wine", "rose wine", "vodka", "whiskey", "white rum", "dark rum",
    "spiced rum", };

    //  Common Spice text matchers.
    private final static String[] commonSpices  = {"allspice", "anise", "cardamom", "cayenne", "five spice", "coriander", "cumin", "curry powder", "fennel", "garam masala",
            "nutmeg", "paprika", "turmeric", "black pepper",  "caraway", "cinnamon", "jalapeno", "mustard seeds", "nutmeg", "saffron", "vanilla essence", "wasabi"};

    //  Common Spice text matchers. Ingredient matching must be exact. IE "Ginger Beer" will not work. "Ginger" will.
    private final static String[] commonSpicesExact = {"ginger", "salt", "cinnamon", "vanilla", "mace", "thyme"};


    /** Find the quantity of an ingredient from a string descriptor
     *  Removes any characters that are non numerical.
     * @param portionDisplayed - Portion input String. (e.g. 100g)
     * @return - Numerical value of portion. Returns -1 if a value cannot be found or the
     *      String input is invalid.
     */
    public static float retrieveQuantity(String portionDisplayed){
        boolean quantityFound = false; // Has a numerical value been found.
        boolean decimalFound = false; // Has a decimal been found.
        float quantity; // final quantity.
        String quantityString = "";

        //  Regex patterns and matchers for numerical values & decimal points.
        Pattern quantityPattern = Pattern.compile("[0-9.]+");
        Matcher portionMatcher = quantityPattern.matcher(portionDisplayed);

        //  Continue if a pattern is matched.
        while(portionMatcher.find()){
            String match = portionMatcher.group(); // Group matched patterns.

            if(match.equals(".") && decimalFound){ // Shouldn't find multiple '.' characters.
                Log.e(TAG, "Multiple decimal points, '.' characters detected. in quantity");
            } else {
                if(match.equals(".")){ // Check if a decimal point is present in the input.
                    decimalFound = true;
                } else {
                    quantityFound = true;
                }

                quantityString = quantityString.concat(match); // Append string to the end of the existing quantity string.
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
            } else if (quantity < 0) { // Is the quantity negative. Take Absolute value if so.
                return Math.abs(quantity);
            }
        }

        return -1; // No value found. Return -1.
    }

    /** Find the measurement quantity from a string descriptor
     *  Removes all numerical characters.
     * @param portionDisplayed - Portion input String. (e.g. 100g)
     * @return - Quantity descriptor string (e.g. Liters). Returns 'null' if the input is blank.
     */
    static String retrieveMeasurement(String portionDisplayed) {
        float quantity = retrieveQuantity(portionDisplayed);

        if(portionDisplayed.equals("")){
            return null;
        } else if (quantity == -1){ // Checks if no quantity was present in the first place.
            return portionDisplayed;
        }

        return portionDisplayed.replaceAll("[0-9.]+", "");
    }

    /** Convert full Ingredients list HashMap input into a new equivalent HashMap with scaled proportions.
     * @param ingredients - Ingredients List.
     * @param prevServes - Previous serving amount stored
     * @param newServes - New serving amount (portions) to be calculated.
     * @return - Scaled ingredients list.
     * @throws PortionConvertException - Exception thrown when portions cannot be converted.
     */
    static HashMap<String , String> convertPortions(HashMap<String, String> ingredients, float prevServes, float newServes) throws PortionConvertException {
        float currentMultiplierScalar; // Adjusts overallMultiplier based on if an ingredient is a spice and(or) alcohol.
        float overallMultiplier = newServes/prevServes; // Scalar multiplier for scaling recipe portions.
        boolean increasePortions; // Should the serving amount (portions) increase or decrease.

        if(overallMultiplier > 1 && overallMultiplier <= MAX_SERVINGS_MULTIPLIER){ // Check if increasing proportions. Must be within limits of max servings amount multiplier.
            increasePortions = true;
        } else if (overallMultiplier < 1 && overallMultiplier >= 1/MAX_SERVINGS_MULTIPLIER) { // Check if decreasing proportions.
            overallMultiplier = 1/overallMultiplier; // Invert multiplier. Used so calculations can be done with an additional scalar multiplier quantity.
            increasePortions = false;
        } else if (overallMultiplier == 1) { // prev serving amount = new serving amount. Throw Exception.
            throw new PortionConvertException("Unable to convert portions of food. New and previous serve amounts cannot be the same");
        } else { // Outside acceptable estimate.
            throw new PortionConvertException("Unable to convert portions of food from " + prevServes + " to " + newServes + ". Amount is outside the range in which an acceptable estimate can be calculated.");
        }
        HashMap<String, String> scaledIngredients = new HashMap<>();

        // Cycle through the HashMap.
        for (Map.Entry<String, String> entry : ingredients.entrySet()) {

            String value = (String) ((Map.Entry) entry).getKey();
            String portion = (String) ((Map.Entry) entry).getValue();

            //  Check from alcohol + spice list the multiplier doesn't need to be modified.
            currentMultiplierScalar = checkMultiplier(value);

            //  Grab quantity amounts in terms of an Integer and Float
            String quantityFloat = Float.toString(retrieveQuantity(portion));
            String quantityInt = Integer.toString((int) retrieveQuantity(portion));

            //  Calculate the new portions quantity amount.
            float newQuantity;
            if (increasePortions) { // Increase the amount in portions.
                newQuantity = retrieveQuantity(portion) * (1 + ((overallMultiplier - 1) * currentMultiplierScalar));
            } else {
                newQuantity = retrieveQuantity(portion) / (1 + ((overallMultiplier - 1) * currentMultiplierScalar));
            }

            //  Create decimal format options for integers and float values. Makes it so ingredient portions are displayed better.
            DecimalFormat dfInteger = new DecimalFormat();   DecimalFormat dfFloat = new DecimalFormat();
            dfInteger.setMaximumFractionDigits(0);           dfFloat.setMaximumFractionDigits(2);

            String scaledPortion;
            String newQuantityString; // Check if the new quantity can be considered an integer value.
            if(newQuantity == (int) newQuantity){
                newQuantityString = dfInteger.format(newQuantity);
            } else { // New quantity is a decimal value.
                newQuantityString = dfFloat.format(newQuantity);
            }

            if (Math.ceil(newQuantity) == newQuantity && !portion.contains(".")) { // Check if the new calculated quantity is an integer. If so ignore decimal value.
                scaledPortion = portion.replaceAll(quantityInt, newQuantityString);
            } else if (!portion.contains(".")) { // Check if previous quantity was an integer but the new quantity is a decimal.
                scaledPortion = portion.replaceAll(quantityInt, newQuantityString);
            } else { // Prev quantity was a decimal, new quantity is decimal.
                scaledPortion = portion.replaceAll(quantityFloat, newQuantityString);
            }
            scaledIngredients.put(value, scaledPortion); // Generate final ingredients HashMap List.
        }

            return scaledIngredients;
    }

    /** Check if the multiplier for scaling ingredient proportions should be modified.
     *  Checks if the ingredient name matches either a known 'alcohol' or 'spice/herb'.
     * @param ingredient - Ingredient name.
     * @return - Adjusted multiplier.
     */
    static float checkMultiplier(String ingredient){
        for(String spice : commonSpices){ // Cycle through list of known values.
            if (ingredient.toLowerCase().contains(spice)){ // Check if ingredient matches.
                return 0.5f; // Adjusted multiplier modifier return.
            }
        }

        for(String alcohol : commonAlcohol){
            if(ingredient.toLowerCase().contains(alcohol)){
                return 0.5f;
            }
        }

        //  All exact spice matching is a singular word. Remove all spaces just incase.
        String ingredientSpacesRemoved = ingredient.replaceAll(" ", "");
        for(String spice : commonSpicesExact){
            //  Check the ingredient is exactly equal to spice name, not just if it contains the name.
            if (ingredientSpacesRemoved.toLowerCase().equals(spice) ){
                return 0.5f;
            }
        }
        return 1.0f; // Default multiplier.
    }

    /** Check for all valid serving amounts that can be used for existing serving amount.
     * @param currentServes - Current serving amount.
     * @param originalServes - Original serving amount.
     * @return - A list of all possible servings amounts within an acceptable range when scaling proportions.
     */
    static ArrayList<Integer> getValidServesAmounts(float currentServes, float originalServes){
        // Max and minimum serving values that can be used.
        int maxValue = (int) Math.floor(currentServes * MAX_SERVINGS_MULTIPLIER);
        int minValue = (int) Math.floor(currentServes * 1/MAX_SERVINGS_MULTIPLIER);

        //  Max and min serving values for the original recipe.
        float maxValueOriginal = (int) Math.floor(originalServes * MAX_SERVINGS_MULTIPLIER);
        float minValueOriginal = (int) Math.floor(originalServes * 1/MAX_SERVINGS_MULTIPLIER);

        ArrayList<Integer> amounts = new ArrayList<>();
        //int[] amounts = new int[servesAmounts.length];

        int amountsCounter = 0;
        for (int servesAmount : servesAmounts) {
            System.out.println("Serves amounts: " + servesAmount);
            //  Check if new serving amount is within max/min values and isn't equal to previous serving amount.
            if (servesAmount <= maxValue && servesAmount >= minValue && servesAmount != currentServes) {
                //  Also check against the original recipe value for servings.
                if(servesAmount <= maxValueOriginal && servesAmount >= minValueOriginal){
                    amounts.add(servesAmount);
                    amountsCounter++;
                }
            }
        }

        return amounts;
    }

    // TODO - Use to convert measurements. Could use this later maybe.
    static String convertPortions(String measurementType, float value){
        return null;
    }

    static String generateWarning(String ingredient, String portion) {
        if (retrieveQuantity(portion) == -1f) {
            return Warning.FAILED;
        } else if (checkMultiplier(ingredient) == 0.5) {
            return Warning.ESTIMATE;
        } else {
            return Warning.NONE;
        }
    }
}

