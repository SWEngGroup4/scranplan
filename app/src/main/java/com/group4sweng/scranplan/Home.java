
package com.group4sweng.scranplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.Administration.SuggestionBox;
import com.group4sweng.scranplan.MealPlanner.PlannerFragment;
import com.group4sweng.scranplan.RecipeCreation.RecipeCreation;
import com.group4sweng.scranplan.SearchFunctions.RecipeFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchListFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.SearchFunctions.SearchQuery;
import com.group4sweng.scranplan.Social.FeedFragment;
import com.group4sweng.scranplan.Social.Messenger.MessengerMenu;
import com.group4sweng.scranplan.Social.Notifications;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import io.sentry.core.Sentry;
import io.sentry.core.protocol.User;

/**
 *  Scran plan home page, giving the user easy navigation around the application and database
 */
public class Home extends AppCompatActivity {
    Context mContext = this;
    final static String TAG = "ScranPlanHome";
    final static int PROFILE_SETTINGS_REQUEST_CODE = 1;
    private SuggestionBox suggestionBox;
    final static int CREATE_RECIPE_REQUEST_CODE = 2;

    // User variable for all preferences saved to device
    private UserInfoPrivate mUser;
    boolean updateFilters = false;

    // Firebase variables
    FirebaseAuth mAuth;
    FirebaseApp mApp;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();

    // Main tab variables
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    Fragment recipeFragment;
    Fragment plannerFragment;
    Fragment feedFragment;
    Fragment fragment;
    TabLayout tabLayout;
    FrameLayout frameLayout;

    // Filter menu variables
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    // Search variables
    SearchView searchView;
    MenuItem sortView;
    SearchQuery query;
    SearchPrefs prefs;

    //Menu check boxes
    CheckBox mPescatarianBox;
    CheckBox mVegetarianBox;
    CheckBox mVeganBox;
    CheckBox mNutsBox;
    CheckBox mMilkBox;
    CheckBox mEggsBox;
    CheckBox mWheatBox;
    CheckBox mShellfishBox;
    CheckBox mSoyBox;
    CheckBox mScoreBox;
    CheckBox mVoteBox;
    CheckBox mTimeBox;
    CheckBox mIngredientsBox;
    CheckBox mNameBox;
    CheckBox mChefBox;
    SideMenu mSideMenu;
    int previousTab;


    String notifications;
    MenuItem notificationMenuItem;

    //Floating action button menu
    private FloatingActionButton fabMain, fabRecipe, fabPost;
    private TextView textViewRecipe, textViewPost;
    private Animation fab_open, fab_close;
    private Boolean isOpen = false;

    // Side menu variable
    NavigationView navigationView;

    /**
     * Passing through user information from login and root pages and setting up the page
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Passed in user information
        if (getIntent().getSerializableExtra("user") != null) {
            mUser = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) getIntent().getSerializableExtra("user");
            prefs = new SearchPrefs(mUser);

            User mSentryUser = new User();
            mSentryUser.setUsername(mUser.getDisplayName());
            mSentryUser.setEmail(mUser.getEmail());
            Sentry.setUser(mSentryUser);
        }
        /*
        // Setting up the action and tab bars
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer setup and and synchronising the states
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        // Drawer setup and and synchronising the states
        mSideMenu = new SideMenu();
        mSideMenu.mMenuToolbar = findViewById(R.id.toolbar);
        mSideMenu.mMenuDrawer = findViewById(R.id.drawer_layout);
        mSideMenu.mNavigationView = findViewById(R.id.side_menu);
        notificationMenuItem = mSideMenu.mNavigationView.getMenu().getItem(1);
        setSupportActionBar(mSideMenu.mMenuToolbar);
        mSideMenu.init(this, this);

        // Page initialises
        initFirebase();
        initPageItems();
        initPageListeners();
        initSearchMenu();
        checkForNotifications();
    }

    protected void onResume() {
        super.onResume();
        if(mUser != null){
            mPescatarianBox.setChecked(mUser.getPreferences().isPescatarian());
            mVegetarianBox.setChecked(mUser.getPreferences().isVegetarian());
            mVeganBox.setChecked(mUser.getPreferences().isVegan());
            mNutsBox.setChecked(mUser.getPreferences().isAllergy_nuts());
            mEggsBox.setChecked(mUser.getPreferences().isAllergy_eggs());
            mMilkBox.setChecked(mUser.getPreferences().isAllergy_milk());
            mWheatBox.setChecked(mUser.getPreferences().isAllergy_gluten());
            mShellfishBox.setChecked(mUser.getPreferences().isAllergy_shellfish());
            mSoyBox.setChecked(mUser.getPreferences().isAllergy_soya());
        }
    }

    /**
     *  Setting up the search menu within the action bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Building the search bar within the action button
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView = (SearchView)item.getActionView();
        sortView = menu.findItem(R.id.menuSortButton);

        // Adding the listener to search for string provided by user
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Search function
                query = new SearchQuery( s, prefs);
                SearchListFragment searchListFragment = new SearchListFragment(mUser);
                searchListFragment.setValue(query.getQuery());
                searchListFragment.setIndex(query.getIndex());
                Log.e(TAG, "User opening search");
                searchListFragment.show(fragmentManager, "search");
                return false;
            }

            // Change in text function currently not used as the recipe fragment is extended to
            // cover the screen, this minimised firebase reads through any changes.
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    /**
     *  Setting open menu button on action bar to open filter menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Opening the filter menu
        int id = item.getItemId();
        if (id == R.id.menuSortButton) {
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     *  Creating an instance of Firebase Auth and App and connecting these to the class variables
     */
    private void initFirebase(){
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }

