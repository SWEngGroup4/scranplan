//package com.group4sweng.scranplan;
//
//import com.group4sweng.scranplan.UserInfo.Preferences;
//import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.HashMap;
//
//import static com.google.firebase.firestore.util.Assert.fail;
//import static junit.framework.TestCase.assertEquals;
//import static junit.framework.TestCase.assertTrue;
//import static org.junit.Assert.assertFalse;
//
////  Unit tests for testing local private user info is able to be stored and retrieved.
//public class UserInfoPrivateTest {
//
//    UserInfoPrivate testInfo;
//
//    //  Initial test preferences. Same as default upon user registration.
//    @Before
//    public void initTestPreferences(){
//        HashMap<String, Object> map = new HashMap<>();
//        HashMap<String, Object> preferences = new HashMap<>();
//        HashMap<String, Object> privacy = new HashMap<>();
//
//        map.put("UID", "testUID");
//        map.put("email", "email@testuser.com");
//        map.put("displayName", "testName");
//        map.put("imageURL", "");
//        map.put("chefRating", (double) 0);
//        map.put("numRecipes", (long) 0);
//        map.put("about", "");
//
//        privacy.put("display_username", true);
//        privacy.put("display_about_me", true);
//        privacy.put("display_recipes", false);
//        privacy.put("display_profile_image", true);
//
//        // Default user food preferences
//        preferences.put("allergy_celery", false);
//        preferences.put("allergy_crustacean", false);
//        preferences.put("allergy_eggs", false);
//        preferences.put("allergy_fish", false);
//        preferences.put("allergy_gluten", false);
//        preferences.put("allergy_milk", false);
//        preferences.put("allergy_mustard", false);
//        preferences.put("allergy_nuts", false);
//        preferences.put("allergy_peanuts", false);
//        preferences.put("allergy_sesame", false);
//        preferences.put("allergy_shellfish", false);
//        preferences.put("allergy_soya", false);
//        preferences.put("allergy_sulphide", false);
//        preferences.put("diabetic", false);
//        preferences.put("halal", false);
//        preferences.put("high_protein", false);
//        preferences.put("kosher", false);
//        preferences.put("lactose_free", false);
//        preferences.put("lactovegetarian", false);
//        preferences.put("low_carb", false);
//        preferences.put("low_sodium", false);
//        preferences.put("no_alcohol", false);
//        preferences.put("no_pork", false);
//        preferences.put("ovovegetarian", false);
//        preferences.put("pescatarian", false);
//        preferences.put("vegan", false);
//        preferences.put("vegetarian", false);
//
//        testInfo = new UserInfoPrivate(map, preferences, privacy);
//    }
//
//    @Test
//    public void testUIDCanBeSet(){
//        testInfo.setUID("newUID");
//
//        assertEquals(testInfo.getUID(), "newUID");
//    }
//
//    //  Test users 'about', 'chef rating' etc... can be set and retrieved,
//    @Test
//    public void testOtherBasicInfoCanBeSet(){
//        testInfo.setAbout("newAbout");
//        testInfo.setChefRating(3.60);
//        testInfo.setNumRecipes(360);
//        testInfo.setImageURL("NewImageURL.com");
//        testInfo.setDisplayName("newName");
//
//        assertEquals(testInfo.getAbout(), "newAbout");
//        assertEquals(testInfo.getChefRating(), 3.60);
//        assertEquals(testInfo.getNumRecipes(), 360);
//        assertEquals(testInfo.getImageURL(), "NewImageURL.com");
//        assertEquals(testInfo.getDisplayName(), "newName");
//    }
//
//    //  Test all allegerns can be set from a basic preferences constructor for the 6 main allegerns.
//    @Test
//    public void testBasicAllergyPreferencesCanBeSet() {
//        Preferences testPref = new Preferences(true, true, true, true, true, true);
//        testInfo.setPreferences(testPref);
//
//        assertTrue(testInfo.getPreferences().isAllergy_nuts());
//        assertTrue(testInfo.getPreferences().isAllergy_eggs());
//        assertTrue(testInfo.getPreferences().isAllergy_milk());
//        assertTrue(testInfo.getPreferences().isAllergy_shellfish());
//        assertTrue(testInfo.getPreferences().isAllergy_soya());
//        assertTrue(testInfo.getPreferences().isAllergy_gluten());
//    }
//
//    //  Test a runtime exception is thrown if our privacy settings HashMap is missing a value or has incorrect key-value pairs.
//    @Test
//    public void testPrivacyOptionsFailOnIncompleteHashMapEntry(){
//        HashMap<String, Object> privacy = new HashMap<>();
//
//        privacy.put("display_username", true);
//        privacy.put("display_about_me", true);
//        privacy.put("display_recipes", false);
//
//        try{
//            testInfo.setPrivacy(privacy);
//            fail("Failed to return valid runtime exception message for an incomplete Privacy HashMap entry");
//        } catch (RuntimeException e){
//            assertEquals(e.getMessage(), "Tried to set privacy settings with invalid or incomplete inputs");
//        }
//    }
//
//    //  Test that privacy options can be set with a correct key-value pair entry.
//    @Test
//    public void testPrivacyOptionsSucceedOnCorrectHashMapEntry(){
//        HashMap<String, Object> privacy = new HashMap<>();
//
//        privacy.put("display_username", false);
//        privacy.put("display_about_me", false);
//        privacy.put("display_recipes", true);
//        privacy.put("display_profile_image", false);
//
//        try {
//            testInfo.setPrivacy(privacy);
//        } catch (RuntimeException e){
//            fail("Should not return an exception with valid Privacy input parameters");
//        }
//
//        assertFalse((boolean) testInfo.getPrivacy().get("display_username"));
//        assertFalse((boolean) testInfo.getPrivacy().get("display_about_me"));
//        assertTrue((boolean) testInfo.getPrivacy().get("display_recipes"));
//        assertFalse((boolean) testInfo.getPrivacy().get("display_profile_image"));
//    }
//
//}
