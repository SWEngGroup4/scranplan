package com.group4sweng.scranplan;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import java.io.Serializable;
import java.util.HashMap;

public class UserInfo implements Serializable {

    private HashMap<String, Object> userInfo = new HashMap<String, Object>();

/*
    To use anywhere in project:
    // set
    ((UserInfo) this.getApplication()).setUserInfo(<all data required>);
    or
    ((UserInfo) this.getApplication()).setUserInfoMap(<map data type>);

    // get
    HashMap<String, Object> user = ((UserInfo) this.getApplication()).getUserInfo();
*/


    public HashMap<String, Object> getUserInfo() {
        return userInfo;
    }

    public void setUserInfoMap(HashMap<String, Object> map){
        this.userInfo = map;
    }


    public void setUserInfo(String UID, String email, String displayName, String imageURL,
                            int chefRating, boolean allergy_celery,
                            boolean allergy_crustacean, boolean allergy_eggs, boolean allergy_fish,
                            boolean allergy_gluten, boolean allergy_milk, boolean allergy_mustard,
                            boolean allergy_nuts, boolean allergy_peanuts, boolean allergy_sesame,
                            boolean allergy_shellfish, boolean allergy_soya, boolean allergy_sulphide,
                            boolean diabetic, boolean halal, boolean high_protein, boolean kosher,
                            boolean lactose_free, boolean lactovegetarian, boolean low_carb,
                            boolean low_sodium, boolean no_alcohol, boolean no_pork,
                            boolean ovovegetarian, boolean pescatarian, boolean vegan,
                            boolean vegetarian, int numRecipes) {
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> preferences = new HashMap<>();

        map.put("UID", UID);
        map.put("email", email);
        map.put("displayName", displayName);
        map.put("imageURL", imageURL);
        map.put("chefRating", chefRating);
        map.put("numRecipes", numRecipes);

        preferences.put("allergy_celery", allergy_celery);
        preferences.put("allergy_crustacean", allergy_crustacean);
        preferences.put("allergy_eggs", allergy_eggs);
        preferences.put("allergy_fish", allergy_fish);
        preferences.put("allergy_gluten", allergy_gluten);
        preferences.put("allergy_milk", allergy_milk);
        preferences.put("allergy_mustard", allergy_mustard);
        preferences.put("allergy_nuts", allergy_nuts);
        preferences.put("allergy_peanuts", allergy_peanuts);
        preferences.put("allergy_sesame", allergy_sesame);
        preferences.put("allergy_shellfish", allergy_shellfish);
        preferences.put("allergy_soya", allergy_soya);
        preferences.put("allergy_sulphide", allergy_sulphide);
        preferences.put("diabetic", diabetic);
        preferences.put("halal", halal);
        preferences.put("high_protein", high_protein);
        preferences.put("kosher", kosher);
        preferences.put("lactose_free", lactose_free);
        preferences.put("lactovegetarian", lactovegetarian);
        preferences.put("low_carb", low_carb);
        preferences.put("low_sodium", low_sodium);
        preferences.put("no_alcohol", no_alcohol);
        preferences.put("no_pork", no_pork);
        preferences.put("ovovegetarian", ovovegetarian);
        preferences.put("pescatarian", pescatarian);
        preferences.put("vegan", vegan);
        preferences.put("vegetarian", vegetarian);

        map.put("preferences", preferences);

        this.userInfo = map;
    }
}
