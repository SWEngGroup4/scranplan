package com.group4sweng.scranplan;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.group4sweng.scranplan.SearchFunctions.SearchListFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.SearchFunctions.SearchQuery;
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

//    FragmentManager fragmentManager = getSupportFragmentManager();
//    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    ImageView mLogoHomeImage;


//    Button mLogoutButton;
//    TabLayout tabLayout;
//    FrameLayout frameLayout;
//
//    AlertDialog.Builder builder;
//    AlertDialog alertDialog;
//
//    SearchQuery query;
//    SearchPrefs prefs;
//
//    //Menu check boxes
//    CheckBox mPescatarianBox;
//    CheckBox mVegetarianBox;
//    CheckBox mVeganBox;
//    CheckBox mNutsBox;
//    CheckBox mMilkBox;
//    CheckBox mEggsBox;
//    CheckBox mWheatBox;
//    CheckBox mShellfishBox;
//    CheckBox mSoyBox;
//    CheckBox mScoreBox;
//    CheckBox mVoteBox;
//    CheckBox mTimeBox;
//    CheckBox mIngredientsBox;
//    CheckBox mNameBox;
//    CheckBox mChefBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG,"??????????????????????????????????????????????????????????? here");

//        if(mUser != null){
//            mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getIntent().getSerializableExtra("user");
//        }
        if(getIntent().getSerializableExtra("user") != null){
            mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getIntent().getSerializableExtra("user");
        }

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().hide();
        initPageItems();
        rotateImageClockwise(mLogoHomeImage);
        initFirebase();


        // Drawer setup and and synchronising the states
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        initFirebase();
//        initPageItems();
//        initPageListeners();
//        initSearchMenu();

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    public void initPageItems(){
        mLogoHomeImage = findViewById(R.id.logoHomeImage);
    }

    public void rotateImageClockwise(View view) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 360f, 0f);
        rotate.setRepeatCount(999);
        rotate.setDuration(1000);
        rotate.start();
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

                                    @SuppressWarnings("unchecked")
                                    HashMap<String, Object> preferences = (HashMap<String, Object>) document.get("preferences");

                                    @SuppressWarnings("unchecked")
                                    HashMap<String, Object> privacy = (HashMap<String, Object>) document.get("privacy");

                                    mUser = new UserInfoPrivate(map, preferences, privacy);

                                    Log.i(TAG, "Successfully logged back in");
                                    Intent returningIntent = new Intent(MainActivity.this, Home.class);

                                    returningIntent.putExtra("user", mUser);

                                    startActivity(returningIntent);

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







