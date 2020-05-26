package com.group4sweng.scranplan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.SearchFunctions.RecipeFragment;
import com.group4sweng.scranplan.Social.ProfilePictures;
import com.group4sweng.scranplan.Social.ProfilePosts;
import com.group4sweng.scranplan.Social.ProfileRecipes;
import com.group4sweng.scranplan.UserInfo.FilterType;
import com.group4sweng.scranplan.UserInfo.Kudos;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;

import static com.group4sweng.scranplan.UserInfo.FilterType.filterType.ALLERGENS;
import static com.group4sweng.scranplan.UserInfo.FilterType.filterType.DIETARY;

/**
 * Class retrieves and displays public profile data on a given user based on a valid UID input string or UserInfoPrivate object.
 * from Firebase or from local data.
 * Author(s): JButler, LNewman
 * (c) CoDev 2020
 *
 *  For firebase pass an extra string via intent with ID = "UID"
 *  For local data pass a UserInfoPrivate object via intent with ID = "user"
 *
 *  Privacy checks are made within 'loadInPrivacySettings' to make sure we adhere to what the user wants to display.
 *  Privacy is always checked first before displaying any content.
 */
public class PublicProfile extends AppCompatActivity implements FilterType{

    //  Check whether we should fully or partially load data from Firebase. Prevents to many queries, faster loading speed etc...
    enum FirebaseLoadType {
        FULL,
        PARTIAL
    }

    //  Public Profile TAG.
    final String TAG = "PublicProfile";

    //  UID for the user in which we want to retrieve data from.
    private String UID;
    protected UserInfoPrivate mUserProfile;
    private boolean followed = false;
    private boolean privateProfile;

    Fragment fragment;
    FrameLayout frameLayout;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    private String searchers;

    //  Default filter type enumeration. Types shown in 'FilterType' interface.
    FilterType.filterType currentFilterType = ALLERGENS;

    // Firebase user variables.
    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    //  Basic user settings.
    ImageView mProfileImage;
    TextView mUsername;
    TextView mAboutMe;
    TextView mAboutMeDesc;
    TextView mNumRecipes;
    TextView mKudos;
    ImageView mKudosIcon;
    TabLayout mStreamTabs;
    TextView mPosts;
    TextView mFollowers;
    TextView mFollowing;
    Button mFollowButton;
    Button mFollowedButton;
    Button mRequestedButton;
    LinearLayout mIsFollowLayout;

    //  Whether we should retrieve different information for the user. E.g. username, about me etc...
    private boolean retrieveAboutMe = false, retrieveUsername = false,
            retrieveImages = false, retrieveRecipes = false, retrieveFilters = false, retrievePosts = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        initPageItems();
        //  Gets extra string UID attached when an intent is sent.
        UID = getIntent().getStringExtra("UID");

        //  Gets extra UserProfilePrivate object if one is sent.
        mUserProfile = (UserInfoPrivate) getIntent().getSerializableExtra("user");


