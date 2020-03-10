package com.group4sweng.scranplan;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

// initial filter test class for testing if all elements are visible to the user.

 // =====MANUAL TESTS=====
 // tests that all sections work were tested manually. Firebase connections all work and update when needed

@RunWith(AndroidJUnit4.class)
@LargeTest
public class InitialUserTest extends RecordedEspressoHelper {

    //  Android Log tag.
    String TAG = "InitialUserFiltersTest";

    private UserInfoPrivate testUser;
    private static final String TEST_EMAIL = "becky.anderson219@gmail.com";
    private static String TEST_PASSWORD = "Hamish100";

    //  How long we should sleep when waiting for Firebase information to update. Increase this value if you have a slower machine or emulator.
    private static final int THREAD_SLEEP_TIME = 10000;

    @Rule
    public ActivityTestRule<InitialUserCustomisation> mActivityTestRule = new ActivityTestRule<InitialUserCustomisation>(InitialUserCustomisation.class);


    @Before
    public void setUp() throws InterruptedException {

        Log.i(TAG, "Starting tests");


        ActivityScenario.launch(Login.class); //Launch the login screen

        onView(withId(R.id.loginButton))
                .perform(click());

        //  Login with the associated test credentials before testing, wait for Firebase to update and enter profile settings.
        onView(withId(R.id.emailEditText))
                .perform(typeText(TEST_EMAIL));
        onView(withId(R.id.passwordEditText))
                .perform(typeText(TEST_PASSWORD));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.loginButton))
                .perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

    }

    @Test
    public void testAllpresent() throws InterruptedException {
        ActivityScenario.launch(InitialUserCustomisation.class); //Launch the initial user customisation class

        //tests that all buttons and check boxes are working and visible
        onView(withId(R.id.submitButton))
                .perform(click());
        onView(withId(R.id.skipButton))
                .perform(click());
        onView(withId(R.id.veganCheckBox)).perform(click());
        onView(withId(R.id.pescatarianCheckBox)).perform(click());
        onView(withId(R.id.VegCheckBox)).perform(click());
        onView(withId(R.id.nutCheckBox)).perform(click());
        onView(withId(R.id.milkCheckBox)).perform(click());
        onView(withId(R.id.shellfishCheckBox)).perform(click());
        onView(withId(R.id.soyCheckBox)).perform(click());
        onView(withId(R.id.wheatCheckBox)).perform(click());
        onView(withId(R.id.eggCheckBox)).perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        onView(withId(R.id.veganCheckBox))
                .check(matches(isDisplayed()));

        onView(withId(R.id.pescatarianCheckBox))
                .check(matches(isDisplayed()));

        onView(withId(R.id.VegCheckBox))
                .check(matches(isDisplayed()));
        onView(withId(R.id.nutCheckBox))
                .check(matches(isDisplayed()));
        onView(withId(R.id.milkCheckBox))
                .check(matches(isDisplayed()));
        onView(withId(R.id.shellfishCheckBox))
                .check(matches(isDisplayed()));
        onView(withId(R.id.soyCheckBox))
                .check(matches(isDisplayed()));
        onView(withId(R.id.wheatCheckBox))
                .check(matches(isDisplayed()));
        onView(withId(R.id.eggCheckBox))
                .check(matches(isDisplayed()));

    }

}