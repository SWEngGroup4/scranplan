package com.group4sweng.scranplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.Exceptions.InvalidUserException;
import com.group4sweng.scranplan.Helper.CheckAndroidServices;
import com.group4sweng.scranplan.UserInfo.FilterType;
import com.group4sweng.scranplan.UserInfo.Preferences;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;

import static java.util.Objects.requireNonNull;

public class ProfileSettings extends AppCompatActivity implements FilterType {

    // TAG for Profile Settings
    final String TAG = "ProfileSettings";

    //  Default filter type enumeration. Types shown in 'FilterType' interface.
    filterType currentFilterType = filterType.ALLERGENS;

    // Firebase user variables.
    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    CollectionReference mRef = mDatabase.collection("users");

    Context mContext;

    UserInfoPrivate mUserProfile;

    //  TODO - Add profile image.
    //  TODO - Add valid network connection checks.

    //  Basic user settings.
    ImageView mProfileImage;
    TextView mUsername;
    TextView mAboutMe;
    TextView mNumRecipes;

    //  User allegern filters.
    CheckBox mAllergy_nuts;
    CheckBox mAllergy_milk;
    CheckBox mAllergy_eggs;
    CheckBox mAllergy_shellfish;
    CheckBox mAllergy_soy;
    CheckBox mAllergy_gluten;

    //  User privacy filters.
    Switch mDisplay_username;
    Switch mDisplay_about_me;
    Switch mDisplay_recipes;
    Switch mDisplay_profile_image;

    //  Timer in milliseconds for minimum interval between 'Save Settings' button presses..
    private final int COUNTDOWN_TIMER_MILLIS = 10000;

    //  Timer in milliseconds for update rate of the Toast message.
    private final int COUNTDOWN_INTERVAL_MILLIS = 1000;

    private long saveCountdownSecondsLeft = 0; //Initial timer is set to 0.
    private boolean saveCountdownFinished = true; //Initial countdown is finished.

    CheckAndroidServices androidServices;
    boolean connectionEstablished;

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

        //  Send back with intent and update the MainActivity's UserInfoPrivate class.
        Intent returnIntent = new Intent(this, MainActivity.class);
        returnIntent.putExtra("user", mUserProfile);
        setResult(Activity.RESULT_OK, returnIntent);
        startActivity(returnIntent);
    }

    public void deleteProfile(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettings.this);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        final EditText passwordConfirm = new EditText(this);
        passwordConfirm.setHint("Enter password");
        passwordConfirm.setPadding(40, 40, 40, 40);
        passwordConfirm.setGravity(Gravity.CENTER);
        passwordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(passwordConfirm);
        builder.setView(layout);

        builder.setMessage("Are you sure you want to delete?");
        builder.setTitle("Delete Profile");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String passwordString = passwordConfirm.getText().toString();
                                deleteAccount(passwordString);
                                finish();
                            }
                        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
     * @param rePassword
     */
    private void deleteAccount(String rePassword){
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);

        final String password = rePassword;

        Log.e(TAG, "INPUTTED PASSWORD IS: " + password);

        mDatabase.collection("users").document(mAuth.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                AuthCredential credential = EmailAuthProvider
                        .getCredential(mUserProfile.getEmail(), password);
                // Prompt the user to re-provide their sign-in credentials
                    mAuth.getCurrentUser().reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mUserProfile = null;
                                    mAuth.getCurrentUser().delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "User account deleted.");
                                                        Toast.makeText(getApplicationContext(),"Account deleted.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
            }
        });
    }

    public void changePassword(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettings.this);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);

        final EditText newPassword = new EditText(this);
        newPassword.setHint("Enter new password");
        newPassword.setPadding(40, 40, 40, 40);
        newPassword.setGravity(Gravity.CENTER);
        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final EditText newPassword2 = new EditText(this);
        newPassword2.setHint("Re-enter new password");
        newPassword2.setPadding(40, 40, 40, 40);
        newPassword2.setGravity(Gravity.CENTER);
        newPassword2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final EditText passwordConfirm = new EditText(this);
        passwordConfirm.setHint("Enter old password");
        passwordConfirm.setPadding(40, 40, 40, 40);
        passwordConfirm.setGravity(Gravity.CENTER);
        passwordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(passwordConfirm);
        layout.addView(newPassword);
        layout.addView(newPassword2);
        builder.setView(layout);

