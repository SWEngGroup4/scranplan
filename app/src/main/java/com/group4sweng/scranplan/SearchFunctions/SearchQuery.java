package com.group4sweng.scranplan.SearchFunctions;

import com.algolia.search.saas.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Builds up a search query from the preferences selected by the user.
 */
public class SearchQuery {

    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    CollectionReference ref = database.collection("recipes");

    public Query getQuery() {
        return query;
    }
    public String getIndex(){ return index; }

    private Query query;
    private String index;


    // Constructor building the query from these parameters
    public SearchQuery(String sentSearch, SearchPrefs preference){
        // All variables that the user is able to manipulate
        Boolean mPescatarianBox;
        Boolean mVegetarianBox;
        Boolean mVeganBox;
        Boolean mNutsBox;
        Boolean mMilkBox;
        Boolean mEggsBox;
        Boolean mWheatBox;
        Boolean mShellfishBox;
        Boolean mSoyBox;
        Boolean mScoreBox;
        Boolean mVoteBox;
        Boolean mTimeBox;
        Boolean mIngredientsBox;
        Boolean mNameBox;
        Boolean mChefBox;
        String mSearch;

        mPescatarianBox = preference.mpescatarianPref;
        mVegetarianBox = preference.mVegetarianPref;
        mVeganBox = preference.mVeganPref;
        mNutsBox = preference.mNutsPref;
        mMilkBox = preference.mMilkPref;
        mEggsBox = preference.mEggsPref;
        mWheatBox = preference.mWheatPref;
        mShellfishBox = preference.mShellfishPref;
        mSoyBox = preference.mSoyPref;
        mScoreBox = preference.mScorePref;
        mVoteBox = preference.mVotePref;
        mTimeBox = preference.mTimePref;
        mIngredientsBox = preference.mIngredientsPref;
        mNameBox = preference.mNamePref;
        mChefBox = preference.mChefPref;
        mSearch = sentSearch;

        query = new Query(mSearch);

        // User diet preferences
        if(mPescatarianBox){
            query = query.setFilters("pescatarian:true");
        }else if(mVegetarianBox){
            query = query.setFilters("vegetarian:true");
        }else if(mVeganBox){
            query = query.setFilters("vegan:true");
        }

        if(mNutsBox){
            query = query.setFilters("noNuts:true");
        }

        if(mMilkBox){
            query = query.setFilters("noMilk:true");
        }

        if(mEggsBox){
            query = query.setFilters("noEggs:true");
        }

        if(mWheatBox){
            query = query.setFilters("noWheat:true");
        }

        if(mShellfishBox){
            query = query.setFilters("noShellfish:true");
        }

        if(mSoyBox){
            query = query.setFilters("noSoy:true");
        }

        // Only allowing the user to search for a single item
        if(mIngredientsBox){
            query = query.setRestrictSearchableAttributes("listIngredients");
        }else if(mNameBox){
            query = query.setRestrictSearchableAttributes("Name");
        }else if(mChefBox){
            query = query.setRestrictSearchableAttributes("Chef");
        }

        // Only enabling a single search order
        if(mScoreBox){
            index = "recipe_score";
        }else if(mVoteBox){
            index = "recipe_votes";
        }else if(mTimeBox){
            index = "recipe_time";
        }else{
            // create a default case
            index = "SCRANPLAN_RECIPES";
        }








    }
}

