
package com.group4sweng.scranplan;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.SearchView;

import androidx.test.espresso.ViewInteraction;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.group4sweng.scranplan.HomeTest.typeSearchViewText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * Recorded Espresso helpers
 * Author: JButler
 * (c) CoDev 2020
 **/
public class EspressoHelper {

    /** All helper functions where generated using the built in Espresso test recorder from 'Run Espresso Test'.
     * The functions are relatively un-readable and the tests take a long time to complete so
     * these helper functions are only for fragments, elements without proper resource ids, or elements which don't have associated text.
     */

    private static final int SEARCH_WAIT_TIME = 2000;

    public static boolean shouldSkip = false; // Should we skip pressing the sidebar button incase we don't need to open the sidebar.

    //  Enumeration for the sidebar element.
    public enum SideBarElement{
        PROFILE,
        EDIT_PROFILE,
        NOTIFICATION,
        LOGOUT,
        SUGGEST,
        GOLD,
        MESSAGE
    }

    //  Matcher view for use from Recorded Espresso Tests.
    public static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    //  Opens the correct side menu element based on an enumeration which is translated into the corresponding side menu row.
    //  to be used by the test recorder output.
    public static void openSideBar(SideBarElement element){
        int ROW_ID; //By default make this the top element in the side menu.

        switch(element) {
            case PROFILE:
                ROW_ID = 1;
                break;
            case EDIT_PROFILE:
                ROW_ID = 3;
                break;
            case LOGOUT:
                ROW_ID = 7;
                break;
            case NOTIFICATION:
                ROW_ID = 2;
                break;
            case SUGGEST:
                ROW_ID = 6;
                break;
            case GOLD:
                ROW_ID = 5;
                break;
            case MESSAGE:
                ROW_ID = 4;
                break;
            default:
                ROW_ID = 1;
        }

        //  Test recorder output.
        if(!shouldSkip){
            ViewInteraction appCompatImageButton = onView(
                    allOf(withContentDescription(R.string.nav_drawer_open),
                            childAtPosition(
                                    allOf(withId(R.id.toolbar),
                                            childAtPosition(
                                                    withClassName(is("com.google.android.material.appbar.AppBarLayout")),
                                                    0)),
                                    1),
                            isDisplayed()));
            appCompatImageButton.perform(click());
        }


        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.side_menu),
                                        0)),
                        ROW_ID), //Was previously 2
                        isDisplayed()));
        navigationMenuItemView.perform(click());
    }

    /** Navigate to a specific recipe through the search bar during an Espresso test.
     * @param recipeName - Exact name of the recipe you want to search for. E.g. 'Ultimate spaghetti carbonara recipe'.
     *                   Has to be the 'exact' name and cannot be an abbreviation.
     */
    public static void navigateToRecipe(String recipeName) throws InterruptedException {
        onView(withId(R.id.menuSortButton))
                .perform(click());

        //  Switches to 'name' sort instead of the default 'ingredient' sort.
        onView(withId(R.id.nameCheckBox))
                .check(matches(isNotChecked()))
                .perform(click());

        onView(withText("OK"))
                .perform(click());

        onView(withId(R.id.menuSearch))
                .perform(click());

        //  Searches for the recipe within the designated view.
        onView(isAssignableFrom(SearchView.class))
                .perform(typeSearchViewText(recipeName))
                .perform(pressKey(KeyEvent.KEYCODE_ENTER));

        //  Wait for the recipe to load.
        Thread.sleep(SEARCH_WAIT_TIME);

        onView(withText(recipeName))
                .perform(click());

    }

}
