package com.group4sweng.scranplan;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for the Profile Settings Activity.
 * Author: JButler
 * (c) CoDev 2020
 *
 *  Tests are included to make sure information is displayed, can be edited and
 *  saved and that spam entries or private email entry is impossible.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileSettingsTest extends RecordedEspressoHelper {

    //  Android Log tag.
    String TAG = "profileSettingsTest";

    private UserInfoPrivate testUser;

    //  Default test values.
    private static final String TEST_EMAIL = "jb2200@york.ac.uk";
    private static String TEST_PASSWORD = "password";

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


        openSideBar(SideBarElement.EDIT_PROFILE);

        Thread.sleep(THREAD_SLEEP_TIME/4);
    }

    //  Used to make sure information is reset before changing it again.
    private void resetBasicInfo(){
        onView(withId(R.id.settings_input_username))
                .perform(clearText())
                .perform(typeText("testUsername"));

        onView(withId(R.id.settings_input_about_me))
                .perform(clearText())
                .perform(typeText("testAboutMe"));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.settings_save_settings))
                .perform(ViewActions.scrollTo())
                .perform(click());
    }

    //  Check a username input field exists.
    @Test
    public void testUsernameLoads() {
        onView(withId(R.id.profile_username))
                .check(matches(isDisplayed()));
    }

    //  Check all filter and privacy tables can be displayed
    @Test
    public void testTablesLoad() {

        onView(withId(R.id.settings_privacy))
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_allegerns))
                .check(matches(isDisplayed()));
    }

    //  Check text input is possible
    @Test
    public void testTextInputsFocusable() {

        onView(withId(R.id.settings_input_about_me))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasFocus()));

        onView(withId(R.id.settings_input_username))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasFocus()));
    }

    //  Test the entire profile is able to load.
    @Test
    public void testFullProfileLoads(){

        onView(withId(R.id.profile_username))
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_about_me))
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_allegerns))
                .check(matches(isDisplayed()));

        //Testing a single allegern checkbox. All checkboxes are the same
        onView(withId(R.id.allergy_eggs))
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_reset_password))
                .perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_delete_profile))
                .perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_save_settings))
                .perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_input_about_me))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasFocus()));

        onView(withId(R.id.settings_input_username))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasFocus()));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.settings_privacy))
                .check(matches(isDisplayed()));

        //Testing a single privacy switch. All switches are the same
        onView(withId(R.id.settings_privacy_about_me))
                .check(matches(isDisplayed()));

    }

    // TODO Replace temporary implementation of Profile Settings info button

    //  Test username displayed matches what is stored in the associated UserInfoPrivate class.
    @Test
    public void testUsernameRetrieved() {

        testUser = (UserInfoPrivate) mActivityTestRule.getActivity().getIntent().getSerializableExtra("user");


        onView(withId(R.id.settings_input_username))
                .check(matches(withText(testUser.getDisplayName())));
    }

    //  Test About me and the number of recipes displayed matches what is stored in the associated UserInfoPrivate class.
    @Test
    public void testAboutMeAndNumRecipesRetrieved() {



        testUser = (UserInfoPrivate) mActivityTestRule.getActivity().getIntent().getSerializableExtra("user");

        onView(withId(R.id.settings_input_about_me))
                .check(matches(withText(testUser.getAbout())));

        String recipesString = "Recipes: " + testUser.getNumRecipes();

        onView(withId(R.id.profile_recipes))
                .check(matches(withText(recipesString)));

    }

    //  Test that upon changing our username and logging out our username is able to be stored and retrieved properly.
    @Test
    public void testUsernameIsStoredAndRetrieved() throws InterruptedException {

        onView(withId(R.id.settings_input_username))
                .perform(clearText())
                .perform(typeText("uniqueUsernameTest"));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.settings_save_settings))
                .perform(ViewActions.scrollTo())
                .perform(click());

        Espresso.pressBack();
        Thread.sleep(THREAD_SLEEP_TIME/4);

        onView(withId(R.id.side_menu))
                .perform();

        setUp();

        onView(withId(R.id.settings_input_username))
                .check(matches(withText("uniqueUsernameTest")));
    }

    //  Test that upon changing our 'about me' input and logging out our info is able to be stored and retrieved properly.
    @Test
    public void testAboutMeIsStoredAndRetrieved() throws InterruptedException {
        onView(withId(R.id.settings_input_about_me))
                .perform(clearText())
                .perform(typeText("uniqueAboutMe"));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.settings_save_settings))
                .perform(ViewActions.scrollTo())
                .perform(click());

        Espresso.pressBack();
        Thread.sleep(THREAD_SLEEP_TIME/4);


        openSideBar(SideBarElement.LOGOUT);

        setUp();

        onView(withId(R.id.settings_input_about_me))
                .perform(ViewActions.scrollTo())
                .check(matches(withText("uniqueAboutMe")));
    }

    /* Retrieves the users current filters and changes all of these filters from the checked value of
        false > true, or equivalently true > false. Makes sure every value is properly updated.
        To test retrieval the test then logs back out and in again to test if the checked value is different to what it was previously. */
    /**
    @Test
    public void testFilterAndPrivacyInfoIsStoredAndRetrieved() throws InterruptedException {
        HashMap<String, Boolean> initialAllergies = new HashMap<>();
        HashMap<String, Boolean> initialPrivacy = new HashMap<>();

        //Thread.sleep(THREAD_SLEEP_TIME/4);
        //ProfileSettings testRule = mActivityTestRule.getActivity();

        //  Store all initial filters + privacy settings in 2 seperate HashMaps.
        initialAllergies.put("eggs",  mActivityTestRule.getActivity().mAllergy_eggs.isChecked());
        initialAllergies.put("milk",  mActivityTestRule.getActivity().mAllergy_milk.isChecked());
        initialAllergies.put("soya",  mActivityTestRule.getActivity().mAllergy_soy.isChecked());
        initialAllergies.put("gluten",  mActivityTestRule.getActivity().mAllergy_gluten.isChecked());
        initialAllergies.put("shellfish",  mActivityTestRule.getActivity().mAllergy_shellfish.isChecked());
        initialAllergies.put("nuts",  mActivityTestRule.getActivity().mAllergy_nuts.isChecked());

        initialPrivacy.put("username", mActivityTestRule.getActivity().mDisplay_username.isChecked());
        initialPrivacy.put("about_me", mActivityTestRule.getActivity().mDisplay_about_me.isChecked());
        initialPrivacy.put("recipes", mActivityTestRule.getActivity().mDisplay_recipes.isChecked());
        initialPrivacy.put("profile_image", mActivityTestRule.getActivity().mDisplay_profile_image.isChecked());
        initialPrivacy.put("filters", mActivityTestRule.getActivity().mDisplay_filters.isChecked());

        onView(withId(R.id.profile_about_me))
                .perform(scrollTo());

        //  Change every switch and Checkboxes value.
        onView(withId(R.id.allergy_soy))
                .perform(click());
        onView(withId(R.id.dietary_vegetarian))
                .perform(click());
        onView(withId(R.id.allergy_shellfish))
                .perform(click());
        onView(withId(R.id.allergy_wheat))
                .perform(click());
        onView(withId(R.id.allergy_eggs))
                .perform(click());
        onView(withId(R.id.dietary_vegan))
                .perform(click());

        onView(withId(R.id.settings_privacy_about_me))
                .perform(click());
        onView(withId(R.id.settings_privacy_profile_image))
                .perform(click());
        onView(withId(R.id.settings_privacy_username))
                .perform(click());
        onView(withId(R.id.settings_privacy_recipes))
                .perform(click());
        onView(withId(R.id.settings_privacy_filters))
                .perform(click());

        onView(withId(R.id.settings_save_settings))
                .perform(scrollTo())
                .perform(click());

        Espresso.pressBack();
        Thread.sleep(THREAD_SLEEP_TIME/4);

        openSideBar(SideBarElement.LOGOUT);

        setUp(); // Relaunch the login screen.

        //ProfileSettings newTestRule = mActivityTestRule.getActivity(); // Initiate a new test rule based on the current activity state.

        onView(withId(R.id.profile_about_me))
                .perform(scrollTo());

        //  Check the new 'checked' boolean value has changed.
        assertNotEquals(initialAllergies.get("eggs"), mActivityTestRule.getActivity().mAllergy_eggs.isChecked());
        assertNotEquals(initialAllergies.get("nuts"), mActivityTestRule.getActivity().mAllergy_nuts.isChecked());
        assertNotEquals(initialAllergies.get("milk"), mActivityTestRule.getActivity().mAllergy_milk.isChecked());
        assertNotEquals(initialAllergies.get("soya"), mActivityTestRule.getActivity().mAllergy_soy.isChecked());
        assertNotEquals(initialAllergies.get("shellfish"), mActivityTestRule.getActivity().mAllergy_shellfish.isChecked());
        assertNotEquals(initialAllergies.get("gluten"), mActivityTestRule.getActivity().mAllergy_gluten.isChecked());

        assertNotEquals(initialPrivacy.get("about_me"), mActivityTestRule.getActivity().mDisplay_about_me.isChecked());
        assertNotEquals(initialPrivacy.get("recipes"), mActivityTestRule.getActivity().mDisplay_recipes.isChecked());
        assertNotEquals(initialPrivacy.get("username"), mActivityTestRule.getActivity().mDisplay_username.isChecked());
        assertNotEquals(initialPrivacy.get("profile_image"), mActivityTestRule.getActivity().mDisplay_profile_image.isChecked());
        assertNotEquals(initialPrivacy.get("filters"), mActivityTestRule.getActivity().mDisplay_filters.isChecked());

        Thread.sleep(1000);

    }**/

    //  Check that if a user accidentally inputs there email into the Username or About Me section it cannot be saved to Firebase.
    /**
    @Test
    public void testEmailCannotBeDisplayed() throws InterruptedException {

        onView(withId(R.id.settings_input_username))
                .perform(clearText())
                .perform(typeText("Some text " + TEST_EMAIL)); //Test that we are checking for the email within a String of other characters.

        onView(withId(R.id.settings_input_about_me))
                .perform(clearText())
                .perform(typeText("Other text included alongside the email. " + TEST_EMAIL )); //Do the same for about me.

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.settings_save_settings))
                .perform(scrollTo())
                .perform(click());

        Thread.sleep(THREAD_SLEEP_TIME/4);

        Espresso.pressBack();

        Thread.sleep(THREAD_SLEEP_TIME/4);

        openSideBar(SideBarElement.EDIT_PROFILE);

        String username =  mActivityTestRule.getActivity().mUsername.getText().toString();
        String aboutMe = mActivityTestRule.getActivity().mAboutMe.getText().toString();

        //
        assertFalse("Able to save email in username input box. This is an issue.", username.contains(TEST_EMAIL));
        assertFalse("Able to save email in About me input box. This is an issue.", aboutMe.contains(TEST_EMAIL));
    }**/

    //  Test for countdown timer for 'Save Settings' button that prevents users spamming the button and overloading the Firebase network capacity.
    @Test
    public void testSpammingSaveSettingsButtonFails() throws InterruptedException {

        resetBasicInfo();

        Thread.sleep(THREAD_SLEEP_TIME/4);

        //  Set our initial usernamd and about me. This is what should be saved to our userInfoPrivate class & ultimately Firebase.
        onView(withId(R.id.settings_input_username))
                .perform(clearText())
                .perform(typeText("newUsername"));

        onView(withId(R.id.settings_input_about_me))
                .perform(clearText())
                .perform(typeText("newAboutMe"));

        Espresso.closeSoftKeyboard();

        for(int i = 0; i <= 3; i++){ //Perform 4 clicks at short 0.2 second intervals to re-enact spam entries.
            Thread.sleep(200);
            onView(withId(R.id.settings_save_settings))
                    .perform(click());
        }

        String username =  mActivityTestRule.getActivity().mUserProfile.getDisplayName();
        String aboutMe =  mActivityTestRule.getActivity().mUserProfile.getAbout();

        assertNotEquals(username, "newUsername"); //Check that this new information is not displayed.
        assertNotEquals(aboutMe, "newAboutMe");
    }


    /** ====BEGIN PROFILE SETTINGS ALERT DIALOG BOX TESTS====
     * All other tests are either manual tests to check data is properly stored, deleted and retrieved within firebase.
     *
     * Tests include:
     *  - After changing a password or deleting an account the client returns a Toast (display message) for confirmation
     *  - Firebase user profiles and associated document data is removed on profile deletion (for multiple accounts)
     *  - Firebase user profile password is reset correctly (for multiple accounts)
     *  - Tests of what happens when an connection to Firebase is lost and if the client updates once the wifi signal is retrieved
     *  and if the client is notified that no signal is available.
     */

    @Test
    public void testDeleteActionIsDisplayed() {

        onView(withId(R.id.settings_delete_profile))
                .perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Delete Profile"))
                .check(matches(isDisplayed()));

        onView(withText("Are you sure you want to delete?"))
                .check(matches(isDisplayed()));

        onView(withHint("Enter password"))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasFocus()));

        onView(withText("YES"))
                .check(matches(isDisplayed()));

        onView(withText("NO"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testResetPasswordActionIsDisplayed(){
        onView(withId(R.id.settings_reset_password))
                .perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withHint("Enter old password"))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasFocus()));

        onView(withHint("Re-enter new password"))
                .check(matches(isDisplayed()))
                .perform(click())
                .check(matches(hasFocus()));

        onView(withText("CANCEL"))
                .check(matches(isDisplayed()));

        onView(withText("CHANGE PASSWORD"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testBlankFieldInputsFail() throws InterruptedException {
        //  Make sure we don't actually delete the test account if the following fails. Set to testing.
        mActivityTestRule.getActivity().isTesting = true;

        onView(withId(R.id.settings_delete_profile))
                .perform(ViewActions.scrollTo())
                .perform(click());

        onView(withHint("Enter password"))
                .perform(click())
                .perform(typeText(""));

        Espresso.closeSoftKeyboard();

        onView(withText("YES"))
                .perform(click());

        onView(withText("Cannot enter a blank password. Please try again."))
                //  Make sure we are not retrieving the canvas (lower level background drawable elements).
                //  Instead we are checking the 'Toast' return message is equal.
                .inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_reset_password))
                .perform(ViewActions.scrollTo())
                .perform(click());

        onView(withHint("Enter old password"))
                .perform(click())
                .perform(typeText(""));
        onView(withHint("Enter new password"))
                .perform(click())
                .perform(typeText(""));
        onView(withHint("Re-enter new password"))
                .perform(click())
                .perform(typeText(""));

        Espresso.closeSoftKeyboard();

        onView(withText("CHANGE PASSWORD"))
                .perform(click());

        onView(withText("Cannot enter a blank password. Please try again."))
                .inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

    }

    @Test
    public void testShortPasswordsFail() {
        //  Make sure we don't actually delete the test account if the following fails. Set to testing.
        mActivityTestRule.getActivity().isTesting = true;

        onView(withId(R.id.settings_delete_profile))
                .perform(ViewActions.scrollTo())
                .perform(click());

        onView(withHint("Enter password"))
                .perform(click())
                .perform(typeText("short1"));

        Espresso.closeSoftKeyboard();

        onView(withText("YES"))
                .perform(click());

        onView(withText("Password must be greater than 6 characters in length"))
                .inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_reset_password))
                .perform(ViewActions.scrollTo())
                .perform(click());

        onView(withHint("Enter new password"))
                .perform(click())
                .perform(typeText("oldPassword"));
        onView(withHint("Enter old password"))
                .perform(click())
                .perform(typeText("short1"));
        onView(withHint("Re-enter new password"))
                .perform(click())
                .perform(typeText("short1"));

        Espresso.closeSoftKeyboard();

        onView(withText("CHANGE PASSWORD"))
                .perform(click());

        onView(withText("Password must be greater than 6 characters in length"))
                .inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

    }

    @Test
    public void testNewPasswordsNeedsToMatch() {
        //  Make sure we don't actually delete the test account if the following fails. Set to testing.
        mActivityTestRule.getActivity().isTesting = true;

        onView(withId(R.id.settings_reset_password))
                .perform(ViewActions.scrollTo())
                .perform(click());

        onView(withHint("Enter new password"))
                .perform(click())
                .perform(typeText("oldPassword"));
        onView(withHint("Enter old password"))
                .perform(click())
                .perform(typeText("newPassword"));
        onView(withHint("Re-enter new password"))
                .perform(click())
                .perform(typeText("newPassword2"));

        Espresso.closeSoftKeyboard();

        onView(withText("CHANGE PASSWORD"))
                .perform(click());

        onView(withText("Passwords do not match. Please try again..."))
                .inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }


    @After
    public void finishOff() {
        mActivityTestRule.getActivity().isTesting = false;
    }
}