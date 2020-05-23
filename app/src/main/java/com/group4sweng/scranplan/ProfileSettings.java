package com.group4sweng.scranplan;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4sweng.scranplan.Exceptions.InvalidUserException;
import com.group4sweng.scranplan.Exceptions.ProfileImageException;
import com.group4sweng.scranplan.Helper.HiddenViews;
import com.group4sweng.scranplan.PreferencesTabs.AllergensFragment;
import com.group4sweng.scranplan.PreferencesTabs.DietaryFragment;
import com.group4sweng.scranplan.UserInfo.FilterType;
import com.group4sweng.scranplan.UserInfo.Preferences;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.group4sweng.scranplan.Helper.ImageHelpers.getExtension;
import static com.group4sweng.scranplan.Helper.ImageHelpers.getPrintableSupportedFormats;
import static com.group4sweng.scranplan.Helper.ImageHelpers.getSize;
import static com.group4sweng.scranplan.Helper.ImageHelpers.isImageFormatSupported;
import static java.util.Objects.requireNonNull;

/**
 * Class for the Profile Settings activity.
 * Author(s): JButler, LNewman
 * (c) CoDev 2020
 *
 * Provides support for Deleting/Resetting user profiles.
 * Also provides user profile settings for:
 *  - Username
 *  - Profile Image
 *  - About Me Info
 *  - Filters
 *  - Privacy options.
 */
public class ProfileSettings extends AppCompatActivity implements FilterType, SupportedFormats {

    //  Current Tab position type for privacy options.
    private enum SyncType {
        PRIVATE,
        PUBLIC
    }

    private final static String TAG = "ProfileSettings"; // Tag for 'Log'.

    // Unique codes for image & permission request activity callbacks.
    private static final int IMAGE_REQUEST_CODE = 2;
    private static final int PROFILE_SETTINGS_REQUEST_CODE = 1;
    private static final int PERMISSION_CODE = 1001;

    private static final int MAX_IMAGE_FILE_SIZE_IN_MB = 4; // Max storage image size for the profile picture.
    protected static boolean IMAGE_IS_UPLOADING = false; // Boolean to determine if the image is uploading currently.
    private String currentImageURI = null;

    //  Default filter type enumeration. Types shown in 'FilterType' interface.
    static filterType currentFilterType = filterType.ALLERGENS;

    // Firebase user variables & authentication access.
    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    // Firebase user collection and storage references.
    CollectionReference mRef = mDatabase.collection("users");
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    StorageReference mStorageReference = mStorage.getReference();
    UploadTask mImageUploadTask;

    private Uri mImageUri; // Unique image uri.

    UserInfoPrivate mUserProfile; // Local user info data. Not edited directly, assigned on return from 'mTempUserProfile' if settings have been saved.
    UserInfoPrivate mTempUserProfile; // Local User info. Accessible only within this Activity

    //  TODO - Add valid network connection checks.

    ProgressBar mProgress;
    TextView mProgressText;

    //  Basic user settings.
    ImageView mProfileImage;
    TextView mUsername;
    TextView mAboutMe;

    Switch mDisplay_username;
    Switch mDisplay_about_me;
    Switch mDisplay_recipes;
    Switch mDisplay_profile_image;
    Switch mDisplay_filters;
    Switch mDisplay_feed;
    Switch mPrivateProfileEnabled;

    //  Input fields (Delete profile & Change password)
    EditText mPasswordConfirm;
    EditText mPasswordNew1;
    EditText mPasswordNew2;
    EditText mPasswordOld;

    //  Preference tab that switches between different types of fragments.
    TabLayout mPreferencesTabs;

    //  Profile visibility tab (Public/Friends)
    TabLayout mProfileVisibilityTab;

    //  Set our initial fragment to Allergens.
    Fragment currentFragment = new AllergensFragment();

    //  Timer in milliseconds for minimum interval between 'Save Settings' button presses..
    private final int COUNTDOWN_TIMER_MILLIS = 10000;

    //  Timer in milliseconds for update rate of the Toast message.
    private final int COUNTDOWN_INTERVAL_MILLIS = 1000;

    private long saveCountdownSecondsLeft = 0; //Initial timer is set to 0.
    private boolean saveCountdownFinished = true; //Initial countdown is finished.

    boolean isTesting = false; //Used to check if the activity is currently being tested. If so, do not delete the users account.

