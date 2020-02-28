package com.group4sweng.scranplan;

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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Checks retrival, removal and initiation of the UserInfoPrivate class which stores all local user data from Firebase.
 */

// TODO Launching individual tests works for the moment. Launching all at once causes Thread issues.

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserInfoPrivateTest{

    private MainActivity mActivity = null;

    FirebaseApp testApp;
    FirebaseAuth testAuth;

    //  Default test values.
    private static final String TEST_EMAIL = "jb2200@york.ac.uk";
    private static String TEST_PASSWORD = "password";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() throws InterruptedException {

        ActivityScenario.launch(Login.class);

        mActivity = mActivityTestRule.getActivity();
        testApp = mActivity.mApp;
        testAuth = mActivity.mAuth;

        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withId(R.id.emailEditText))
                .perform(typeText(TEST_EMAIL));
        onView(withId(R.id.passwordEditText))
                .perform(typeText(TEST_PASSWORD));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.loginButton))
                .perform(click());

    }

    /*@Test
    public void testUIDIsStoredInUserInfoPrivate() {
        assertEquals(mActivity.mUser.getUID(), (String) testAuth.getUid());
    }

    @Test
    public void testPreferencesAreStoredInUserInfoPrivate(){
        assertNotNull(mActivity.mUser.getPreferences());
    }*/

    //  Checks on log out all of the users data is removed.
    @Test
    public void testDataInUserInfoPrivateRemovedOnLogOut() throws InterruptedException {

        Thread.sleep(10000);
        onView(withId(R.id.logoutButton))
                .perform(click());


        assertNull(mActivity.mAuth.getCurrentUser());
        assertNull(mActivity.mUser);
        Thread.sleep(10000);

    }

    //  Test all data grabbed from Firebase at login is stored locally within the app.
    @Test
    public void testAllDataIsStoredInUserInfoPrivate() throws InterruptedException {

        Thread.sleep(2000); //Gives enough time for userInfo object to be passed to MainActivity screen
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

        Thread.sleep(2000); //Gives enough time for userInfo object to be passed to MainActivity screen
        onView(withId(R.id.logoutButton))
            .perform(click());

    }

    @Test void testInitialPreferencesCanBeSet() throws


    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }


}
