package com.group4sweng.scranplan.MealPlanner;

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
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.group4sweng.scranplan.EspressoHelper.navigateToRecipe;

/** Test the Info Planner Fragment.
 * Testing adding to the meal planner
 *
 *  -- USER STORY TESTS LINKED WITH ---
 *  C4 , C14, C22
 */
@RunWith(AndroidJUnit4.class)
public class PlannerInfoFragmentTest implements Credentials {

    @Rule
    public ActivityTestRule<Home> mActivityTestRule = new ActivityTestRule<>(Home.class);

    private String TAG = "plannerFragmentTest";

    private static final int THREAD_SLEEP_TIME = 4000;

    //Logging in wuth test account
    @Before
    public void setUp() throws Exception {
        Log.d(TAG, "Starting tests");
        ActivityScenario.launch(Login.class);

        onView(ViewMatchers.withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.emailEditText)).perform(typeText(TEST_EMAIL));
        onView(withId(R.id.passwordEditText)).perform(typeText(TEST_PASSWORD));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(THREAD_SLEEP_TIME);
    }

    /**
     * Searches for a bacon sandwich and makes sure that all the information is there and that
     * the icons can be clicked to show their data.
     *
     * This branch didn't have the meal planner saving system so meal planner refreshes each time, hence
     * one big test method to test icons.
     * // TODO failing because planner searching fails
     */
    @Test
    public void searchAndAddToPlanner() throws InterruptedException {
        Log.d(TAG, "Testing searching");

        Thread.sleep(THREAD_SLEEP_TIME);

        onView(withText("Meal Planner")).perform(click());
        onView(withId(0)).perform(longClick());
        Thread.sleep(THREAD_SLEEP_TIME/4);
        onView(withId(0)).perform(click());

        navigateToRecipe("Bacon sandwich");

        Thread.sleep(THREAD_SLEEP_TIME);

        //Click image buttons and icons to test data is displayed from the searching screen

        onView(withId(R.id.reheatInfoButton)).perform(click());

        onView(isRoot()).perform(pressBack());

        onView(withId(R.id.recipeInfoEggs)).perform(click());

        onView(isRoot()).perform(pressBack());

        onView(withId(R.id.recipeInfoWheat)).perform(click());

        onView(isRoot()).perform(pressBack());

        onView(withText("Add")).perform(click());

        onView(isRoot()).perform(pressBack());

        //Click image buttons and icons to test data is displayed from the meal planner screen

        onView(withId(0)).perform(click());

        onView(withId(R.id.reheatInfoButton)).perform(click());

        onView(isRoot()).perform(pressBack());

        onView(withId(R.id.recipeInfoEggs)).perform(click());

        onView(isRoot()).perform(pressBack());

        onView(withId(R.id.recipeInfoWheat)).perform(click());

        onView(isRoot()).perform(pressBack());

    }

    @After
    public void tearDown() {
            EspressoHelper.shouldSkip = false;
            this.mActivityTestRule.finishActivity();
        Log.d(TAG, "Tests complete"); }
}