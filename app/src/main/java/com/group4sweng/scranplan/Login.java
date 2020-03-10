package com.group4sweng.scranplan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;

import static java.util.Objects.requireNonNull;

/**
 * Login class
 * Everything necessary for new users to register or existing users to log in before moving into the
 * main section of the application
 */
public class Login extends AppCompatActivity{

    // TAG for log info
    final String TAG = "FirebaseTestLogin";

    Context mContext = this;

    private UserInfoPrivate mUser;

    // Firebase variables needed for login/register
    FirebaseApp mApp;
    FirebaseAuth mAuth;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    CollectionReference ref = database.collection("users");

    // All edit text local variables to link to XML page
    EditText mEmailEditText;
    EditText mPasswordEditText;
    EditText mConfirmPasswordEditText;
    EditText mDisplayNameText;

    // All button local variables used to give function to XML page
    Button mLoginButton;
    TextView mForgottenPasswordText;
    Button mFacebookLoginButton;
    Button mGoogleLoginButton;
    Button mRegisterButton;
    ImageView mInfoButton;

    // Variables to save state of page view
    Boolean mLoginInProgress = false;
    Boolean mRegisterInProgress = false;

    // Variable to save display name when registering for a new profileView
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

    /**
     * initPageItems
     * Connecting all XML page values with local variables and hiding all parts that's the user
     * isn't intended to be able to see yet.
     */
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