    private void checkForNotifications(){
        if(mUser != null){
            database.collection("users").document(mUser.getUID()).collection("notifications").orderBy("timestamp", Query.Direction.DESCENDING).limit(6).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult() != null){
                            if((task.getResult().size()) == 6){
                                notifications = "5+";
                            }else{
                                notifications = Integer.toString(task.getResult().size());
                            }
                            notificationMenuItem.setTitle("Notifications (" + notifications + ")");
                        }else{
                            notificationMenuItem.setTitle("Notifications (0)");
                        }

                    }
                }
            });
        }
    }

    /**
     *  Connecting up elements on the screen to variable names
     */
    private void initPageItems(){
        //Defining all relevant members of signin & register page
        tabLayout = findViewById(R.id.tabLayout);
        frameLayout = findViewById(R.id.frameLayout);
        fragment = new RecipeFragment(mUser);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit ();
        navigationView = (NavigationView) findViewById(R.id.side_menu);
        tabLayout.addTab(tabLayout.newTab().setText("Recipes"));
        tabLayout.addTab(tabLayout.newTab().setText("Meal Planner"));
        tabLayout.addTab(tabLayout.newTab().setText("Feed"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        fabMain = findViewById(R.id.fabMain);
        fabRecipe = findViewById(R.id.fabRecipe);
        fabPost = findViewById(R.id.fabPost);
        textViewRecipe = findViewById(R.id.textViewRecipe);
        textViewPost = findViewById(R.id.textViewPost);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);

        recipeFragment = new RecipeFragment(mUser);
        plannerFragment = new PlannerFragment(mUser);
        feedFragment = new FeedFragment(mUser);

        previousTab = 0;
    }

    /**
     *  Setting up page listeners for when buttons are pressed on the home screen
     */
    private void initPageListeners() {
        // Side menu is used to give the buttons that were on the main screen functionality
        // this includes logout, profile and settings
        // Setting up the side menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_publicProfile:
                        Intent intentProfile = new Intent(mContext, PublicProfile.class);

                        intentProfile.putExtra("user", mUser);
                        startActivity(intentProfile);
                        break;
                    case R.id.nav_notifications:
                        Notifications notificationDialogFragment = new Notifications(mUser);
                        notificationDialogFragment.show(fragmentManager, "Show notification dialog fragment");
                        break;
                    case R.id.nav_editProfile:
                        Intent intentSettings = new Intent(mContext, ProfileSettings.class);
                        intentSettings.putExtra("user", mUser);

                        setResult(RESULT_OK, intentSettings);
                        startActivityForResult(intentSettings, PROFILE_SETTINGS_REQUEST_CODE);
                        break;
                    case R.id.nav_suggestionBox:

                        onSuggestionBoxClick();

                        break;
                    case R.id.nav_logout:
                        Log.e(TAG, "Logout button has been pressed and user has been logged out.");
                        mUser = null;
                        mAuth.signOut();
                        Intent returningIntent = new Intent(Home.this, MainActivity.class);
                        startActivity(returningIntent);

                        finish();
                    case R.id.nav_messenger:
                        Log.e(TAG, "Messenger Has been entered");
                        Intent intentMessenger = new Intent(mContext, MessengerMenu.class);
                        intentMessenger.putExtra("user", mUser);
                        startActivity(intentMessenger);
                        break;

                    default:
                        return false;
                }
                return false;
            }
        });


        // Listener for layout tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                checkForNotifications();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Log.d(TAG, String.valueOf(fragment));
                switch (tab.getPosition()) {
                    case 0:
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        fragmentTransaction.replace(R.id.frameLayout, recipeFragment);
                        fabMain.setVisibility(View.VISIBLE);
                        previousTab = 0;
                        break;
                    case 1:
                        if(previousTab == 0){
                            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                        } else if (previousTab == 2){
                            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        }
                        fragmentTransaction.replace(R.id.frameLayout, plannerFragment);
                        fabMain.setVisibility(View.GONE);
                        previousTab = 1;
                        break;
                    case 2:
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                        fragmentTransaction.replace(R.id.frameLayout, feedFragment);
                        fabMain.setVisibility(View.GONE);
                        previousTab = 2;
                        break;
                }
                fragmentTransaction.commit();
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        fabMain.setOnClickListener(v -> {
            fabMainListener(isOpen);
        });

        fabMain.setOnFocusChangeListener((v, hasFocus) -> {
            fabMainListener(hasFocus);
        });

        fabRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, RecipeCreation.class);
            intent.putExtra("user", mUser);
            startActivityForResult(intent, CREATE_RECIPE_REQUEST_CODE);
            fabMainListener(true);
        });

        fabPost.setOnClickListener(v -> {
            tabLayout.getTabAt(2).select();
            Toast.makeText(getApplicationContext(), "Create post", Toast.LENGTH_SHORT).show();
            fabMainListener(true);
            fabMain.setVisibility(View.GONE);
        });
    }

    void fabMainListener(Boolean condition) {
        if (condition) {
            textViewRecipe.setVisibility(View.INVISIBLE);
            textViewPost.setVisibility(View.INVISIBLE);
            fabRecipe.startAnimation(fab_close);
            fabRecipe.setClickable(false);
            fabPost.startAnimation(fab_close);
            fabPost.setClickable(false);
            isOpen = false;
        } else {
            textViewRecipe.setVisibility(View.VISIBLE);
            textViewPost.setVisibility(View.VISIBLE);
            fabRecipe.startAnimation(fab_open);
            fabRecipe.setClickable(true);
            fabPost.startAnimation(fab_open);
            fabPost.setClickable(true);
            isOpen = true;
        }
    }

    // Quick function for getting the search menu in other fragments
    public SearchView getSearchView() {
        return searchView;
    }

    public MenuItem getSortView() {
        return sortView;
    }

    // Quick function for getting the search prefs in other fragments
    public SearchPrefs getSearchPrefs() {
        return prefs;
    }

    /**
     * Getting serializable data from other activities
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PROFILE_SETTINGS_REQUEST_CODE):
                if (resultCode == RESULT_OK) {
                    mUser = (UserInfoPrivate) data.getSerializableExtra("user");

                    // If user edited filters in edit profile. Update all recipes in scrollview.
                    boolean updateFilters = data.getBooleanExtra("updateFilters", false);

                    if(updateFilters){ // If filters have updated, reload the recipe fragment with new user info.
                        recipeFragment = null;

                        recipeFragment = new RecipeFragment(mUser);
                        /*plannerFragment = new PlannerFragment(mUser);
                        feedFragment = new FeedFragment(mUser);*/
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout, recipeFragment);
                        fragmentTransaction.commit();
                    }
                }
                break;
            case (CREATE_RECIPE_REQUEST_CODE):
                if (resultCode == RESULT_OK) {
                        // Reloads recipe fragment
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, new RecipeFragment(mUser));
                    fragmentTransaction.commit();
                }
            default:
                Log.e(TAG, "I am not returning anything. Should return new profile settings from Profile Settings Activity.");
        }
    }

    /**
     *  Initialise all check boxes to user preferences and ensure that queries are only query that is allowed
     */
    public void initMenuCheckBoxes(TabHost tabs){
        // Ensure that only the correct boxes are ticked at any one time
        mPescatarianBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mPescatarianBox.isChecked()) {
                    mVegetarianBox.setChecked(false);
                    mVeganBox.setChecked(false);
                }
            }
        });
        mVeganBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mVeganBox.isChecked()) {
                    mVegetarianBox.setChecked(false);
                    mPescatarianBox.setChecked(false);
                }
            }
        });
        mVegetarianBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mVegetarianBox.isChecked()) {
                    mVeganBox.setChecked(false);
                    mPescatarianBox.setChecked(false);
                }
            }
        });

        mScoreBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mScoreBox.isChecked()) {
                    mVoteBox.setChecked(false);
                    mTimeBox.setChecked(false);
                }else if(!mVoteBox.isChecked() && !mTimeBox.isChecked()){
                    mScoreBox.setChecked(true);
                }
            }
        });

        mVoteBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mVoteBox.isChecked()) {
                    mScoreBox.setChecked(false);
                    mTimeBox.setChecked(false);
                }else if(!mScoreBox.isChecked() && !mTimeBox.isChecked()){
                    mVoteBox.setChecked(true);
                }
            }
        });

        mTimeBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mTimeBox.isChecked()) {
                    mVoteBox.setChecked(false);
                    mScoreBox.setChecked(false);
                }else if(!mVoteBox.isChecked() && !mScoreBox.isChecked()){
                    mTimeBox.setChecked(true);
                }
            }
        });

        mChefBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mChefBox.isChecked()) {
                    mIngredientsBox.setChecked(false);
                    mNameBox.setChecked(false);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.GONE);
                    tabs.setCurrentTab(1);
                    tabs.getCurrentTabView().setVisibility(View.GONE);
                    tabs.setCurrentTab(0);
                }else if(!mNameBox.isChecked() && !mIngredientsBox.isChecked()){
                    mChefBox.setChecked(true);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.GONE);
                    tabs.setCurrentTab(0);
                }
            }
        });

        mIngredientsBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mIngredientsBox.isChecked()) {
                    mChefBox.setChecked(false);
                    mNameBox.setChecked(false);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.VISIBLE);
                    tabs.setCurrentTab(1);
                    tabs.getCurrentTabView().setVisibility(View.VISIBLE);
                    tabs.setCurrentTab(0);
                }else if(!mNameBox.isChecked() && !mChefBox.isChecked()){
                    mIngredientsBox.setChecked(true);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.VISIBLE);
                    tabs.setCurrentTab(0);
                }
            }
        });

        mNameBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mNameBox.isChecked()) {
                    mChefBox.setChecked(false);
                    mIngredientsBox.setChecked(false);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.VISIBLE);
                    tabs.setCurrentTab(1);
                    tabs.getCurrentTabView().setVisibility(View.VISIBLE);
                    tabs.setCurrentTab(0);
                }else if(!mChefBox.isChecked() && !mIngredientsBox.isChecked()){
                    mNameBox.setChecked(true);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.GONE);
                    tabs.setCurrentTab(0);
                }
            }
        });
        if(mUser != null){
            mPescatarianBox.setChecked(mUser.getPreferences().isPescatarian());
            mVegetarianBox.setChecked(mUser.getPreferences().isVegetarian());
            mVeganBox.setChecked(mUser.getPreferences().isVegan());
            mNutsBox.setChecked(mUser.getPreferences().isAllergy_nuts());
            mEggsBox.setChecked(mUser.getPreferences().isAllergy_eggs());
            mMilkBox.setChecked(mUser.getPreferences().isAllergy_milk());
            mWheatBox.setChecked(mUser.getPreferences().isAllergy_gluten());
            mShellfishBox.setChecked(mUser.getPreferences().isAllergy_shellfish());
            mSoyBox.setChecked(mUser.getPreferences().isAllergy_soya());
        }
        // Set up user preferences
        mScoreBox.setChecked(true);
        mIngredientsBox.setChecked(true);

    }

    /**
     *  Initial set up of the search menu that enables the user to select search filters and
     *  sorting.
     */
    public void initSearchMenu(){
        // Build the inflater alert dialog
        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        // fill the alter dialogue with tabs to enable the user to switch between the filters,
        // sorting and what to search for
        View layout = inflater.inflate(R.layout.filter_tab_dialog,
                (ViewGroup) findViewById(R.id.tabhost));
        // Setting up the tabs and giving them names
        TabHost tabs = (TabHost) layout.findViewById(R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec tabpage1 = tabs.newTabSpec("type");
        tabpage1.setContent(R.id.ScrollView01);
        tabpage1.setIndicator("Type");
        TabHost.TabSpec tabpage2 = tabs.newTabSpec("type");
        tabpage2.setContent(R.id.ScrollView02);
        tabpage2.setIndicator("Diet");
        TabHost.TabSpec tabpage3 = tabs.newTabSpec("sort");
        tabpage3.setContent(R.id.ScrollView03);
        tabpage3.setIndicator("Sort");


        // Adding the XML for each tab
        tabs.addTab(tabpage1);
        tabs.addTab(tabpage2);
        tabs.addTab(tabpage3);

        // Connecting variables up to each component within the tabs
        mPescatarianBox = layout.findViewById(R.id.menuPescatarianCheckBox);
        mVegetarianBox = layout.findViewById(R.id.menuVegCheckBox);
        mVeganBox = layout.findViewById(R.id.menuVeganCheckBox);
        mNutsBox = layout.findViewById(R.id.menuNutCheckBox);
        mEggsBox = layout.findViewById(R.id.menuEggCheckBox);
        mMilkBox = layout.findViewById(R.id.menuMilkCheckBox);
        mWheatBox = layout.findViewById(R.id.menuWheatCheckBox);
        mShellfishBox = layout.findViewById(R.id.menuShellfishCheckBox);
        mSoyBox = layout.findViewById(R.id.menuSoyCheckBox);
        mScoreBox = layout.findViewById(R.id.scoreCheckBox);
        mVoteBox = layout.findViewById(R.id.voteCheckBox);
        mTimeBox = layout.findViewById(R.id.timestampCheckBox);
        mIngredientsBox = layout.findViewById(R.id.ingredientCheckBox);
        mNameBox = layout.findViewById(R.id.nameCheckBox);
        mChefBox = layout.findViewById(R.id.chefCheckBox);

        // Initialise the check boxes by filling them with users current preferences
        initMenuCheckBoxes(tabs);

        // add the alert dialogue to the current context
        builder = new AlertDialog.Builder(Home.this);
        // Set listeners for the button presses for the dialogue
        builder
                .setCancelable(false)
                // if positive picked then create a new preferences variable to reflect what the user has selected
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                prefs = new SearchPrefs(mPescatarianBox.isChecked(), mVegetarianBox.isChecked(), mVeganBox.isChecked(), mNutsBox.isChecked(),
                                        mMilkBox.isChecked(), mEggsBox.isChecked(), mWheatBox.isChecked(), mShellfishBox.isChecked(), mSoyBox.isChecked(),
                                        mScoreBox.isChecked(), mVoteBox.isChecked(), mTimeBox.isChecked(),mIngredientsBox.isChecked(), mNameBox.isChecked(),
                                        mChefBox.isChecked());
                                storeSearchOptions();
                            }
                        })
                .setNegativeButton("Cancel",
                        // If negative button clicked then cancel the action of changing user prefs
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.setTitle("Search options");
        builder.setView(layout);
        alertDialog = builder.create();

        // Change the colour of the tabs to grey
        TextView tv;
        tv = (TextView)tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tv.setTextColor(Color.GRAY);
        tv = (TextView)tabs.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        tv.setTextColor(Color.GRAY);
        tv = (TextView)tabs.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
        tv.setTextColor(Color.GRAY);
    }
    public void resetFilters(){
        // RESET filters for testing DO NOT USE IN PROD.
        //  Store checkbox values within shared preferences.
        SharedPreferences Preference = getSharedPreferences("filter_preferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor e = Preference.edit();

        //  Filters
        e.putBoolean("vegetarian_filter", false);
        e.putBoolean("vegan_filter", false);
        e.putBoolean("pesc_filter", false);
        e.putBoolean("allergy_nuts", false);
        e.putBoolean("allergy_eggs", false);
        e.putBoolean("allergy_shellfish", false);
        e.putBoolean("allergy_soy", false);
        e.putBoolean("allergy_gluten", false);

        //  Sorting
        e.putBoolean("sorting_score", true);
        e.putBoolean("sorting_votes", false);
        e.putBoolean("sorting_timestamp", false);

        //  Type
        e.putBoolean("type_ingredients", true);
        e.putBoolean("type_name", false);
        e.putBoolean("type_chef", false);

        e.apply();
    }

    /** Store shared preferences search options */
    private void storeSearchOptions(){
        //  Store checkbox values within shared preferences.
        SharedPreferences Preference = getSharedPreferences("filter_preferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor e = Preference.edit();

        //  Filters
        e.putBoolean("vegetarian_filter", mVegetarianBox.isChecked());
        e.putBoolean("vegan_filter", mVeganBox.isChecked());
        e.putBoolean("pesc_filter", mPescatarianBox.isChecked());
        e.putBoolean("allergy_nuts", mNutsBox.isChecked());
        e.putBoolean("allergy_eggs", mEggsBox.isChecked());
        e.putBoolean("allergy_shellfish", mShellfishBox.isChecked());
        e.putBoolean("allergy_soy", mSoyBox.isChecked());
        e.putBoolean("allergy_gluten", mWheatBox.isChecked());
        e.putBoolean("allergy_milk", mMilkBox.isChecked());

        //  Sorting
        e.putBoolean("sorting_score", mScoreBox.isChecked());
        e.putBoolean("sorting_votes", mVoteBox.isChecked());
        e.putBoolean("sorting_timestamp", mTimeBox.isChecked());

        //  Type
        e.putBoolean("type_ingredients", mIngredientsBox.isChecked());
        e.putBoolean("type_name", mNameBox.isChecked());
        e.putBoolean("type_chef", mChefBox.isChecked());

        e.apply();
    }

    /** Load in shared preferences for search options. */
    private void loadSearchOptions(){
        SharedPreferences sp = getSharedPreferences("filter_preferences", Activity.MODE_PRIVATE);

        //  Filters
        mVeganBox.setChecked(sp.getBoolean("vegan_filter", false));
        mVegetarianBox.setChecked(sp.getBoolean("vegetarian_filter", false));
        mPescatarianBox.setChecked(sp.getBoolean("pesc_filter", false));
        mNutsBox.setChecked(sp.getBoolean("allergy_nuts", false));
        mMilkBox.setChecked(sp.getBoolean("allergy_milk", false));
        mShellfishBox.setChecked(sp.getBoolean("allergy_shellfish", false));
        mSoyBox.setChecked(sp.getBoolean("allergy_soy", false));
        mWheatBox.setChecked(sp.getBoolean("allergy_gluten", false));
        mEggsBox.setChecked(sp.getBoolean("allergy_eggs", false));

        //  Sorting
        mScoreBox.setChecked(sp.getBoolean("sorting_score", false));
        mVoteBox.setChecked(sp.getBoolean("sorting_votes", false));
        mTimeBox.setChecked(sp.getBoolean("sorting_timestamp", false));

        //  Type
        mIngredientsBox.setChecked(sp.getBoolean("type_ingredients", false));
        mNameBox.setChecked(sp.getBoolean("type_name", false));
        mChefBox.setChecked(sp.getBoolean("type_chef", false));
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

    public void onGoldClick() {
        GoldMembership goldMembership = new GoldMembership();
        goldMembership.show(getSupportFragmentManager(), "Show defaults fragment");
    }

    public void onSuggestionBoxClick(){

        suggestionBox = new SuggestionBox(Home.this, mUser.getUID());
        suggestionBox.startSuggestionDialog();
    }

    /**
     * Method to change logout when it's clicked in the side menu
     */
    public void onLogoutMenuClick(){
        mUser = null;
        mAuth.signOut();
    }

    // Disable user from pressing back button on home page
    @Override
    public void onBackPressed() {
        //Do nothing
    }
}
