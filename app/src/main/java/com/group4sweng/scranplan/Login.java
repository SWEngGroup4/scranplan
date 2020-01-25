package com.group4sweng.scranplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Login extends AppCompatActivity {

    final String TAG = "FirebaseTestLogin";

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    EditText mEmailEditText;
    EditText mPasswordEditText;
    EditText mConfirmPasswordEditText;
    EditText mDisplayNameText;

    Button mLoginButton;
    TextView mForgottenPasswordText;
    Button mFacebookLoginButton;
    Button mGoogleLoginButton;
    Button mRegisterButton;

    ImageView mInfoButton;

    Boolean mLoginInProgress = false;
    Boolean mRegisterInProgress = false;

    String mDisplayName = "Unknown";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        Log.e(TAG, "Starting Login activity");

        initPageItems();

        initPageListeners();

        initFirebase();
    }

    private void initPageItems(){
        //Defining all relevant members of signin & register page
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        mDisplayNameText = (EditText) findViewById(R.id.displayNameEditText);

        mLoginButton = (Button) findViewById(R.id.loginButton);

        mForgottenPasswordText = (TextView) findViewById(R.id.forgottenPasswordText);

        mFacebookLoginButton = (Button) findViewById(R.id.facebookLoginButton);
        mGoogleLoginButton = (Button) findViewById(R.id.googleLoginButton);

        mRegisterButton = (Button) findViewById(R.id.registerButton);

        mInfoButton = (ImageView) findViewById(R.id.infoButton);

        //setting all irrelevant parts to begin as invisible
        mEmailEditText.setVisibility(View.GONE);
        mPasswordEditText.setVisibility(View.GONE);
        mConfirmPasswordEditText.setVisibility(View.GONE);
        mDisplayNameText.setVisibility(View.GONE);
    }

    private void initPageListeners(){
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRegisterInProgress = false;
                if(!mLoginInProgress){
                    // First button press
                    mLoginInProgress = true;
                    mEmailEditText.setVisibility(View.VISIBLE);
                    mPasswordEditText.setVisibility(View.VISIBLE);
                    mConfirmPasswordEditText.setVisibility(View.GONE);
                    mDisplayNameText.setVisibility(View.GONE);
                }else{
                    // Second button press
                    String email = mEmailEditText.getText().toString();
                    String password =mPasswordEditText.getText().toString();
                    loginUser(email, password);
                }

            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginInProgress = false;

                if(!mRegisterInProgress){
                    // First button press
                    mRegisterInProgress = true;
                    mEmailEditText.setVisibility(View.VISIBLE);
                    mPasswordEditText.setVisibility(View.VISIBLE);
                    mConfirmPasswordEditText.setVisibility(View.VISIBLE);
                    mDisplayNameText.setVisibility(View.VISIBLE);
                }else{
                    // Second button press
                    String email = mEmailEditText.getText().toString();
                    String password =mPasswordEditText.getText().toString();
                    String confirmPassword = mConfirmPasswordEditText.getText().toString();
                    String displayName = mDisplayNameText.getText().toString();
                    Boolean passwordCheck = password.equals(confirmPassword);
                    if(passwordCheck){
                        registerUser(email, password, displayName);
                    }else{
                        //TODO need a toast to say passwords do not match
                    }
                }
            }
        });


        mForgottenPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implement password reset via Firebase -> easily done via Tools->FireBase
            }
        });

        mFacebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implement facebook login

            }
        });

        mGoogleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implement google login

            }
        });

        mInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Make info page explaining application and how it works
            }
        });
    }

    private void initFirebase() {

        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.e(TAG, "SignIn : Valid current user : email [" + user.getEmail() + "]");
                    //TODO Grab user display name from database along with any other main account info
                    if (mRegisterInProgress) {
                        setDisplayName(user);
                    } else {
                        mDisplayName = user.getDisplayName();
                    }
                    mLoginInProgress = false;
                    mRegisterInProgress = false;

                    finishActivity();
                }
                else
                    Log.e(TAG, "SignIn : No Current user");
            }
        };
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void registerUser(String email, String password, String displayName) {

        OnCompleteListener<AuthResult> complete = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                    Log.e(TAG, "SignIn : User registered ");
                //TODO Save display name to user profile in database, also save any other information needed to default settings for user to change
                else
                    Log.e(TAG, "SignIn : User registration response, but failed ");
            }
        };

        OnFailureListener failure = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"SignIn : Register user failure");
            }
        };

        Log.e(TAG, "SignIn : Registering : eMail [" + email + "] password [" + password + "] Display Name [" + displayName + "]");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(complete).addOnFailureListener(failure);
    }

    private void loginUser(String email, String password) {

        OnCompleteListener<AuthResult> complete = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                    Log.e(TAG, "SignIn : User logged on ");
                else
                    Log.e(TAG, "SignIn : User log on response, but failed ");
            }
        };

        OnFailureListener failure = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"SignIn : Log on user failure");
            }
        };

        Log.e(TAG, "SignIn : Logging in : eMail [" + email + "] password [" + password + "]");
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(complete).addOnFailureListener(failure);
    }

    private void setDisplayName(FirebaseUser user) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().setDisplayName(mDisplayName).build();
        user.updateProfile(changeRequest);
    }

    private void finishActivity() {

        Log.e(TAG,"SignIn Returning to main activity");
        mAuth.removeAuthStateListener(mAuthStateListener);

        Intent returningIntent = new Intent();
        returningIntent.putExtra("displayname", mDisplayName);
        setResult(RESULT_OK, returningIntent);

        finish();
    }
}
