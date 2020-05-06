package com.group4sweng.scranplan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
    TextView mNumRecipes;
    TextView mKudos;
    ImageView mKudosIcon;
    TabLayout mStreamTabs;

    //  Whether we should retrieve different information for the user. E.g. username, about me etc...
    private boolean retrieveAboutMe = false, retrieveUsername = false,
            retrieveImages = false, retrieveRecipes = false, retrieveFilters = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        initPageItems();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //  Gets extra string UID attached when an intent is sent.
        UID = getIntent().getStringExtra("UID");

        //  Gets extra UserProfilePrivate object if one is sent.
        mUserProfile = (UserInfoPrivate) getIntent().getSerializableExtra("user");

        if(mUserProfile != null){ // Check if local data is available to reference. Don't have to grab from firebase.
            updatePublicProfile(FirebaseLoadType.PARTIAL);
        } else if(UID != null){ // If not instead search for the profile via the associated UID and reference Firebase.
            updatePublicProfile(FirebaseLoadType.FULL);
        } else {
            Log.e(TAG, "Unable to retrieve extra UID intent string. Cannot initialize profile.");
        }
    }


    /** Update the public profile based on what has been passed via the Intent.
     * @param flt - Firebase Load Type. Either PARTIAL or FULL. FULL = all data from firebase from a UID string.
     *            PARTIAL = some data has already been loaded from the UserInfoPrivate object so we only need to grab
     *            some Firebase realtime data such as Kudos.
     */
    private void updatePublicProfile(FirebaseLoadType flt){
        if(flt == FirebaseLoadType.PARTIAL){
            Log.i(TAG, "Loading local user data");
            loadInPrivacySettings(mUserProfile.getPublicPrivacy());
            loadLocalProfile();
            loadFirebase(FirebaseLoadType.PARTIAL);
        } else if(UID != null){ // If not instead search for the profile via the associated UID and reference Firebase.
            Log.i(TAG, "Loading data from Firebase");
            loadFirebase(FirebaseLoadType.FULL);
        } else {
            Log.e(TAG, "Unable to retrieve extra UID intent string. Cannot initialize profile.");
        }
    }

    private void initPageItems(){
        mProfileImage = findViewById(R.id.public_profile_image);
        mAboutMe = findViewById(R.id.public_profile_about_me_desc);
        mNumRecipes = findViewById(R.id.profile_recipes);
        mUsername = findViewById(R.id.profile_username);
        mKudos = findViewById(R.id.profile_kudos);
        mKudosIcon = findViewById(R.id.profile_kudos_icon);
        mStreamTabs = findViewById(R.id.profileStreamTabs);
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
        }
    }

    //  Load all the data grabbed from the Firebase document snapshot.
    private void loadProfile(DocumentSnapshot profile) {
        if(retrieveAboutMe && !profile.get("about").equals("")) { //  If we are allowed to retrieve this data. do so.
            mAboutMe.setText((String) profile.get("about"));
            LinearLayout aboutLayout = findViewById(R.id.aboutMeLayout);
            aboutLayout.setVisibility(View.VISIBLE);
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

        if(retrieveUsername){ mUsername.setText((String) profile.get("displayName")); }


        if(retrieveFilters){
            @SuppressWarnings("unchecked")
            HashMap<String, Object> filters = (HashMap<String, Object>) profile.get("preferences");
            initFiltersIcons(ALLERGENS, filters);
            initFiltersIcons(DIETARY, filters);
            LinearLayout dietLayout = findViewById(R.id.dietLayout);
            dietLayout.setVisibility(View.VISIBLE);

            allergyLayout.setVisibility(View.GONE);
            allergyPressInfo.setVisibility(View.GONE);
            profileSettingsAllergens.setVisibility(View.GONE);
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
                        @SuppressWarnings("unchecked")
                        HashMap<String, Object> privacy = (HashMap<String, Object>) document.get("privacyPublic");
                        loadInPrivacySettings(privacy); // Load in privacy settings first (always)
                        loadProfile(document); // Then we load the public users profile.
                    }
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
        if(retrieveRecipes){ mNumRecipes.setText(numOfRecipesString);} else {
            mNumRecipes.setText(""); // Set number of recipes to nothing if hidden.
        }
    }

    //  Load the information from the local UserInfoPrivate object if the profile corresponds to that of the user using the app/
    //  Reduces amount of Firebase Queries overall.
    private void loadLocalProfile() {
        if (retrieveAboutMe) { //  If we are allowed to retrieve this data. do so.
            mAboutMe.setText(mUserProfile.getAbout());
            LinearLayout aboutLayout = findViewById(R.id.aboutMeLayout);
            aboutLayout.setVisibility(View.VISIBLE);
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

            initFiltersIcons(ALLERGENS, filters);
            initFiltersIcons(DIETARY, filters);
            LinearLayout dietLayout = findViewById(R.id.dietLayout);
            dietLayout.setVisibility(View.VISIBLE);

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
