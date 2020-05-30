package com.group4sweng.scranplan.UserInfo;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.sentry.core.Sentry;

import static com.google.firebase.firestore.util.Assert.fail;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Unit tests for testing local private user info is able to be stored and retrieved.
 * Author: JButler
 * Edited by: NBillis, JButler
 * (c) CoDev 2020
 **/
public class UserInfoPrivateTest {

    private UserInfoPrivate testInfo;

    //  Initial test preferences. Same as default upon user registration.
    @Before
    public void initTestPreferences(){
        // Setting up default user profileView on database with email and display name
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> preferences = new HashMap<>();
        HashMap<String, Object> privacyPublic = new HashMap<>();
        HashMap<String, Object> privacyPrivate = new HashMap<>();

        // Add empty MealPlan list
        List<Bundle> mealPlan = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            mealPlan.add(null);
        }

        map.put("UID", "testUID");
        map.put("email", "email@testuser.com");
        map.put("displayName", "testName");
        map.put("imageURL", "");
        map.put("numRecipes", (long) 0);
        map.put("about", "");
        map.put("mealPlan", mealPlan);
        map.put("shortPreferences", true);
        map.put("firstAppLaunch", true);
        map.put("firstPresentationLaunch", true);
        map.put("firstMealPlannerLaunch", true);
        map.put("kudos", (long) 0);
        map.put("posts", (long) 0);
        map.put("following", (long)0);
        map.put("followers", (long)0);

        map.put("privateProfileEnabled", false);
        privacyPublic.put("display_username", true);
        privacyPublic.put("display_about_me", true);
        privacyPublic.put("display_recipes", false);
        privacyPublic.put("display_profile_image", true);
        privacyPublic.put("display_filters", false);
        privacyPublic.put("display_feed", false);

        privacyPrivate.put("display_username", true);
        privacyPrivate.put("display_about_me", true);
        privacyPrivate.put("display_recipes", true);
        privacyPrivate.put("display_profile_image", true);
        privacyPrivate.put("display_filters", true);
        privacyPrivate.put("display_feed", true);

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

        map.put("preferences", preferences);
        map.put("privacyPublic", privacyPublic);
        map.put("privacyPrivate", privacyPrivate);

        // Generate our UserInfoPrivate object.
        testInfo = new UserInfoPrivate(map, preferences, privacyPrivate, privacyPublic);
    }

    @Test
    public void testUIDCanBeSet(){
        testInfo.setUID("newUID");

        assertEquals(testInfo.getUID(), "newUID");
    }

    //  Test users 'about', 'chef rating' etc... can be set and retrieved,
    @Test
    public void testOtherBasicInfoCanBeSet(){
        testInfo.setAbout("newAbout");
        testInfo.setImageURL("NewImageURL.com");
        testInfo.setDisplayName("newName");
        testInfo.setMealPlanner(null);
        testInfo.setShortPreferences(false);
        testInfo.setIsPrivateProfileEnabled(true);

        assertEquals(testInfo.getAbout(), "newAbout");
        assertEquals(testInfo.getImageURL(), "NewImageURL.com");
        assertEquals(testInfo.getDisplayName(), "newName");
        assertNull(testInfo.getMealPlanner());
        assertFalse(testInfo.getShortPreferences());
        assertTrue(testInfo.isPrivateProfileEnabled());
    }

    @Test
    public void testInitialInfoCanBeSet(){
        testInfo.setFirstPresentationLaunch(false);
        testInfo.setFirstAppLaunch(false);
        testInfo.setFirstMealPlannerLaunch(false);

        assertFalse(testInfo.getFirstAppLaunch());
        assertFalse(testInfo.getFirstPresentationLaunch());
        assertFalse(testInfo.getFirstMealPlannerLaunch());
    }

    //  Test all allergens can be set from a basic preferences constructor for the 6 main allergens.
    //  All set to true to make sure every allergy & dietary preference can be changed from false > true.
    @Test
    public void testBasicAllergyPreferencesCanBeSet() {
        //  Create a new 'short' preferences object.
        Preferences testPref = new Preferences(true, true, true, true, true, true, true, true, true);
        testInfo.setPreferences(testPref); // Set our preferences to this new object.

        assertTrue(testInfo.getPreferences().isAllergy_nuts());
        assertTrue(testInfo.getPreferences().isAllergy_eggs());
        assertTrue(testInfo.getPreferences().isAllergy_milk());
        assertTrue(testInfo.getPreferences().isAllergy_shellfish());
        assertTrue(testInfo.getPreferences().isAllergy_soya());
        assertTrue(testInfo.getPreferences().isAllergy_gluten());
        assertTrue(testInfo.getPreferences().isVegetarian());
        assertTrue(testInfo.getPreferences().isVegan());
        assertTrue(testInfo.getPreferences().isPescatarian());
    }

    //  Test a runtime exception is thrown if our privacy settings HashMap is missing a value or has incorrect key-value pairs.
    @Test
    public void testPrivacyOptionsFailOnIncompleteHashMapEntry(){
        HashMap<String, Object> privacy = new HashMap<>();

        privacy.put("display_username", true);
        privacy.put("display_about_me", true);
        privacy.put("display_recipes", false);
        privacy.put("display_profile_image", false);
        privacy.put("display_feed", false);

        try{
            testInfo.setPrivatePrivacy(privacy);
            fail("Failed to return valid runtime exception message for an incomplete Private Privacy HashMap entry");
        } catch (RuntimeException e){
            assertEquals(e.getMessage(), "Tried to set privacy settings for private profile with invalid or incomplete inputs");
        }

        try {
            testInfo.setPrivacyPublic(privacy);
            fail("Failed to return valid runtime exception message for an incomplete Public Privacy HashMap entry");
        } catch (RuntimeException e){
            assertEquals(e.getMessage(), "Tried to set privacy settings for public profile with invalid or incomplete inputs");
        }
    }

    //  Test that privacy options can be set with a correct key-value pair entry.
    @Test
    public void testPrivacyOptionsSucceedOnCorrectHashMapEntry(){
        HashMap<String, Object> privacy = new HashMap<>();

        privacy.put("display_username", false);
        privacy.put("display_about_me", false);
        privacy.put("display_recipes", true);
        privacy.put("display_profile_image", false);
        privacy.put("display_filters", false);
        privacy.put("display_feed", true);

        try {
            testInfo.setPrivatePrivacy(privacy);
        } catch (RuntimeException e){
            Sentry.captureException(e);
            fail("Should not return an exception with valid Private Privacy input parameters");
        }

        try {
            testInfo.setPrivacyPublic(privacy);
        } catch (RuntimeException e){
            Sentry.captureException(e);
            fail("Should not return an exception with valid Public Privacy input parameters");
        }

    }

}
