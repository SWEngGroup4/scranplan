package com.group4sweng.scranplan.MealPlanner.Ingredients;

//  Warnings for portion conversion calculations.
public interface Warning {
    String NONE = null; // No issue converting portions.
    String FAILED = "Failed to calculate portions.";
    String ESTIMATE = "Portions estimates are different for Alcohol and Herbs";
}
