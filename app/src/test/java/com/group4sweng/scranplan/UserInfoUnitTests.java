package com.group4sweng.scranplan;

import android.content.Context;

import com.group4sweng.scranplan.Exceptions.InvalidContextException;
import com.group4sweng.scranplan.Exceptions.MultipleSingletonCreationException;

import org.junit.Test;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserInfoUnitTests {

    //  Default test values.
    String TEST_UID = "0UxMqO57gjSfAXuSwUiE1ip1Ca83";
    String TEST_ALTERNATE_UID = "9YEzEhqlOYbhwEqS334JEzhdKPi2";
    String TEST_EMAIL = "Fake@email.com";
    String TEST_DISPLAYNAME = "FakeDisplayName";
    String TEST_ABOUT = "I am a fake person.";
    String TEST_IMAGEURL = "https://www.displayfakefoods.com/store/pc/catalog/9815e_2030_detail.jpg";
    double TEST_CHEFRATING = 3.5;
    int TEST_NUM_RECIPES = 10;

    //  Mocked context of the Login screen.
    Context LOGIN_CONTEXT = mock(Login.class);

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

    //  Test data can be stored in Singleton class and retrieved using HashMaps.
    @Test
    public void testCanBeCreatedAndRetrievedFromHashMap() {

        HashMap<String, Object> map = createUserInfoHashMap();
        HashMap<String, Object> preferences = createPreferencesHashMap();

        try {
            UserInfoPrivate.createInstance(map, preferences);
        } catch (Exception e){
            e.printStackTrace();
        }

        UserInfoPrivate ui = UserInfoPrivate.getInstance();
        assertSame(ui.getUID(), TEST_UID);
        assertSame(ui.getAbout(), TEST_ABOUT);
        assertSame(ui.getDisplayName(), TEST_DISPLAYNAME);
        assertEquals(ui.getNumRecipes(), TEST_NUM_RECIPES);
        assertSame(ui.getImageURL(), TEST_IMAGEURL);
        assertEquals(ui.getChefRating(), TEST_CHEFRATING);

        //Only check one preference. Will take forever to write every preference check.
        assertSame(ui.getPreferences().isAllergy_celery(), preferences.get("allergy_celery"));
    }

    //  Test we cannot get an instance of the Singleton class 'UserInfoPrivate' before it is initialized.
    @Test
    public void testCannotGetInstanceBeforeInitiating(){
        try {
            UserInfoPrivate.getInstance();
            fail("UserInfoPrivate() Should throw Runtime Exception if we try to retrieve an instance before initiating it");
        } catch (RuntimeException e){
            System.out.println("Runtime exception caught properly");
        }
    }

    //  Test we have a valid context for the 'ProfileSettings' page activity and therefore any edits made to user info are only made within this activity.
    @Test
    public void testUserInfoCannotBeSetOutsideProfileSettings(){

        HashMap<String, Object> map = createUserInfoHashMap();
        HashMap<String, Object> preferences = createPreferencesHashMap();

        try {
            UserInfoPrivate.createInstance(map, preferences);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            UserInfoPrivate.getInstance().setUID(TEST_UID, LOGIN_CONTEXT);
            fail("Failed to throw error when setting user info outside user profile settings");
        } catch (InvalidContextException e){
            assertEquals("Current Activity context required to set UID of user is: com.group4sweng.scranplan.ProfileSettings", e.getMessage());
        }

    }

    //  Test that multiple instances of the Singleton class 'UserInfoPrivate' are not possible.
    @Test
    public void testMultipleInstanceCreationFails(){
        HashMap<String, Object> map = createUserInfoHashMap();
        HashMap<String, Object> preferences = createPreferencesHashMap();

        try {
            UserInfoPrivate.createInstance(map, preferences);
            UserInfoPrivate.createInstance(map, preferences);
            fail("Unable to retrieve exception when creating multiple instances of the Singleton 'UserInfoPrivate' class.");
        } catch (MultipleSingletonCreationException e){
            assertEquals("Cannot create multiple instances of Singleton class 'UserInfoPrivate'", e.getMessage());
        }

    }
}