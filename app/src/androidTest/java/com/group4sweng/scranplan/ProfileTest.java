package com.group4sweng.scranplan;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Profile test class for testing if all elements are visible to the user.
 * Author: JButler
 * (c) CoDev 2020
 *
 * =====MANUAL TESTS=====
 * Tests for if data is able to be properly retrieved for multiple users and tests that this information is not shown if
 * the users privacy settings don't permit it have been completed manually due to Firebase only access.*/
public class ProfileTest extends RecordedEspressoHelper{

    //  Default test values.
    private static final String TEST_EMAIL = "jb2200@york.ac.uk";
    private static String TEST_PASSWORD = "password";
    private static final int THREAD_SLEEP_TIME = 4000; //How long Espresso should wait for Firebase data to update.

    @Rule
    public ActivityTestRule<PublicProfile> mActivityTestRule = new ActivityTestRule<PublicProfile>(PublicProfile.class);

    //  Login with the associated test credentials before testing, wait for Firebase to update and enter profile settings.
    @Before
    public void setUp() throws InterruptedException {

        ActivityScenario.launch(Login.class);

        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withId(R.id.emailEditText))
                .perform(typeText(TEST_EMAIL));
        onView(withId(R.id.passwordEditText))
                .perform(typeText(TEST_PASSWORD));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.loginButton))
                .perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        openSideBar(SideBarElement.PROFILE);

        Thread.sleep(THREAD_SLEEP_TIME/4);
    }

    //  Check the username field exists.
    @Test
    public void testUsernameAndAboutMeIsVisible() {

        onView(withId(R.id.profile_username))
                .check(matches(isDisplayed()));

        onView(withId(R.id.public_profile_about_me_desc))
                .check(matches(isDisplayed()));
    }

    //  Check the number of recipes is visible.
    @Test
    public void testNumberOfRecipesIsVisible(){
        onView(withId(R.id.profile_recipes))
                .check(matches(isDisplayed()));
    }

    //  Check filters are visible.
    @Test
    public void testFiltersAreVisible(){


        onView(withId(R.id.profile_filters))
                .check(matches(isDisplayed()));

        onView(withId(R.id.allergy_wheat))
                .check(matches(isDisplayed()));
        onView(withId(R.id.allergy_soy))
                .check(matches(isDisplayed()));
        onView(withId(R.id.dietary_vegetarian))
                .check(matches(isDisplayed()));
        onView(withId(R.id.allergy_shellfish))
                .check(matches(isDisplayed()));
        onView(withId(R.id.dietary_vegan))
                .check(matches(isDisplayed()));
        onView(withId(R.id.allergy_eggs))
                .check(matches(isDisplayed()));
    }

}