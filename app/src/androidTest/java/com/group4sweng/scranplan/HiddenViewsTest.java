package com.group4sweng.scranplan;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HiddenViewsTest {

    @Before
    public void setUp() throws InterruptedException {

        // Test credentials & keyword
        String TEST_EMAIL = "jb2200@york.ac.uk";
        String TEST_PASSWORD = "password";
        String KEYWORD = "AccioLogin2020";
        int THREAD_SLEEP_TIME = 4000; // Time to sleep in milliseconds

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

        RecordedEspressoHelper.openSideBar(RecordedEspressoHelper.SideBarElement.EDIT_PROFILE);

        Thread.sleep(THREAD_SLEEP_TIME/4);

        onView(withId(R.id.settings_input_username))
                .perform(clearText())
                .perform(typeText(KEYWORD));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.settings_save_settings))
                .perform(scrollTo())
                .perform(click());

        Thread.sleep(THREAD_SLEEP_TIME/4);
    }

    @Test
    public void testGoesToView() {
    }

}