    //  Initialize 'Save Settings minimum interval countdown timer.
    CountDownTimer saveCountdown = new CountDownTimer(COUNTDOWN_TIMER_MILLIS, COUNTDOWN_INTERVAL_MILLIS) {

        @Override
        public void onTick(long millisUntilFinished) {
            saveCountdownSecondsLeft = millisUntilFinished/1000;
        }

        @Override
        public void onFinish() {
            saveCountdownFinished = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        initPageItems();
    }

    @Override
    protected void onStart(){
        super.onStart();

        mUserProfile = (UserInfoPrivate) getIntent().getSerializableExtra("user"); //Grabs serializable UserInfoPrivate data from main activity.
        if(mUserProfile != null){ //Checks if there is actually any Serializable data received.
            mTempUserProfile = mUserProfile.deepClone(); // Creates a replica of the UserInfoPrivate object. Prevents passing by reference.
            mProgress.setVisibility(View.GONE); // Hide image upload progress bar.
            mProgressText.setVisibility(View.GONE);
            mPreferencesTabs = findViewById(R.id.preferences_tab_bar);

            //  Send initial serializable preferences data to the initial preferences checkbox fragment + initiate listeners for the tab bar for these fragments.
            sendSerializableToFragment();
            initPreferencesFragmentsTabListener();
            initProfileVisibilityTabListener();

            loadProfileData();
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("user", mUserProfile); // Send Serializable UserInfoPrivate class data back when leaving the activity.

        //  If an image is still uploading create a dialog box that asks if the user wants to continue existing.
        //  Otherise, ignore and simply return.
        if(IMAGE_IS_UPLOADING){
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettings.this); //Create an alert.

            //  Create a new linear layout which fits the proportions of the screen and descends vertically.
            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(params);
            builder.setView(layout);

            builder.setMessage("Your profile image is still uploading. Are you sure you want to exit?");
            builder.setPositiveButton("Yes", (dialog, which) -> { //  Send back with intent and update the Home's UserInfoPrivate class with the new User Info.
                IMAGE_IS_UPLOADING = false;
                mImageUploadTask.cancel(); // Cancel the image upload.
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            });
            builder.setNegativeButton("No", (dialog, which) -> { //Allow the user to cancel the operation.
                dialog.cancel(); // Close dialog and return.
            });

            //  Create and display the dialog.
            AlertDialog alertDialog = builder.create(); //
            alertDialog.show();
        } else {
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

    }

    /** Initiated on a 'Delete Profile' button press.
     *  Creates an alert dialog box that checks a users old password through re-authentication
     *  and then deletes a users profile if a valid Firebase and local profile is found.
     * @param v - Button View.
     */
    public void deleteProfile(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettings.this); //Create an alert.

        //  Create a new linear layout which fits the proportions of the screen and descends vertically.
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);

        //  Add a text input password confirm box
        mPasswordConfirm = new EditText(this);
        mPasswordConfirm.setHint("Enter password");
        mPasswordConfirm.setPadding(40, 40, 40, 40);
        mPasswordConfirm.setGravity(Gravity.CENTER); //Center the box.
        mPasswordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); //Set the input type to 'password' (hidden).

        layout.addView(mPasswordConfirm);
        builder.setView(layout);

        builder.setMessage("Are you sure you want to delete?");
        builder.setTitle("Delete Profile");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            final String passwordString = mPasswordConfirm.getText().toString(); // Retrieve text input, convert to a string.