//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_search, menu);
//        MenuItem item = menu.findItem(R.id.menuSearch);
//        SearchView searchView = (SearchView)item.getActionView();
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                // Search function
//                query = new SearchQuery( s, prefs);
//                SearchListFragment searchListFragment = new SearchListFragment();
//                searchListFragment.setValue(query);
//                Log.e(TAG, "I'm pressing it boss");
//                searchListFragment.show(fragmentManager, "test");
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return false;
//            }
//        });
//
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    //Handle action bar button press
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.menuSortButton) {
//            alertDialog.show();
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    private void initPageItems(){
//        //Defining all relevant members of signin & register page
//        mLogoutButton = (Button) findViewById(R.id.logoutButton);
//        tabLayout = findViewById(R.id.tabLayout);
//        frameLayout = findViewById(R.id.frameLayout);
//
//        Fragment fragment = new RecipeFragment();
//        fragmentTransaction.replace(R.id.frameLayout, fragment);
//        fragmentTransaction.commit();
//
//        tabLayout.addTab(tabLayout.newTab().setText("Recipes"));
//        tabLayout.addTab(tabLayout.newTab().setText("Meal Planner"));
//        tabLayout.addTab(tabLayout.newTab().setText("Timeline"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//    }

//    private void initPageListeners() {
//        mLogoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e(TAG, "Logout button has been pressed and user has been logged out.");
//               mUser = null;
//                mAuth.signOut();
//            }
//        });
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                Fragment fragment = null;
//                switch (tab.getPosition()) {
//                    case 0:
//                        fragment = new RecipeFragment();
//                        break;
//                    case 1:
//                        fragment = new PlannerFragment();
//                        break;
//                    case 2:
//                        fragment = new TimelinePlanner();
//                        break;
//                }
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.frameLayout, fragment);
//                fragmentTransaction.commit();
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//      /
//        final Button tempProfileSettings = findViewById(R.id.profile_settings_button);
//        tempProfileSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)  {
//                tempOpenProfileSettings();
//            }
//        });
//
//        final Button tempPublicProfile = findViewById(R.id.public_profile_button);
//        tempPublicProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)  {
//                tempOpenPublicProfile();
//            }
//        });
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch(requestCode) {
//            case (PROFILE_SETTINGS_REQUEST_CODE):
//                if (resultCode == RESULT_OK) {
//                    mUser = (UserInfoPrivate) getIntent().getSerializableExtra("user");
//                }
//                break;
//            default:
//                Log.e(TAG, "I am not returning anything. Should return new profile settings from Profile Settings Activity.");
//        }
//    }
//
//    public void tempOpenPublicProfile() {
//        Intent intentProfile = new Intent(this, PublicProfile.class);
//
//        intentProfile.putExtra("user", mUser);
//        //setResult(RESULT_OK, intentProfile);
//        startActivity(intentProfile);
//    }
//
//    public void tempOpenProfileSettings() {
//        Intent intentProfile = new Intent(this, ProfileSettings.class);
//        intentProfile.putExtra("user", mUser);
//
//        setResult(RESULT_OK, intentProfile);
//        startActivityForResult(intentProfile, PROFILE_SETTINGS_REQUEST_CODE);
//
//    }
//
//    public void initMenuCheckBoxes(){
//        mScoreBox.setChecked(true);
//        mIngredientsBox.setChecked(true);
//
//        mPescatarianBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (mPescatarianBox.isChecked()) {
//                    mVegetarianBox.setChecked(false);
//                    mVeganBox.setChecked(false);
//                }
//            }
//        });
//        mVeganBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (mVeganBox.isChecked()) {
//                    mVegetarianBox.setChecked(false);
//                    mPescatarianBox.setChecked(false);
//                }
//            }
//        });
//        mVegetarianBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (mVegetarianBox.isChecked()) {
//                    mVeganBox.setChecked(false);
//                    mPescatarianBox.setChecked(false);
//                }
//            }
//        });
//
//        mScoreBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (mScoreBox.isChecked()) {
//                    mVoteBox.setChecked(false);
//                    mTimeBox.setChecked(false);
//                }else if(!mVoteBox.isChecked() && !mTimeBox.isChecked()){
//                    mScoreBox.setChecked(true);
//                }
//            }
//        });
//
//        mVoteBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (mVoteBox.isChecked()) {
//                    mScoreBox.setChecked(false);
//                    mTimeBox.setChecked(false);
//                }else if(!mScoreBox.isChecked() && !mTimeBox.isChecked()){
//                    mVoteBox.setChecked(true);
//                }
//            }
//        });
//
//        mTimeBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (mTimeBox.isChecked()) {
//                    mVoteBox.setChecked(false);
//                    mScoreBox.setChecked(false);
//                }else if(!mVoteBox.isChecked() && !mScoreBox.isChecked()){
//                    mTimeBox.setChecked(true);
//                }
//            }
//        });
//
//        mChefBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (mChefBox.isChecked()) {
//                    mIngredientsBox.setChecked(false);
//                    mNameBox.setChecked(false);
//                }else if(!mNameBox.isChecked() && !mIngredientsBox.isChecked()){
//                    mChefBox.setChecked(true);
//                }
//            }
//        });
//
//        mIngredientsBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (mIngredientsBox.isChecked()) {
//                    mChefBox.setChecked(false);
//                    mNameBox.setChecked(false);
//                }else if(!mNameBox.isChecked() && !mChefBox.isChecked()){
//                    mIngredientsBox.setChecked(true);
//                }
//            }
//        });
//
//        mNameBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (mNameBox.isChecked()) {
//                    mChefBox.setChecked(false);
//                    mIngredientsBox.setChecked(false);
//                }else if(!mChefBox.isChecked() && !mIngredientsBox.isChecked()){
//                    mNameBox.setChecked(true);
//                }
//            }
//        });
//    }
//
//    public void initSearchMenu(){
//        LayoutInflater inflater = (LayoutInflater)
//                getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(R.layout.filter_tab_dialog,
//                (ViewGroup) findViewById(R.id.tabhost));
//
//
//
//        TabHost tabs = (TabHost) layout.findViewById(R.id.tabhost);
//        tabs.setup();
//        TabHost.TabSpec tabpage1 = tabs.newTabSpec("type");
//        tabpage1.setContent(R.id.ScrollView01);
//        tabpage1.setIndicator("Type");
//        TabHost.TabSpec tabpage2 = tabs.newTabSpec("type");
//        tabpage2.setContent(R.id.ScrollView02);
//        tabpage2.setIndicator("Diet");
//        TabHost.TabSpec tabpage3 = tabs.newTabSpec("sort");
//        tabpage3.setContent(R.id.ScrollView03);
//        tabpage3.setIndicator("Sort");
//        tabs.addTab(tabpage1);
//        tabs.addTab(tabpage2);
//        tabs.addTab(tabpage3);
//        mPescatarianBox = layout.findViewById(R.id.menuPescatarianCheckBox);
//        mVegetarianBox = layout.findViewById(R.id.menuVegCheckBox);
//        mVeganBox = layout.findViewById(R.id.menuVeganCheckBox);
//        mNutsBox = layout.findViewById(R.id.menuNutCheckBox);
//        mEggsBox = layout.findViewById(R.id.menuEggCheckBox);
//        mMilkBox = layout.findViewById(R.id.menuMilkCheckBox);
//        mWheatBox = layout.findViewById(R.id.menuWheatCheckBox);
//        mShellfishBox = layout.findViewById(R.id.menuShellfishCheckBox);
//        mSoyBox = layout.findViewById(R.id.menuSoyCheckBox);
//        mScoreBox = layout.findViewById(R.id.scoreCheckBox);
//        mVoteBox = layout.findViewById(R.id.voteCheckBox);
//        mTimeBox = layout.findViewById(R.id.timestampCheckBox);
//        mIngredientsBox = layout.findViewById(R.id.ingredientCheckBox);
//        mNameBox = layout.findViewById(R.id.nameCheckBox);
//        mChefBox = layout.findViewById(R.id.chefCheckBox);
//
//        initMenuCheckBoxes();
//
//        builder = new AlertDialog.Builder(MainActivity.this);
//
//
//        builder
//                .setCancelable(false)
//                .setPositiveButton("Ok",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                prefs = new SearchPrefs(mPescatarianBox.isChecked(), mVegetarianBox.isChecked(), mVeganBox.isChecked(), mNutsBox.isChecked(),
//                                        mMilkBox.isChecked(), mEggsBox.isChecked(), mWheatBox.isChecked(), mShellfishBox.isChecked(), mSoyBox.isChecked(),
//                                        mScoreBox.isChecked(), mVoteBox.isChecked(), mTimeBox.isChecked(),mIngredientsBox.isChecked(), mNameBox.isChecked(),
//                                        mChefBox.isChecked());
//
//                            }
//                        })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//        builder.setTitle("Search options");
//        builder.setView(layout);
//        alertDialog = builder.create();
//
//        TextView tv;
//        tv = (TextView)tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
//        tv.setTextColor(Color.GRAY);
//        tv = (TextView)tabs.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
//        tv.setTextColor(Color.GRAY);
//        tv = (TextView)tabs.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
//        tv.setTextColor(Color.GRAY);
//    }

}
