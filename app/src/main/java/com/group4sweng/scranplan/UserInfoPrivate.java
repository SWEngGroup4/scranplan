//package com.group4sweng.scranplan;
//
//import android.app.Application;
//
//import com.group4sweng.scranplan.UserInfo.Preferences;
//
//import java.io.Serializable;
//import java.util.HashMap;
//
///**
// * UserInfo class
// * Used to save current user vital variables that will be used throughout the app for customisation
// */
//public class UserInfoPrivate extends Application implements Serializable {
//
//    final static String TAG = "UserInfo";
//
//    //  User information
//    private String mUID;
//    private String mDisplayName;
//    private String mImageURL;
//    private String mAbout;
//    private double mChefRating;
//    private long mNumRecipes;
//
//    /*TODO
//        Add saved recipes here. Check UML profile diagram and Recipe diagram for more information.
//     */
//
//    // User preferences
//    private Preferences mPreferences;
//
//
//    public UserInfoPrivate(HashMap<String, Object> map, HashMap<String, Object> prefs){
//
//        this.mUID = (String) map.get("UID");
//        System.out.println("UID is" + this.mUID);
//
//        this.mDisplayName = (String) map.get("displayName");
//        this.mImageURL = (String) map.get("imageURL");
//        this.mChefRating = (double) map.get("chefRating");
//        this.mNumRecipes =  (long) map.get("numRecipes");
//        this.mAbout = (String) map.get("about");
//        this.mPreferences = new Preferences((boolean) prefs.get("allergy_celery"),
//                (boolean) prefs.get("allergy_crustacean"), (boolean) prefs.get("allergy_eggs"),
//                (boolean) prefs.get("allergy_fish"), (boolean) prefs.get("allergy_gluten"),
//                (boolean) prefs.get("allergy_milk"), (boolean) prefs.get("allergy_mustard"),
//                (boolean) prefs.get("allergy_nuts"), (boolean) prefs.get("allergy_peanuts"),
//                (boolean) prefs.get("allergy_sesame"), (boolean) prefs.get("allergy_shellfish"),
//                (boolean) prefs.get("allergy_soya"), (boolean) prefs.get("allergy_sulphide"),
//                (boolean) prefs.get("diabetic"), (boolean) prefs.get("halal"),
//                (boolean) prefs.get("high_protein"), (boolean) prefs.get("kosher"),
//                (boolean) prefs.get("lactose_free"), (boolean) prefs.get("lactovegetarian"),
//                (boolean) prefs.get("low_carb"), (boolean) prefs.get("low_sodium"),
//                (boolean) prefs.get("no_alcohol"), (boolean) prefs.get("no_pork"),
//                (boolean) prefs.get("ovovegetarian"), (boolean) prefs.get("pescatarian"),
//                (boolean) prefs.get("vegan"), (boolean) prefs.get("vegetarian"));
//
//    }
//
//    /*TODO
//        Instead of implementing shared preferences for user profile. Use instead for Saved Recipes.
//     */
//    /*
//    private void loadSharedPreferences(){
//        SharedPreferences userInfo = getApplicationContext().getSharedPreferences(USER_INFO_SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        UID = userInfo.getString(UID_KEY, null);
//        displayName = userInfo.getString(DISPLAY_NAME_KEY, null);
//        imageURL = userInfo.getString(IMAGEURL_KEY, null);
//        about = userInfo.getString(ABOUT_KEY, null);
//        numRecipes = userInfo.getInt(NUM_OF_RECIPES, -1);
//        chefRating = userInfo.getInt(CHEF_RATING_KEY, -1);
//        preferences.setAllergy_nuts(userInfo.getBoolean("ALLERGY_NUTS_KEY", false));
//        preferences.setAllergy_shellfish(userInfo.getBoolean("ALLERGY_SHELLFISH_KEY", false));
//        preferences.setAllergy_milk(userInfo.getBoolean("ALLERGY_MILK_KEY", false));
//        preferences.setAllergy_soya(userInfo.getBoolean("ALLERGY_SOYA_KEY", false));
//        preferences.setAllergy_eggs(userInfo.getBoolean("ALLERGY_EGGS_KEY", false));
//    }
//     */
//
//    /*
//    private void saveSharedPreferences(){
//        SharedPreferences userInfo = getApplicationContext().getSharedPreferences(USER_INFO_SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = userInfo.edit();
//        editor.putString(UID_KEY, getUID());
//        editor.putString(DISPLAY_NAME_KEY, getDisplayName());
//        editor.putString(IMAGEURL_KEY, getImageURL());
//        editor.putString(ABOUT_KEY, getAbout());
//        editor.putInt(NUM_OF_RECIPES, (int) getNumRecipes());
//        editor.putInt(CHEF_RATING_KEY, (int) getChefRating());
//        editor.putBoolean("ALLERGY_NUTS_KEY", getPreferences().isAllergy_nuts());
//        editor.putBoolean("ALLERGY_SHELLFISH_KEY", getPreferences().isAllergy_shellfish());
//        editor.putBoolean("ALLERGY_MILK", getPreferences().isAllergy_milk());
//        editor.putBoolean("ALLERGY_SOYA", getPreferences().isAllergy_soya());
//        editor.putBoolean("ALLERGY_EGGS", getPreferences().isAllergy_eggs());
//        editor.apply();
//    }
//    */
//
//
//    public String getUID() {
//        return mUID;
//    }
//
//    public void setUID(String UID )  {
//        mUID = UID;
//    }
//
//    public String getDisplayName() {
//        return mDisplayName;
//    }
//
//    public void setDisplayName(String displayName ){
//        mDisplayName = displayName;
//    }
//
//    public String getImageURL() {
//        return mImageURL;
//    }
//
//    public void setImageURL(String imageURL)  {
//        mImageURL = imageURL;
//    }
//
//    public double getChefRating() {
//        return mChefRating;
//    }
//
//    public void setChefRating(double chefRating)  {
//        mChefRating = chefRating;
//    }
//
//    public long getNumRecipes() {
//        return mNumRecipes;
//    }
//
//    public void setNumRecipes(int numRecipes)  {
//        mNumRecipes = numRecipes;
//    }
//
//    public String getAbout() {
//        return mAbout;
//    }
//
//    public void setAbout(String about)  {
//        mAbout = about;
//    }
//
//    public Preferences getPreferences() {
//        return mPreferences;
//    }
//
//    public void setPreferences(Preferences preferences)  {
//        mPreferences = preferences;
//    }
//
//}