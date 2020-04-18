package com.group4sweng.scranplan;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.group4sweng.scranplan.UserInfo.Kudos;
import com.group4sweng.scranplan.UserInfo.Preferences;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Local copy of Public Profile test class.
 * Author: JButler
 * (c) CoDev 2020
 *
 *  =====MANUAL TESTS=====
 *  Tests for if data is able to be properly retrieved for multiple users. Also included tests that an appropriate image is displayed
 *  when the user updates this in there profile settings.*/
public class PublicProfileTest extends RecordedEspressoHelper implements Credentials{

    //  Default test values.
    private static final int THREAD_SLEEP_TIME = 4000; //How long Espresso should wait for Firebase data to update.
    UserInfoPrivate testUser;

    @Rule
    public ActivityTestRule<PublicProfile> mActivityTestRule = new ActivityTestRule<>(PublicProfile.class);

    //  Login with the associated test credentials before testing, wait for Firebase to update and enter profile settings.
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

        openSideBar(SideBarElement.PROFILE);

        testUser = mActivityTestRule.getActivity().mUserProfile;

        Thread.sleep(THREAD_SLEEP_TIME/4);
    }

    //  Check the username field exists.
    @Test
    public void testUsernameAndAboutMeIsVisible() {

        onView(withId(R.id.profile_username))
                .check(matches(isDisplayed()));

        //  Only check for 'about me' section if the user has enabled it in there privacy settings.
        if((boolean) testUser.getPublicPrivacy().get("display_about_me")) {
            onView(withId(R.id.public_profile_about_me_desc))
                    .check(matches(isDisplayed()));
        }
    }

    //  Check the number of recipes & Kudos are visible.
    @Test
    public void testNumberOfRecipesAndKudosAreVisible(){
        onView(withId(R.id.profile_recipes))
                .check(matches(isDisplayed()));

        onView(withId(R.id.profile_kudos))
                .check(matches(isDisplayed()));
    }

    //  Test the username and about me info matches what is expected from the local UserInfoPrivate object.
    @Test
    public void testUsernameAndAboutMeMatchAndDisplayed(){
        if((boolean) testUser.getPublicPrivacy().get("display_username")) {
            onView(withText(testUser.getDisplayName()))
                    .check(matches(isDisplayed()));
        }

        if((boolean) testUser.getPublicPrivacy().get("display_about_me")){
            onView(withText(testUser.getAbout()))
                    .check(matches(isDisplayed()));
        }
    }

    //  Check filters are visible, match what we expect to be displayed and there dialog boxes produce the correct results.
    //  Only works effectively if the user sets all filters available to 'enabled'. If no filters are selected will also check the absence of dietary filters.
    @Test
    public void testDietaryFiltersMatchAndDisplayed(){
        Preferences pref = testUser.getPreferences();

        if((boolean) testUser.getPublicPrivacy().get("display_filters")){
            if(pref.isPescatarian()) {
                onView(withId(R.id.recipeInfoPesc))
                        .perform(scrollTo()) // Scrolls horizontally if number of filters exceeds length of screen
                        .check(matches(isDisplayed()))
                        .perform(click()); // Open dialog.

                onView(withText("Pescatarian")) // Checks the dialog box result is correct.
                        .check(matches(isDisplayed()));
                Espresso.pressBack(); // Return from dialog.
            }
            if(pref.isVegan()) {
                onView(withId(R.id.recipeInfoVegan))
                        .perform(scrollTo())
                        .check(matches(isDisplayed()))
                        .perform(click());

                onView(withText("Vegan"))
                        .check(matches(isDisplayed()));
                Espresso.pressBack();
            }
            if(pref.isVegetarian()) {
                onView(withId(R.id.recipeInfoVeggie))
                        .perform(scrollTo())
                        .check(matches(isDisplayed()))
                        .perform(click());

                onView(withText("Vegetarian"))
                        .check(matches(isDisplayed()));
                Espresso.pressBack();
            }
            if(pref.isAllergy_nuts()) {
                onView(withId(R.id.recipeInfoNuts))
                        .perform(scrollTo())
                        .check(matches(isDisplayed()))
                        .perform(click());

                onView(withText("Allergic to Nuts"))
                        .check(matches(isDisplayed()));
                Espresso.pressBack();
            }
            if(pref.isAllergy_eggs()) {
                onView(withId(R.id.recipeInfoEggs))
                        .perform(scrollTo())
                        .check(matches(isDisplayed()))
                        .perform(click());

                onView(withText("Allergic to Eggs"))
                        .check(matches(isDisplayed()));
                Espresso.pressBack();
            }
            if(pref.isAllergy_milk()) {
                onView(withId(R.id.recipeInfoMilk))
                        .perform(scrollTo())
                        .check(matches(isDisplayed()))
                        .perform(click());

                onView(withText("Allergic to Lactose"))
                        .check(matches(isDisplayed()));
                Espresso.pressBack();
            }
            if(pref.isAllergy_shellfish()) {
                onView(withId(R.id.recipeInfoShellfish))
                        .perform(scrollTo())
                        .check(matches(isDisplayed()))
                        .perform(click());

                onView(withText("Allergic to Shellfish"))
                        .check(matches(isDisplayed()));
                Espresso.pressBack();
            }
            if(pref.isAllergy_soya()) {
                onView(withId(R.id.recipeInfoSoy))
                        .perform(scrollTo())
                        .check(matches(isDisplayed()))
                        .perform(click());

                onView(withText("Allergic to Soya"))
                        .check(matches(isDisplayed()));
                Espresso.pressBack();
            }
            if(pref.isAllergy_gluten()) {
                onView(withId(R.id.recipeInfoWheat))
                        .perform(scrollTo())
                        .check(matches(isDisplayed()))
                        .perform(click());

                onView(withText("Allergic to Gluten"))
                        .check(matches(isDisplayed()));
                Espresso.pressBack();
            }
        }

    }

    //  Test that the Kudos retrieved matches the contents contained within the static Kudos class.
    @Test
    public void testKudosRetrieved(){
        onView(withId(R.id.profile_kudos))
                .check(matches(withText("Kudos: " + Kudos.getKudos())));
    }

    //  Test that the kudos icon & dialog contents, including chef level retrieved matches the contents contained within the static Kudos class.
    @Test
    public void testKudosIconDialogRetrieved(){
        onView(withId(R.id.profile_kudos_icon))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Chef Rank"))
                .check(matches(isDisplayed()));

        onView(withText(Kudos.chefLevel))
                .check(matches(isDisplayed()));
    }
}