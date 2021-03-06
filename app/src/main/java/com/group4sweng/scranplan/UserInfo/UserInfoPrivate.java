package com.group4sweng.scranplan.UserInfo;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


/**
 * UserInfoPrivate class
 *  Author: LNewman
 *  Edited by: JButler, JClawley
 *  (c) CoDev 2020
 *
 * Used to save the current users attributes locally on the device that will be used throughout the app for customisation.
 */
public class UserInfoPrivate implements Serializable, Cloneable{

    //  Unique Log TAG ID.
    final static String TAG = "UserInfo";
    final static int INITIAL_KUDOS = 0;

    //  User information
    private String UID;
    private String displayName;
    private String imageURL;
    private String about;
    private String email;
    private long recipes;
    private long posts;
    private List<HashMap<String, Object>> mealPlanner;
    private boolean shortPreferences;
    private boolean firstAppLaunch;
    private boolean firstPresentationLaunch;
    private boolean firstMealPlannerLaunch;
    private long followers;
    private long following;


    //  HashMap privacy values are Boolean values of: 'display_username', 'display_about_me', 'display_recipes', 'display_profile_image' & 'display_feed'.
    private HashMap<String, Object> privacyPublic;
    private HashMap<String, Object> privacyPrivate;
    //  Which type of profile should the user have. If true the profile is by default hidden.
    private boolean isPrivateProfileEnabled;

    /*TODO
        Add saved recipes here. Check UML profile diagram and Recipe diagram for more information.
     */

    // User preferences
    private Preferences preferences;

    /** Initiate a users private profile.
     * @param map - Basic user information. E.g. UID, display name. (HashMap, String) pair
     * @param prefs - The users preferences found within the 'Preferences' class. (HashMap, Boolean) pair
     * @param privacyPublic - A HashMap of the users public privacy settings. (HashMap, Boolean) pair.
     * @param privacyPrivate - A HashMap of the users privacy settings determining what is viewable by followers.
     */
    public UserInfoPrivate(HashMap<String, Object> map, HashMap<String, Object> prefs, HashMap<String, Object> privacyPrivate, HashMap<String, Object> privacyPublic) {
        this.posts = (long) map.get("posts");
        this.followers = (long) map.get("followers");
        this.following = (long) map.get("following");
        this.recipes = (long) map.get("numRecipes");
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

        this.isPrivateProfileEnabled = (boolean) map.get("privateProfileEnabled");
        this.privacyPrivate = privacyPrivate;
        this.privacyPublic = privacyPublic;
    }

    //  Produce a deep clone of the UserInfoPrivate class to editing purposes. Java is inherently pass-by-reference.
    //  This provides a way of getting around this at least for temporary storage of user settings whilst editing profile settings.
    public UserInfoPrivate deepClone() {
        try {
            //  Setup byte and object streams.
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOutStream = new ObjectOutputStream(byteArrayOut);
            objectOutStream.writeObject(this); //   Write the object

            //  Make a copy from the original serializable bytecode and return this.
            ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(byteArrayOut.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayIn);
            return (UserInfoPrivate) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    //  Weak clone.
    @NotNull
    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }


    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public long getRecipes() {
        return recipes;
    }

    public void setRecipes(long recipes) {
        this.recipes = recipes;
    }

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

    public HashMap<String, Object> getPrivacyPrivate() { return privacyPrivate; }

    public boolean isPrivateProfileEnabled() { return isPrivateProfileEnabled; }

    public void setIsPrivateProfileEnabled(boolean isPrivateProfileEnabled ){ this.isPrivateProfileEnabled = isPrivateProfileEnabled; }

    void setPrivacyPublic(HashMap<String, Object> privacy) {
        //  Check the HashMap has been properly initialized with all valid privacy parameters., otherwise return a runtime exception.
        if (privacy.containsKey("display_username") && privacy.containsKey("display_profile_image") && privacy.containsKey("display_about_me") && privacy.containsKey("display_recipes") && privacy.containsKey("display_filters") && privacy.containsKey("display_feed")) {
            this.privacyPublic = privacy;
        } else {
            throw new RuntimeException("Tried to set privacy settings for public profile with invalid or incomplete inputs");
        }
    }

    void setPrivatePrivacy(HashMap<String, Object> privacy) {
        //  Check the HashMap has been properly initialized with all valid privacy parameters., otherwise return a runtime exception.
        if (privacy.containsKey("display_username") && privacy.containsKey("display_profile_image") && privacy.containsKey("display_about_me") && privacy.containsKey("display_recipes") && privacy.containsKey("display_filters") && privacy.containsKey("display_feed")) {
            this.privacyPrivate = privacy;
        } else {
            throw new RuntimeException("Tried to set privacy settings for private profile with invalid or incomplete inputs");
        }
    }

    void setShortPreferences(boolean shortTrue) { shortPreferences = shortTrue; }

    public boolean getShortPreferences(){
        return shortPreferences;
    }

    void setFirstAppLaunch(boolean firstLaunch)  {firstAppLaunch = firstLaunch; }

    public boolean getFirstAppLaunch() {
        return firstAppLaunch;
    }

    void setFirstPresentationLaunch(boolean firstLaunch) {firstPresentationLaunch = firstLaunch; }

    public boolean getFirstPresentationLaunch() {
        return firstPresentationLaunch;
    }

    public void setFirstMealPlannerLaunch(boolean firstLaunch) {firstMealPlannerLaunch = firstLaunch; }

    public boolean getFirstMealPlannerLaunch() {
        return firstMealPlannerLaunch;
    }


    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }
}