            // Check invalid password entries.
            if(passwordString.equals("")){
                Log.e(TAG, "Password cannot be set to an empty field");
                Toast.makeText(getApplicationContext(), "Cannot enter a blank password. Please try again.", Toast.LENGTH_SHORT).show();
            } else if(passwordString.length() <= 6){
                Log.e(TAG, "Password must be greater than 6 characters in length");
                Toast.makeText(getApplicationContext(),"Password must be greater than 6 characters in length", Toast.LENGTH_SHORT).show();
            } else {
                deleteAccount(passwordString);  //Delete our account
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> { //Allow the user to cancel the operation.
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    /**
     * deleteAccount
     * called when user wants to delete their account, needs users password to be re-entered
     * FireBase Auth is deleted along with all local data and their personal account in the database
     * @param rePassword - Existing password the user must enter to confirm account deletion.
     */
    private void deleteAccount(String rePassword){
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);

        final String tempUID = mAuth.getUid();
        final String password = rePassword;

        if(isTesting){ //  Checks if we are testing so we don't actually delete a users account.
            Log.e(TAG, "Currently in testing phase, haven't deleted Firebase or local profile");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(mUserProfile.getEmail(), password); //Obtain a users credentials and therefore equivalent password.

        if(mAuth.getCurrentUser() != null){ //  Check the current user exists.

            /*  Brute force deletes all associated user images for every possible combination of image extensions.
                Initiated before re-authentication to make sure all images are deleted and that the user always has permission to delete these files. */
            for(ImageFormats extension : ImageFormats.values()){
                mStorageReference.child("images/profile/" + mAuth.getUid() + "/profile_image." + extension.toString()).delete().addOnSuccessListener(aVoid12 -> {
                    Log.e(TAG, "Deleted entry: images/profile/" + mAuth.getUid() + "/profile_image." + extension.toString());
                }).addOnFailureListener(e -> {
                    //  Common failure. delete function is attempting to brute force delete files so the reference may not always exist.#
                    //  This isn't an issue but Firebase will flag it in the console.
                    Log.e(TAG, "Entry: images/profile/" + mAuth.getUid() + "/profile_image." + extension.toString() + " dosen't exist. Ignoring");
                });
            }

            //  Attempt to re-authenticate the user.
            mAuth.getCurrentUser().reauthenticate(credential).addOnSuccessListener(aVoid -> {
                /* Delete a reference to the users data in the following order.
                   Users collection stored within Firebase > Local reference to the user 'UserInfoPrivate' > Firebase stored Auth details for the user.
                   Order chosen since collection data is visible in plaintext from the Firebase Panel and therefore should be our priority to remove even if any one of the other operations fail. */
                mDatabase.collection("users").document(mAuth.getCurrentUser().getUid()).delete().addOnSuccessListener(aVoid1 -> {
                    mUserProfile = null;
                    mAuth.getCurrentUser().delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");

                            //  Verify the account has been deleted.
                            Toast.makeText(getApplicationContext(), "Account deleted.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Deleted associated collection 'users'. Couldn't delete all users data. User has been asked for this to be removed manually.");
                    Toast.makeText(getApplicationContext(), "Failed to delete all associated user info. Please contact Scranplan with your email address to have your data manually removed.", Toast.LENGTH_LONG).show();
                });
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to re-authenticate. May have entered the wrong password");
                Toast.makeText(getApplicationContext(),"Could not delete data. Make sure you are connected to the internet and have inputted a correct previous password.", Toast.LENGTH_LONG).show();
            });
        } else {
            Log.e(TAG, "Unable to get current user details");
            Toast.makeText(getApplicationContext(), "Unable to get current user details. Make sure you are connected to the internet", Toast.LENGTH_SHORT).show();
        }
    }

    /** Initiated on a 'Change Password' button press.
     *  Creates an alert dialog box that checks a users old password through re-authentication
     *  and then asks for a new password.
     * @param v - Change Password button view.
     */
    public void changePassword(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettings.this);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        /* Creates 3 unique input boxes:
            - New password
            - Repeated new password
            - Old password
         */
        mPasswordNew1 = new EditText(this);
        mPasswordNew1.setHint("Enter new password");
        mPasswordNew1.setPadding(40, 40, 40, 40);
        mPasswordNew1.setGravity(Gravity.CENTER);
        mPasswordNew1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mPasswordNew2 = new EditText(this);
        mPasswordNew2.setHint("Re-enter new password");
        mPasswordNew2.setPadding(40, 40, 40, 40);
        mPasswordNew2.setGravity(Gravity.CENTER);
        mPasswordNew2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mPasswordOld = new EditText(this);
        mPasswordOld.setHint("Enter old password");
        mPasswordOld.setPadding(40, 40, 40, 40);
        mPasswordOld.setGravity(Gravity.CENTER);
        mPasswordOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(mPasswordOld);
        layout.addView(mPasswordNew1);
        layout.addView(mPasswordNew2);
        builder.setView(layout);

        builder.setTitle("Change Password");
        builder.setCancelable(true);
        builder.setPositiveButton("Change password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String passwordString = mPasswordOld.getText().toString();
                final String newPasswordString = mPasswordNew1.getText().toString();
                final String passwordNew2String = mPasswordNew2.getText().toString();

                if(passwordString.equals("") || newPasswordString.equals("") || passwordNew2String.equals("")){
                    Toast.makeText(getApplicationContext(), "Cannot enter a blank password. Please try again.", Toast.LENGTH_SHORT).show();
                } else if (passwordNew2String.length() <= 6 || newPasswordString.length() <= 6){
                    Toast.makeText(getApplicationContext(), "Password must be greater than 6 characters in length", Toast.LENGTH_SHORT).show();
                } else if(newPasswordString.equals(passwordNew2String)){ //Check that both of the new password inputs are equal.
                    resetPassword(passwordString, newPasswordString); //Attempt to reset a users password.
                } else {
                    Toast.makeText(getApplicationContext(),"Passwords do not match. Please try again...",Toast.LENGTH_LONG).show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /** Takes both the old password and the new password and resets the users password to the new one.
     * @param rePassword - Previous password
     * @param newPassword - New password
     */
    private void resetPassword(final String rePassword, final String newPassword){
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);

        if(isTesting){ //  Checks if we are testing so we don't actually reset a users password.
            Log.e(TAG, "Currently in testing phase, haven't reset the users password.");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(mUserProfile.getEmail(), rePassword);

        if(mAuth.getCurrentUser() != null){
            // Prompt the user to re-provide their sign-in credentials
            mAuth.getCurrentUser().reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mAuth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password updated.");
                            Toast.makeText(getApplicationContext(),"Password updated.",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG,"Password update failed.");
                        Toast.makeText(getApplicationContext(),"Password update failed, please try again.",Toast.LENGTH_SHORT).show();
                    });
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG,"Failed to re-authenticate.");
                Toast.makeText(getApplicationContext(),"Failed to reset password. Make sure you are connected to the internet and have inputted a correct previous password.",Toast.LENGTH_LONG).show();
            });
        } else {
            Log.e(TAG, "Unable to get current user details");
            Toast.makeText(getApplicationContext(), "Unable to get current user details. Make sure you are connected to the internet", Toast.LENGTH_SHORT).show();
        }

    }


    /** Activated on 'save settings' button press.
     * @param v - Button View.
     */
    public void saveSettings(View v){

        //  Ignore save button press if countdown isn't finished or isn't equal to 0s.

        // TEST METHODS - Checks for very specific username string input to enter test screens.
        for(HiddenViews hv : HiddenViews.values()){
            if(mUsername.getText().toString().equals(hv.getUsernameKeyWord())){
                Intent destination = new Intent(this, hv.getClassDestination());
                startActivity(destination);
            }
        }

        if(saveCountdownFinished || saveCountdownSecondsLeft == 0) {

            //  Set all info not set in other proprietary methods.
            mTempUserProfile.setAbout(mAboutMe.getText().toString());
            mTempUserProfile.setDisplayName(mUsername.getText().toString());

            //  Update the server relative to the client or throw an exception if a valid user isn't found.
            try {
                updateFirebase();
            } catch (InvalidUserException | ProfileImageException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please wait " + saveCountdownSecondsLeft + " second(s) before pressing save again", Toast.LENGTH_LONG).show();
        }

    }


    /** Activated on checking of privacy switches
     * @param v - Switch view.
     */
    public void onSwitchClicked(View v){
        boolean switched = ((Switch) v).isChecked(); // Check if switch is on or not.

        HashMap<String, Object> tempPrivacy = new HashMap<>();

        int tabPosition = mProfileVisibilityTab.getSelectedTabPosition();

        switch(tabPosition){
            case 0:
                tempPrivacy = mTempUserProfile.getPublicPrivacy();
                syncVisibility(SyncType.PUBLIC);
                break;
            case 1:
                tempPrivacy = mTempUserProfile.getPrivacyPrivate();
                syncVisibility(SyncType.PRIVATE);
        }

        switch(v.getId()){ // Retrieve switches unique ID.

            //  Checks the 'private profile' switch. Hides the tab bar if private is enabled & defaults the switches to the 'private' profile options.
            //  Otherwise makes sure the profile privacy settings tab bar is shown.
            case R.id.settings_private_toggle:
                if(switched) {
                    setPrivacyOptions(mTempUserProfile.getPrivacyPrivate());
                    mProfileVisibilityTab.setVisibility(View.GONE);
                } else {
                    setPrivacyOptions(mTempUserProfile.getPublicPrivacy());
                    TabLayout.Tab tab = mProfileVisibilityTab.getTabAt(0);
                    assert tab != null;
                    tab.select();
                    mProfileVisibilityTab.setVisibility(View.VISIBLE);
                }

            case R.id.settings_privacy_about_me:
                if(switched)
                    tempPrivacy.put("display_about_me", true);
                else
                    tempPrivacy.put("display_about_me", false);
                break;
            case R.id.settings_privacy_recipes:
                if(switched)
                    tempPrivacy.put("display_recipes", true);
                else
                    tempPrivacy.put("display_recipes", false);
                break;
            case R.id.settings_privacy_username:
                if(switched)
                    tempPrivacy.put("display_username", true);
                else
                    tempPrivacy.put("display_username", false);
                break;
            case R.id.settings_privacy_profile_image:
                if(switched)
                    tempPrivacy.put("display_profile_image", true);
                else
                    tempPrivacy.put("display_profile_image", false);
                break;
            case R.id.settings_privacy_filters:
                if(switched)
                    tempPrivacy.put("display_filters", true);
                else
                    tempPrivacy.put("display_filters", false);
                break;
            case R.id.settings_privacy_feed:
                if(switched)
                    tempPrivacy.put("display_feed", true);
                else
                    tempPrivacy.put("display_feed", false);
                break;
        }

        switch(tabPosition){
            case 0:
                syncVisibility(SyncType.PUBLIC);
                break;
            case 1:
                syncVisibility(SyncType.PRIVATE);
        }

    }

    /** Activated on Filters checked
     * @param v - Filters checkbox view.
     */
    public void onCheckboxClicked(View v) {
        // Is the Checkbox Checked?
        boolean checked = ((CheckBox) v).isChecked();

        Preferences preferences = mTempUserProfile.getPreferences();

        //  Check the type of filter. For example, dietary, religious.
        switch(currentFilterType){
            case ALLERGENS:
                // Check which checkbox was clicked.
                switch(v.getId()) {
                    case R.id.allergy_nuts:
                        if (checked)
                            preferences.setAllergy_nuts(true);
                        else
                            preferences.setAllergy_nuts(false);
                        break;
                    case R.id.allergy_eggs:
                        if (checked)
                            preferences.setAllergy_eggs(true);
                        else
                            preferences.setAllergy_eggs(false);
                        break;
                    case R.id.allergy_milk:
                        if (checked)
                            preferences.setAllergy_milk(true);
                        else
                            preferences.setAllergy_milk(false);
                        break;
                    case R.id.allergy_shellfish:
                        if (checked)
                            preferences.setAllergy_shellfish(true);
                        else
                            preferences.setAllergy_shellfish(false);
                        break;
                    case R.id.allergy_soy:
                        if (checked)
                            preferences.setAllergy_soya(true);
                        else
                            preferences.setAllergy_soya(false);
                        break;
                    case R.id.allergy_wheat:
                        if (checked)
                            preferences.setAllergy_gluten(true);
                        else
                            preferences.setAllergy_gluten(false);
                        break;
                }
            case DIETARY:
                CheckBox vegan = findViewById(R.id.dietary_vegan);
                CheckBox vegetarian = findViewById(R.id.dietary_vegetarian);
                CheckBox pescatarian = findViewById(R.id.dietary_pescatarian);

                switch(v.getId()) {
                    case R.id.dietary_vegan:
                        if (checked) {
                            preferences.setVegan(true);
                            preferences.setVegetarian(false);
                            preferences.setPescatarian(false);
                            vegetarian.setChecked(false);
                            pescatarian.setChecked(false);
                        } else
                            preferences.setVegan(false);
                        break;
                    case R.id.dietary_pescatarian:
                        if(checked) {
                            preferences.setPescatarian(true);
                            preferences.setVegetarian(false);
                            preferences.setVegan(false);
                            vegetarian.setChecked(false);
                            vegan.setChecked(false);
                        } else
                            preferences.setPescatarian(false);
                        break;
                    case R.id.dietary_vegetarian:
                        if(checked) {
                            preferences.setVegetarian(true);
                            preferences.setVegan(false);
                            preferences.setPescatarian(false);
                            vegan.setChecked(false);
                            pescatarian.setChecked(false);
                        } else
                            preferences.setVegetarian(false);
                }
            case RELIGIOUS:
                //TODO
            case HEALTH:
                //TODO
        }
    }

    /** Initiate all page items that are not already placed within separate fragments **/
    private void initPageItems(){
        //  Basic user info.
        mUsername = findViewById(R.id.settings_input_username);
        mAboutMe = findViewById(R.id.settings_input_about_me);
        mProfileImage = findViewById(R.id.public_profile_image);

        //  Privacy
        mDisplay_about_me = findViewById(R.id.settings_privacy_about_me);
        mDisplay_profile_image = findViewById(R.id.settings_privacy_profile_image);
        mDisplay_recipes = findViewById(R.id.settings_privacy_recipes);
        mDisplay_username = findViewById(R.id.settings_privacy_username);
        mDisplay_filters = findViewById(R.id.settings_privacy_filters);
        mDisplay_feed = findViewById(R.id.settings_privacy_feed);

        //  Tabbed profile listener (Public/Private)
        mProfileVisibilityTab = findViewById(R.id.profile_visibility_tab_bar);

        mPrivateProfileEnabled = findViewById(R.id.settings_private_toggle);

        mProgress = findViewById(R.id.settings_progress);
        mProgressText = findViewById(R.id.settings_progress_text);
    }

    /** Method called when we click to change the users profile image.
     * @param v - The associated view object of the users profile image.
     */
    public void changeProfileImage(View v) {

        //  Check if the version of Android is above 'Marshmallow' we check for additional permission.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            //  Checks if permission has already been granted to read from external storage (our image picker)
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
               //   Ask for permission.
               String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
               requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //  Read permission has been granted already.
                imageSelector();
            }
        } else {
            imageSelector();
        }
    }

    /** returns the result of our permission caller.
     * @param requestCode - Unique permission request code. (default = 1001)
     * @param permissions - A string array of the permissions to be granted/denied (Should only a single READ_EXTERNAL_STORAGE permission)
     * @param grantResults - Whether the permission is accepted or denied. Accepted = 0, Denied = -1.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_CODE){ // Checks if we are at least receiving the correct unique permission code.

            //  Checks we return a string and not a null value and that the permission is granted.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                imageSelector();
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //  Open our image picker.
    private void imageSelector(){
        Intent images = new Intent(Intent.ACTION_PICK);
        images.setType("image/*"); // Only open the 'image' file picker. Don't include videos, audio etc...
        startActivityForResult(images, IMAGE_REQUEST_CODE); // Start the image picker and expect a result once an image is selected.
    }

    /** Handle our activity result for the image picker.
     * @param requestCode - Image request code.
     * @param resultCode - Success/failure code. 0 = success, -1 = failure.
     * @param data - Our associated image data.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //   Check for a valid request code and successful result.
        if(requestCode == IMAGE_REQUEST_CODE && resultCode==RESULT_OK){
            if(data!=null && data.getData()!= null){
                mProfileImage.setImageResource(R.drawable.temp_settings_profile_image); // Set to a default image if image uploading fails.

                mImageUri = data.getData();
                currentImageURI = mImageUri.toString();

                //  Use Glides image functionality to quickly load a circular, center cropped image.
                Glide.with(this)
                        .load(mImageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mProfileImage);
            }
        }
    }

    /** Image checker.
     *  Used to reduce wait times for the user when uploading on a slow network.
     *  Also limits the data that has to be stored and queried from Firebase.
     *  @param uri - The unique uri of the image file location from the users storage.
     *  @throws ProfileImageException - Throws if the image file is too large or the format isn't a supported image format.
     */
    protected void checkImage(Uri uri) throws ProfileImageException {

        //  If the image files size is greater than the max file size in mb converted to bytes throw an exception and return this issue to the user.
        if(getSize(this, uri) > MAX_IMAGE_FILE_SIZE_IN_MB * 1000000){
            Toast.makeText(this, "Image exceeded: " + MAX_IMAGE_FILE_SIZE_IN_MB + "mb limit. Please choose a different file.", Toast.LENGTH_LONG).show();
            throw new ProfileImageException("Profile image exceeded max file size: " + MAX_IMAGE_FILE_SIZE_IN_MB + "mb");
        }

        boolean formatIsSupported = isImageFormatSupported(this, uri); // Check if the image is of a supported format
        String extension = getExtension(this, uri); // Grab the extension as a string.

        //  If our format isn't supported then throw an exception. Otherwise continue and don't throw an exception indicating a successful image check.
        if(!formatIsSupported) {
            Toast.makeText(this, "Image extension: '" + getExtension(this, uri) +"' is not supported.", Toast.LENGTH_LONG).show();

            new CountDownTimer(3600, 200){ // Display another toast message after the existing one. Long Toast messages last 3500ms, hence 3600 delay.
                @Override
                public void onTick(long millisUntilFinished) { /*Do Nothing...*/ }
                @Override
                public void onFinish() {
                    //   Make the user aware of the supported formats they can upload.
                    Toast.makeText(getApplicationContext(), "Supported formats: " + getPrintableSupportedFormats(), Toast.LENGTH_LONG).show();
                }
            }.start();

            throw new ProfileImageException("Image format type: " + extension + " is not supported");
        }
    }

    /** Upload the image to Firebase. Initiated before saving preferences if the format and size is supported to give some time for the app to upload the image.
     *  Does not update the users reference to the image. This is updated after saving the users preferences.
     *
     * @param uri - The unique uri of the image file location from the users storage.
     * @throws ProfileImageException - Thrown if URL cannot be retrieved. This will only fail if there is a reference to a blank file.
     *  In normal operation this shouldn't happen.
     */
    protected void uploadImage(Uri uri) throws ProfileImageException {
        String extension = getExtension(this, uri);

        if(!isTesting){ // Check if we are testing, if not check the image.
            checkImage(uri); // Check the image doesn't throw any exceptions
        }

        IMAGE_IS_UPLOADING = true; // State that the image is still uploading and therefore we shouldn't save a reference on firebase to it yet.

        if(isTesting){
            runOnUiThread(() -> { // Force onto UI thread when testing. By default picks the wrong thread.
                mProgress.setVisibility(View.VISIBLE);
                mProgressText.setVisibility(View.VISIBLE);
            });
            return; // Don't execute any extra code.
        } else {
            mProgress.setVisibility(View.VISIBLE);
            mProgressText.setVisibility(View.VISIBLE);
        }

        /*  Create a unique reference of the format. 'image/profile/[UNIQUE UID]/profile_image.[EXTENSION].
            Whereby [UNIQUE UID] = the Unique id of the user, [EXTENSION] = file image extension. E.g. .jpg,.png. */
        StorageReference mImageStorage = mStorageReference.child("images/profile/" + mUserProfile.getUID() + "/profile_image." + extension);

        mImageUploadTask = mImageStorage.putFile(uri);

        //  Check if the upload fails
        mImageUploadTask
                .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Failed to upload profile image.", Toast.LENGTH_SHORT).show();
                        mProgress.setVisibility(View.GONE);
                        mProgressText.setVisibility(View.GONE);
                    })
                .addOnSuccessListener(taskSnapshot -> mImageStorage.getDownloadUrl()
                        .addOnSuccessListener(locationUri -> { // Successful upload.
                            mUserProfile.setImageURL(locationUri.toString()); // Update the UserInfoPrivate class with this new image URL.
                            IMAGE_IS_UPLOADING = false; // State we have finished uploading (a reference exists).
                            currentImageURI = locationUri.toString();
                        }).addOnFailureListener(e -> {
                            mProgress.setVisibility(View.GONE);
                            mProgressText.setVisibility(View.GONE);
                            throw new RuntimeException("Unable to grab image URL from Firebase for image URL being uploaded currently. This shouldn't happen.");
                        }))
                .addOnProgressListener(taskSnapshot -> {
                    double progressDouble = ((double) taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()) * 100;

                    Log.e(TAG, "PROGRESS DOUBLE IS: " + progressDouble);

                    int progressAmount = (int) progressDouble;
                    mProgress.setProgress(progressAmount);

                    String saveText = "Saving Image... " + progressAmount + "%";
                    mProgressText.setText(saveText);
        });
    }

    /** Load basic firebase data & any data not present in fragments. **/
    private void loadProfileData(){
        mUsername.setText(mTempUserProfile.getDisplayName());
        mAboutMe.setText(mTempUserProfile.getAbout());
        mPrivateProfileEnabled.setChecked(mTempUserProfile.isPrivateProfileEnabled());

        //  Load privacy switches.
        setPrivacyOptions(mTempUserProfile.getPublicPrivacy());
    }


    /** Set which privacy switches should be selected
     * @param privacy - HashMap of valid privacy options.
     */
    private void setPrivacyOptions(HashMap<String, Object> privacy){
        mDisplay_username.setChecked( (boolean) privacy.get("display_username"));
        mDisplay_about_me.setChecked( (boolean) privacy.get("display_about_me"));
        mDisplay_profile_image.setChecked( (boolean) privacy.get("display_profile_image"));
        mDisplay_recipes.setChecked( (boolean) privacy.get("display_recipes"));
        mDisplay_filters.setChecked( (boolean) privacy.get("display_filters"));
        mDisplay_feed.setChecked( (boolean) privacy.get("display_feed"));
    }

    /** Update the server relative to the client on a valid 'Save Settings' button press
     * @throws InvalidUserException - Error returned when a valid user cannot be found and therefore the server
     * cannot update relative to the client. This can occur when Wifi signal is lost.
     */
    private void updateFirebase() throws InvalidUserException, ProfileImageException {

        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);
        FirebaseUser user = mAuth.getCurrentUser();

        String usernameInput = mUsername.getText().toString();
        String aboutMeInput = mAboutMe.getText().toString();

        if(user != null){ //Checks for a valid user.
            String usersEmail = requireNonNull(mAuth.getCurrentUser()).getEmail();

            if(IMAGE_IS_UPLOADING){ //  Prevent saving to firebase if an image is currently uploading.
                Toast.makeText(this, "We are still uploading your profile image. Try again in a second.", Toast.LENGTH_SHORT).show();
                return;
            }

            //  If the 'saveCountdown' countdown has finished. Restart it.
            if(saveCountdownFinished){
                saveCountdown.start();
                saveCountdownFinished = false;
            }

            //  Check the users email exists. It should do.
            if(usersEmail != null){
                if (usernameInput.contains(usersEmail) || aboutMeInput.contains(usersEmail)){ //Make sure none of the TextView input fields have the users email contained in them.
                    Log.e(TAG, "Email address found in Username/About me section");
                    Toast.makeText(getApplicationContext(), "Found your email address in Username/About me section. Unable to save settings.", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                throw new RuntimeException("Unable to find associated email address of the user");
            }

            //  Return if image is not of a supported format & of the correct size. Else throw an exception and prevent saving of settings.
            if(mImageUri != null && !currentImageURI.equals(mUserProfile.getImageURL())){
                Log.e(TAG, "I am checking and uploading the image image");
                checkImage(mImageUri); // Check the image URI object for it's file type and file size. Throw an error and exit if an issue is present.
                try {
                    uploadImage(mImageUri); // Attempt to upload the image in storage to Firebase.
                } catch (ProfileImageException e) {
                    e.printStackTrace();
                    return;
                }
            }

            //  Create maps to store associated data.
            HashMap<String, Object> map = new HashMap<>();
            HashMap<String, Object> prefMap = new HashMap<>();
            HashMap<String, Object> privacyPublic = new HashMap<>();
            HashMap<String, Object> privacyPrivate = new HashMap<>();

            map.put("UID", requireNonNull(mAuth.getCurrentUser()).getUid());
            map.put("email", usersEmail);
            map.put("displayName", mTempUserProfile.getDisplayName());
            map.put("imageURL", mTempUserProfile.getImageURL());
            map.put("about", mTempUserProfile.getAbout());
            map.put("mealPlan", mTempUserProfile.getMealPlanner());
            map.put("shortPreferences", mTempUserProfile.getShortPreferences());
            map.put("firstAppLaunch", mTempUserProfile.getFirstAppLaunch());
            map.put("firstPresentationLaunch", mTempUserProfile.getFirstPresentationLaunch());
            map.put("firstMealPlannerLaunch", mTempUserProfile.getFirstMealPlannerLaunch());
            map.put("posts", mTempUserProfile.getPosts());

            Preferences preferences = mTempUserProfile.getPreferences();

            prefMap.put("allergy_eggs", preferences.isAllergy_eggs());
            prefMap.put("allergy_gluten", preferences.isAllergy_gluten());
            prefMap.put("allergy_milk", preferences.isAllergy_milk());
            prefMap.put("allergy_nuts", preferences.isAllergy_nuts());
            prefMap.put("allergy_shellfish", preferences.isAllergy_shellfish());
            prefMap.put("allergy_soya", preferences.isAllergy_soya());
            prefMap.put("vegan", preferences.isVegan());
            prefMap.put("vegetarian", preferences.isVegetarian());
            prefMap.put("pescatarian", preferences.isPescatarian());

            map.put("preferences", prefMap);

            HashMap<String, Object> privacyPublicMap = mTempUserProfile.getPublicPrivacy();
            HashMap<String, Object> privacyPrivateMap = mTempUserProfile.getPrivacyPrivate();

            privacyPublic.put("display_username", privacyPublicMap.get("display_username"));
            privacyPublic.put("display_about_me", privacyPublicMap.get("display_about_me"));
            privacyPublic.put("display_recipes", privacyPublicMap.get("display_recipes"));
            privacyPublic.put("display_profile_image", privacyPublicMap.get("display_profile_image"));
            privacyPublic.put("display_filters", privacyPublicMap.get("display_filters"));
            privacyPublic.put("display_feed", privacyPublicMap.get("display_feed"));

            privacyPrivate.put("display_username", privacyPrivateMap.get("display_username"));
            privacyPrivate.put("display_about_me", privacyPrivateMap.get("display_about_me"));
            privacyPrivate.put("display_recipes", privacyPrivateMap.get("display_recipes"));
            privacyPrivate.put("display_profile_image", privacyPrivateMap.get("display_profile_image"));
            privacyPrivate.put("display_filters", privacyPrivateMap.get("display_filters"));
            privacyPrivate.put("display_feed", privacyPrivateMap.get("display_feed"));

            map.put("privateProfileEnabled", mTempUserProfile.isPrivateProfileEnabled());
            map.put("privacyPublic", privacyPublic);
            map.put("privacyPrivate", privacyPrivate);

            // TODO - Sync public and private privacy.
            //syncVisibility(privacyPublic);

            DocumentReference usersRef = mRef.document(user.getUid());

            //  Sync the Firebase info with the client info if successful.
            usersRef.update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"User Preferences Saved",Toast.LENGTH_SHORT).show();
                            mUserProfile = new UserInfoPrivate(map, prefMap, privacyPrivate, privacyPublic); //Create a new local UserInfoPrivate class based upon our inputs.
                            Log.e(TAG, "About me is: " + mUserProfile.getAbout());
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to save user preferences", Toast.LENGTH_SHORT).show());

        } else {
            Toast.makeText(getApplicationContext(),"Invalid user login credentials. Consider logging out and then back in again.",Toast.LENGTH_LONG).show();
            throw new InvalidUserException("Invalid user login credentials. Consider logging out and then back in again.");
        }

    }

    /** Sync the Public and Private privacy settings. For e.g. if a user enabled an option in the public profile
     *  by default it is enabled on the private profile privacy section as well.
     * @param st - Current tab type. IE PRIVATE or PUBLIC.
     */
    private void syncVisibility(SyncType st){

        HashMap<String, Object> privacyPublic = mTempUserProfile.getPublicPrivacy();
        HashMap<String, Object> privacyPrivate = mTempUserProfile.getPrivacyPrivate();

        //  Construct iterators for both HashMaps.
        Iterator publicIterator = privacyPublic.entrySet().iterator();
        Iterator privateIterator = privacyPrivate.entrySet().iterator();

        if(st == SyncType.PRIVATE){ //  If the current tab is set to 'PRIVATE continue'
            while (privateIterator.hasNext()) {
                Map.Entry privatePrivacyElement = (Map.Entry)privateIterator.next(); // Increment value.

                String key = (String) privatePrivacyElement.getKey();
                boolean value = (boolean) privatePrivacyElement.getValue();

                if(!value) // If private privacy switch is off, set public switch to off.
                   privacyPublic.put(key, false);
            }
        } else {
            while (publicIterator.hasNext()) {
                Map.Entry publicPrivacyElement = (Map.Entry)publicIterator.next();

                String key = (String) publicPrivacyElement.getKey();
                boolean value = (boolean) publicPrivacyElement.getValue();

                if(value){ // If public privacy switch is on, set public switch to on.
                    privacyPrivate.put(key, true);
                }
            }
        }
    }

    /** Handle sending serializable data to tabbed fragments **/
    private void sendSerializableToFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Create a new bundle, complete with the users preferences to be set as arguments for the fragment.
        Bundle prefBundle = new Bundle();
        prefBundle.putSerializable("preferences", mTempUserProfile.getPreferences());
        currentFragment.setArguments(prefBundle);

        //  Switch current checkbox table with our new fragment.
        fragmentTransaction.replace(R.id.settings_checkbox_table, currentFragment);
        fragmentTransaction.commit();
    }

    /** Listeners for preferences fragment.
     *  Check which tab is selected, load the associated fragment and send associated serializable preference data to accommodate.
     */
    private void initPreferencesFragmentsTabListener(){

        mPreferencesTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0: // Tab order in index. 0 = leftmost element.
                        currentFragment = new AllergensFragment(); // Create a new fragment.
                        currentFilterType = filterType.ALLERGENS; // Set current filter type to handle 'checkbox click' actions.
                        break;
                    case 1:
                        currentFragment = new DietaryFragment();
                        currentFilterType = filterType.DIETARY;
                        break;

                }
                sendSerializableToFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { /* Nothing here */ }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { /* Nothing here */}

        });
    }

    /** Listeners for privacy public/private tab.
     *  Check which tab is selected, load associated data.
     */
    private void initProfileVisibilityTabListener(){

        mProfileVisibilityTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0: // Tab order in index. 0 = leftmost element.
                            setPrivacyOptions(mTempUserProfile.getPublicPrivacy());
                        break;
                    case 1:
                            setPrivacyOptions(mTempUserProfile.getPrivacyPrivate());
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { /* Nothing here */ }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { /* Nothing here */}

        });
    }
}
