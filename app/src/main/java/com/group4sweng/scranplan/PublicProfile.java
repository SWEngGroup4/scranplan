package com.group4sweng.scranplan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.MealPlanner.PlannerFragment;
import com.group4sweng.scranplan.SearchFunctions.RecipeFragment;
import com.group4sweng.scranplan.Social.FeedFragment;
import com.group4sweng.scranplan.Social.ProfilePosts;
import com.group4sweng.scranplan.UserInfo.FilterType;
import com.group4sweng.scranplan.UserInfo.Kudos;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;

import static com.group4sweng.scranplan.UserInfo.FilterType.filterType.ALLERGENS;
import static com.group4sweng.scranplan.UserInfo.FilterType.filterType.DIETARY;

/** Class retrieves and displays public profile data on a given user based on a valid UID input string or UserInfoPrivate object.
 *  from Firebase or from local data.
 *
 *  For firebase pass an extra string via intent with ID = "UID"
 *  For local data pass a UserInfoPrivate object via intent with ID = "user"
 *
 *  Privacy checks are made within 'loadInPrivacySettings' to make sure we adhere to what the user wants to display.
 *  Privacy is always checked first before displaying any content.
 */
public class PublicProfile extends AppCompatActivity implements FilterType{

    enum FirebaseLoadType {
        FULL,
        PARTIAL
    }

    //TODO - Remove Kudos image on very teeny screens.
    // TAG for Profile Settings
    final String TAG = "PublicProfile";

    //  UID for the user in which we want to retrieve data from.
    private String UID;
    private UserInfoPrivate mUserProfile;

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
        //TODO add method to pass intent with a valid UID string.

    }

    @Override
    protected void onStart() {
        super.onStart();

        //  Gets extra string UID attached when an intent is sent.
        UID = getIntent().getStringExtra("UID");

        //  Gets extra UserProfilePrivate object if one is sent.
        mUserProfile = (UserInfoPrivate) getIntent().getSerializableExtra("user");

        if(mUserProfile != null){ // Check if local data is available to reference. Don't have to grab from firebase.
            Log.i(TAG, "Loading local user data");
            loadInPrivacySettings(mUserProfile.getPublicPrivacy());
            loadLocalProfile();
            loadFirebase(FirebaseLoadType.PARTIAL);
            searchers = mUserProfile.getUID();
        } else if(UID != null){ // If not instead search for the profile via the associated UID and reference Firebase.
            Log.i(TAG, "Loading data from Firebase");
            loadFirebase(FirebaseLoadType.FULL);
            searchers = UID;
        } else {
            Log.e(TAG, "Unable to retrieve extra UID intent string. Cannot initialize profile.");
        }
        fragment = new ProfilePosts(searchers);
        fragmentTransaction.replace(R.id.profileFrameLayout, fragment);
        fragmentTransaction.commit ();
        initPageListeners();
    }

    private void initPageItems(){
        mProfileImage = findViewById(R.id.public_profile_image);
        mAboutMe = findViewById(R.id.public_profile_about_me_desc);
        mNumRecipes = findViewById(R.id.profile_recipes);
        mUsername = findViewById(R.id.profile_username);
        mKudos = findViewById(R.id.profile_kudos);
        mKudosIcon = findViewById(R.id.profile_kudos_icon);
        mStreamTabs = findViewById(R.id.profileStreamTabs);

        frameLayout = findViewById(R.id.profileFrameLayout);

    }

    private void initPageListeners(){
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
                        fragment = new ProfilePosts(searchers);
                        break;
                    case 2:
                        fragment = new ProfilePosts(searchers);
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                        break;
                }
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
    public void onBackPressed() {
        Intent returnIntent = new Intent(this, Home.class);
        returnIntent.putExtra("user", mUserProfile);
        startActivity(returnIntent);
        finish(); //    We don't need to send anything back but do need to destroy the current activity.
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
            } else if(!imageURL.equals("")){
                Glide.with(this)
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

        }
    }

    /*
    private void makeFiltersInvisible(){
        switch(currentFilterType){
            case ALLERGENS:
                Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT); // Create a transparent drawable background.

                //  Remove checkboxes by setting a transparent background.
                mAllergy_gluten.setButtonDrawable(transparentDrawable);
                mAllergy_milk.setButtonDrawable(transparentDrawable);
                mAllergy_soy.setButtonDrawable(transparentDrawable);
                mAllergy_shellfish.setButtonDrawable(transparentDrawable);
                mAllergy_eggs.setButtonDrawable(transparentDrawable);
                mAllergy_nuts.setButtonDrawable(transparentDrawable);

                //  Set top-left element (nuts) to a new 'hidden info' string and remove text from all other filters.
                mAllergy_nuts.setText((String) "Filter information has been hidden");
                mAllergy_gluten.setText("");
                mAllergy_soy.setText("");
                mAllergy_shellfish.setText("");
                mAllergy_eggs.setText("");
                mAllergy_milk.setText("");
            case RELIGIOUS:
                //TODO
                Log.i(TAG, "Religious");
            case DIETARY:
                //TODO
                Log.i(TAG, "Religious");
            case HEALTH:
                //TODO
                Log.i(TAG, "Religious");
        }

    }*/

    /** Set which filter checkboxes should be selected
     * @param - Enumeration of the type of filter to be displayed. E.g. Allegern, Religious...
     */
    /*
    private void displayFilters(HashMap<String, Object> filters){



        switch(type){
            case ALLERGENS:
                mAllergy_eggs.setChecked((boolean) filters.get("allergy_eggs"));
                mAllergy_milk.setChecked((boolean) filters.get("allergy_milk"));
                mAllergy_nuts.setChecked((boolean) filters.get("allergy_nuts"));
                mAllergy_shellfish.setChecked((boolean) filters.get("allergy_shellfish"));
                mAllergy_soy.setChecked((boolean) filters.get("allergy_soya"));
                mAllergy_gluten.setChecked((boolean) filters.get("allergy_gluten"));
            case RELIGIOUS:
                //TODO
            case DIETARY:
                //TODO
            case HEALTH:
                //TODO
        }
    }*/

    //  Load in all associated public profile data for a given UID. (defined at Activity Start).
    private void loadFirebase(FirebaseLoadType flt) {
        final FirebaseLoadType fltFinal = flt;

        if(fltFinal == FirebaseLoadType.PARTIAL){
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
                        HashMap<String, Object> privacy = (HashMap<String, Object>) document.get("privacy");
                        loadInPrivacySettings(privacy); // Load in privacy settings first (always)
                        loadProfile(document); // Then we load the public users profile.
                    }
                    loadKudosAndRecipes(document);
                    initKudosIconPressListener(document);
                    Log.i(TAG, "Successfully loaded the users profile");
                } else {
                    Log.e(TAG, "No such document, " + document.toString() + " exists");
                }
        } else {
                Log.e(TAG, "Failed to get document for public user profile");
            }
        });
    }

    private void loadKudosAndRecipes(DocumentSnapshot profile){
        //  Load in kudos.
        String kudosString = "Kudos: " + ((long) profile.get("kudos"));
        Log.e(TAG, "KUDOS is: " + kudosString);

        Kudos.setKudos((long) profile.get("kudos"));
        Kudos.updateKudos();

        Log.e(TAG, "Image resource number: " + Kudos.chefLevelIcon);
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

        }
    }

    private void initKudosIconPressListener(DocumentSnapshot profile){
        mKudosIcon.setOnClickListener(v -> {
            Kudos.updateKudos();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.create();
            dialog.setTitle("Chef Rank");
            dialog.setMessage(Kudos.chefLevel);
            dialog.show();
        });
    }

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