        if(UID != null){ // If not instead search for the profile via the associated UID and reference Firebase.
            updatePublicProfile(FirebaseLoadType.FULL);
        } else if(mUserProfile != null){ // Check if local data is available to reference. Don't have to grab from firebase.
            updatePublicProfile(FirebaseLoadType.PARTIAL);
        } else {
            Log.e(TAG, "Unable to retrieve extra UID intent string. Cannot initialize profile.");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    /** Update the public profile based on what has been passed via the Intent.
     * @param flt - Firebase Load Type. Either PARTIAL or FULL. FULL = all data from firebase from a UID string.
     *            PARTIAL = some data has already been loaded from the UserInfoPrivate object so we only need to grab
     *            some Firebase realtime data such as Kudos.
     */
    private void updatePublicProfile(FirebaseLoadType flt){
        if(flt == FirebaseLoadType.PARTIAL){
            Log.i(TAG, "Loading local user data");
            mIsFollowLayout.setVisibility(View.GONE);
            followed = true;
            loadInPrivacySettings(mUserProfile.getPublicPrivacy());
            loadLocalProfile();
            loadFirebase(FirebaseLoadType.PARTIAL);
            searchers = mUserProfile.getUID();
            fragment = new ProfilePosts(searchers);
            fragmentTransaction.replace(R.id.profileFrameLayout, fragment);
            fragmentTransaction.commit();
        } else if(UID != null){ // If not instead search for the profile via the associated UID and reference Firebase.
            Log.i(TAG, "Loading data from Firebase");
            searchers = UID;
            checkFollowed();
        } else {
            Log.e(TAG, "Unable to retrieve extra UID intent string. Cannot initialize profile.");
        }
        initPageListeners();

    }



    private void initPageItems(){
        mProfileImage = findViewById(R.id.public_profile_image);
        mAboutMeDesc = findViewById(R.id.public_profile_about_me_desc);
        mAboutMe = findViewById(R.id.profile_about_me);
        mNumRecipes = findViewById(R.id.profile_recipes);
        mUsername = findViewById(R.id.profile_username);
        mKudos = findViewById(R.id.profile_kudos);
        mKudosIcon = findViewById(R.id.profile_kudos_icon);
        mStreamTabs = findViewById(R.id.profileStreamTabs);

        frameLayout = findViewById(R.id.profileFrameLayout);

        mPosts = findViewById(R.id.postsNum);
        mFollowers = findViewById(R.id.followersNum);
        mFollowing = findViewById(R.id.followingNum);
        mFollowButton = findViewById(R.id.followButton);
        mFollowedButton = findViewById(R.id.followedButton);
        mRequestedButton = findViewById(R.id.requestedButton);
        mIsFollowLayout = findViewById(R.id.isFollowLayout);

    }

    private void initPageListeners(){
        mFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(privateProfile){
                    mFollowButton.setVisibility(View.GONE);
                    mRequestedButton.setVisibility(View.VISIBLE);
                    mDatabase.collection("followers").document(UID).update("requested", FieldValue.arrayUnion(mUserProfile.getUID()));
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("body", "has requested to follow you:");
                    map.put("ifRequested", true);
                    map.put("senderID", mUserProfile.getUID());
                    map.put("timestamp", FieldValue.serverTimestamp());
                    mDatabase.collection("users").document(UID).collection("notifications").add(map);
                }else{
                    mFollowButton.setVisibility(View.GONE);
                    mFollowedButton.setVisibility(View.VISIBLE);
                    mDatabase.collection("followers").document(UID).update("users", FieldValue.arrayUnion(mUserProfile.getUID()));
                    mDatabase.collection("users").document(UID).update("followers", FieldValue.increment(1));
                    String newFollowers = Integer.toString(Integer.parseInt(mFollowers.getText().toString())+1);
                    mFollowers.setText(newFollowers);
                    mDatabase.collection("users").document(mUserProfile.getUID()).update("following", FieldValue.increment(1));
                }

            }
        });

        mRequestedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFollowButton.setVisibility(View.VISIBLE);
                mRequestedButton.setVisibility(View.GONE);
                mDatabase.collection("followers").document(UID).update("requested", FieldValue.arrayRemove(mUserProfile.getUID()));
                mDatabase.collection("users").document(UID).collection("notifications")
                        .whereEqualTo("ifRequested", true).whereEqualTo("senderID", mUserProfile.getUID())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    }
                });
            }
        });

        mFollowedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFollowButton.setVisibility(View.VISIBLE);
                mFollowedButton.setVisibility(View.GONE);
                mDatabase.collection("followers").document(UID).update("users", FieldValue.arrayRemove(mUserProfile.getUID()));
                mDatabase.collection("users").document(UID).update("followers", FieldValue.increment(-1));
                mDatabase.collection("users").document(mUserProfile.getUID()).update("following", FieldValue.increment(-1));
                finish();
                startActivity(getIntent());
            }
        });
        // Listener for layout tab selection
        mStreamTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Log.d("Test", String.valueOf(fragment));
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new ProfilePosts(searchers);
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        break;
                    case 1:
                        if (fragment.getClass() == RecipeFragment.class) fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                        else fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        fragment = new ProfilePictures(searchers, mUsername.getText().toString());
                        break;
                    case 2:
                        fragment = new ProfileRecipes(searchers);
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                        break;
                }
                fragmentTransaction.replace(R.id.profileFrameLayout, fragment);
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
    public void onBackPressed() {
        Intent returnIntent = new Intent(this, Home.class);
        returnIntent.putExtra("user", mUserProfile);
        startActivity(returnIntent);
        finish(); //    Don't need to send anything back but do need to destroy the current activity.
    }

    //  Checks for if a user wants specific data to be retrieved based on there privacy settings.
    private void loadInPrivacySettings(HashMap<String, Object> privacy){
        if(privacy != null){
            retrieveAboutMe = (boolean) privacy.get("display_about_me");
            retrieveUsername = (boolean) privacy.get("display_username");
            retrieveRecipes = (boolean) privacy.get("display_recipes");
            retrieveImages = (boolean) privacy.get("display_profile_image");
            retrieveFilters = (boolean) privacy.get("display_filters");
            retrievePosts = (boolean) privacy.get("display_feed");
        }
    }

    //  Load all the data grabbed from the Firebase document snapshot.
    private void loadProfile(DocumentSnapshot profile) {
        if(retrieveAboutMe) { //  If we are allowed to retrieve this data. do so.
            mAboutMeDesc.setText((String) profile.get("about"));
        } else {
            mAboutMe.setVisibility(View.GONE); // Adjust if the view is visible.
            mAboutMeDesc.setVisibility(View.GONE);
        }

        if(retrieveImages) {
            String imageURL = (String) profile.get("imageURL");

            if(imageURL == null){
                throw new NullPointerException("Unable to load Profile image URL. Image URL is null");
            } else if(!imageURL.equals("")){ // If Image URL is equal to "" (default) display a placeholder image.
                Glide.with(this) // Use glide to load in a circular image.
                        .load(profile.get("imageURL"))
                        .apply(RequestOptions.circleCropTransform())
                        .into(mProfileImage); }
        }

//        if(retrieveUsername){ mUsername.setText((String) profile.get("displayName")); }
        mUsername.setText((String) profile.get("displayName"));



        if(retrieveFilters){
            @SuppressWarnings("unchecked")
            HashMap<String, Object> filters = (HashMap<String, Object>) profile.get("preferences");

            boolean showFilters = false;
            //  Checks hashmap to see if any filters are enabled.
            assert filters != null;
            for (HashMap.Entry<String, Object> entry : filters.entrySet()) {
                boolean isEnabled = (boolean) entry.getValue();

                if(isEnabled){
                    showFilters = true;
                }
            }

            if(showFilters){
                initFiltersIcons(ALLERGENS, filters);
                initFiltersIcons(DIETARY, filters);
                LinearLayout dietLayout = findViewById(R.id.dietLayout);
                dietLayout.setVisibility(View.VISIBLE);
            }
        } else { // Remove all checkboxes if filters are hidden.
            LinearLayout allergyLayout = findViewById(R.id.allergyLayout);
            View allergyPressInfo = findViewById(R.id.allergyPressInfo);
            View profileSettingsAllergens = findViewById(R.id.profile_settings_allergens);

            allergyLayout.setVisibility(View.GONE);
            allergyPressInfo.setVisibility(View.GONE);
            profileSettingsAllergens.setVisibility(View.GONE);
        }
    }

    public void checkFollowed(){
        if(UID != null){
            mDatabase.collection("followers").document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document != null){
                            ArrayList followers = (ArrayList) document.get("users");
                            if(followers.contains(mUserProfile.getUID())){
                                mFollowButton.setVisibility(View.GONE);
                                mFollowedButton.setVisibility(View.VISIBLE);
                                followed = true;
                            }else{
                                if(document.get("requested") != null){
                                    ArrayList requested = (ArrayList) document.get("requested");
                                    if(requested.contains(mUserProfile.getUID())){
                                        mFollowButton.setVisibility(View.GONE);
                                        mRequestedButton.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }
                    loadFirebase(FirebaseLoadType.FULL);
                }
            });
        }
    }

    /** Load in all associated Firebase user data either fully or partially.
     * @param flt - Firebase Load Type. Either PARTIAL or FULL. FULL = all data from firebase from a UID string.
     *            PARTIAL = some data has already been loaded from the UserInfoPrivate object so we only need to grab
     *            some Firebase realtime data such as Kudos.
     */
    private void loadFirebase(FirebaseLoadType flt) {
        final FirebaseLoadType fltFinal = flt;

        if(fltFinal == FirebaseLoadType.PARTIAL){ // If only a partial load, grab the UID from the UserInfoPrivate object.
            UID = mUserProfile.getUID();
        }
        //  Grab the 'users' collection corresponding to the correct document UID.
        DocumentReference usersRef = mDatabase.collection("users").document(UID);
        usersRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){ // Check a document can be accessed.
                DocumentSnapshot document = task.getResult();

                assert document != null;
                if(document.exists()){ // Check a document exists.
                    if(fltFinal == FirebaseLoadType.FULL){
                        Log.i(TAG, "Loading a full user profile!");
                        privateProfile = (boolean) document.get("privateProfileEnabled");
                        if(followed || !privateProfile){
                            @SuppressWarnings("unchecked")
                            HashMap<String, Object> privacy = (HashMap<String, Object>) document.get("privacyPrivate");
                            loadInPrivacySettings(privacy); // Load in privacy settings first (always)
                            loadPostsAndRecipeList();
                        }else{
                            @SuppressWarnings("unchecked")
                            HashMap<String, Object> privacy = (HashMap<String, Object>) document.get("privacyPublic");
                            mStreamTabs.setVisibility(View.GONE);
                            frameLayout.setVisibility(View.GONE);
                            loadInPrivacySettings(privacy); // Load in privacy settings first (always)
                        }
                        loadProfile(document); // Then we load the public users profile.
                    }
                    postsFollowersFollowing(document);
                    loadKudosAndRecipes(document);
                    initKudosIconPressListener();
                    Log.i(TAG, "Successfully loaded the users profile");
                } else {
                    Log.e(TAG, "No such document, " + document.toString() + " exists");
                }
        } else {
                Log.e(TAG, "Failed to get document for public user profile");
            }
        });
    }

    /**
     * Loading recipes and posts lists if privacy setting allow and followed or public user
     */
    private void loadPostsAndRecipeList(){
        if(retrievePosts && retrieveRecipes){
            fragment = new ProfilePosts(searchers);
            fragmentTransaction.replace(R.id.profileFrameLayout, fragment);
            fragmentTransaction.commit();
        }else if(retrievePosts){
            fragment = new ProfilePosts(searchers);
            fragmentTransaction.replace(R.id.profileFrameLayout, fragment);
            fragmentTransaction.commit();
            mStreamTabs.removeTab(mStreamTabs.getTabAt(2));
        }else if(retrieveRecipes){
            mStreamTabs.removeTab(mStreamTabs.getTabAt(1));
            mStreamTabs.removeTab(mStreamTabs.getTabAt(0));
            fragment = new ProfileRecipes(searchers);
            fragmentTransaction.replace(R.id.profileFrameLayout, fragment);
            fragmentTransaction.commit();
        }
    }

    /** Method to load in Kudos and Recipe information.
     *  Always loads directly from Firebase.
     * @param profile - A snapshot of the Firebase user profile document.
     */
    private void loadKudosAndRecipes(DocumentSnapshot profile){
        String kudosString = "Kudos: " + ((long) profile.get("kudos"));

        Kudos.setKudos((long) profile.get("kudos"));
        Kudos.updateKudos(); // Update the kudos icon + chef descriptor.

        // Assign Kudos icon + chef descriptor.
        mKudosIcon.setImageResource(Kudos.chefLevelIcon);
        mKudos.setText(kudosString);

        //  Load in number of recipes.
        String numOfRecipesString = "Recipes: " + ((long) profile.get("numRecipes")); //  Convert 'long' value to something we can use.
        //Number of recipes seen by all
        mNumRecipes.setText(numOfRecipesString);
//        if(retrieveRecipes){ mNumRecipes.setText(numOfRecipesString);} else {
//            mNumRecipes.setText(""); // Set number of recipes to nothing if hidden.
//        }
    }

    /** Method to load in posts, followers and following.
     *  Always loads directly from Firebase.
     * @param profile - A snapshot of the Firebase user profile document.
     */
    private void postsFollowersFollowing(DocumentSnapshot profile){
        String postsString =  Long.toString(((long) profile.get("livePosts")));
        if(profile.get("followers") == null){
            mFollowers.setText("0");
            mFollowing.setText("0");
            if(UID != null){
                mDatabase.collection("users").document(UID).update("followers", 0, "following", 0);
            }else{
                mDatabase.collection("users").document(mUserProfile.getUID()).update("followers", 0, "following", 0);
            }
        }else{
            String followingString =  Long.toString(((long) profile.get("following")));
            String followersString =  Long.toString(((long) profile.get("followers")));
            mFollowing.setText(followingString);
            mFollowers.setText(followersString);
        }

        mPosts.setText(postsString);

    }

    //  Load the information from the local UserInfoPrivate object if the profile corresponds to that of the user using the app/
    //  Reduces amount of Firebase Queries overall.
    private void loadLocalProfile() {
        if (retrieveAboutMe) { //  If we are allowed to retrieve this data. do so.
            mAboutMeDesc.setText(mUserProfile.getAbout());
        } else {
            mAboutMe.setVisibility(View.GONE);
            mAboutMeDesc.setVisibility(View.GONE);
            //LinearLayout aboutLayout = findViewById(R.id.aboutMeLayout);
            //aboutLayout.setVisibility(View.VISIBLE);
        }

        if(retrieveUsername){ mUsername.setText(mUserProfile.getDisplayName()); }

        if(retrieveImages) {
            if(mUserProfile.getImageURL() == null){
                throw new NullPointerException("Unable to load Profile image URL. Image URL is null");
            } else if(!mUserProfile.getImageURL().equals("")){
                Glide.with(this)
                        .load(mUserProfile.getImageURL())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mProfileImage); }
        }

        if(retrieveFilters){
            HashMap<String, Object> filters = new HashMap<>();

            //  Store all filters in a HashMap for convenience.
            filters.put("allergy_nuts", mUserProfile.getPreferences().isAllergy_nuts());
            filters.put("allergy_soya", mUserProfile.getPreferences().isAllergy_soya());
            filters.put("allergy_milk", mUserProfile.getPreferences().isAllergy_milk());
            filters.put("allergy_shellfish", mUserProfile.getPreferences().isAllergy_shellfish());
            filters.put("allergy_gluten", mUserProfile.getPreferences().isAllergy_gluten());
            filters.put("allergy_eggs", mUserProfile.getPreferences().isAllergy_eggs());
            filters.put("pescatarian", mUserProfile.getPreferences().isPescatarian());
            filters.put("vegan", mUserProfile.getPreferences().isVegan());
            filters.put("vegetarian", mUserProfile.getPreferences().isVegetarian());

            boolean showFilters = false;

            //  Checks hashmap to see if any filters are enabled.
            for (HashMap.Entry<String, Object> entry : filters.entrySet()) {
                boolean isEnabled = (boolean) entry.getValue();

                if(isEnabled){
                    showFilters = true;
                }
            }

            if(showFilters){
                initFiltersIcons(ALLERGENS, filters);
                initFiltersIcons(DIETARY, filters);
                LinearLayout dietLayout = findViewById(R.id.dietLayout);
                dietLayout.setVisibility(View.VISIBLE);
            }

        } else { // Remove preferences icons if required.
            LinearLayout allergyLayout = findViewById(R.id.allergyLayout);
            View allergyPressInfo = findViewById(R.id.allergyPressInfo);
            View profileSettingsAllergens = findViewById(R.id.profile_settings_allergens);

            allergyLayout.setVisibility(View.GONE);
            allergyPressInfo.setVisibility(View.GONE);
            profileSettingsAllergens.setVisibility(View.GONE);
        }
    }

    //  Initiate click listener for Kudos press. Creates an alert dialog with the chef rank.
    private void initKudosIconPressListener(){
        mKudosIcon.setOnClickListener(v -> {
            Kudos.updateKudos();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.create();
            dialog.setTitle("Chef Rank");
            dialog.setMessage(Kudos.chefLevel);
            dialog.show();
        });
    }

    /** Initiate the horizontal scroll filter icon collection with corresponding icons
     *  and click listeners.
     *
     * @param type - Type of filters we want to display. IE ALLERGENS, DIETARY.
     * @param filters - HashMap collection of all available filters.
     */
    private void initFiltersIcons(filterType type, HashMap<String , Object> filters){
        ArrayList<Boolean> filterValues = new ArrayList<>();
        ArrayList<ImageView> filterIcons = new ArrayList<>();
        ArrayList<String> filterMessages = new ArrayList<>();
        final String title;

        switch(type){
            case ALLERGENS:
                title = "Allergy";
                filterValues.add((boolean) filters.get("allergy_eggs"));
                filterValues.add((boolean) filters.get("allergy_milk"));
                filterValues.add((boolean) filters.get("allergy_nuts"));
                filterValues.add((boolean) filters.get("allergy_shellfish"));
                filterValues.add((boolean) filters.get("allergy_soya"));
                filterValues.add((boolean) filters.get("allergy_gluten"));

                //  Messages to display on icon click via alert dialog box.
                filterMessages.add("Allergic to Eggs");
                filterMessages.add("Allergic to Lactose");
                filterMessages.add("Allergic to Nuts");
                filterMessages.add("Allergic to Shellfish");
                filterMessages.add("Allergic to Soya");
                filterMessages.add("Allergic to Gluten");

                filterIcons.add(findViewById(R.id.recipeInfoEggs));
                filterIcons.add(findViewById(R.id.recipeInfoMilk));
                filterIcons.add(findViewById(R.id.recipeInfoNuts));
                filterIcons.add(findViewById(R.id.recipeInfoShellfish));
                filterIcons.add(findViewById(R.id.recipeInfoSoy));
                filterIcons.add(findViewById(R.id.recipeInfoWheat));
                break;
            case DIETARY:
                title = "Dietary";
                filterValues.add((boolean) filters.get("pescatarian"));
                filterValues.add((boolean) filters.get("vegan"));
                filterValues.add((boolean) filters.get("vegetarian"));

                filterMessages.add("Pescatarian");
                filterMessages.add("Vegan");
                filterMessages.add("Vegetarian");

                filterIcons.add(findViewById(R.id.recipeInfoPesc));
                filterIcons.add(findViewById(R.id.recipeInfoVegan));
                filterIcons.add(findViewById(R.id.recipeInfoVeggie));
                break;
            default:
                title = "";
        }

        /*  Cycle through the filter icons collection and add alert dialogs for the description for each
            of each Icon presented upon a users click. */
        for(int i = 0; i < filterIcons.size(); i++){
            if (filterValues.get(i)) {
                ImageView icon = filterIcons.get(i);
                String message = filterMessages.get(i);

                icon.setVisibility(View.VISIBLE);
                icon.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    AlertDialog dialog = builder.create();
                    dialog.setTitle(title);
                    dialog.setMessage(message);
                    dialog.show();
                });
            }
        }
    }

    
}
