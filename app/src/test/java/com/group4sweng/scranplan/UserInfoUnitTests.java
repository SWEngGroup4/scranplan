package com.group4sweng.scranplan;

import android.content.Context;

import com.group4sweng.scranplan.Exceptions.InvalidContextException;

import org.junit.After;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * JUnit 4 tests (Including Mockito) for User Info testing that do not require instrumentation tests.
 */
public class UserInfoUnitTests {

    UserInfoPrivate ui;


    //  Default test values.
    private final String TEST_UID = "0UxMqO57gjSfAXuSwUiE1ip1Ca83";
    private final String TEST_EMAIL = "Fake@email.com";
    private final String TEST_DISPLAYNAME = "FakeDisplayName";
    private final String TEST_ABOUT = "I am a fake person.";
    private final String TEST_IMAGEURL = "https://www.displayfakefoods.com/store/pc/catalog/9815e_2030_detail.jpg";
    private double TEST_CHEFRATING = 3.5;
    private int TEST_NUM_RECIPES = 10;

    private Context PROFILE_SETTINGS_CONTEXT = mock(ProfileSettings.class);

    //  Creates a test user info HashMap
    private HashMap<String, Object> createUserInfoHashMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("UID", TEST_UID);
        map.put("email", TEST_EMAIL);
        map.put("displayName", TEST_DISPLAYNAME);
        map.put("imageURL", TEST_IMAGEURL);
        map.put("chefRating", TEST_CHEFRATING);
        map.put("numRecipes", TEST_NUM_RECIPES);
        map.put("about", TEST_ABOUT);
        return map;
    }

    //  Creates a test preferences HashMap
    private HashMap<String, Object> createPreferencesHashMap() {
        HashMap<String, Object> preferences = new HashMap<>();
        // Default user food preferences
        preferences.put("allergy_celery", false);
        preferences.put("allergy_crustacean", false);
        preferences.put("allergy_eggs", false);
        preferences.put("allergy_fish", false);
        preferences.put("allergy_gluten", false);
        preferences.put("allergy_milk", false);
        preferences.put("allergy_mustard", false);
        preferences.put("allergy_nuts", false);
        preferences.put("allergy_peanuts", false);
        preferences.put("allergy_sesame", false);
        preferences.put("allergy_shellfish", false);
        preferences.put("allergy_soya", false);
        preferences.put("allergy_sulphide", false);
        preferences.put("diabetic", false);
        preferences.put("halal", false);
        preferences.put("high_protein", false);
        preferences.put("kosher", false);
        preferences.put("lactose_free", false);
        preferences.put("lactovegetarian", false);
        preferences.put("low_carb", false);
        preferences.put("low_sodium", false);
        preferences.put("no_alcohol", false);
        preferences.put("no_pork", false);
        preferences.put("ovovegetarian", false);
        preferences.put("pescatarian", false);
        preferences.put("vegan", false);
        preferences.put("vegetarian", false);
        return preferences;
    }
    /*
    //  Test data can be stored in Singleton class and retrieved using HashMaps.
    @Test
    public void testCanBeCreatedAndRetrievedFromCorrectContext() {

        HashMap<String, Object> map = createUserInfoHashMap();
        HashMap<String, Object> preferences = createPreferencesHashMap();

        try {
             ui = new UserInfoPrivate(map, preferences, MAIN_ACTIVITY_CONTEXT);
        } catch (Exception e){
            e.printStackTrace();
        }

        assertSame(ui.getUID(), TEST_UID);
        assertSame(ui.getAbout(), TEST_ABOUT);
        assertSame(ui.getDisplayName(), TEST_DISPLAYNAME);
        assertEquals(ui.getNumRecipes(), TEST_NUM_RECIPES);
        assertSame(ui.getImageURL(), TEST_IMAGEURL);
        assertEquals(ui.getChefRating(), TEST_CHEFRATING);

        //Only check one preference. Will take forever to write every preference check.
        assertSame(ui.getPreferences().isAllergy_celery(), preferences.get("allergy_celery"));

        ui = null;

        try {
            ui = new UserInfoPrivate(map, preferences, LOGIN_CONTEXT);
        } catch (Exception e){
            e.printStackTrace();
        }

        assertSame(ui.getUID(), TEST_UID);
        assertSame(ui.getAbout(), TEST_ABOUT);
        assertSame(ui.getDisplayName(), TEST_DISPLAYNAME);
        assertEquals(ui.getNumRecipes(), TEST_NUM_RECIPES);
        assertSame(ui.getImageURL(), TEST_IMAGEURL);
        assertEquals(ui.getChefRating(), TEST_CHEFRATING);

        assertSame(ui.getPreferences().isAllergy_celery(), preferences.get("allergy_celery"));
    }*/

    @Test
    public void testFalseContextFails(){
        HashMap<String, Object> map = createUserInfoHashMap();
        HashMap<String, Object> preferences = createPreferencesHashMap();

        try {
            ui = new UserInfoPrivate(map, preferences, PROFILE_SETTINGS_CONTEXT);
            fail("Failed to throw error when setting a false context when creating the UserInfoPrivate Class.");
        } catch (InvalidContextException e){
            assertEquals("Current Activity context does not relate to: com.group4sweng.scranplan.Login or com.group4sweng.scranplan.MainActivity. Hence Will not initialize class.", e.getMessage());
        }
    }

    /*
    //  Test we have a valid context for the 'ProfileSettings' page activity and therefore any edits made to user info are only made within this activity.
    @Test
    public void testUserInfoCannotBeSetOutsideProfileSettings(){

        HashMap<String, Object> map = createUserInfoHashMap();
        HashMap<String, Object> preferences = createPreferencesHashMap();

        try {
            ui = new UserInfoPrivate(map, preferences, LOGIN_CONTEXT);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            ui.setUID(TEST_UID, LOGIN_CONTEXT);
            fail("Failed to throw error when setting user info outside user profile settings");
        } catch (InvalidContextException e){
            assertEquals("Current Activity context required to set UID of user is: com.group4sweng.scranplan.ProfileSettings", e.getMessage());
        }

    }
    */

    @After
    public void cleanUp(){
        ui = null;
    }
}