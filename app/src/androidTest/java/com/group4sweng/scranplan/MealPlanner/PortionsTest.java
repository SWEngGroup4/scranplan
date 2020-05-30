package com.group4sweng.scranplan.MealPlanner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;

import com.group4sweng.scranplan.Credentials;
import com.group4sweng.scranplan.EspressoHelper;
import com.group4sweng.scranplan.Login;
import com.group4sweng.scranplan.R;

import org.junit.Before;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


/** UI Tests for portions conversion
 * Author: JButler
 * (c) CoDev 2020
 *
 *  ===MANUAL TESTS===
 *  Espresso can't find text within the Framelayout > LinearLayout ingredients view so couldn't check automatically for
 *  ingredients conversions. This is mainly checked through the Portions JUnit tests however.
 *
 *  Despite this the following manual tests have still been conducted:
 *  - Ingredients list displays warnings for appropriate ingredients when changing portions.
 *  - Portion conversions are displayed properly within the LinearLayout.
 *
 * -- USER STORY TESTS LINKED WITH ---
 *   C34 , A14
 **/
public class PortionsTest implements Credentials {

    private String TAG = "portionsTest";
    private static int THREAD_SLEEP_TIME = 4000;
    private final String TEST_RECIPE = "Slow cooker spiced root and lentil casserole"; //

    /*  Login > Switch to Mealplanner Tab > Remove existing recipe at top left > Add recipe at top left
        Search for test recipe > Add recipe > Return to Mealplanner.
     */
    @Before
    public void setUp() throws InterruptedException {
        EspressoHelper.shouldSkip = false;

        ActivityScenario.launch(Login.class); //Launch the login screen

        onView(ViewMatchers.withId(R.id.loginButton))
                .perform(click());

        onView(withId(R.id.emailEditText))
                .perform(typeText(TEST_EMAIL));
        onView(withId(R.id.passwordEditText))
                .perform(typeText(TEST_PASSWORD));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.loginButton))
                .perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        onView(withText("Meal Planner")).perform(click());
        onView(withId(0)).perform(longClick());
        Thread.sleep(THREAD_SLEEP_TIME/4);
        onView(withId(0)).perform(click());

        Thread.sleep(THREAD_SLEEP_TIME/4);

        EspressoHelper.navigateToRecipe(TEST_RECIPE);
        Thread.sleep(THREAD_SLEEP_TIME/4);
        onView(withText("ADD")).perform(click());

        Espresso.pressBack();
    }

    //  Checks change portions button is visible, clickable and is completely displayed.
    @Test
    public void testChangePortionsDisplayed(){
        onView(withId(0)).perform(click());

        onView(withId(R.id.changePortions))
                .check(matches(isDisplayed()))
                .check(matches(isCompletelyDisplayed()))
                .check(matches(isClickable()));
    }

    //  Tests dialog displays upon 'Change portion" button press.
    @Test
    public void testDialogDisplays() {
        onView(withId(0)).perform(click());

        onView(withId(R.id.changePortions))
                .perform(click());

        onView(withText("Change Portion Amounts"))
                .check(matches(isDisplayed()));
    }

    //  Test expected dialog portion amounts are displayed for the given serving amount.
    @Test
    public void testCorrectServings() {
        onView(withId(0)).perform(click());

        onView(withId(R.id.changePortions))
                .perform(click());

        //  Expected serving portion conversion amounts.
        final String[] TEST_RECIPE_SERVINGS = {"1", "2", "6", "10"};

        //  Cycle through each button portion amount in the array.
        for (String portionAmount : TEST_RECIPE_SERVINGS){
            onView(withText(portionAmount))
                    .check(matches(isDisplayed()));
        }

        onView(withText("10")) // Change portions to serves = 10.
                .perform(click());

        onView(withId(R.id.changePortions))
                .perform(click());

        //  New expected serving portion conversion amounts after conversion.
        final String[] NEW_TEST_RECIPE_SERVINGS = {"2", "4", "6"};

        for (String portionAmount : NEW_TEST_RECIPE_SERVINGS){
            onView(withText(portionAmount))
                    .check(matches(isDisplayed()));
        }
    }


}
