package com.group4sweng.scranplan.SearchFunctions;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;

/**
 * Builds up a search query from the preferences selected by the user.
 */
public class SearchQuery {

    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    CollectionReference ref = database.collection("recipes");

    public Query getQuery() {
        return query;
    }
    com.google.firebase.firestore.Query query;

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


    // Constructor building the query from these parameters
    public SearchQuery(String sentSearch, SearchPrefs preference){
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

        query = ref;

        // User diet preferences
        if(mPescatarianBox){
            query = query.whereEqualTo("pescatarian", true);
        }else if(mVegetarianBox){
            query = query.whereEqualTo("vegetarian", true);
        }else if(mVeganBox){
            query = query.whereEqualTo("vegan", true);
        }

        if(mNutsBox){
            query = query.whereEqualTo("noNuts", true);
        }

        if(mMilkBox){
            query = query.whereEqualTo("noMilk", true);
        }

        if(mEggsBox){
            query = query.whereEqualTo("noEggs", true);
        }

        if(mWheatBox){
            query = query.whereEqualTo("noWheat", true);
        }

        if(mShellfishBox){
            query = query.whereEqualTo("noShellfish", true);
        }

        if(mSoyBox){
            query = query.whereEqualTo("noSoy", true);
        }

        // Only allowing the user to search for a single item
        if(mIngredientsBox){
            query = query.whereArrayContains("listIngredients", mSearch.toLowerCase());
        }else if(mNameBox){
            query = query.whereEqualTo("name", mSearch.toLowerCase());
        }else if(mChefBox){
            query = query.whereEqualTo("chef", mSearch.toLowerCase());
        }

        // Only enabling a single search order
        if(mScoreBox){
            query = query.orderBy("score", Direction.DESCENDING);
        }else if(mVoteBox){
            query = query.orderBy("votes", Direction.DESCENDING);
        }else if(mTimeBox){
            query = query.orderBy("timestamp", Direction.DESCENDING);
        }

        // Limiting the search to sets of 5 items until the user scrolls to bottom
        query = query.limit(5);






    }
}