//        builder.setMessage("Are you sure you want to delete?");
        builder.setTitle("Change Password");
        builder.setCancelable(true);
        builder.setPositiveButton("Change password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String passwordString = passwordConfirm.getText().toString();
                final String newPasswordString = newPassword.getText().toString();
                final String newPassword2String = newPassword2.getText().toString();
                if(newPasswordString.equals(newPassword2String)){
                    resetPassword(passwordString, newPasswordString);
                }else{
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

    /**
     * resetPassword
     * taked both the old password and the new password and resets the users password to the new one
     * @param rePassword
     * @param newPassword
     */
    private void resetPassword(final String rePassword, final String newPassword){
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);
        AuthCredential credential = EmailAuthProvider
                .getCredential(mUserProfile.getEmail(), rePassword);

        // Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mAuth.getCurrentUser().updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                return;
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Failed to re-authenticate.");
                Toast.makeText(getApplicationContext(),"Failed to re-authenticate. Please try again and make sure you are connected to the internet.",Toast.LENGTH_LONG).show();
            }
        });



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
        if(saveCountdownFinished || saveCountdownSecondsLeft == 0) {

            //  Set all info not set in other proprietary methods.
            mUserProfile.setAbout(mAboutMe.getText().toString());
            mUserProfile.setDisplayName(mUsername.getText().toString());
            mUserProfile.setNumRecipes(Long.parseLong(mNumRecipes.getText().toString()));

            //  Update the server relative to the client or throw an exception if a valid user isn't found.
            try {
                updateFirebase();
            } catch (InvalidUserException e) {
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
        }
    }

    /** Activated on Filters checked
     * @param v - Filters checkbox view.
     */
    public void onCheckboxClicked(View v) {
        // Is the Checkbox Checked?
        boolean checked = ((CheckBox) v).isChecked();

        Preferences preferences = mUserProfile.getPreferences();

        //  Check the type of filter. For example, dietary, religious.
        switch(currentFilterType){
            case ALLERGENS:
                // Check which checkbox was clicked.
                switch(v.getId()) {
                    case R.id.settings_allergy_nuts:
                        if (checked)
                            preferences.setAllergy_nuts(true);
                        else
                            preferences.setAllergy_nuts(false);
                        break;
                    case R.id.settings_allergy_eggs:
                        if (checked)
                            preferences.setAllergy_eggs(true);
                        else
                            preferences.setAllergy_eggs(false);
                        break;
                    case R.id.settings_allergy_milk:
                        if (checked)
                            preferences.setAllergy_milk(true);
                        else
                            preferences.setAllergy_milk(false);
                        break;
                    case R.id.settings_allergy_shellfish:
                        if (checked)
                            preferences.setAllergy_shellfish(true);
                        else
                            preferences.setAllergy_shellfish(false);
                        break;
                    case R.id.settings_allergy_soy:
                        if (checked)
                            preferences.setAllergy_soya(true);
                        else
                            preferences.setAllergy_soya(false);
                        break;
                    case R.id.settings_allergy_wheat:
                        if (checked)
                            preferences.setAllergy_gluten(true);
                        else
                            preferences.setAllergy_gluten(false);
                        break;
                }
            case RELIGIOUS:
                //TODO
            case DIETARY:
                //TODO
            case HEALTH:
                //TODO
        }
    }

    // TODO Make proper image download class.
    /*private void downloadImage(String UID){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getInstance().getReference()
                .child("user_profile_images")
                .child("megaMan.png");
        imageRef.getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        mProfileImage.setImageBitmap(bitmap);
                    }
                });
    }*/

    private void initPageItems(){
        //  Basic user info.
        mUsername = findViewById(R.id.settings_input_username);
        mAboutMe = findViewById(R.id.settings_input_about_me);
        mNumRecipes = findViewById(R.id.public_profile_recipes);

        //  Allergens
        mAllergy_eggs = findViewById(R.id.settings_allergy_eggs);
        mAllergy_gluten = findViewById(R.id.settings_allergy_wheat);
        mAllergy_milk = findViewById(R.id.settings_allergy_milk);
        mAllergy_nuts = findViewById(R.id.settings_allergy_nuts);
        mAllergy_shellfish = findViewById(R.id.settings_allergy_shellfish);
        mAllergy_soy = findViewById(R.id.settings_allergy_soy);

        //  Privacy
        mDisplay_about_me = findViewById(R.id.settings_privacy_about_me);
        mDisplay_profile_image = findViewById(R.id.settings_privacy_profile_image);
        mDisplay_recipes = findViewById(R.id.settings_privacy_recipes);
        mDisplay_username = findViewById(R.id.settings_privacy_username);

    }

    private void loadProfileData(){
        mUsername.setText(mUserProfile.getDisplayName());
        mAboutMe.setText(mUserProfile.getAbout());
        mNumRecipes.setText(String.valueOf(mUserProfile.getNumRecipes()));

        //  Load allergen checkBoxes.
        setFilters(currentFilterType);

        //  Load privacy switches.
        setPrivacyOptions(mUserProfile.getPrivacy());
    }

    /** Set which filter checkboxes should be selected
     * @param type - Enumeration of the type of filter to be displayed. E.g. Allegern, Religious...
     */
    private void setFilters(filterType type){
        switch(type){
            case ALLERGENS:
                mAllergy_eggs.setChecked(mUserProfile.getPreferences().isAllergy_eggs());
                mAllergy_milk.setChecked(mUserProfile.getPreferences().isAllergy_milk());
                mAllergy_nuts.setChecked(mUserProfile.getPreferences().isAllergy_nuts());
                mAllergy_shellfish.setChecked(mUserProfile.getPreferences().isAllergy_shellfish());
                mAllergy_soy.setChecked(mUserProfile.getPreferences().isAllergy_soya());
                mAllergy_gluten.setChecked(mUserProfile.getPreferences().isAllergy_gluten());
            case RELIGIOUS:
                //TODO
            case DIETARY:
                //TODO
            case HEALTH:
                //TODO
        }
    }

    /** Set which privacy swtiches should be selected
     * @param privacy - HashMap of valid privacy options.
     */
    private void setPrivacyOptions(HashMap<String, Object> privacy){
        mDisplay_username.setChecked( (boolean) privacy.get("display_username"));
        mDisplay_about_me.setChecked( (boolean) privacy.get("display_about_me"));
        mDisplay_profile_image.setChecked( (boolean) privacy.get("display_profile_image"));
        mDisplay_recipes.setChecked( (boolean) privacy.get("display_recipes"));
    }

    /** Update the server relative to the client on a valid 'Save Settings' button press
     * @throws InvalidUserException - Error returned when a valid user cannot be found and therefore the server
     * cannot update relative to the client. This can occur when Wifi signal is lost.
     */
    private void updateFirebase() throws InvalidUserException {
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);
        FirebaseUser user = mAuth.getCurrentUser();

        String usernameInput = mUsername.getText().toString();
        String aboutMeInput = mAboutMe.getText().toString();

        if(user != null){ //Checks for a valid user.
            String usersEmail = requireNonNull(mAuth.getCurrentUser()).getEmail();

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

            Preferences preferences = mUserProfile.getPreferences();

            prefMap.put("allergy_eggs", preferences.isAllergy_eggs());
            prefMap.put("allergy_gluten", preferences.isAllergy_gluten());
            prefMap.put("allergy_milk", preferences.isAllergy_milk());
            prefMap.put("allergy_nuts", preferences.isAllergy_nuts());
            prefMap.put("allergy_shellfish", preferences.isAllergy_shellfish());
            prefMap.put("allergy_soya", preferences.isAllergy_soya());

            //  TODO Finish all other preferences. Currently just set to false by default.
            prefMap.put("allergy_celery", false);
            prefMap.put("allergy_crustacean", false);
            prefMap.put("allergy_fish", false);
            prefMap.put("allergy_mustard", false);
            prefMap.put("allergy_peanuts", false);
            prefMap.put("allergy_sesame", false);
            prefMap.put("allergy_sulphide", false);
            prefMap.put("diabetic", false);
            prefMap.put("halal", false);
            prefMap.put("high_protein", false);
            prefMap.put("kosher", false);
            prefMap.put("lactose_free", false);
            prefMap.put("lactovegetarian", false);
            prefMap.put("low_carb", false);
            prefMap.put("low_sodium", false);
            prefMap.put("no_alcohol", false);
            prefMap.put("no_pork", false);
            prefMap.put("ovovegetarian", false);
            prefMap.put("pescatarian", false);
            prefMap.put("vegan", false);
            prefMap.put("vegetarian", false);

            map.put("preferences", prefMap);

            HashMap<String, Object> privacyMap = mUserProfile.getPrivacy();

            privacy.put("display_username", privacyMap.get("display_username"));
            privacy.put("display_about_me", privacyMap.get("display_about_me"));
            privacy.put("display_recipes", privacyMap.get("display_recipes"));
            privacy.put("display_profile_image", privacyMap.get("display_profile_image"));

            map.put("privacy", privacy);

            mUserProfile = new UserInfoPrivate(map, prefMap, privacy); //Create a new UserInfoPrivate class based upon our inputs.

            DocumentReference usersRef = mRef.document(user.getUid());

            //  Sync the Firebase info with the client info if successful.
            usersRef.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
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

}
