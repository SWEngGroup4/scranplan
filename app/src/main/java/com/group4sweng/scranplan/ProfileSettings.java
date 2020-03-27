package com.group4sweng.scranplan;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.group4sweng.scranplan.Helper.CheckAndroidServices;
import com.group4sweng.scranplan.Helper.HiddenViews;
import com.group4sweng.scranplan.PreferencesTabs.AllergensFragment;
import com.group4sweng.scranplan.PreferencesTabs.DietaryFragment;
import com.group4sweng.scranplan.UserInfo.FilterType;
import com.group4sweng.scranplan.UserInfo.Preferences;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;

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

    // TAG for Profile Settings
    private final static String TAG = "ProfileSettings";
    private static final int IMAGE_REQUEST_CODE = 2;
    private static final int PERMISSION_CODE = 1001;
    private static final int MAX_IMAGE_FILE_SIZE_IN_MB = 6;
    private static boolean IMAGE_IS_UPLOADING = false;
    private String prevProfileImage;

    //  Default filter type enumeration. Types shown in 'FilterType' interface.
    static filterType currentFilterType = filterType.ALLERGENS;

    // Firebase user variables.
    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    CollectionReference mRef = mDatabase.collection("users");
    FirebaseStorage mStorage = FirebaseStorage.getInstance();

    StorageReference mStorageReference = mStorage.getReference();

    private Uri mImageUri;

    Context mContext;

    UserInfoPrivate mUserProfile;

    //  TODO - Add profile image.
    //  TODO - Add valid network connection checks.

    //  Basic user settings.
    ImageView mProfileImage;
    TextView mUsername;
    TextView mAboutMe;
    TextView mNumRecipes;

    /*//  User allegern filters.
    CheckBox mAllergy_nuts;
    CheckBox mAllergy_milk;
    CheckBox mAllergy_eggs;
    CheckBox mAllergy_shellfish;
    CheckBox mAllergy_soy;
    CheckBox mAllergy_gluten;

    //  User dietary filters.
    CheckBox mDietary_vegan;
    CheckBox mDietary_vegetarian;
    CheckBox mDietary_pescatarian;*/




    //  User privacy filters.
    Switch mDisplay_username;
    Switch mDisplay_about_me;
    Switch mDisplay_recipes;
    Switch mDisplay_profile_image;
    Switch mDisplay_filters;

    //  Input fields (Delete profile & Change password)
    EditText mPasswordConfirm;
    EditText mPasswordNew1;
    EditText mPasswordNew2;
    EditText mPasswordOld;

    TabLayout mPreferencesTabs;
    Fragment currentFragment = new AllergensFragment();

    AllergensFragment mAllergensFragment;
    DietaryFragment mDietaryFragment;

    //  Timer in milliseconds for minimum interval between 'Save Settings' button presses..
    private final int COUNTDOWN_TIMER_MILLIS = 10000;

    //  Timer in milliseconds for update rate of the Toast message.
    private final int COUNTDOWN_INTERVAL_MILLIS = 1000;

    private long saveCountdownSecondsLeft = 0; //Initial timer is set to 0.
    private boolean saveCountdownFinished = true; //Initial countdown is finished.

    CheckAndroidServices androidServices;
    boolean connectionEstablished;
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

        //connectionEstablished = checkNetworkConnection(mContext);
        mUserProfile = (UserInfoPrivate) getIntent().getSerializableExtra("user"); //Grabs serializable UserInfoPrivate data from main activity.
        if(mUserProfile != null){ //Checks if there is actually any Serializable data received.
            //  Preferences
            mPreferencesTabs = findViewById(R.id.preferences_tab_bar);
            sendSerializableToFragment();
            initTabFragmentListener();

            loadProfileData();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //connectionEstablished = checkNetworkConnection(mContext);
        if(mUserProfile != null){
            loadProfileData();
        }
    }


    @Override
    public void onBackPressed() {
        //  Send back with intent and update the MainActivity's UserInfoPrivate class with the new User Info.
        Intent returnIntent = new Intent(this, Home.class);
        returnIntent.putExtra("user", mUserProfile);
        setResult(Activity.RESULT_OK, returnIntent);
        startActivity(returnIntent);
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
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String passwordString = mPasswordConfirm.getText().toString();

                                if(passwordString.equals("")){
                                    Log.e(TAG, "Password cannot be set to an empty field");
                                    Toast.makeText(getApplicationContext(), "Cannot enter a blank password. Please try again.", Toast.LENGTH_SHORT).show();
                                } else if(passwordString.length() <= 6){
                                    Log.e(TAG, "Password must be greater than 6 characters in length");
                                    Toast.makeText(getApplicationContext(),"Password must be greater than 6 characters in length", Toast.LENGTH_SHORT).show();
                                } else {
                                    deleteAccount(passwordString);  //Delete our account
                                }
                            }
                        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { //Allow the user to cancel the operation.
                                dialog.cancel();
                            }
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

        if(mAuth.getCurrentUser() != null){

            for(ImageFormats extension : ImageFormats.values()){
                mStorageReference.child("images/profile/" + mAuth.getUid() + "/profile_image." + extension.toString()).delete().addOnSuccessListener(aVoid12 -> {
                    Log.e(TAG, "Deleted entry: images/profile/" + mAuth.getUid() + "/profile_image." + extension.toString());
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Entry: images/profile/" + mAuth.getUid() + "/profile_image." + extension.toString() + " dosen't exist. Ignoring");
                    }
                });
            }

            mAuth.getCurrentUser().reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
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
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Deleted associated collection 'users'. Couldn't delete all users data. User has been asked for this to be removed manually.");
                            Toast.makeText(getApplicationContext(), "Failed to delete all associated user info. Please contact Scranplan with your email address to have your data manually removed.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to re-authenticate. May have entered the wrong password");
                    Toast.makeText(getApplicationContext(),"Could not delete data. Make sure you are connected to the internet and have inputted a correct previous password.", Toast.LENGTH_LONG).show();
                }
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
                    mAuth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Password updated.");
                                Toast.makeText(getApplicationContext(),"Password updated.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"Password update failed.");
                            Toast.makeText(getApplicationContext(),"Password update failed, please try again.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"Failed to re-authenticate.");
                    Toast.makeText(getApplicationContext(),"Failed to reset password. Make sure you are connected to the internet and have inputted a correct previous password.",Toast.LENGTH_LONG).show();
                }
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
        //connectionEstablished = checkNetworkConnection(mContext);

        /*if(!connectionEstablished){
            Toast.makeText(getApplicationContext(), "A connection cannot be established. Please make sure you are connected to the internet otherwise your settings may not be saved.", Toast.LENGTH_LONG).show();
        }*/

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
            mUserProfile.setAbout(mAboutMe.getText().toString());
            mUserProfile.setDisplayName(mUsername.getText().toString());
            //mUserProfile.setNumRecipes(Long.parseLong(mNumRecipes.getText().toString()));

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

        HashMap<String, Object> privacy = mUserProfile.getPrivacy();

        switch(v.getId()){ // Retrieve switches unique ID.
            case R.id.settings_privacy_about_me:
                if(switched)
                    privacy.put("display_about_me", true);
                else
                    privacy.put("display_about_me", false);
            case R.id.settings_privacy_recipes:
                if(switched)
                    privacy.put("display_recipes", true);
                else
                    privacy.put("display_recipes", false);
            case R.id.settings_privacy_username:
                if(switched)
                    privacy.put("display_username", true);
                else
                    privacy.put("display_username", false);
            case R.id.settings_privacy_profile_image:
                if(switched)
                    privacy.put("display_profile_image", true);
                else
                    privacy.put("display_profile_image", false);
            case R.id.settings_privacy_filters:
                if(switched)
                    privacy.put("display_filters", true);
                else
                    privacy.put("display_filters", false);
        }
    }

    /** Activated on Filters checked
     * @param v - Filters checkbox view.
     */
    public void onCheckboxClicked(View v) {
        // Is the Checkbox Checked?
        boolean checked = ((CheckBox) v).isChecked();

        Preferences preferences = mUserProfile.getPreferences();

        Log.e(TAG, "Current filter type: " + currentFilterType.name());
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
                switch(v.getId()){
                    case R.id.dietary_vegan:
                        if(checked)
                            mUserProfile.getPreferences().setVegan(true);
                        else
                            mUserProfile.getPreferences().setVegan(false);
                        break;
                    case R.id.dietary_pescatarian:
                        if(checked)
                            preferences.setPescatarian(true);
                        else
                            preferences.setPescatarian(false);
                        break;
                    case R.id.dietary_vegetarian:
                        if(checked)
                            preferences.setVegetarian(true);
                        else
                            preferences.setVegetarian(false);
                }
            case RELIGIOUS:
                //TODO
            case HEALTH:
                //TODO
        }
    }

    private void initPageItems(){
        //  Basic user info.
        mUsername = findViewById(R.id.settings_input_username);
        mAboutMe = findViewById(R.id.settings_input_about_me);
        mNumRecipes = findViewById(R.id.profile_recipes);
        mProfileImage = findViewById(R.id.public_profile_image);

        //  Privacy
        mDisplay_about_me = findViewById(R.id.settings_privacy_about_me);
        mDisplay_profile_image = findViewById(R.id.settings_privacy_profile_image);
        mDisplay_recipes = findViewById(R.id.settings_privacy_recipes);
        mDisplay_username = findViewById(R.id.settings_privacy_username);
        mDisplay_filters = findViewById(R.id.settings_privacy_filters);

    }

    public void changeProfileImage(View v) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
               //Permission isn't granted. So ask for permission.
               String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
               requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //Permission has been granted already
                imageSelector();
            }
        } else {
            //System OS is less than Marshmallow.
            imageSelector();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    imageSelector();
                } else {
                    Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void imageSelector(){
        Intent images = new Intent(Intent.ACTION_PICK);
        images.setType("image/*");
        startActivityForResult(images, IMAGE_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST_CODE && resultCode==RESULT_OK){
            if(data!=null && data.getData()!= null){
                mProfileImage.setImageResource(R.drawable.temp_settings_profile_image);

                mImageUri = data.getData();

                //mProfileImage.setImageURI(mImageUri);
                Glide.with(this)
                        .load(mImageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mProfileImage);

                try {
                    uploadImage(mImageUri);
                } catch (ProfileImageException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void checkImage(Uri uri) throws ProfileImageException {
        if(getSize(this, uri) > MAX_IMAGE_FILE_SIZE_IN_MB * 100000){
            Toast.makeText(this, "Image exceeded: " + MAX_IMAGE_FILE_SIZE_IN_MB + "mb limit. Please choose a different file.", Toast.LENGTH_LONG).show();
            throw new ProfileImageException("Profile image exceeded max file size: " + MAX_IMAGE_FILE_SIZE_IN_MB + "mb");
        }

        boolean formatIsSupported = isImageFormatSupported(this, uri);
        String extension = getExtension(this, uri);

        if(!formatIsSupported) {
            Toast.makeText(this, "Image extension: '" + getExtension(this, uri) +"' is not supported.", Toast.LENGTH_LONG).show();

            new CountDownTimer(3600, 200){
                @Override
                public void onTick(long millisUntilFinished) { /*Do Nothing...*/ }
                @Override
                public void onFinish() {
                    Toast.makeText(getApplicationContext(), "Supported formats: " + getPrintableSupportedFormats(), Toast.LENGTH_LONG).show();
                }
            }.start();

            throw new ProfileImageException("Image format type: " + extension + " is not supported");
        }
    }

    private void uploadImage(Uri uri) throws ProfileImageException {
        String extension = getExtension(this, uri);

        checkImage(uri);

        IMAGE_IS_UPLOADING = true;
        StorageReference mImageStorage = mStorageReference.child("images/profile/" + mUserProfile.getUID() + "/profile_image." + extension);
        mImageStorage.putFile(uri).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload profile image.", Toast.LENGTH_SHORT).show()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mImageStorage.getDownloadUrl().addOnSuccessListener(locationUri -> {
                    mUserProfile.setImageURL(locationUri.toString());
                    IMAGE_IS_UPLOADING = false;
                }).addOnFailureListener(e -> {
                    throw new RuntimeException("Unable to grab image URL from Firebase for image URL being uploaded currently. This shouldn't happen.");
                });
            }
        });
    }


    private void loadProfileData(){
        mUsername.setText(mUserProfile.getDisplayName());
        mAboutMe.setText(mUserProfile.getAbout());
        String numOfRecipesString = "Recipes: " +  mUserProfile.getNumRecipes();
        mNumRecipes.setText(numOfRecipesString);

        //  Load allergen checkBoxes.
        //setFilters(currentFilterType);

        //  Load privacy switches.
        setPrivacyOptions(mUserProfile.getPrivacy());

    }


    /** Set which filter checkboxes should be selected
     * @param type - Enumeration of the type of filter to be displayed. E.g. Allegern, Religious...
     */
    /*
    private void setFilters(filterType type){
        switch(type){
            case ALLERGENS:
                mAllergy_eggs.setChecked(mUserProfile.getPreferences().isAllergy_eggs());
                mAllergy_milk.setChecked(mUserProfile.getPreferences().isAllergy_milk());
                mAllergy_nuts.setChecked(mUserProfile.getPreferences().isAllergy_nuts());
                mAllergy_shellfish.setChecked(mUserProfile.getPreferences().isAllergy_shellfish());
                mAllergy_soy.setChecked(mUserProfile.getPreferences().isAllergy_soya());
                mAllergy_gluten.setChecked(mUserProfile.getPreferences().isAllergy_gluten());
            case DIETARY:
                mDietary_pescatarian.setChecked(mUserProfile.getPreferences().isPescatarian());
                mDietary_vegan.setChecked(mUserProfile.getPreferences().isVegan());
                mDietary_vegetarian.setChecked(mUserProfile.getPreferences().isVegetarian());
            case RELIGIOUS:
                //TODO
            case HEALTH:
                //TODO
        }
    }*/


    /** Set which privacy swtiches should be selected
     * @param privacy - HashMap of valid privacy options.
     */
    private void setPrivacyOptions(HashMap<String, Object> privacy){
        mDisplay_username.setChecked( (boolean) privacy.get("display_username"));
        mDisplay_about_me.setChecked( (boolean) privacy.get("display_about_me"));
        mDisplay_profile_image.setChecked( (boolean) privacy.get("display_profile_image"));
        mDisplay_recipes.setChecked( (boolean) privacy.get("display_recipes"));
        mDisplay_filters.setChecked( (boolean) privacy.get("display_filters"));
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

            if(IMAGE_IS_UPLOADING){
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

            //  Return if image is not of a supported format
            if(mImageUri != null){
                checkImage(mImageUri);
            }

            HashMap<String, Object> map = new HashMap<>();
            HashMap<String, Object> prefMap = new HashMap<>();
            HashMap<String, Object> privacy = new HashMap<>();

            map.put("UID", requireNonNull(mAuth.getCurrentUser()).getUid());
            map.put("email", usersEmail);
            map.put("displayName", mUserProfile.getDisplayName());
            map.put("imageURL", mUserProfile.getImageURL());
            map.put("chefRating", mUserProfile.getChefRating());
            map.put("numRecipes", mUserProfile.getNumRecipes());
            map.put("about", mUserProfile.getAbout());
            map.put("mealPlan", mUserProfile.getMealPlanner());
            map.put("shortPreferences", mUserProfile.getShortPreferences());
            map.put("firstAppLaunch", mUserProfile.getFirstAppLaunch());
            map.put("firstPresentationLaunch", mUserProfile.getFirstPresentationLaunch());

            Preferences preferences = mUserProfile.getPreferences();

            prefMap.put("allergy_eggs", preferences.isAllergy_eggs());
            prefMap.put("allergy_gluten", preferences.isAllergy_gluten());
            prefMap.put("allergy_milk", preferences.isAllergy_milk());
            prefMap.put("allergy_nuts", preferences.isAllergy_nuts());
            prefMap.put("allergy_shellfish", preferences.isAllergy_shellfish());
            prefMap.put("allergy_soya", preferences.isAllergy_soya());
            prefMap.put("vegan", preferences.isVegan());
            prefMap.put("vegetarian", preferences.isVegetarian());
            prefMap.put("pescatarian", preferences.isPescatarian());


            Log.e(TAG, "PREFERENCES PESC FIREBASE" + mUserProfile.getPreferences().isPescatarian());
            //  TODO Finish all other preferences.

            map.put("preferences", prefMap);

            HashMap<String, Object> privacyMap = mUserProfile.getPrivacy();

            privacy.put("display_username", privacyMap.get("display_username"));
            privacy.put("display_about_me", privacyMap.get("display_about_me"));
            privacy.put("display_recipes", privacyMap.get("display_recipes"));
            privacy.put("display_profile_image", privacyMap.get("display_profile_image"));
            privacy.put("display_filters", privacyMap.get("display_filters"));

            map.put("privacy", privacy);

            mUserProfile = new UserInfoPrivate(map, prefMap, privacy); //Create a new UserInfoPrivate class based upon our inputs.

            DocumentReference usersRef = mRef.document(user.getUid());

            //  Sync the Firebase info with the client info if successful.
            usersRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"User Preferences Saved",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to save user preferences", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getApplicationContext(),"Invalid user login credentials. Consider logging out and then back in again.",Toast.LENGTH_LONG).show();
            throw new InvalidUserException("Invalid user login credentials. Consider logging out and then back in again.");
        }

    }

    private void sendSerializableToFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle prefBundle = new Bundle();
        prefBundle.putSerializable("preferences", mUserProfile.getPreferences());
        currentFragment.setArguments(prefBundle);

        fragmentTransaction.replace(R.id.settings_checkbox_table, currentFragment);
        fragmentTransaction.commit();
    }

    private void initTabFragmentListener(){

        mPreferencesTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        currentFragment = new AllergensFragment();
                        currentFilterType = filterType.ALLERGENS;
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


}
