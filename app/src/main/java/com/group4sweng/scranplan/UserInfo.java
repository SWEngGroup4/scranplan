package com.group4sweng.scranplan;

import java.io.Serializable;
import java.util.HashMap;

/**
 * UserInfo class
 * Used to save current user vital variables that will be used throughout the app for customisation
 */
public class UserInfo implements Serializable {

    // Basic user information
    private Boolean firstSetup = true;
    private String UID;
    private String email;
    private String displayName;
    private String imageURL;
    private double chefRating;
    private long numRecipes;

    // User preferences
    private Preferences preferences;

    // Constructor with all variables
    UserInfo(String UID, String email, String displayName, String imageURL,
             int chefRating, boolean allergy_celery,
             boolean allergy_crustacean, boolean allergy_eggs, boolean allergy_fish,
             boolean allergy_gluten, boolean allergy_milk, boolean allergy_mustard,
             boolean allergy_nuts, boolean allergy_peanuts, boolean allergy_sesame,
             boolean allergy_shellfish, boolean allergy_soya, boolean allergy_sulphide,
             boolean diabetic, boolean halal, boolean high_protein, boolean kosher,
             boolean lactose_free, boolean lactovegetarian, boolean low_carb,
             boolean low_sodium, boolean no_alcohol, boolean no_pork,
             boolean ovovegetarian, boolean pescatarian, boolean vegan,
             boolean vegetarian, int numRecipes){
        this.UID = UID;
        this.email = email;
        this.displayName = displayName;
        this.imageURL = imageURL;
        this.chefRating = chefRating;
        this.numRecipes = numRecipes;
        this.preferences = new Preferences(allergy_celery, allergy_crustacean, allergy_eggs, allergy_fish,
                allergy_gluten, allergy_milk, allergy_mustard, allergy_nuts, allergy_peanuts, allergy_sesame,
                allergy_shellfish, allergy_soya, allergy_sulphide, diabetic, halal, high_protein, kosher,
                lactose_free, lactovegetarian, low_carb, low_sodium, no_alcohol, no_pork,
                ovovegetarian, pescatarian, vegan, vegetarian);
    }

    // Constructor using maps
    UserInfo(HashMap<String, Object> map, HashMap<String, Object> prefs) {
        this.UID = (String) map.get("UID");
        this.email = (String) map.get("email");
        this.displayName = (String) map.get("displayName");
        this.imageURL = (String) map.get("imageURL");
        this.chefRating = (double) map.get("chefRating");
        this.numRecipes = (long) map.get("numRecipes");
        this.preferences = new Preferences((boolean) prefs.get("allergy_celery"),
                (boolean) prefs.get("allergy_crustacean"), (boolean) prefs.get("allergy_eggs"),
                (boolean) prefs.get("allergy_fish"), (boolean) prefs.get("allergy_gluten"),
                (boolean) prefs.get("allergy_milk"), (boolean) prefs.get("allergy_mustard"),
                (boolean) prefs.get("allergy_nuts"), (boolean) prefs.get("allergy_peanuts"),
                (boolean) prefs.get("allergy_sesame"), (boolean) prefs.get("allergy_shellfish"),
                (boolean) prefs.get("allergy_soya"), (boolean) prefs.get("allergy_sulphide"),
                (boolean) prefs.get("diabetic"), (boolean) prefs.get("halal"),
                (boolean) prefs.get("high_protein"), (boolean) prefs.get("kosher"),
                (boolean) prefs.get("lactose_free"), (boolean) prefs.get("lactovegetarian"),
                (boolean) prefs.get("low_carb"), (boolean) prefs.get("low_sodium"),
                (boolean) prefs.get("no_alcohol"), (boolean) prefs.get("no_pork"),
                (boolean) prefs.get("ovovegetarian"), (boolean) prefs.get("pescatarian"),
                (boolean) prefs.get("vegan"), (boolean) prefs.get("vegetarian"));
    }

