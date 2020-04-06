package com.group4sweng.scranplan.SearchFunctions;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;

public class BreakfastQueries {

    public HashMap getQueries() {
        return queries;
    }

    public void setQueries(HashMap queries) {
        this.queries = queries;
    }

    HashMap queries;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    CollectionReference ref = database.collection("recipes");


    // Constructor to build all queries in the home page
    public BreakfastQueries(UserInfoPrivate user){
        queries = new HashMap();
        queries.put("breakfast", ref.whereEqualTo("breakfast", true));
        queries.put("favourite", ref.whereArrayContains("favourite", user.getUID()));
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

        return query;
    }
}
