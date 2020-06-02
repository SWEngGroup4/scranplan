package com.group4sweng.scranplan;

import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.group4sweng.scranplan.Exceptions.ProfileImageException;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the Profile Settings Activity.
 * Author: JButler
 * (c) CoDev 2020
 *
 *  Tests are included to make sure information is displayed, can be edited and
 *  saved and that spam entries or private email entry is impossible.
 *
 * All other tests are manual tests to check data is properly stored, deleted and retrieved within firebase and that the image picker functions work correctly.
 *
 * Manual Tests include:
 *  - Image picker works as expected with correct accepted formats & filesize only.
 *  - Image Progress bar updates as expected.
 *  - Firebase images can be deleted from the associated profile when required and if this fails appropriate messages are sent.
 *  - After changing a password or deleting an account the client returns a Toast (display message) for confirmation
 *  - Firebase user profiles and associated document data is removed on profile deletion (for multiple accounts)
 *  - Firebase user profile password is reset correctly (for multiple accounts)
 *  - Tests of what happens when an connection to Firebase is lost and if the client updates once the wifi signal is retrieved
 *  and if the client is notified that no signal is available.
 *
 *  -- USER STORY TESTS LINKED WITH ---
 *  C20,A1
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfileSettingsTest extends EspressoHelper implements Credentials {

    enum PrivacyType {
        PRIVATE,
        PUBLIC
    }

    //  Android Log tag.
    private String TAG = "profileSettingsTest";

    private UserInfoPrivate testUser;
    private ProfileSettings activityResult;



    @Rule
    public ActivityTestRule<ProfileSettings> mActivityTestRule = new ActivityTestRule<>(ProfileSettings.class);

    //  Login with the associated test credentials before testing, wait for Firebase to update and enter profile settings.
    @Before
    public void setUp() throws InterruptedException {
        EspressoHelper.shouldSkip = false;

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
        openSideBar(SideBarElement.EDIT_PROFILE);

        Thread.sleep(THREAD_SLEEP_TIME/4);

        activityResult = mActivityTestRule.getActivity(); // Load our activity in.
        activityResult.isTesting = true;
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
                .perform(scrollTo())
                .perform(click());
    }

    //  Check a username input field exists.
    @Test
    public void testUsernameLoads() throws InterruptedException {

        onView(withId(R.id.profile_username))
                .check(matches(isDisplayed()));
    }

    //  Check all filter and privacy tables can be displayed
    @Test
    public void testPrivacyLoad() {

        onView(withId(R.id.settings_privacy))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_private_toggle))
                .perform(click());

        onView(withId(R.id.settings_privacy))
                .check(matches(isDisplayed()));
    }

    //  Test Our filters table loads. Used fragments so resource IDs do not work.
    @Test
    public void testFiltersLoad() {

        onView(withText("Nuts"))
                .check(matches(isDisplayed()));
        onView(withText("Shellfish"))
                .check(matches(isDisplayed()));
        onView(withText("Soy"))
                .check(matches(isDisplayed()));
        onView(withText("Wheat"))
                .check(matches(isDisplayed()));
        onView(withText("Eggs"))
                .check(matches(isDisplayed()));
        onView(withText("Milk"))
                .check(matches(isDisplayed()));

        onView(withText("DIETARY"))
                .perform(click());

        onView(withText("Vegan"))
                .check(matches(isDisplayed()));
        onView(withText("Vegetarian"))
                .check(matches(isDisplayed()));
        onView(withText("Pescatarian"))
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
    public void testButtonsLoad(){
        onView(withId(R.id.settings_reset_password))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_delete_profile))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_save_settings))
                .perform(scrollTo())
                .check(matches(isDisplayed()));
    }

    //  Test username & about me displayed matches what is stored in the associated UserInfoPrivate class.
    @Test
    public void testUsernameAndAboutMeRetrieved() {

        testUser = (UserInfoPrivate) mActivityTestRule.getActivity().getIntent().getSerializableExtra("user");

        onView(withId(R.id.settings_input_username))
                .check(matches(withText(testUser.getDisplayName())));

        testUser = (UserInfoPrivate) mActivityTestRule.getActivity().getIntent().getSerializableExtra("user");

        onView(withId(R.id.settings_input_about_me))
                .check(matches(withText(testUser.getAbout())));
    }

    //  Test that upon changing our username/about me and logging out our username/about me is able to be stored and retrieved properly.
    @Test
    public void testUsernameAndAboutMeIsStoredAndRetrieved() throws InterruptedException {
        
        testUser = (UserInfoPrivate) mActivityTestRule.getActivity().getIntent().getSerializableExtra("user");

        onView(withId(R.id.settings_input_username))
                .check(matches(withText(testUser.getDisplayName())));

        testUser = (UserInfoPrivate) mActivityTestRule.getActivity().getIntent().getSerializableExtra("user");

        onView(withId(R.id.settings_input_about_me))
                .check(matches(withText(testUser.getAbout())));
    }

    /** Switches all privacy switches from there previous value to the new value. e.g. either false > true or true > false.
     * @param pt - Enum defining if we should update the public of private privacy switches.
     * @return - A hashmap of the previous switch states for comparison
     */
    private HashMap<String, Boolean> switchAllPrivacySwitches(PrivacyType pt) throws InterruptedException {
        if(activityResult.mPrivateProfileEnabled.isChecked()){
            onView(withId(R.id.settings_private_toggle))
                    .perform(scrollTo())
                    .perform(click());
        }

        if(pt == PrivacyType.PRIVATE){ // Check if we need to press the 'PRIVATE' tab selector to assign 'private' privacy values only.
            if(!activityResult.mPrivateProfileEnabled.isChecked()){
                onView(withId(R.id.settings_private_toggle))
                        .perform(scrollTo())
                        .perform(click());
            }
            onView(withText("PRIVATE"))
                    .perform(scrollTo())
                    .perform(click());
        }
        HashMap<String, Boolean> initialPrivacy = new HashMap<>();

        //  Log the results of what the switches used to be into a Hashmap. in order to reference later in testing.
        initialPrivacy.put("about_me", activityResult.mDisplay_about_me.isChecked());
        initialPrivacy.put("profile_image", activityResult.mDisplay_profile_image.isChecked());
        initialPrivacy.put("filters", activityResult.mDisplay_filters.isChecked());

        //  Begin switching.
        onView(withId(R.id.settings_privacy_about_me))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.settings_privacy_profile_image))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.settings_privacy_filters))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.settings_save_settings))
                .perform(scrollTo())
                .perform(click());

        Espresso.pressBack();
        Thread.sleep(THREAD_SLEEP_TIME/4);

        return initialPrivacy;
    }

    //  Test public privacy info can be stored and retrieved.
    @Test
    public void testPrivacyInfoIsStoredAndRetrieved() throws InterruptedException {

        HashMap<String, Boolean> initialPrivacy = switchAllPrivacySwitches(PrivacyType.PUBLIC);

        assertNotEquals(initialPrivacy.get("about_me"), activityResult.mDisplay_about_me.isChecked());
        assertNotEquals(initialPrivacy.get("profile_image"), activityResult.mDisplay_profile_image.isChecked());
        assertNotEquals(initialPrivacy.get("filters"), activityResult.mDisplay_filters.isChecked());
    }

    //  Test private privacy info can be stored and retrieved.
    @Test
    public void testPrivatePrivacyInfoIsStoredAndRetrieved() throws InterruptedException {
        onView(withId(R.id.settings_private_toggle))
                .perform(scrollTo())
                .perform(click());

        HashMap<String, Boolean> initialPrivacy = switchAllPrivacySwitches(PrivacyType.PRIVATE);

        assertNotEquals(initialPrivacy.get("about_me"), activityResult.mDisplay_about_me.isChecked());
        assertNotEquals(initialPrivacy.get("profile_image"), activityResult.mDisplay_profile_image.isChecked());
        assertNotEquals(initialPrivacy.get("filters"), activityResult.mDisplay_filters.isChecked());
    }

    /** Either turn on or turn off all available privacy switches before testing.
     * @param enableAllSwitches - true if we want to turn on all switches, false if we want to turn off.
     */
    private void resetPrivacy(boolean enableAllSwitches){

        if(enableAllSwitches){
            //  Checks if the filters turned off and if so switches on.
            if(!activityResult.mDisplay_filters.isChecked()){ onView(withId(R.id.settings_privacy_filters)).perform(scrollTo()).perform(click()); }
            if(!activityResult.mDisplay_profile_image.isChecked()){ onView(withId(R.id.settings_privacy_profile_image)).perform(scrollTo()).perform(click()); }
            if(!activityResult.mDisplay_about_me.isChecked()){ onView(withId(R.id.settings_privacy_about_me)).perform(scrollTo()).perform(click()); }
        } else {
            //  Checks if the filters turned on and if so switches off.
            if(activityResult.mDisplay_filters.isChecked()){ onView(withId(R.id.settings_privacy_filters)).perform(scrollTo()).perform(click()); }
            if(activityResult.mDisplay_profile_image.isChecked()){ onView(withId(R.id.settings_privacy_profile_image)).perform(scrollTo()).perform(click()); }
            if(activityResult.mDisplay_about_me.isChecked()){ onView(withId(R.id.settings_privacy_about_me)).perform(scrollTo()).perform(click()); }
        }

    }

    private void switchAllPrivacySwitches(){
        onView(withId(R.id.settings_privacy_filters)).perform(scrollTo()).perform(click());
        onView(withId(R.id.settings_privacy_profile_image)).perform(scrollTo()).perform(click());
        onView(withId(R.id.settings_privacy_about_me)).perform(scrollTo()).perform(click());
    }

    /*  Test that when we either...
            Enable public privacy setting > Equivalent Private setting is also enabled.
            Disable private privacy setting > Equivalent Public setting is also disabled.
     */
    @Test
    public void testPrivacyOptionsSync() throws InterruptedException {

        if(activityResult.mPrivateProfileEnabled.isChecked()){
            onView(withId(R.id.settings_private_toggle))
                    .perform(scrollTo())
                    .perform(click());
        }

        resetPrivacy(false); // Set all switches off
        switchAllPrivacySwitches(); // Turn all switches back on so the ProfileSettings class can register these changes.

        Thread.sleep(THREAD_SLEEP_TIME/4);

        assertTrue(activityResult.mDisplay_about_me.isChecked());
        assertTrue(activityResult.mDisplay_profile_image.isChecked());
        assertTrue(activityResult.mDisplay_filters.isChecked());

        resetPrivacy(true); //  Set all switches on
        switchAllPrivacySwitches();

        if(!activityResult.mPrivateProfileEnabled.isChecked()){
            onView(withId(R.id.settings_private_toggle))
                    .perform(scrollTo())
                    .perform(click());
        }

        onView(withText("PUBLIC"))
                .perform(scrollTo())
                .perform(click());

        assertFalse(activityResult.mDisplay_about_me.isChecked());
        assertFalse(activityResult.mDisplay_profile_image.isChecked());
        assertFalse(activityResult.mDisplay_filters.isChecked());
    }

    //  Test that we can create a clone of the UserInfoPrivate class for use before saving.
    //  Tested using privacy switches since these update before the save button is pressed.
    @Test
    public void testUserInfoPrivateDeepClone() {
        onView(withId(R.id.settings_private_toggle))
                .perform(click());
        switchAllPrivacySwitches();

        HashMap<String, Object> tempPrivacy = activityResult.mTempUserProfile.getPrivacyPrivate();
        HashMap<String, Object> actualPrivacy = activityResult.mUserProfile.getPrivacyPrivate();

        boolean displayFeed = (boolean) tempPrivacy.get("display_feed");

        assertEquals(displayFeed, actualPrivacy.get("display_feed"));
    }


    //  Check that if a user accidentally inputs there email into the Username or About Me section it cannot be saved to Firebase.
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

        EspressoHelper.shouldSkip = true;

        openSideBar(SideBarElement.EDIT_PROFILE);

        String username =  onView(withId(R.id.settings_input_username)).toString();
        String aboutMe = onView(withId(R.id.settings_input_about_me)).toString();

        //
        assertFalse("Able to save email in username input box. This is an issue.", username.contains(TEST_EMAIL));
        assertFalse("Able to save email in About me input box. This is an issue.", aboutMe.contains(TEST_EMAIL));
    }

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
                    .perform(scrollTo())
                    .perform(click());
        }

        String username =  activityResult.mUserProfile.getDisplayName();
        String aboutMe =  activityResult.mUserProfile.getAbout();

        assertNotEquals(username, "newUsername"); //Check that this new information is not displayed.
        assertNotEquals(aboutMe, "newAboutMe");
    }

    @Test
    public void testDeleteActionIsDisplayed() {

        onView(withId(R.id.settings_delete_profile))
                .perform(scrollTo())
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
                .perform(scrollTo())
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

    //  Test that if we input nothing into either the delete profile or reset settings dialog boxes we get an error.
    @Test
    public void testBlankFieldInputsFail() throws InterruptedException {
        //  Make sure we don't actually delete the test account if the following fails. Set to testing.
        mActivityTestRule.getActivity().isTesting = true;

        onView(withId(R.id.settings_delete_profile))
                .perform(scrollTo())
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
                .perform(scrollTo())
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

    //  Test that really short passwords are not allowed for security reasons.
    @Test
    public void testShortPasswordsFail() {
        //  Make sure we don't actually delete the test account if the following fails. Set to testing.
        mActivityTestRule.getActivity().isTesting = true;

        onView(withId(R.id.settings_delete_profile))
                .perform(scrollTo())
                .perform(click());

        onView(withHint("Enter password"))
                .perform(click())
                .perform(typeText("short1"));

        Espresso.closeSoftKeyboard();

        onView(withText("YES"))
                .perform(click());

        onView(withText("Password must be greater than 7 characters in length"))
                .inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_reset_password))
                .perform(scrollTo())
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

        onView(withText("Password must be at least 8 characters and contain at least 1 number and 1 letter"))
                .inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

    }

    //  Test that the new password input matches the previous one.
    @Test
    public void testNewPasswordsNeedsToMatch() {
        //  Make sure we don't actually delete the test account if the following fails. Set to testing.
        mActivityTestRule.getActivity().isTesting = true;

        onView(withId(R.id.settings_reset_password))
                .perform(scrollTo())
                .perform(click());

        onView(withHint("Enter new password"))
                .perform(click())
                .perform(typeText("oldPassword2"));
        onView(withHint("Enter old password"))
                .perform(click())
                .perform(typeText("newPassword2"));
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


    //  Test that the new password input matches the previous one.
    @Test
    public void testNewPasswordsMatchCriteria() {
        //  Make sure we don't actually delete the test account if the following fails. Set to testing.
        mActivityTestRule.getActivity().isTesting = true;

        onView(withId(R.id.settings_reset_password))
                .perform(scrollTo())
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

        onView(withText("Password must be at least 8 characters and contain at least 1 number and 1 letter"))
                .inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    //  Test that using a Fake URL and asserting we are testing (ignoring checks for image size, type etc...)
    //  the image progress bar is able to be displayed to the user.
    @Test
    public void testImageProgressBarDisplays() throws ProfileImageException, InterruptedException {
        Uri fakeUri = Uri.parse("Http://FakeImageScranplan.org");

        activityResult.isTesting = true;

        activityResult.uploadImage(fakeUri);

        onView(withId(R.id.settings_progress))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.settings_progress_text))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .check(matches(withText("Saving Image... 0%")));

        // Since variable is static. Makes sure app dosen't assume image is still uploading for future tests.
        ProfileSettings.IMAGE_IS_UPLOADING = false;
    }

    //  Test that if we are still uploading an image that upon pressing back the correct dialog box is displayed
    //  and options to cancel or continue are functional.
    @Test
    public void testImageStillUploadingDialogBox() throws ProfileImageException, InterruptedException {
        Uri fakeUri = Uri.parse("Http://FakeImageScranplan.org");
        activityResult.isTesting = true;

        activityResult.uploadImage(fakeUri);
        Espresso.pressBack();

        onView(withText("Your profile image is still uploading. Are you sure you want to exit?"))
                .check(matches(isDisplayed()));

        onView(withText("NO"))
                .perform(click());

        onView(withId(R.id.settings_input_username))
                .check(matches(isDisplayed()));

        ProfileSettings.IMAGE_IS_UPLOADING = false;
    }

    @After
    public void finishOff() {
        EspressoHelper.shouldSkip = false;
        this.mActivityTestRule.finishActivity();
//        activityResult.isTesting = false;
//        activityResult = null;
    }
}