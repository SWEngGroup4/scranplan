package com.group4sweng.scranplan.RecipeInfo;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.group4sweng.scranplan.Credentials;
import com.group4sweng.scranplan.EspressoHelper;
import com.group4sweng.scranplan.Login;
import com.group4sweng.scranplan.ProfileSettings;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.group4sweng.scranplan.EspressoHelper.childAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/** Test Recipe Info Fragment
 *
 *  -- USER STORY TESTS LINKED WITH ---
 *  B1, B2, B3, B4 , B6, C8
 *
 */
public class RecipeReviewTest implements Credentials {

    //  Android Log tag.
    String TAG = "profileSettingsTest";

    private UserInfoPrivate testUser;

    //  How long we should sleep when waiting for Firebase information to update. Increase this value if you have a slower machine or emulator.
    private static final int THREAD_SLEEP_TIME = 4000;

    @Rule
    public ActivityTestRule<ProfileSettings> mActivityTestRule = new ActivityTestRule<ProfileSettings>(ProfileSettings.class);

    //  Login with the associated test credentials before testing, wait for Firebase to update and enter profile settings.
    @Before
    public void setUp() throws InterruptedException {

        Log.i(TAG, "Starting tests");

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
    }

    @Test
    public void fragment_can_be_instantiated() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }


    //Manually tested loading a picture as the emulator doesn't have pics to upload
    @Test
    public void createReview() throws InterruptedException {
        Thread.sleep(THREAD_SLEEP_TIME);

        onView(withId(R.id.topLayout)).
                perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        ViewInteraction tabView = onView(
                allOf(withContentDescription("Reviews"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tabLayout2),
                                        0),
                                1),
                        isDisplayed()));
        tabView.perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.ReturnButton),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.core.widget.NestedScrollView")),
                                        0),
                                18),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        onView(withId(R.id.topLayout)).
                perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.ReturnButton),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.core.widget.NestedScrollView")),
                                        0),
                                18),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        onView(withId(R.id.topLayout)).
                perform(click());

        ViewInteraction tabView2 = onView(
                allOf(withContentDescription("Reviews"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tabLayout2),
                                        0),
                                1),
                        isDisplayed()));
        tabView2.perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.postBodyInput),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.addPostLinearLayout),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("great"), closeSoftKeyboard());

        onView(withId(R.id.postRecipeRating)).
                perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.sendPostButton), withText("Post"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.addPostLinearLayout),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton3.perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.ReturnButton),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.core.widget.NestedScrollView")),
                                        0),
                                18),
                        isDisplayed()));
        appCompatImageButton3.perform(click());
    }

    @After
    public void tearDown() throws Exception {
        EspressoHelper.shouldSkip = false;
        this.mActivityTestRule.finishActivity();
    }
}
