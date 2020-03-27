package com.group4sweng.scranplan.Helper;

import com.group4sweng.scranplan.Drawing.LayoutCreator;
import com.group4sweng.scranplan.Login;

/**
 * Recorded Espresso helpers
 * Author: JButler
 * (c) CoDev 2020
 *
 *  Enumeration that allows the creation of custom views (used for testing or as easter eggs)
 *  linked by a unique (rare) username input in the profile setting screens that goes to following destination class once the profile settings are saved.
 *      For e.g. if I wanted to create a link to the Login page for testing purposes I could put in a new enum as...
 *      LOGIN("AccioLogin2020", Login.class) where the first input is my username and the 2nd my destination.
 *
 *  To make use of this for your own tests simply add the following code from...
 *  Link: https://pastebin.com/nTB0NguQ (Ctrl + click to go to website)
 */
public enum HiddenViews {

    LAYOUT_TEST("AccioLayoutCreator2020", LayoutCreator.class);
    LOGIN("AccioLogin2020", Login.class);

    private String usernamekeyWord;
    private Class<?> classDestination;

    HiddenViews(String usernameKeyWord, Class<?> classDestination) {
        this.usernamekeyWord = usernameKeyWord;
        this.classDestination = classDestination;

    }

    public String getUsernameKeyWord() {
        return usernamekeyWord;
    }

    public Class<?> getClassDestination() {
        return classDestination;
    }
}
