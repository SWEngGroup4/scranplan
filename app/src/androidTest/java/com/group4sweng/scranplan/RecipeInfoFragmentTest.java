package com.group4sweng.scranplan;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class RecipeInfoFragmentTest  {

    //  Android Log tag.
    String TAG = "profileSettingsTest";

    private UserInfoPrivate testUser;

    //  Default test values.
    private static final String TEST_EMAIL = "823513405@qq.com";
    private static String TEST_PASSWORD = "123456";

    //  How long we should sleep when waiting for Firebase information to update. Increase this value if you have a slower machine or emulator.
    private static final int THREAD_SLEEP_TIME = 4000;

    @Rule
    public ActivityTestRule<ProfileSettings> mActivityTestRule = new ActivityTestRule<ProfileSettings>(ProfileSettings.class);

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

    @Test
    public void favourite_recipe_button_can_work() throws InterruptedException {
        Thread.sleep(THREAD_SLEEP_TIME);

        onView(withId(R.id.topLayout)).
                perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        onView(withId(R.id.addFavorite)).
                perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        onView(withId(R.id.addFavorite)).
                perform(click());

        Thread.sleep(THREAD_SLEEP_TIME);

        onView(withId(R.id.addFavorite)).
                perform(click());
    }

    @Test
    public void favourite_recipe_can_be_added() throws InterruptedException{
        Thread.sleep(THREAD_SLEEP_TIME);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
