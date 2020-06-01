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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/** Test the Planner Fragment.
 *
 *  -- USER STORY TESTS LINKED WITH ---
 *  C4 , C18 (includes searching)
 */
@RunWith(AndroidJUnit4.class)
public class PlannerFragmentTest extends EspressoHelper implements Credentials {

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

    //Checks correct elements appear when add button is pressed
    @Test
    public void checkAddButton() throws InterruptedException {
        Log.d(TAG, "Testing add button");

        onView(withText("Meal Planner")).perform(click());
        onView(withId(2)).perform(longClick());
        Thread.sleep(THREAD_SLEEP_TIME / 4);
        onView(withId(2)).perform(click());

        onView(withId(R.id.menuSearch)).check(
                matches(isDisplayed()));
        onView(withText("Trending")).check(
                matches(isDisplayed()));
    }

    //Checks searching for a recipe and adding it to the planner is performed correctly
    @Test
    public void searchAndAddToPlanner() throws InterruptedException {
        Log.d(TAG, "Testing searching");
        mActivityTestRule.getActivity().resetFilters();
        onView(withText("Meal Planner")).perform(click());
        onView(withId(4)).perform(longClick());
        Thread.sleep(THREAD_SLEEP_TIME / 4);
        onView(withId(4)).perform(click());
        Thread.sleep(THREAD_SLEEP_TIME / 4);
        navigateToRecipe("Hearty pasta soup");
        onView(withId(R.id.LetsCook)).perform(click());
    }

    //Checks meal planner saves for user after re-logging in
    @Test
    public void checkMealPlannerSaves() throws InterruptedException {
        Log.d(TAG, "Testing saving");

        onView(withText("Meal Planner")).perform(click());
        onView(withId(4)).perform(click());
        onView(withId(R.id.LetsCook))
                .check(matches(isDisplayed()));
    }

    //Checks long pressing a recipe removes it from the planner
    @Test
    public void removeItem() {
        Log.d(TAG, "Testing removal");

        onView(withText("Meal Planner")).perform(click());
        onView(withId(0)).perform(longClick());
        onView(withId(1)).perform(longClick());
        onView(withId(2)).perform(longClick());
        onView(withId(0)).perform(click());
        onView(withId(R.id.menuSearch)).check(
                matches(isDisplayed()));
    }

    //Checks adding breakfast to the planner is performed correctly
    @Test
    public void breakfastAddToPlanner() throws InterruptedException {
        Log.d(TAG, "Testing dinner");

        onView(withText("Meal Planner")).perform(click());
        onView(withId(0)).perform(longClick());
        Thread.sleep(THREAD_SLEEP_TIME/4);
        onView(withId(0)).perform(click());
        Thread.sleep(THREAD_SLEEP_TIME/4);
        navigateToRecipe("Bacon sandwich");
        onView(withText("Add")).perform(click());
    }

    //Checks adding lunch to the planner is performed correctly
    @Test
    public void lunchAddToPlanner() throws InterruptedException {
        Log.d(TAG, "Testing dinner");

        onView(withText("Meal Planner")).perform(click());
        onView(withId(1)).perform(longClick());
        Thread.sleep(THREAD_SLEEP_TIME/4);
        onView(withId(1)).perform(click());
        Thread.sleep(THREAD_SLEEP_TIME/4);

        onView(withText("Avocado and black bean eggs")).perform(click());
        onView(withText("Add")).perform(click());
    }

    //Checks adding dinner to the planner is performed correctly
    @Test
    public void dinnerAddToPlanner() throws InterruptedException {
        Log.d(TAG, "Testing dinner");

        onView(withText("Meal Planner")).perform(click());
        onView(withId(2)).perform(longClick());
        Thread.sleep(THREAD_SLEEP_TIME/4);
        onView(withId(2)).perform(click());
        Thread.sleep(THREAD_SLEEP_TIME/4);

        onView(withText("Avocado and black bean eggs")).perform(click());
        onView(withText("Add")).perform(click());
    }

    @After
    public void tearDown() {
        this.mActivityTestRule.getActivity().resetFilters();
        EspressoHelper.shouldSkip = false;
        this.mActivityTestRule.finishActivity();
        Log.d(TAG, "Tests complete"); }
}
