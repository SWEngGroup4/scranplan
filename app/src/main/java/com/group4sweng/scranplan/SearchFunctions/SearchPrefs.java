package com.group4sweng.scranplan.SearchFunctions;

import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

/**
 * Building search preferences class.
 * Author(s): LNewman
 * (c) CoDev 2020
 *
 * This class holds user search preferences which are saved whenever the user opens the new search
 * options and selects new filters and method of sorting/searching
 * This is initialised to the user preferences with default searching parameters
 */
public class SearchPrefs {
    Boolean mpescatarianPref;
    Boolean mVegetarianPref;
    Boolean mVeganPref;
    Boolean mNutsPref;
    Boolean mMilkPref;
    Boolean mEggsPref;
    Boolean mWheatPref;
    Boolean mShellfishPref;
    Boolean mSoyPref;
    Boolean mScorePref;
    Boolean mVotePref;
    Boolean mTimePref;
    Boolean mIngredientsPref;
    Boolean mNamePref;
    Boolean mChefPref;

    // Building a new search preferences following user input
    public SearchPrefs(Boolean pes, Boolean vegi, Boolean vega, Boolean nut,
                       Boolean milk, Boolean egg, Boolean wheat, Boolean shell,
                       Boolean soy, Boolean score, Boolean vote, Boolean time,
                       Boolean ingred, Boolean name, Boolean chef){
        mpescatarianPref = pes;
        mVegetarianPref = vegi;
        mVeganPref = vega;
        mNutsPref = nut;
        mMilkPref = milk;
        mEggsPref = egg;
        mWheatPref = wheat;
        mShellfishPref = shell;
        mSoyPref = soy;
        mScorePref = score;
        mVotePref = vote;
        mTimePref = time;
        mIngredientsPref = ingred;
        mNamePref = name;
        mChefPref = chef;

    }

    // Default user preference search without user input
    public SearchPrefs(UserInfoPrivate user){
        mpescatarianPref = user.getPreferences().isPescatarian();
        mVegetarianPref = user.getPreferences().isVegetarian();
        mVeganPref = user.getPreferences().isVegan();
        mNutsPref = user.getPreferences().isAllergy_nuts();
        mMilkPref = user.getPreferences().isAllergy_milk();
        mEggsPref = user.getPreferences().isAllergy_eggs();
        mWheatPref = user.getPreferences().isAllergy_gluten();
        mShellfishPref = user.getPreferences().isAllergy_shellfish();
        mSoyPref = user.getPreferences().isAllergy_soya();
        mScorePref = true;
        mVotePref = false;
        mTimePref = false;
        mIngredientsPref = true;
        mNamePref = false;
        mChefPref = false;

    }
}
