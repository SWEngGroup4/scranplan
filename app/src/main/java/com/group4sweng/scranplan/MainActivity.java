package com.group4sweng.scranplan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Context mContext = this;

    final static String TAG = "FirebaseTest";
    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    final static int PROFILE_SETTINGS_REQUEST_CODE = 1;

    UserInfoPrivate mUser;

    FirebaseAuth mAuth;
    FirebaseApp mApp;

    FirebaseAuth.AuthStateListener mAuthListener;
    String mDisplayName;

    TabLayout tabLayout;
    FrameLayout frameLayout;
    SideMenu mSideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mUser != null){
            mUser = (UserInfoPrivate) getIntent().getSerializableExtra("user");
        }

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        // Drawer setup and and synchronising the states
        mSideMenu = new SideMenu();
        mSideMenu.mMenuToolbar = findViewById(R.id.toolbar);
        mSideMenu.mMenuDrawer = findViewById(R.id.drawer_layout);
        mSideMenu.mNavigationView = findViewById(R.id.side_menu);
        setSupportActionBar(mSideMenu.mMenuToolbar);
        mSideMenu.init(this, this);


        initFirebase();
        initPageItems();
        initPageListeners();

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }



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

    private void initPageItems(){
        //Defining all relevant members of signin & register page
        tabLayout = findViewById(R.id.tabLayout);
        frameLayout = findViewById(R.id.frameLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Recipes"));
        tabLayout.addTab(tabLayout.newTab().setText("Meal Planner"));
        tabLayout.addTab(tabLayout.newTab().setText("Timeline"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void initPageListeners() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new RecipeFragment();
                        break;
                    case 1:
                        fragment = new PlannerFragment();
                        break;
                    case 2:
                        fragment = new TimelinePlanner();
                        break;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case (PROFILE_SETTINGS_REQUEST_CODE):
                if (resultCode == RESULT_OK) {
                    mUser = (UserInfoPrivate) getIntent().getSerializableExtra("user");
                }
                break;
            default:
                Log.e(TAG, "I am not returning anything. Should return new profile settings from Profile Settings Activity.");
        }
    }

    /**
     * Method to change Intent when profile is clicked in the side menu
     */
    public void onPublicProfileClick() {
        Intent intentProfile = new Intent(this, PublicProfile.class);

        //intentProfile.putExtra("UID", mUser.getUID()); // For the profile we use the local users UID.
        intentProfile.putExtra("user", mUser); // Not required but used for efficiency. Means we aren't loading from Firebase each time.
        setResult(RESULT_OK, intentProfile);
        startActivity(intentProfile);
    }

    /**
     * Method to change Intent when profile edit is clicked in the side menu
     */
    public void onProfileEditClick() {

        Intent intentProfile = new Intent(this, ProfileSettings.class);
        intentProfile.putExtra("user", mUser);

        setResult(RESULT_OK, intentProfile);
        startActivityForResult(intentProfile, PROFILE_SETTINGS_REQUEST_CODE);

    }

    /**
     * Method to change logout when it's clicked in the side menu
     */
    public void onLogoutMenuClick(){
        mUser = null;
        mAuth.signOut();
    }


}
