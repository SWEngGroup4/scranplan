package com.group4sweng.scranplan;

import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserInfoPrivateTest{

    final String TAG = "UserInfoTest";
    private MainActivity mActivity = null;

    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText displayName;
    private Button loginButton;
    private Button registerButton;
    FirebaseApp testApp;
    FirebaseAuth testAuth;

    UserInfoPrivate ui;
    /*
        Only added private, restricted, public retrieval tests.
        Tests for whether the user info is stored correctly is not included and should be done
        when a user story requires it. E.g. adding chef star integration into the userInfo ProfileView.
     */

    //  Default test values.
    private final String TEST_UID = "0UxMqO57gjSfAXuSwUiE1ip1Ca83";
    private final String TEST_EMAIL = "Fake@email.com";
    private final String TEST_DISPLAYNAME = "FakeDisplayName";
    private final String TEST_ABOUT = "I am a fake person.";
    private final String TEST_IMAGEURL = "https://www.displayfakefoods.com/store/pc/catalog/9815e_2030_detail.jpg";
    private double TEST_CHEFRATING = 3.5;
    private int TEST_NUM_RECIPES = 10;

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

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp(){
        mActivity = mActivityTestRule.getActivity();
        testApp = mActivity.mApp;
        testAuth = mActivity.mAuth;

        ActivityScenario.launch(Login.class);
        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withId(R.id.emailEditText))
                .perform(typeText("jb2200@york.ac.uk"));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton))
                .perform(click());
    }

    //  Test data can be stored in Singleton class and retrieved using HashMaps.
    @Test
    public void testDataIsStored() {
        assertEquals(mActivity.mUser.getUID(), (String) testAuth.getUid());
        assertNotNull(mActivity.mUser.getAbout());
        assertNotNull(mActivity.mUser.getPreferences());
        assertNotNull(mActivity.mUser.getDisplayName());
        assertNotNull(mActivity.mUser.getImageURL());

        if(mActivity.mUser.getNumRecipes() >= 0){
        } else {
            fail();
        }
        if(mActivity.mUser.getChefRating() >= 0){
        } else {
            fail();
        }
    }


    @Test
    public void testBasicInfoCanBeRetrievedFromUser() {
    }


    @Test
    public void testPrivateInfoIsHiddenToOtherUsers() {
        /*TODO
            Check that we are unable to retrieve private info on other users.
         */
    }

    @Test
    public void testPublicInfoIsVisible() {
        /*TODO
            Check that the public profile info is visible from another user account.
         */
    }

    @Test
    public void testOnlyRestrictedInfoIsDisplayed() {
        /*TODO
            Test that for the restriced 'enum' preset only a limited amount of options are displayed.
         */
    }
    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }


}
