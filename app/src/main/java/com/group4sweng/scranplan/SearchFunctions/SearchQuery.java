package com.group4sweng.scranplan.SearchFunctions;

import com.algolia.search.saas.Query;

import java.util.ArrayList;

/**
 * Building search query class.
 * Author(s): LNewman, NBillis
 * (c) CoDev 2020
 *
 * Builds up a search query from the preferences selected by the user.
 */
public class SearchQuery {

    public Query getQuery() {
        if(!index.equals("SCRANPLAN_USERS") && !searchFacets.isEmpty()){
            query = query.setFilters(getFilters(searchFacets));
        }
        return query;
    }
    public String getIndex(){ return index; }

    private Query query;
    private String index;
    private ArrayList<String> searchFacets;

    private String getFilters(ArrayList<String> searchFacets){
        StringBuilder filters = new StringBuilder();
        for (String filter : searchFacets) {
            filters.append(filter);
            filters.append("AND");
        }
        // remove final "AND"
        filters.delete(filters.length() - 3, filters.length());
        return filters.toString();
    }


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
        searchFacets = new ArrayList<>();


        query = new Query(mSearch);

        // User diet preferences
        if(mPescatarianBox){
            searchFacets.add("pescatarian:true");
        }else if(mVegetarianBox){
            searchFacets.add("vegetarian:true");
        }else if(mVeganBox){
            searchFacets.add("vegan:true");
        }

        if(mNutsBox){
            searchFacets.add("noNuts:true");
        }

        if(mMilkBox){
            searchFacets.add("noMilk:true");
        }

        if(mEggsBox){
            searchFacets.add("noEggs:true");
        }

        if(mWheatBox){
            searchFacets.add("noWheat:true");
        }

        if(mShellfishBox){
            searchFacets.add("noShellfish:true");
        }

        if(mSoyBox){
            searchFacets.add("noSoy:true");
        }

        // Only enabling a single search order
        if(mScoreBox){
            index = "recipe_score";
        }else if(mVoteBox){
            index = "recipe_votes";
        }else if(mTimeBox){
            index = "recipe_time";
        }else if(mChefBox){
            index = "SCRANPLAN_USERS";
        }else{
            // create a default case
        }

        // Only allowing the user to search for a single item
        if(mIngredientsBox){
            query = query.setRestrictSearchableAttributes("listIngredients");
        }else if(mNameBox){
            query = query.setRestrictSearchableAttributes("Name");
        }else if(mChefBox){
            query = new Query(mSearch);
            index = "SCRANPLAN_USERS";
        }
    }
}

