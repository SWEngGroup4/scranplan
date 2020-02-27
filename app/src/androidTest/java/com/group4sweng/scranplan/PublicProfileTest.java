package com.group4sweng.scranplan;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class PublicProfileTest {
    private PublicProfile mActivity = null;

    FirebaseApp testApp;
    FirebaseAuth testAuth;
    UserInfoPrivate testUser;

    Context mContext;

    //  Default test values.
    private static final String TEST_EMAIL = "jb2200@york.ac.uk";
    private static String TEST_PASSWORD = "password";
    private static final int THREAD_SLEEP_TIME = 4000;

    PublicProfile mPublicProfile;

    @Rule
    public ActivityTestRule<PublicProfile> mActivityTestRule = new ActivityTestRule<PublicProfile>(PublicProfile.class);

    @Before
    public void setUp() throws InterruptedException {

        System.out.println("Happens before stuff!");

        ActivityScenario.launch(Login.class);

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

    @Test
    public void testUsernameAndAboutMeRetrieved() {
        onView(allOf(withId(R.id.public_profile_button), withText("public profile")))
                .perform(click());

        onView(withId(R.id.public_profile_username))
                .check(matches(isDisplayed()));

        onView(withId(R.id.public_profile_about_me))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testRecipesRetrieved(){
        onView(allOf(withId(R.id.public_profile_button), withText("public profile")))
                .perform(click());

        onView(withId(R.id.public_profile_recipes))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAllergensRetrieved(){
        onView(allOf(withId(R.id.public_profile_button), withText("public profile")))
                .perform(click());

        onView(withId(R.id.public_profile_allergens))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testFullProfileLoad(){
        onView(allOf(withId(R.id.public_profile_button), withText("public profile")))
                .perform(click());

        onView(withId(R.id.public_profile_username))
                .check(matches(isDisplayed()));

        onView(withId(R.id.public_profile_about_me))
                .check(matches(isDisplayed()));

        onView(withId(R.id.public_profile_recipes))
                .check(matches(isDisplayed()));

        onView(withId(R.id.public_profile_allergens))
                .check(matches(isDisplayed()));
    }
}