    /**
     * initPageListeners
     * Giving each actionable item on the XML page function.
     */
    private void initPageListeners(){

        /**
         * Setting login button, first click enables login components, second click initiated sign in
         */
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiding register user components
                mRegisterInProgress = false;
                if(!mLoginInProgress){
                    // First button press, set login components to visible
                    mLoginInProgress = true;
                    mEmailEditText.setVisibility(View.VISIBLE);
                    mPasswordEditText.setVisibility(View.VISIBLE);
                    mConfirmPasswordEditText.setVisibility(View.GONE);
                    mDisplayNameText.setVisibility(View.GONE);
                }else{
                    // Second button press, log user in with entered email and password
                    String email = mEmailEditText.getText().toString();
                    String password =mPasswordEditText.getText().toString();
                    loginUser(email, password);
                }

            }
        });

        /**
         * Setting register button, first click enables register components, second click initiates
         * new user registration
         */
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginInProgress = false;
                if(!mRegisterInProgress){
                    // First button press - set all components visible
                    mRegisterInProgress = true;
                    mEmailEditText.setVisibility(View.VISIBLE);
                    mPasswordEditText.setVisibility(View.VISIBLE);
                    mConfirmPasswordEditText.setVisibility(View.VISIBLE);
                    mDisplayNameText.setVisibility(View.VISIBLE);
                }else{
                    // Second button press - register new user with credentials
                    String email = mEmailEditText.getText().toString();
                    String password =mPasswordEditText.getText().toString();
                    String confirmPassword = mConfirmPasswordEditText.getText().toString();
                    String displayName = mDisplayNameText.getText().toString();
                    // Check none of fields are just empty strings
                    if(!email.equals("") && !password.equals("") && !displayName.equals("") ){
                        // Check both passwords match before registering new user
                        if(password.equals(confirmPassword)){
                            registerUser(email, password, displayName);
                        }else{
                            // Display message to user if passwords do not match
                            Toast.makeText(getApplicationContext(),"Passwords do not match, please try again.",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        // Display message to user if passwords do not match
                        Toast.makeText(getApplicationContext(),"One or more of the fields are empty, please fill in all sections of the form.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        /**
         * Setting forgotten password button
         */
        mForgottenPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checking if email is filled in to be able to send recovery email.
                if(mEmailEditText.getText() != null && !mEmailEditText.getText().toString().equals("") && mEmailEditText.getVisibility() != View.GONE){
                    mAuth.sendPasswordResetEmail(mEmailEditText.getText().toString());
                    Log.e(TAG, "SignIn : Password reset initiated with recovery email.");
                    Toast.makeText(getApplicationContext(),"Password reset sent to email.",Toast.LENGTH_LONG).show();
                }else{
                    Log.e(TAG, "SignIn : Unable to send email to recover password, lack of email.");
                    Toast.makeText(getApplicationContext(),"Please fill in email for password reset to be sent.",Toast.LENGTH_SHORT).show();
                }
                mLoginInProgress = false;
                mRegisterInProgress = false;
                mEmailEditText.setVisibility(View.VISIBLE);

            }
        });

        /**
         * Setting Facebook Login button
         */
        mFacebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implement facebook login

            }
        });

        /**
         * Setting Google Login button
         */
        mGoogleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO implement google login

            }
        });

        /**
         * Setting info button
         */
        mInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Make info page explaining application and how it works
            }
        });
    }

    /**
     * Setting up the Firebase, creating a Firebase authentication instance and linking it to a
     * Firebase app instance. A listener is then added to authentication instance to check when
     * a user has been logged in. If a user has been logged in the user is sent forward through
     * to the main menu.
     */
    private void initFirebase() {

        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);

    }

    /**
     * Setting up a new user both via Firebase authentication and setting up a user profileView
     * on the database
     * @param email - users email address, saved as authentication and in database
     * @param password - Users password, saved as authentication
     * @param displayName - users display name, saved in database
     */
    private void registerUser(final String email, String password, final String displayName) {
        mDisplayName = displayName;
        OnCompleteListener<AuthResult> complete = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // if authentication successful
                if (task.isSuccessful()){
                    Log.e(TAG, "SignIn : User registered ");
                    // Setting up default user profileView on database with email and display name
                    HashMap<String, Object> map = new HashMap<>();
                    HashMap<String, Object> preferences = new HashMap<>();
                    HashMap<String, Object> privacy = new HashMap<>();

                    map.put("UID", requireNonNull(mAuth.getCurrentUser()).getUid());
                    map.put("email", mAuth.getCurrentUser().getEmail());
                    map.put("displayName", mDisplayName);
                    map.put("imageURL", "");
                    map.put("chefRating", (double) 0);
                    map.put("numRecipes", (long) 0);
                    map.put("about", "");
                    map.put("firstTimeLogin", true);

                    map.put("shortPreferences", true);
                    map.put("firstAppLaunch", true);
                    map.put("firstPresentationLaunch", true);


                    privacy.put("display_username", true);
                    privacy.put("display_about_me", true);
                    privacy.put("display_recipes", false);
                    privacy.put("display_profile_image", true);
                    privacy.put("display_filters", false);

                    // Default user food preferences
                    preferences.put("allergy_celery", false);
                    preferences.put("allergy_crustacean", false);
                    preferences.put("allergy_eggs", false);
                    preferences.put("allergy_fish", false);
                    preferences.put("allergy_gluten", false);
                    preferences.put("allergy_milk", false);
                    preferences.put("allergy_mustard", false);
                    preferences.put("allergy_nuts", false);
                    preferences.put("allergy_peanuts", false);
                    preferences.put("allergy_sesame", false);
                    preferences.put("allergy_shellfish", false);
                    preferences.put("allergy_soya", false);
                    preferences.put("allergy_sulphide", false);
                    preferences.put("diabetic", false);
                    preferences.put("halal", false);
                    preferences.put("high_protein", false);
                    preferences.put("kosher", false);
                    preferences.put("lactose_free", false);
                    preferences.put("lactovegetarian", false);
                    preferences.put("low_carb", false);
                    preferences.put("low_sodium", false);
                    preferences.put("no_alcohol", false);
                    preferences.put("no_pork", false);
                    preferences.put("ovovegetarian", false);
                    preferences.put("pescatarian", false);
                    preferences.put("vegan", false);
                    preferences.put("vegetarian", false);

                    map.put("preferences", preferences);
                    map.put("privacy", privacy);
                    // Saving default profile locally to user

                    // Saving default user to Firebase Firestore database
                    DocumentReference usersRef = ref.document(mAuth.getCurrentUser().getUid());
                    mAuth.getCurrentUser().sendEmailVerification();
                    Log.e(TAG, "SignIn : Email authentication sent for new user and logged out.");
                    Toast.makeText(getApplicationContext(),"Email authentication sent to email, please verify email and log in with your new account.",Toast.LENGTH_LONG).show();
                    usersRef.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mAuth.signOut();
                        }
                    });
                    mRegisterInProgress = false;
                    mDisplayNameText.getText().clear();
                    mPasswordEditText.getText().clear();
                    mConfirmPasswordEditText.getText().clear();
                    mLoginInProgress = true;
                }else{
                    // Log and alert user if unsuccessful
                    Log.e(TAG, "SignIn : User registration response, but failed ");
                    Toast.makeText(getApplicationContext(),"User registration failed, please try again.",Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Log and alert user if unsuccessful
        OnFailureListener failure = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"SignIn : Register user failure");
                Toast.makeText(getApplicationContext(),"User registration failed, please try again.",Toast.LENGTH_SHORT).show();
            }
        };

        // Creating new authentication account
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(complete).addOnFailureListener(failure);
    }

    /**
     * Log in know user both via Firebase authentication and downloading user profileView from Firestore
     * @param email - users email address, to log into authentication
     * @param password - Users password, to log into authentication
     */
    private void loginUser(String email, String password) {

        // Listening to check if login complete
        OnCompleteListener<AuthResult> complete = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try{
                    if (task.isSuccessful()){
                        if(mAuth.getCurrentUser().isEmailVerified()){
                            Log.e(TAG, "SignIn : User logged on ");
                            // If successful, log complete and attempt to download user data
                            DocumentReference usersRef = ref.document(mAuth.getCurrentUser().getUid());
                            usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult() == null){
                                            Log.d(TAG, "getResult is null, setting up new user.");
                                        }else{
                                            // If download successful, data is saved to local variable
                                            Log.d(TAG, "getResult: " + task.getResult());
                                            DocumentSnapshot document = task.getResult();
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("UID", mAuth.getCurrentUser().getUid());
                                            map.put("email", document.get("email"));
                                            map.put("displayName", document.get("displayName"));
                                            map.put("imageURL", document.get("imageURL"));
                                            map.put("chefRating", document.get("chefRating"));
                                            map.put("numRecipes", document.get("numRecipes"));
                                            map.put("preferences", document.get("preferences"));
                                            map.put("privacy", document.get("privacy"));
                                            map.put("about", document.get("about"));

                                            map.put("shortPreferences", document.get("shortPreferences"));
                                            map.put("firstAppLaunch", document.get("firstAppLaunch"));
                                            map.put("firstPresentationLaunch", document.get("firstPresentationLaunch"));

                                            @SuppressWarnings("unchecked")
                                            HashMap<String, Object> preferences = (HashMap<String, Object>) document.get("preferences");

                                            @SuppressWarnings("unchecked")
                                            HashMap<String, Object> privacy = (HashMap<String, Object>) document.get("privacy");

                                            mUser = new UserInfoPrivate(map, preferences, privacy);

                                            if(mUser.getFirstAppLaunch()){
                                                Log.e(TAG,"Sending user to initial preference setup page");
                                                Intent initialCustom = new Intent(getApplicationContext(), InitialUserCustomisation.class);
                                                initialCustom.putExtra("user", mUser);
                                                startActivity(initialCustom);
                                            }
                                            else{Log.i(TAG, "test one");}
                                            Log.i(TAG, "SignIn : Valid current user : UID [" + mUser.getUID() + "]");
                                            mLoginInProgress = false;
                                            mRegisterInProgress = false;
                                            finishActivity();
                                        }
                                    }else {
                                        // Log and alert user if unsuccessful with data retrieval
                                        Log.e(TAG, "SignIn : Unable to retrieve user document in Firestore ");
                                        Toast.makeText(getApplicationContext(),"Sign in failed, unable to retrieve user details, please try again.",Toast.LENGTH_SHORT).show();
                                        // Log user back out as they have no user profile in database
                                        mAuth.signOut();
                                    }
                                }
                            });
                        }else{
                            mAuth.getCurrentUser().sendEmailVerification();
                            Log.e(TAG, "SignIn : Email authentication sent for user trying to log in with unverified email, user logged out.");
                            Toast.makeText(getApplicationContext(),"Email is not yet verified, a new verification email has been sent, please verify email and try again.",Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        }

                    }
                    else {
                        // Log and alert user if unsuccessful with sign in
                        Log.e(TAG, "SignIn : User log on response, but failed ");
                        Toast.makeText(getApplicationContext(),"Sign in failed, invalid email or password, please try again.",Toast.LENGTH_SHORT).show();

                    }
                }catch(Exception e){
                    // Log that an exception has been called when trying to download from database
                    Log.e(TAG, "ERROR - Caught when trying to save data to UserInfo, error: (" + e + ")");
                }
            }
        };

        // Unable to log in so user alerted and logged
        OnFailureListener failure = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"SignIn : Log on user failure");
                Toast.makeText(getApplicationContext(),"Register failed, please try again.",Toast.LENGTH_SHORT).show();
            }
        };

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(complete).addOnFailureListener(failure);
    }


    /**
     * When user logged-in/registered and all data downloaded-from/saved-to database, user sent to
     * main menu
     */
    private void finishActivity() {

        Log.e(TAG,"SignIn Returning to main activity");

        Intent returningIntent = new Intent(Login.this, MainActivity.class);

        returningIntent.putExtra("user", mUser);

        mUser = null;
        startActivity(returningIntent);

        finish();
    }

    // Disable user from pressing back button on login page
    @Override
    public void onBackPressed() {
        //Do nothing
    }


}
