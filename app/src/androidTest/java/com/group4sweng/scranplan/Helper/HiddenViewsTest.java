package com.group4sweng.scranplan.Helper;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.group4sweng.scranplan.Credentials;
import com.group4sweng.scranplan.EspressoHelper;
import com.group4sweng.scranplan.Login;
import com.group4sweng.scranplan.R;

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
public class HiddenViewsTest implements Credentials {

    @Before
    public void setUp() throws InterruptedException {

        // Test credentials & keyword
        String KEYWORD = "AccioLogin2020";
        int THREAD_SLEEP_TIME = 4000; // Time to sleep in milliseconds

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

        EspressoHelper.openSideBar(EspressoHelper.SideBarElement.EDIT_PROFILE);

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
