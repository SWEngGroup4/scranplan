package com.group4sweng.scranplan.RecipeInfo;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.group4sweng.scranplan.Credentials;
import com.group4sweng.scranplan.Login;
import com.group4sweng.scranplan.ProfileSettings;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/** Test Recipe Info Fragment
 *
 *  -- USER STORY TESTS LINKED WITH ---
 *  B1, B2, B3, B4 , B6, C8
 *
 */
public class RecipeInfoFragmentTest implements Credentials {

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
}
