package com.group4sweng.scranplan.Presentation;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

/**
 * Tests that the timer view is displayed properly for each slide within a test presentation.
 * Also that the timer updates according to it being played.
 * Author: JButler
 * (c) CoDev 2020
 *
 * IMPORTANT: The following may not work if profile filters are included that exclude the recipe search criteria for the chosen test recipe.
 *
 *  -- USER STORY TESTS LINKED WITH ---
 *  C21, Audio/timer
 **/
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TimerTest extends EspressoHelper implements Credentials {


    //  Test recipe to search for and compare our results to.
    private static final String TEST_RECIPE = "Ultimate spaghetti carbonara recipe";

    //  Timer interval in milliseconds corresponding to each slide from start to finish. E.g. slide 1 = index 0.
    //  -1 means no timer is present.
    private static final int[] TEST_RECIPE_TIMER_VALUES = {-1, -1, -1, 600000, -1, -1, 300000, -1, -1, -1, -1, -1};


    @Rule
    public ActivityTestRule<Home> mActivityTestRule = new ActivityTestRule<Home>(Home.class);

    //  Login, search for the appropriate recipe and open the presentation.
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

        Thread.sleep(THREAD_SLEEP_TIME/4);

        EspressoHelper.navigateToRecipe(TEST_RECIPE);

        onView(withText("LETS COOK!"))
                .perform(click());

        Thread.sleep(THREAD_SLEEP_TIME/2);
    }

    //  Test that a timer is not displayed on the first slide for the test recipe.
    @Test
    public void testTimerNotVisibleOnFirstSlide() throws InterruptedException {
        onView(withId(R.id.timerLayout))
                .check(matches(not(isDisplayed())));
    }

    //  Cycle through all available slides to check if a timer should be displayed.
    @Test
    public void testTimerVisibilityOnAllSlides() {
        for(int timerValue : TEST_RECIPE_TIMER_VALUES){
            if(timerValue == -1){
                onView(withId(R.id.timerLayout))
                        .check(matches(not(isDisplayed())));
            } else {
                onView(withId(R.id.timerLayout))
                        .check(matches(isDisplayed()));
            }
            onView(withId(R.id.nextButton))
                    .perform(click());
        }
    }

    //  Method that allows us to repeatedly press the 'next' button when moving between slides.
    private void nextSlideRepeat(int amount){
        for(int i=0; i < amount; i++){
            onView(withId(R.id.nextButton))
                    .perform(click());
        }
    }

    //  Check that for the first timer all elements including icons, start, stop times and a progress bar are displayed.
    @Test
    public void testAllTimerElementsAreDisplayed() {
        nextSlideRepeat(3); // Navigate to the fourth slide.

        onView(withId(R.id.timer_play_pause))
                .check(matches(isDisplayed()));
        onView(withId(R.id.timer_progress))
                .check(matches(isDisplayed()));
        onView(withId(R.id.timer_icon))
                .check(matches(isDisplayed()));
        onView(withId(R.id.current_duration_text))
                .check(matches(isDisplayed()));
        onView(withId(R.id.final_duration_text))
                .check(matches(isDisplayed()));
    }

    //  Check that the initial start and end times displayed for the timers match what should be present for each slide.
    @Test
    public void testCurrentAndEndTimeMatch() {
        for(int timerValue : TEST_RECIPE_TIMER_VALUES){
            if(timerValue != -1){  // Timer exists.

                //  Grab the expected printable format.
                String initialTime = PresentationTimer.printOutputTime(0);
                String finalTime = PresentationTimer.printOutputTime(timerValue);

                onView(withId(R.id.current_duration_text))
                        .check(matches(withText(initialTime)));

                onView(withId(R.id.final_duration_text))
                        .check(matches(withText(finalTime)));
            }
            onView(withId(R.id.nextButton))
                    .perform(click());
        }
    }

    /*  Checks if the timer has started based on feedback from the current time displayed in minutes:seconds.
        Does not check if audio is playing, only if the timer works. Audio tests are in 'AudioTest' and SoundHandler > AudioURLTest.
     */
    @Test
    public void testStartTimerCountdown() throws InterruptedException {
        nextSlideRepeat(3); // Nav to the 4th slide.

        //  Start playback.
        onView(withId(R.id.timer_play_pause))
                .perform(click());

        //  Wait...
        Thread.sleep(THREAD_SLEEP_TIME/2);

        //  Check the timer has started counting down.
        onView(withId(R.id.current_duration_text))
                .check(matches(not(withText("00:00"))));
    }

    //  Test that the timer is able to reset when switching between slides.
    @Test
    public void testTimerResets() throws InterruptedException {
        nextSlideRepeat(3);

        onView(withId(R.id.timer_play_pause)) // Play timer.
                .perform(click());

        Thread.sleep(THREAD_SLEEP_TIME/2); // Wait

        nextSlideRepeat(3); // Move to the 7th slide.

        //  Check that the timer on the 7th slide matches the expected initial values.
        //  In other words the previous timer has reset.
        onView(withId(R.id.current_duration_text))
                .check(matches(isDisplayed()))
                .check(matches(withText("00:00")));

        onView(withId(R.id.final_duration_text))
                .check(matches(isDisplayed()))
                .check(matches(withText(PresentationTimer.printOutputTime(TEST_RECIPE_TIMER_VALUES[6]))));
    }
    @After
    public void tearDown() throws Exception {
        mActivityTestRule.getActivity().resetFilters();
        mActivityTestRule.finishActivity();
        EspressoHelper.shouldSkip = false;
    }
}
