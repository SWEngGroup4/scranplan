package com.group4sweng.scranplan;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;

/**
 * Root page for the application offering notification to the user that the page is loading
 */
public class MainActivity extends AppCompatActivity {
    Context mContext = this;
    final static String TAG = "ROOT";
    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    final static int PROFILE_SETTINGS_REQUEST_CODE = 1;

    // User info and preferences variable
    UserInfoPrivate mUser;

    // Firebase variables
    FirebaseAuth mAuth;
    FirebaseApp mApp;
    FirebaseAuth.AuthStateListener mAuthListener;
    String mDisplayName;

    // LOGO that rotates on screen to show user that page is loading
    ImageView mLogoHomeImage;

    // Sets the page up on create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG,"At root page - logging in user or sending to login page");

        if(getIntent().getSerializableExtra("user") != null){
            mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getIntent().getSerializableExtra("user");
        }



        initPageItems();
        // Rotates the logo clockwise
        initFirebase();
        rotateImageClockwise(mLogoHomeImage);

    }

    public void initPageItems(){
        mLogoHomeImage = findViewById(R.id.logoHomeImage);
    }

    // Rotating method
    public void rotateImageClockwise(View view) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 360f, 0f);
        rotate.setRepeatCount(999);
        rotate.setDuration(1000);
        rotate.start();
    }


    /**
     * setting up the firebase and sets the user authentication listener,
     * checking if the user is ever logged out that the user is taken back to the login screen.
     */
    private void initFirebase(){
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null){
                    if(mAuth.getCurrentUser().isEmailVerified()){

                        DocumentReference usersRef = database.collection("users").document(mAuth.getCurrentUser().getUid());
                        usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() == null) Log.d(TAG, "getResult is null");
                                    Log.d(TAG, "getResult: " + task.getResult());
                                    DocumentSnapshot document = task.getResult();
                                    HashMap<String, Object> map = new HashMap<>();

                                    map.put("UID", document.get("UID"));
                                    map.put("email", document.get("email"));
                                    map.put("displayName", document.get("displayName"));
                                    map.put("imageURL", document.get("imageURL"));
                                    map.put("chefRating", document.get("chefRating"));
                                    map.put("numRecipes", document.get("numRecipes"));
                                    map.put("preferences", document.get("preferences"));
                                    map.put("about", document.get("about"));
                                    map.put("shortPreferences", document.get("shortPreferences"));
                                    map.put("firstAppLaunch", document.get("firstAppLaunch"));
                                    map.put("firstPresentationLaunch", document.get("firstPresentationLaunch"));


                                    @SuppressWarnings("unchecked")
                                    HashMap<String, Object> preferences = (HashMap<String, Object>) document.get("preferences");

                                    @SuppressWarnings("unchecked")
                                    HashMap<String, Object> privacy = (HashMap<String, Object>) document.get("privacy");

                                    mUser = new UserInfoPrivate(map, preferences, privacy);

                                    Log.i(TAG, "Successfully logged back in");
                                    if(mUser.getFirstAppLaunch()){
                                        Log.e(TAG,"Sending user to initial preference setup page");
                                        Intent initialCustom = new Intent(getApplicationContext(), InitialUserCustomisation.class);
                                        initialCustom.putExtra("user", mUser);
                                        startActivity(initialCustom);
                                    } else {
                                        Log.i(TAG, "Successfully logged back in");
                                        Intent returningIntent = new Intent(MainActivity.this, Home.class);

                                        returningIntent.putExtra("user", mUser);

                                        startActivity(returningIntent);
                                    }

                                }else {
                                    Log.e(TAG, "User details retrieval : Unable to retrieve user document in Firestore ");
                                    Toast.makeText(getApplicationContext(),"Unable to retrieve current user details, please sign in again.",Toast.LENGTH_SHORT).show();
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
                }else{
                    Log.e(TAG,"AUTHENTICATION STATE UPDATE : No Valid current user logged in");
                    mDisplayName = "No Valid User";
                    mAuth.removeAuthStateListener(mAuthListener);
                    Intent signIn = new Intent(getApplicationContext(), Login.class);
                    startActivity(signIn);
                    mUser = (UserInfoPrivate) getIntent().getSerializableExtra("user");


                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    // Disable user from pressing back button on main activity page
    @Override
    public void onBackPressed() {
        //Do nothing
    }

}
