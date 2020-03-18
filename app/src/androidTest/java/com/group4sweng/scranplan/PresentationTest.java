package com.group4sweng.scranplan;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SearchView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/** Test the Presentation Activity.
 *  Tests are included to make sure information is displayed, user can flick through and see comments,
 *  add comments and delete comments.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PresentationTest {

    //  Android Log tag.
    String TAG = "homeTest";

    private UserInfoPrivate testUser;

    //  Default test values.
    private static final String TEST_EMAIL = "louisnewman@live.co.uk";
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

    }

    // Checking all parts of the slide comments
    @Test
    public void testBaconComment() throws InterruptedException {
        onView(withId(R.id.menuSearch)).perform(click());
        onView(isAssignableFrom(SearchView.class))
                .perform(typeSearchViewText("bacon"))
                .perform(pressKey(KeyEvent.KEYCODE_ENTER));


        Thread.sleep(THREAD_SLEEP_TIME/4);

        onView(withText("Bacon Sandwich"))
                .perform(click());

        onView(withText("LETS COOK!"))
                .perform(click());



        Thread.sleep(THREAD_SLEEP_TIME/4);

        onView(withText("COMMENTS"))
                .perform(click());


        Thread.sleep(THREAD_SLEEP_TIME/4);

        onView(withId(R.id.addCommentEditText))
                .perform(typeText("bacon"));
        Espresso.closeSoftKeyboard();

        Thread.sleep(THREAD_SLEEP_TIME/4);

        onView(withText("SEND"))
                .perform(click());


        Thread.sleep(THREAD_SLEEP_TIME/4);

        onView(withText("bacon"))
                .check(matches(isDisplayed()));

        onView(withText("bacon"))
                .perform(click());

        onView(withText("Delete"))
                .perform(click());

        onView(withText("COMMENTS"))
                .perform(click());



    }

    // Insert text to search bar
    public static ViewAction typeSearchViewText(final String text){
        return new ViewAction(){
            @Override
            public Matcher<View> getConstraints() {
                //Ensures that only applied when search view is visible
                return allOf(isDisplayed(), isAssignableFrom(SearchView.class));
            }

            @Override
            public String getDescription() {
                return "Change view text";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((SearchView) view).setQuery(text,false);
            }


        };
    }




    @After
    public void finishOff() {
    }
}