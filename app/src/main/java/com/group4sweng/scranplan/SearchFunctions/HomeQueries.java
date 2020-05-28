package com.group4sweng.scranplan.SearchFunctions;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;

/**
 * Class for the home page horizontal scroll queries.
 * Author(s): LNewman
 * (c) CoDev 2020
 *
 * This builds up the queries, taken from user preferences and feeds them into the infinite
 * horizontal scroll views on the home page
 */
public class HomeQueries {

    // Returns the hashmap of all the home screen queries
    public HashMap getQueries() {
        return queries;
    }

    public void setQueries(HashMap queries) {
        this.queries = queries;
    }

    HashMap queries;
    public static final int NUMBER_OF_RECIPES = 10;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    CollectionReference ref = database.collection("recipes");


    // Constructor to build all queries in the home page
    public HomeQueries(UserInfoPrivate user){
        queries = new HashMap();
        queries.put("score", buildQuery(user).orderBy("rating.overallRating", Query.Direction.DESCENDING));
        queries.put("votes", buildQuery(user).orderBy("rating.totalRates", Query.Direction.DESCENDING));
        queries.put("timestamp", buildQuery(user).orderBy("timestamp", Query.Direction.DESCENDING));
        queries.put("favourite", ref.whereArrayContains("favourite", user.getUID()));
        queries.put("breakfastScore",buildQuery(user).whereEqualTo("breakfast", true).orderBy("score", Query.Direction.DESCENDING));
        queries.put("lunchScore",buildQuery(user).whereEqualTo("lunch", true).orderBy("score", Query.Direction.DESCENDING));
        queries.put("dinnerScore",buildQuery(user).whereEqualTo("dinner", true).orderBy("score", Query.Direction.DESCENDING));
        queries.put("breakfastVotes", buildQuery(user).whereEqualTo("breakfast", true).orderBy("votes", Query.Direction.DESCENDING));
        queries.put("lunchVotes", buildQuery(user).whereEqualTo("lunch", true).orderBy("votes", Query.Direction.DESCENDING));
        queries.put("dinnerVotes", buildQuery(user).whereEqualTo("dinner", true).orderBy("votes", Query.Direction.DESCENDING));
        queries.put("breakfastTimestamp", buildQuery(user).whereEqualTo("breakfast", true).orderBy("timestamp", Query.Direction.DESCENDING));
        queries.put("lunchTimestamp", buildQuery(user).whereEqualTo("lunch", true).orderBy("timestamp", Query.Direction.DESCENDING));
        queries.put("dinnerTimestamp", buildQuery(user).whereEqualTo("dinner", true).orderBy("timestamp", Query.Direction.DESCENDING));
        queries.put("breakfastFavourite", ref.whereEqualTo("breakfast", true).whereArrayContains("favourite", user.getUID().hashCode()));
        queries.put("lunchFavourite", ref.whereEqualTo("lunch", true).whereArrayContains("favourite", user.getUID().hashCode()));
        queries.put("dinnerFavourite", ref.whereEqualTo("dinner", true).whereArrayContains("favourite", user.getUID().hashCode()));
        if(!user.getPreferences().isVegan()){
            queries.put("topVegan", buildQuery(user).whereEqualTo("vegan", true).orderBy("score", Query.Direction.DESCENDING));
            if(!user.getPreferences().isVegetarian()){
                queries.put("topVegetarian", buildQuery(user).whereEqualTo("vegetarian", true).orderBy("score", Query.Direction.DESCENDING));
                if(!user.getPreferences().isPescatarian()){
                    queries.put("topPescatarian", buildQuery(user).whereEqualTo("pescatarian", true).orderBy("score", Query.Direction.DESCENDING));
                }
            }
        }
    }

    // Function to build all queries to be saved in the constructor, taking user preferences and
    // adding them to the query
    private Query buildQuery(UserInfoPrivate user){
        Query query = ref;
        if(user.getPreferences().isPescatarian()){
            query = query.whereEqualTo("pescatarian", true);
        }else if(user.getPreferences().isVegetarian()){
            query = query.whereEqualTo("vegetarian", true);
        }else if(user.getPreferences().isVegan()){
            query = query.whereEqualTo("vegan", true);
        }

        if(user.getPreferences().isAllergy_nuts()){
            query = query.whereEqualTo("noNuts", true);
        }

        if(user.getPreferences().isAllergy_milk()){
            query = query.whereEqualTo("noMilk", true);
        }

        if(user.getPreferences().isAllergy_eggs()){
            query = query.whereEqualTo("noEggs", true);
        }

        if(user.getPreferences().isAllergy_gluten()){
            query = query.whereEqualTo("noWheat", true);
        }

        if(user.getPreferences().isAllergy_shellfish()){
            query = query.whereEqualTo("noShellfish", true);
        }

        if(user.getPreferences().isAllergy_soya()){
            query = query.whereEqualTo("noSoy", true);
        }
        return query.limit(NUMBER_OF_RECIPES);
    }
}
