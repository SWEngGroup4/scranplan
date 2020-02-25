package com.group4sweng.scranplan;

import android.widget.Button;
import android.widget.EditText;

import androidx.test.rule.ActivityTestRule;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class UserInfoPrivateTest{

    final String TAG = "UserInfoTest";
    private Login mActivity = null;

    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText displayName;
    private Button loginButton;
    private Button registerButton;
    FirebaseApp testApp;
    FirebaseAuth testAuth;
    /*
        Only added private, restricted, public retrieval tests.
        Tests for whether the user info is stored correctly is not included and should be done
        when a user story requires it. E.g. adding chef star integration into the userInfo ProfileView.
     */

    @Rule
    public ActivityTestRule<Login> mActivityTestRule = new ActivityTestRule<Login>(Login.class);

    // Connect XML to tests
    @Before
    public void setUp() {
        forceLogin();
    }

    public void forceLogin() {
    }

    @Test
    public void testBasicInfoCanBeRetrievedFromUser() {
    }


    @Test
    public void testPrivateInfoIsHiddenToOtherUsers() {
        /*TODO
            Check that we are unable to retrieve private info on other users.
         */
    }

    @Test
    public void testPublicInfoIsVisible() {
        /*TODO
            Check that the public profile info is visible from another user account.
         */
    }

    @Test
    public void testOnlyRestrictedInfoIsDisplayed() {
        /*TODO
            Test that for the restriced 'enum' preset only a limited amount of options are displayed.
         */
    }
    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }


}
