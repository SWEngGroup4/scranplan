package com.group4sweng.scranplan;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.test.rule.ActivityTestRule;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginTest {


    @Rule
    public ActivityTestRule<Login> mActivityTestRule = new ActivityTestRule<Login>(Login.class);

    private Login mActivity = null;

    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText displayName;
    private Button loginButton;
    private Button registerButton;

    @Before
    public void setUp() throws Exception {

        mActivity = mActivityTestRule.getActivity();

        email = (EditText)mActivity.findViewById(R.id.emailEditText);
        password = (EditText)mActivity.findViewById(R.id.passwordEditText);
        confirmPassword = (EditText)mActivity.findViewById(R.id.confirmPasswordEditText);
        displayName = (EditText)mActivity.findViewById(R.id.displayNameEditText);
        loginButton = (Button)mActivity.findViewById(R.id.loginButton);
        registerButton = (Button)mActivity.findViewById(R.id.registerButton);



    }

    @Test
    public void testPreconditions() {
        assertNotNull(email);
        assertNotNull(password);
    }

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

    @Test
    public void onCreate() {
    }


    @Test
    public void testInvalidUserNamePassword() {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                loginButton.callOnClick();
                email.requestFocus();
                email.setText("testingEmail");
                password.requestFocus();
                password.setText("testingPassword");
                loginButton.callOnClick();
            }
        });
    }

    @Test
    public void testInvalidRegisterUserNamePassword() {
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
            }
        });
    }



    @Test
    public void testLaunch(){
        //Checking that the page is displaying the XML associated with the Login page
        View view = mActivity.findViewById(R.id.loginButton);

        assertNotNull(view);
    }

    @Test
    public void testValidUserNamePassword() {
        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                loginButton.callOnClick();
                email.requestFocus();
                email.setText("testuser@email.com");
                password.requestFocus();
                password.setText("passwordTest1");
                loginButton.callOnClick();
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }


}