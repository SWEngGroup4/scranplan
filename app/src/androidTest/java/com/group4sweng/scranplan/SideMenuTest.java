package com.group4sweng.scranplan;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/***
 * Testing the side menu
 * -- USER STORY TESTS LINKED WITH ---
 * C35
 */

public class SideMenuTest implements Credentials{
    private MainActivity mMainActivity = null;
    FirebaseApp testApp;
    FirebaseAuth testAuth;
    Context mContext;
    
    private static final int THREAD_SLEEP_TIME = 9000;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() throws InterruptedException {
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
    

}