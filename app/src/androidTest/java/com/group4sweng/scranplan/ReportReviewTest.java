package com.group4sweng.scranplan;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
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

public class ReportReviewTest implements Credentials {

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

        onView(withId(R.id.postMenu)).
                perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        ViewInteraction appCompatTextView = onView(
                allOf(withId(android.R.id.title), withText("Report comment"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button2), withText("Cancel"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatButton4.perform(scrollTo(), click());

        Thread.sleep(THREAD_SLEEP_TIME);

        ViewInteraction appCompatImageButton6 = onView(
                allOf(withId(R.id.postMenu),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatImageButton6.perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(android.R.id.title), withText("Report comment"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView2.perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(android.R.id.button1), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton5.perform(scrollTo(), click());

        Thread.sleep(THREAD_SLEEP_TIME);

        ViewInteraction editText = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.custom),
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0)),
                        0),
                        isDisplayed()));
        editText.perform(replaceText("test"), closeSoftKeyboard());

        Thread.sleep(THREAD_SLEEP_TIME);

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(android.R.id.button1), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton6.perform(scrollTo(), click());

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
