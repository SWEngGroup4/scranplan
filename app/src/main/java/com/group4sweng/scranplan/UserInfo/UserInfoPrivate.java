package com.group4sweng.scranplan.UserInfo;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * UserInfoPrivate class
 * Used to save the current users attributes locally on the device that will be used throughout the app for customisation.
 */
public class UserInfoPrivate implements Serializable{

    //  Unique Log TAG ID.
    final static String TAG = "UserInfo";
    final static int INITIAL_KUDOS = 0;

    //  User information
    private String UID;
    private String displayName;
    private String imageURL;
    private String about;
    private String email;
    private List<HashMap<String, Object>> mealPlanner;
    private boolean shortPreferences = true;
    private boolean firstAppLaunch = true;
    private boolean firstPresentationLaunch = true;
    private boolean firstMealPlannerLaunch = true;

    //  HashMap privacy values are Boolean values of: 'display_username', 'display_about_me', 'display_recipes' & 'display_profile_image'.
    private HashMap<String, Object> privacyPublic;
    private HashMap<String, Object> privacyFriends;

    /*TODO
        Add saved recipes here. Check UML profile diagram and Recipe diagram for more information.
     */

    // User preferences
    private Preferences preferences;

    /** Initiate a users private profile.
     * @param map - Basic user information. E.g. UID, display name. (HashMap, String) pair
     * @param prefs - The users preferences found within the 'Preferences' class. (HashMap, Boolean) pair
     * @param privacyPublic - A HashMap of the users public privacy settings. (HashMap, Boolean) pair.
     * @param privacyFriends - A HashMap of the users privacy settings determining what is viewable by friends.
     */
    public UserInfoPrivate(HashMap<String, Object> map, HashMap<String, Object> prefs, HashMap<String, Object> privacyFriends, HashMap<String, Object> privacyPublic) {
        this.email = (String) map.get("email");
        this.UID = (String) map.get("UID");
        this.displayName = (String) map.get("displayName");
        this.imageURL = (String) map.get("imageURL");
        this.about = (String) map.get("about");
        this.mealPlanner = (List<HashMap<String, Object>>) map.get("mealPlan");
        this.shortPreferences = (boolean) map.get("shortPreferences");
        this.firstAppLaunch = (boolean) map.get("firstAppLaunch");
        this.firstPresentationLaunch = (boolean) map.get("firstPresentationLaunch");
        this.firstMealPlannerLaunch = (boolean) map.get("firstMealPlannerLaunch");

        if(shortPreferences){
            this.preferences = new Preferences((boolean) prefs.get("allergy_nuts"),
                    (boolean) prefs.get("allergy_eggs"), (boolean) prefs.get("allergy_milk"),
                    (boolean) prefs.get("allergy_shellfish"), (boolean)prefs.get("allergy_soya"),
                    (boolean) prefs.get("allergy_gluten"), (boolean)prefs.get("vegetarian"),
                    (boolean) prefs.get("vegan"), (boolean) prefs.get("pescatarian"));
        } else {
            Log.e(TAG, "REACHING LONG PREFERENCES FOR SOME WEIRD REASON");
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
        this.privacyFriends = privacyFriends;
        this.privacyPublic = privacyPublic;

    }

    /*TODO
        Instead of implementing shared preferences for user profile. Use instead for Saved Recipes and any other locally stored info.
        Likely to come in iteration 2/3.
     */
    /*
    private void loadSharedPreferences(){
        SharedPreferences userInfo = getApplicationContext().getSharedPreferences(USER_INFO_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        UID = userInfo.getString(UID_KEY, null);
        displayName = userInfo.getString(DISPLAY_NAME_KEY, null);
        imageURL = userInfo.getString(IMAGEURL_KEY, null);
        about = userInfo.getString(ABOUT_KEY, null);
        numRecipes = userInfo.getInt(NUM_OF_RECIPES, -1);
        chefRating = userInfo.getInt(CHEF_RATING_KEY, -1);
        preferences.setAllergy_nuts(userInfo.getBoolean("ALLERGY_NUTS_KEY", false));
        preferences.setAllergy_shellfish(userInfo.getBoolean("ALLERGY_SHELLFISH_KEY", false));
        preferences.setAllergy_milk(userInfo.getBoolean("ALLERGY_MILK_KEY", false));
        preferences.setAllergy_soya(userInfo.getBoolean("ALLERGY_SOYA_KEY", false));
        preferences.setAllergy_eggs(userInfo.getBoolean("ALLERGY_EGGS_KEY", false));
    }
     */

    /*
    private void saveSharedPreferences(){
        SharedPreferences userInfo = getApplicationContext().getSharedPreferences(USER_INFO_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString(UID_KEY, getUID());
        editor.putString(DISPLAY_NAME_KEY, getDisplayName());
        editor.putString(IMAGEURL_KEY, getImageURL());
        editor.putString(ABOUT_KEY, getAbout());
        editor.putInt(NUM_OF_RECIPES, (int) getNumRecipes());
        editor.putInt(CHEF_RATING_KEY, (int) getChefRating());
        editor.putBoolean("ALLERGY_NUTS_KEY", getPreferences().isAllergy_nuts());
        editor.putBoolean("ALLERGY_SHELLFISH_KEY", getPreferences().isAllergy_shellfish());
        editor.putBoolean("ALLERGY_MILK", getPreferences().isAllergy_milk());
        editor.putBoolean("ALLERGY_SOYA", getPreferences().isAllergy_soya());
        editor.putBoolean("ALLERGY_EGGS", getPreferences().isAllergy_eggs());
        editor.apply();
    }
    */

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<HashMap<String, Object>> getMealPlanner() {
        return mealPlanner;
    }

    public void setMealPlanner(List<HashMap<String, Object>> mealPlanner) {
        this.mealPlanner = mealPlanner;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
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

    public void setImageURL(String imageURL)  {
        this.imageURL = imageURL;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about)   { this.about = about; }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences)   {
        this.preferences = preferences;
    }

    public HashMap<String, Object> getPublicPrivacy() { return privacyPublic; }

    public HashMap<String, Object> getPrivacyFriends() { return privacyFriends; }

    public void setPrivacyPublic(HashMap<String, Object> privacy) {
        //  Check the HashMap has been properly initialized with all valid privacy parameters., otherwise return a runtime exception.
        if (privacy.containsKey("display_username") && privacy.containsKey("display_profile_image") && privacy.containsKey("display_about_me") && privacy.containsKey("display_recipes") && privacy.containsKey("display_filters")) {
            this.privacyPublic = privacy;
        } else {
            throw new RuntimeException("Tried to set privacy settings for public profile with invalid or incomplete inputs");
        }
    }

    public void setPrivacyFriends(HashMap<String, Object> privacy) {
        //  Check the HashMap has been properly initialized with all valid privacy parameters., otherwise return a runtime exception.
        if (privacy.containsKey("display_username") && privacy.containsKey("display_profile_image") && privacy.containsKey("display_about_me") && privacy.containsKey("display_recipes") && privacy.containsKey("display_filters")) {
            this.privacyFriends = privacy;
        } else {
            throw new RuntimeException("Tried to set privacy settings for friends with invalid or incomplete inputs");
        }
    }

    public void setShortPreferences(boolean shortTrue) { shortPreferences = shortTrue; }

    public boolean getShortPreferences(){
        return shortPreferences;
    }

    public void setFirstAppLaunch(boolean firstLaunch)  {firstAppLaunch = firstLaunch; }

    public boolean getFirstAppLaunch() {
        return firstAppLaunch;
    }

    public void setFirstPresentationLaunch(boolean firstLaunch) {firstPresentationLaunch = firstLaunch; }

    public boolean getFirstPresentationLaunch() {
        return firstPresentationLaunch;
    }

    public void setFirstMealPlannerLaunch(boolean firstLaunch) {firstMealPlannerLaunch = firstLaunch; }

    public boolean getFirstMealPlannerLaunch() {
        return firstMealPlannerLaunch;
    }



}