    public void updatePreferences (HashMap<String, Object> prefs){
        this.preferences = new Preferences((boolean) prefs.get("allergy_celery"),
                (boolean) prefs.get("allergy_crustacean"), (boolean) prefs.get("allergy_eggs"),
                (boolean) prefs.get("allergy_fish"), (boolean) prefs.get("allergy_gluten"),
                (boolean) prefs.get("allergy_milk"), (boolean) prefs.get("allergy_mustard"),
                (boolean) prefs.get("allergy_nuts"), (boolean) prefs.get("allergy_peanuts"),
                (boolean) prefs.get("allergy_sesame"), (boolean) prefs.get("allergy_shellfish"),
                (boolean) prefs.get("allergy_soya"), (boolean) prefs.get("allergy_sulphide"),
                (boolean) prefs.get("diabetic"), (boolean) prefs.get("halal"),
                (boolean) prefs.get("high_protein"), (boolean) prefs.get("kosher"),
                (boolean) prefs.get("lactose_free"), (boolean) prefs.get("lactovegetarian"),
                (boolean) prefs.get("low_carb"), (boolean) prefs.get("low_sodium"),
                (boolean) prefs.get("no_alcohol"), (boolean) prefs.get("no_pork"),
                (boolean) prefs.get("ovovegetarian"), (boolean) prefs.get("pescatarian"),
                (boolean) prefs.get("vegan"), (boolean) prefs.get("vegetarian"));
        this.firstSetup = false;
    }



    // All getters and setters for all variables

    public Boolean getFirstSetup() {
        return firstSetup;
    }

    public void setFirstSetup(Boolean firstSetup) {
        this.firstSetup = firstSetup;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public double getChefRating() {
        return chefRating;
    }

    public void setChefRating(int chefRating) {
        this.chefRating = chefRating;
    }

    public long getNumRecipes() {
        return numRecipes;
    }

    public void setNumRecipes(int numRecipes) {
        this.numRecipes = numRecipes;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }



//    public HashMap<String, Object> getUserInfo() {
//        return userInfo;
//    }
//
//
//
//    public void setUserInfo(String UID, String email, String displayName, String imageURL,
//                            int chefRating, boolean allergy_celery,
//                            boolean allergy_crustacean, boolean allergy_eggs, boolean allergy_fish,
//                            boolean allergy_gluten, boolean allergy_milk, boolean allergy_mustard,
//                            boolean allergy_nuts, boolean allergy_peanuts, boolean allergy_sesame,
//                            boolean allergy_shellfish, boolean allergy_soya, boolean allergy_sulphide,
//                            boolean diabetic, boolean halal, boolean high_protein, boolean kosher,
//                            boolean lactose_free, boolean lactovegetarian, boolean low_carb,
//                            boolean low_sodium, boolean no_alcohol, boolean no_pork,
//                            boolean ovovegetarian, boolean pescatarian, boolean vegan,
//                            boolean vegetarian, int numRecipes) {
//        HashMap<String, Object> map = new HashMap<>();
//        HashMap<String, Object> preferences = new HashMap<>();
//
//        map.put("UID", UID);
//        map.put("email", email);
//        map.put("displayName", displayName);
//        map.put("imageURL", imageURL);
//        map.put("chefRating", chefRating);
//        map.put("numRecipes", numRecipes);
//
//        preferences.put("allergy_celery", allergy_celery);
//        preferences.put("allergy_crustacean", allergy_crustacean);
//        preferences.put("allergy_eggs", allergy_eggs);
//        preferences.put("allergy_fish", allergy_fish);
//        preferences.put("allergy_gluten", allergy_gluten);
//        preferences.put("allergy_milk", allergy_milk);
//        preferences.put("allergy_mustard", allergy_mustard);
//        preferences.put("allergy_nuts", allergy_nuts);
//        preferences.put("allergy_peanuts", allergy_peanuts);
//        preferences.put("allergy_sesame", allergy_sesame);
//        preferences.put("allergy_shellfish", allergy_shellfish);
//        preferences.put("allergy_soya", allergy_soya);
//        preferences.put("allergy_sulphide", allergy_sulphide);
//        preferences.put("diabetic", diabetic);
//        preferences.put("halal", halal);
//        preferences.put("high_protein", high_protein);
//        preferences.put("kosher", kosher);
//        preferences.put("lactose_free", lactose_free);
//        preferences.put("lactovegetarian", lactovegetarian);
//        preferences.put("low_carb", low_carb);
//        preferences.put("low_sodium", low_sodium);
//        preferences.put("no_alcohol", no_alcohol);
//        preferences.put("no_pork", no_pork);
//        preferences.put("ovovegetarian", ovovegetarian);
//        preferences.put("pescatarian", pescatarian);
//        preferences.put("vegan", vegan);
//        preferences.put("vegetarian", vegetarian);
//
//        map.put("preferences", preferences);
//
//        this.userInfo = map;
//    }
}