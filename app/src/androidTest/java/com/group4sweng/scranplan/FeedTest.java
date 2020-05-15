package com.group4sweng.scranplan;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.group4sweng.scranplan.Credentials;
import com.group4sweng.scranplan.EspressoHelper;
import com.group4sweng.scranplan.Home;
import com.group4sweng.scranplan.Login;
import com.group4sweng.scranplan.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.group4sweng.scranplan.EspressoHelper.navigateToRecipe;

/**
 *
 * Test the Feed Fragment.
 * Author(s): LNewman
 * (c) CoDev 2020
 *
 *  Tests are included to make sure information is displayed, user can successfully after search
 *  parameters and search for the meals they want.
 */
@RunWith(AndroidJUnit4.class)
public class FeedTest {

    //  Android Log tag.
    String TAG = "FeedTest";


    //  Default test values
    private static final String TEST_EMAIL = "lifn501@york.ac.uk";
    private static String TEST_PASSWORD = "password";

    //  How long we should sleep when waiting for Firebase information to update. Increase this value if you have a slower machine or emulator.
    private static final int THREAD_SLEEP_TIME = 4000;

    @Rule
    public ActivityTestRule<Home> mActivityTestRule = new ActivityTestRule<Home>(Home.class);

    //  Login with the associated test credentials before testing, wait for Firebase to update and enter profile settings.
    @Before
    public void setUp() throws InterruptedException {

        Log.i(TAG, "Starting tests");

        ActivityScenario.launch(Login.class); //Launch the login screen


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

        onView(withText("FEED"))
                .perform(click());

    }

//    // Check search field can be activated and searched in, also testing results.
//    @Test
//    public void addRecipe() throws InterruptedException {
//        Log.d(TAG, "Testing navigate to add recipe");
//
//        onView(withId(R.id.postBodyInput))
//                .perform(typeText(TEST_EMAIL));
//        Espresso.closeSoftKeyboard();
//
//        Thread.sleep(THREAD_SLEEP_TIME/4);
//
//        onView(withId(R.id.recipeIcon))
//                .perform(click());
//        Thread.sleep(THREAD_SLEEP_TIME/4);
//        onView(withId(R.id.menuSearch)).check(
//                matches(isDisplayed()));
//        onView(withText("Trending")).check(
//                matches(isDisplayed()));
//    }

    // Check search field can be activated and searched in, also testing results.
    @Test
    public void testPostAndDel() throws InterruptedException {
        Log.d(TAG, "Testing navigate to add recipe");

        onView(withId(R.id.postBodyInput))
                .perform(typeText(TEST_EMAIL));
        Espresso.closeSoftKeyboard();

        Thread.sleep(THREAD_SLEEP_TIME/4);

        onView(withId(R.id.recipeIcon))
                .perform(click());
        Thread.sleep(THREAD_SLEEP_TIME/4);
        onView(withId(R.id.menuSearch)).check(
                matches(isDisplayed()));
        navigateToRecipe("Braised peas with bacon, lentils and cod");
        Thread.sleep(THREAD_SLEEP_TIME);
        onView(withText("Add")).perform(click());

//
//        Thread.sleep(THREAD_SLEEP_TIME/4);
//        onView(withId(R.id.recipeIcon))
//                .perform(click());
//
//        Thread.sleep(THREAD_SLEEP_TIME);
//
//        onView(withId(R.id.menuSearch)).perform(click());
//
//
//        onView(isAssignableFrom(SearchView.class))
//                .perform(typeSearchViewText("bacon"))
//                .perform(pressKey(KeyEvent.KEYCODE_ENTER));
//
//
//        Thread.sleep(THREAD_SLEEP_TIME/4);
//
////        onView(withText("Braised peas with bacon, lentils and cod"))
////                .check(matches(isDisplayed()));
//
//        onView(withText("Braised peas with bacon, lentils and cod"))
//                .perform(click());
//
//        onView(withId(R.id.recipeIcon))
//                .perform(click());
//
//        Thread.sleep(THREAD_SLEEP_TIME);
//
////        onView(withId(R.id.recipeListCardView))
////                .perform(click());
//
//        onView(withText("Braised"))
//                .perform(click());
//
//        Thread.sleep(THREAD_SLEEP_TIME);
//
//        onView(withText("ADD"))
//                .perform(click());
//
//        Thread.sleep(THREAD_SLEEP_TIME);

//        onView(withId(R.id.sendPostButton))
//                .perform(click());
//
//        Thread.sleep(THREAD_SLEEP_TIME);
//
//
//        onView(withText(TEST_EMAIL))
//                .perform(click());
//
//        onView(withId(R.id.postMenu))
//                .perform(click());
//
//        Thread.sleep(THREAD_SLEEP_TIME);
//
//        onView(withText("Delete"))
//                .perform(click());
//
//        Thread.sleep(THREAD_SLEEP_TIME);
//
//        Espresso.pressBack();

    }




    @After
    public void finishOff() {
    }
}