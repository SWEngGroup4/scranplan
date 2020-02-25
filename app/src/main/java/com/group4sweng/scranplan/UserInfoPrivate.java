package com.group4sweng.scranplan;

import android.app.Application;
import android.content.Context;

import com.group4sweng.scranplan.Exceptions.InvalidContextException;
import com.group4sweng.scranplan.Exceptions.MultipleSingletonCreationException;

import java.util.HashMap;

/**
 * UserInfo class
 * Used to save current user vital variables that will be used throughout the app for customisation
 */
public class UserInfoPrivate extends Application {

    final static String TAG = "UserInfo";
    private static UserInfoPrivate INSTANCE; //Instance of current class. Used for Singletons.

    /*  The class names relating to the context of the Activity in which private user info is to be retrieved from.
            Login = At initial login
            Profile Settings = Whilst on the profile settings Activity.
    */
    private static final String CONTEXT_PROFILE_SETTINGS = "com.group4sweng.scranplan.ProfileSettings";
    private static final String CONTEXT_LOGIN = "com.group4sweng.scranplan.Login";
    //private static final String USER_INFO_SHARED_PREFERENCES = "userinfo";

    /*  Key-value pairs for SharedPreferences.
    private static final String UID_KEY = "UID";
    private static final String DISPLAY_NAME_KEY = "Display_Name";
    private static final String IMAGEURL_KEY = "Image_URL";
    private static final String ABOUT_KEY = "About";
    private static final String CHEF_RATING_KEY = "Chef_Rating";
    private static final String NUM_OF_RECIPES = "Number_Of_Recipes";
    */

    //  User information
    private static String UID;
    private static String displayName;
    private static String imageURL;
    private static String about;
    private static double chefRating;
    private static int numRecipes;

    /*TODO
        Add saved recipes here. Check UML profile diagram and Recipe diagram for more information.
     */

    // User preferences
    private static Preferences preferences;


    //  Private constructor for associated 'createInstance(HashMap<String, Object> map, HashMap<String, Object> prefs)'
    private UserInfoPrivate(HashMap<String, Object> map, HashMap<String, Object> prefs) {
        if (INSTANCE != null) {
            throw new RuntimeException("Tried to retrieve without getting instance of Singleton class");
        }

        UID = (String) map.get("UID");
        displayName = (String) map.get("displayName");
        imageURL = (String) map.get("imageURL");
        chefRating = (double) map.get("chefRating");
        numRecipes =  (int) map.get("numRecipes");
        about = (String) map.get("about");
        preferences = new Preferences((boolean) prefs.get("allergy_celery"),
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

        //saveSharedPreferences();
    }

    //  Private constructor for associated 'createInstance(Context context)'
    private UserInfoPrivate(Context context) {

    }

    /*TODO
        Instead of implementing shared preferences for user profile. Use instead for Saved Recipes.
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

    /**
     * Initiate Singleton User Profile Class from a HashMap of values.
     * Initiated on login/registering for the first time.
     *
     * @param map   - Map of user specific information
     * @param prefs - Map of dietary preferences
     */
    public synchronized static void createInstance(HashMap<String, Object> map, HashMap<String, Object> prefs) throws MultipleSingletonCreationException {
        if (INSTANCE == null) {
            INSTANCE = new UserInfoPrivate(map, prefs);
        } else {
            throw new MultipleSingletonCreationException("Cannot create multiple instances of Singleton class 'UserInfoPrivate'");
        }
    }

    /* TODO implement only if Shared Preferences (local storage) is required.
    /**
     * Constructor for initiating Singleton User Info Class from the apps 'Login' Activity context
     * Initiated when user has already logged in and is returning to the MainActivity screen.
     *
     * @param context - The context of the current Activity. Should be 'Login'.
     */
    /*
    public synchronized static void createInstance(Context context) throws InvalidContextException {

        //Checks we can only initiate from the Login.class & an instance of the class hasn't already been created.
        if (checkContext(CONTEXT_LOGIN, context) && INSTANCE == null) {
            INSTANCE = new UserInfoPrivate(context);
        } else if (!checkContext(CONTEXT_LOGIN, context) && INSTANCE == null) {
            throw new InvalidContextException("Activity context required to create instance of class is: " + CONTEXT_LOGIN);
        } else {
            Log.e(TAG, "Instance of singleton class 'UserInfoPrivate' already exists");
        }
    }
    */

    /**
     * Get an instance of the users info (Singleton class) or throw an error if an instance of the class dosen't exist.
     * It should exist at runtime.
     *
     * @return - Valid instance of the class.
     */
    public synchronized static UserInfoPrivate getInstance() { //Thread safe
        if (INSTANCE == null) {
            throw new java.lang.RuntimeException("Cannot retrieve an instance of the users information from UserInfoPrivate. Make sure an instance of the class is created before retrieving an instance.");
        }
        return INSTANCE;
    }


    /**
     * Checks the context we expect for the associated Activity against what is retrieved.
     *
     * @param expectedContextClassName - The class name identifier of the expected context. E.g. com.group4sweng.scranplan.[name]
     * @param context                  - Our current actual context.
     * @return - Boolean value specifying if the context matches or not.
     */
    private static boolean checkContext(String expectedContextClassName, Context context) {
        String actualContextClassname = context.getClass().getName();
        if (actualContextClassname.equals(expectedContextClassName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Replica of 'checkContext' but with a reference 'name' and throwable error value instead of a boolean.
     * @param expectedContextClassname - The class name identifier of the expected context. E.g. com.group4sweng.scranplan.[name]
     * @param context                  - Our current actual context.
     * @param name                     - Name identifier to use within the InvalidContextException message.
     * @throws InvalidContextException - Throws if contexts don't match.
     */
    private static void checkContext(String expectedContextClassname, Context context, String name) throws InvalidContextException {
        if (!checkContext(expectedContextClassname, context)) {
            throw new InvalidContextException("Current Activity context required to set " + name + " of user is: " + expectedContextClassname);
        }
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID, Context context) throws InvalidContextException {
        checkContext(CONTEXT_PROFILE_SETTINGS, context, "UID");
        UserInfoPrivate.UID = UID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName, Context context) throws InvalidContextException {
        checkContext(CONTEXT_PROFILE_SETTINGS, context, "Display Name");
        UserInfoPrivate.displayName = displayName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL, Context context) throws InvalidContextException {
        checkContext(CONTEXT_PROFILE_SETTINGS, context, "Image URL");
        UserInfoPrivate.imageURL = imageURL;
    }

    public double getChefRating() {
        return chefRating;
    }

    public void setChefRating(double chefRating, Context context) throws InvalidContextException {
        checkContext(CONTEXT_PROFILE_SETTINGS, context, "Chef Rating");
        UserInfoPrivate.chefRating = chefRating;
    }

    public long getNumRecipes() {
        return numRecipes;
    }

    public void setNumRecipes(int numRecipes, Context context) throws InvalidContextException {
        checkContext(CONTEXT_PROFILE_SETTINGS, context, "Number Of Recipes");
        UserInfoPrivate.numRecipes = numRecipes;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about, Context context) throws InvalidContextException {
        checkContext(CONTEXT_PROFILE_SETTINGS, context, "About");
        UserInfoPrivate.about = about;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences, Context context) throws InvalidContextException {
        checkContext(CONTEXT_PROFILE_SETTINGS, context, "Preferences");
        UserInfoPrivate.preferences = preferences;
    }

}