package com.group4sweng.scranplan.SearchFunctions;

import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

/**
 * This class holds user search preferences which are saved whenever the user opens the new search
 * options and selects new filters and method of sorting/searching
 * This is initialised to the user preferences with default searching parameters
 */
public class SearchTime {
    Boolean mBreakfast;
    Boolean mLunch;
    Boolean mDinner;

    // Building a new search preferences following user input
    public SearchTime( Boolean breakfast, Boolean lunch, Boolean dinner){
        mBreakfast = breakfast;
        mLunch = lunch;
        mDinner = dinner;

    }

    // Default user preference search without user input
    public SearchTime(UserInfoPrivate user){
        mBreakfast = user.getPreferences().isBreakfast();
        mLunch = user.getPreferences().isLunch();
        mDinner = user.getPreferences().isDinner();

    }


}