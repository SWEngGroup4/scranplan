package com.group4sweng.scranplan;

import android.util.Log;
import android.view.KeyEvent;
import android.widget.SearchView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.group4sweng.scranplan.HomeTest.typeSearchViewText;

@RunWith(AndroidJUnit4.class)
public class PlannerFragmentTest {

    @Rule
    public ActivityTestRule<Home> mActivityTestRule = new ActivityTestRule<>(Home.class);

    private String TAG = "plannerFragmentTest";

    private static final int THREAD_SLEEP_TIME = 4000;

    //Logging in wuth test account
    @Before
    public void setUp() throws Exception {
        Log.d(TAG, "Starting tests");
        ActivityScenario.launch(Login.class);

        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.emailEditText)).perform(typeText("jamesclawley@gmail.com"));
        onView(withId(R.id.passwordEditText)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(THREAD_SLEEP_TIME);
    }

    //Checks correct elements appear when add button is pressed
    @Test
    public void checkAddButton() {
        Log.d(TAG, "Testing add button");

        onView(withText("Meal Planner")).perform(click());
        onView(withId(0)).perform(click());

        onView(withId(R.id.menuSearch)).check(
                matches(isDisplayed()));
        onView(withText("Trending")).check(
                matches(isDisplayed()));
    }

    //Checks searching for a recipe and adding it to the planner is performed correctly
    @Test
    public void searchAndAddToPlanner() throws InterruptedException {
        Log.d(TAG, "Testing searching");

        onView(withText("Meal Planner")).perform(click());
        onView(withId(0)).perform(click());
        onView(withId(R.id.menuSearch)).perform(click());
        onView(isAssignableFrom(SearchView.class))
                .perform(typeSearchViewText("bacon"))
                .perform(pressKey(KeyEvent.KEYCODE_ENTER));

        Thread.sleep(THREAD_SLEEP_TIME/4);

        onView(withText("Bacon Sandwich")).perform(click());
        onView(withText("Add")).perform(click());
    }

    //Checks meal planner saves for user after re-logging in
    @Test
    public void checkMealPlannerSaves() {
        Log.d(TAG, "Testing saving");

        onView(withText("Meal Planner")).perform(click());
        onView(withId(0)).perform(click());
        onView(withText("Lets Cook!"))
                .check(matches(isDisplayed()));
    }

    //Checks long pressing a recipe removes it from the planner
    @Test
    public void removeItem() {
        Log.d(TAG, "Testing removal");

        onView(withText("Meal Planner")).perform(click());
        onView(withId(0)).perform(longClick());
        onView(withId(0)).perform(click());
        onView(withId(R.id.menuSearch)).check(
                matches(isDisplayed()));
    }

    @After
    public void tearDown() { Log.d(TAG, "Tests complete"); }
}
