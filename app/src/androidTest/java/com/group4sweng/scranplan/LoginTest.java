package com.group4sweng.scranplan;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.test.rule.ActivityTestRule;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * LoginTest class
 *  Author(s): LNewman
 *  (c) CoDev 2020
 * JUnit testing for the Login class testing all components
 *
 * -- USER STORY TESTS LINKED WITH ---
 * A2, A5, A3 , A1
 */
public class LoginTest implements Credentials {

    @Rule
    public ActivityTestRule<Login> mActivityTestRule = new ActivityTestRule<Login>(Login.class);

    private Login mActivity = null;


    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText displayName;
    private Button loginButton;
    private Button registerButton;
    FirebaseApp testApp;
    FirebaseAuth testAuth;

    // Connect XML to tests
    @Before
    public void setUp() throws Exception {

        mActivity = mActivityTestRule.getActivity();
        testApp = mActivity.mApp;
        testAuth = mActivity.mAuth;

        email = (EditText)mActivity.findViewById(R.id.emailEditText);
        password = (EditText)mActivity.findViewById(R.id.passwordEditText);
        confirmPassword = (EditText)mActivity.findViewById(R.id.confirmPasswordEditText);
        displayName = (EditText)mActivity.findViewById(R.id.displayNameEditText);
        loginButton = (Button)mActivity.findViewById(R.id.loginButton);
        registerButton = (Button)mActivity.findViewById(R.id.registerButton);

    }

    // Test components exist on screen
    @Test
    public void testPreconditions() {
        assertNotNull(email);
        assertNotNull(password);
    }

    // Test user selecting edit text
    @Test
    public void testUsernameTextFocus() {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                loginButton.callOnClick();
                assertNotNull(email);
                assertTrue(email.requestFocus());
                assertTrue(email.hasFocus());
            }
        });
    }

    // Test user selecting edit text
    @Test
    public void testPasswordTextFocus() {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                loginButton.callOnClick();
                assertNotNull(password);
                assertTrue(password.requestFocus());
                assertTrue(password.hasFocus());
            }
        });

    }

    // Test user selecting edit text
    @Test
    public void testConfirmPasswordTextFocus() {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                registerButton.callOnClick();
                assertNotNull(confirmPassword);
                confirmPassword.requestFocus();
                assertTrue(confirmPassword.hasFocus());
            }
        });

    }

    // Test invalid username and password
    @Test
    public void testInvalidUserEmailAndPassword() {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                loginButton.callOnClick();
                email.requestFocus();
                email.setText("testingEmail");
                password.requestFocus();
                password.setText("testingPassword");
                loginButton.callOnClick();

                testAuth.signOut();
                testAuth.signInWithEmailAndPassword(email.toString(), password.toString());
                assertNull(testAuth.getCurrentUser());
            }
        });
    }

    // Test creating new user
    @Test
    public void testInvalidRegisterEmailAndPassword() {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                registerButton.callOnClick();
                email.requestFocus();
                email.setText("testingEmail");
                password.requestFocus();
                password.setText("testingPassword");
                confirmPassword.requestFocus();
                confirmPassword.setText("testingPassword");
                displayName.requestFocus();
                displayName.setText("testName");
                registerButton.callOnClick();


                testAuth.signOut();
                testAuth.createUserWithEmailAndPassword(email.toString(), password.toString());
                assertNull(testAuth.getCurrentUser());
            }
        });
    }


    // Check the screen is displaying as expected
    @Test
    public void testLaunch(){
        //Checking that the page is displaying the XML associated with the Login page
        View view = mActivity.findViewById(R.id.loginButton);

        assertNotNull(view);
    }

    // test if a know username and password successfully logs in
    @Test
    public void testValidEmailAndPassword() {
        //mActivity.mAuth.signOut();
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                loginButton.callOnClick();
                email.requestFocus();
                email.setText(TEST_EMAIL);
                password.requestFocus();
                password.setText(TEST_PASSWORD);
                loginButton.callOnClick();
            }
        });
    }


    @After
    public void tearDown() throws Exception {
        mActivity = null;
        EspressoHelper.shouldSkip = false;
        this.mActivityTestRule.finishActivity();
    }


}