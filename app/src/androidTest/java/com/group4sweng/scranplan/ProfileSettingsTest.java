package com.group4sweng.scranplan;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileSettingsTest {

    private ProfileSettings mActivity = null;

    String TAG = "profileSettingsTest";

    FirebaseApp testApp;
    FirebaseAuth testAuth;
    UserInfoPrivate testUser;

    //  Default test values.
    private static final String TEST_EMAIL = "jb2200@york.ac.uk";
    private static String TEST_PASSWORD = "password";

    ProfileSettings mProfileSettings;

    @Rule
    public ActivityTestRule<ProfileSettings> mActivityTestRule = new ActivityTestRule<ProfileSettings>(ProfileSettings.class);

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

        Thread.sleep(4000);
    }

    @Test
    public void testUsernameLoads() {
        onView(allOf(withId(R.id.profile_settings_button), withText("profile settings")))
                .perform(click());

        onView(withId(R.id.settings_username))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testTablesLoad() {
        onView(allOf(withId(R.id.profile_settings_button), withText("profile settings")))
                .perform(click());

        onView(withId(R.id.settings_privacy))
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_allergens))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testTextInputsFocusable() {
        onView(allOf(withId(R.id.profile_settings_button), withText("profile settings")))
                .perform(click());

        onView(withId(R.id.settings_input_about_me))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasFocus()));

        onView(withId(R.id.settings_input_username))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasFocus()));
    }

    @Test
    public void testFullProfileLoads(){
        onView(allOf(withId(R.id.profile_settings_button), withText("profile settings")))
                .perform(click());

        onView(withId(R.id.settings_username))
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_about_me))
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_allergens))
                .check(matches(isDisplayed()));

        //Testing a single allegern checkbox. All checkboxes are the same
        onView(withId(R.id.settings_allergy_eggs))
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_delete_profile))
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_input_about_me))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasFocus()));

        onView(withId(R.id.settings_input_username))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasFocus()));

        onView(withId(R.id.settings_privacy_profile_image))
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_privacy))
                .check(matches(isDisplayed()));

        //Testing a single privacy switch. All switches are the same
        onView(withId(R.id.settings_privacy_about_me))
                .check(matches(isDisplayed()));

    }

    // TODO Replace temporary implementation of Profile Settings info button

    @Test
    public void testUsernameRetrieved() {

        onView(allOf(withId(R.id.profile_settings_button), withText("profile settings")))
                .perform(click());

        testUser = (UserInfoPrivate) mActivityTestRule.getActivity().getIntent().getSerializableExtra("user");


        onView(withId(R.id.settings_input_username))
                .check(matches(withText(testUser.getDisplayName())));
    }

    @Test
    public void testAboutMeAndNumRecipesRetrieved() {
        onView(allOf(withId(R.id.profile_settings_button), withText("profile settings")))
                .perform(click());

        testUser = (UserInfoPrivate) mActivityTestRule.getActivity().getIntent().getSerializableExtra("user");

        onView(withId(R.id.settings_input_about_me))
                .check(matches(withText(testUser.getAbout())));

        onView(withId(R.id.public_profile_recipes))
                .check(matches(withText(String.valueOf(testUser.getNumRecipes()))));

    }

    @Test
    public void testFiltersRetrieved() throws InterruptedException {
        Preferences testPref = new Preferences(true, false, true, false, true, false);
        onView(allOf(withId(R.id.profile_settings_button), withText("profile settings")))
                .perform(click());

        testUser = (UserInfoPrivate) mActivityTestRule.getActivity().getIntent().getSerializableExtra("user");
        testUser.setPreferences(testPref);
        //mActivityTestRule.getActivity().updatePreferences();


        onView(withId(R.id.settings_allergy_nuts))
                .check(matches(isChecked()));
        onView(withId(R.id.settings_allergy_eggs))
                .check(matches(isNotChecked()));
        onView(withId(R.id.settings_allergy_milk))
                .check(matches(isChecked()));
        onView(withId(R.id.settings_allergy_shellfish))
                .check(matches(isNotChecked()));
        onView(withId(R.id.settings_allergy_soy))
                .check(matches(isChecked()));
        onView(withId(R.id.settings_allergy_wheat))
                .check(matches(isNotChecked()));
    }


}
