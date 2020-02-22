package com.group4sweng.scranplan;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.rule.ActivityTestRule;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import static com.google.firebase.firestore.util.Assert.fail;
import static org.junit.Assert.assertNotNull;


public class UserInfoTest implements StorageRead {

    final String TAG = "UserInfoTest";
    UserInfo user;
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
        mActivity = mActivityTestRule.getActivity();
        testApp = mActivity.mApp;
        testAuth = mActivity.mAuth;

        email = (EditText)mActivity.findViewById(R.id.emailEditText);
        password = (EditText)mActivity.findViewById(R.id.passwordEditText);
        confirmPassword = (EditText)mActivity.findViewById(R.id.confirmPasswordEditText);
        displayName = (EditText)mActivity.findViewById(R.id.displayNameEditText);
        loginButton = (Button)mActivity.findViewById(R.id.loginButton);
        registerButton = (Button)mActivity.findViewById(R.id.registerButton);

        deserialize();
        forceLogin();
    }

    //TODO Implement serialization.
    public void deserialize() {

        System.out.println("DESERIALIZATION STARTED!");
        try {
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream input = new ObjectInputStream(file);

            user = (UserInfo)input.readObject();
            input.close();
            file.close();
        }
        catch (IOException e){
            System.out.println("IOException when attempting to deserialize user info");
        } catch (ClassNotFoundException e){
            System.out.println("ClassNotFoundException. Unable to retrieve an instance of a users user info");
        }
    }

    public void forceLogin() {
        //mActivity.mAuth.signOut();
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

    @Test
    public void testBasicInfoCanBeRetrievedFromUser() {
        assertNotNull(user.getClass());
        assertNotNull(user.getEmail());
        assertNotNull(user.getPreferences());
        assertNotNull(user.getUID());
        assertNotNull(user.getDisplayName());
        assertNotNull(user.getImageURL());
        assertNotNull(user.getChefRating());
        if (user.getNumRecipes() > 0){
            System.out.println("Valid number of recipes");
        } else {
            fail("Invalid number of recipes");
        }
        if (user.getChefRating() > 0){
            System.out.println("Valid chef rating");
        } else {
            fail("Invalid chef rating");
        }

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
