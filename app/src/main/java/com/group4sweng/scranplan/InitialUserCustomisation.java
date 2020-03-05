package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class InitialUserCustomisation extends AppCompatActivity {
    final String TAG = "userCustomisation";

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    UserInfoPrivate userDetails;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();

    Preferences preferences = userDetails.getPreferences();

    Button mSkipButton;
    Button mSubmitButton;
    CheckBox mVegetarianBox;
    CheckBox mVeganBox;
    CheckBox mNutsBox;
    CheckBox mMilkBox;
    CheckBox mEggsBox;
    CheckBox mWheatBox;
    CheckBox mShellfishBox;
    CheckBox mSoyBox;
    CheckBox mPescatarianBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_user_customisation);

        getSupportActionBar().hide();
        Log.e(TAG, "Starting initial user customisation activity");

        initPageItems();
        getResources().getColor(R.color.colorPrimary);
        initPageListeners();
        initFirebase();
    }

  /*  public void popUpCreate(View view){

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_initial_user_customisation, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }*/

    private void initFirebase() {
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initPageItems() {
        //Defining all relevant members of signin & register page
        mSkipButton = findViewById(R.id.skipButton);
        mSubmitButton = findViewById(R.id.submitButton);
        mVegetarianBox = findViewById(R.id.VegCheckBox);
        mVeganBox = findViewById(R.id.veganCheckBox);
        mNutsBox = findViewById(R.id.nutCheckBox);
        mEggsBox = findViewById(R.id.eggCheckBox);
        mMilkBox = findViewById(R.id.milkCheckBox);
        mWheatBox = findViewById(R.id.wheatCheckBox);
        mShellfishBox = findViewById(R.id.shellfishCheckBox);
        mSoyBox = findViewById(R.id.soyCheckBox);
        mPescatarianBox = findViewById(R.id.pescatarianCheckBox);


    }

    private void initPageListeners() {
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Initial user returning to main activity");

                Intent returningIntent = new Intent();
                setResult(RESULT_OK, returningIntent);

                finish();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePref();
                Log.e(TAG, "Initial user returning to main activity");

                Intent returningIntent = new Intent();
                setResult(RESULT_OK, returningIntent);

                finish();
            }

            //make function to add info to firestore
            private void savePref() {
                DocumentReference usersRef = database.document(mAuth.getCurrentUser().getUid());

                if (mEggsBox.isChecked()) {
                    userDetails.getPreferences().setAllergy_eggs(true);
                    mMilkBox.setChecked(false);
                    mNutsBox.setChecked(false);
                    mShellfishBox.setChecked(false);
                    mSoyBox.setChecked(false);
                    mWheatBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }

                if (mMilkBox.isChecked()) {
                    userDetails.getPreferences().setAllergy_milk(true);
                    mEggsBox.setChecked(false);
                    mNutsBox.setChecked(false);
                    mShellfishBox.setChecked(false);
                    mSoyBox.setChecked(false);
                    mWheatBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }

                if (mNutsBox.isChecked()) {
                    userDetails.getPreferences().setAllergy_nuts(true);
                    mMilkBox.setChecked(false);
                    mEggsBox.setChecked(false);
                    mShellfishBox.setChecked(false);
                    mSoyBox.setChecked(false);
                    mWheatBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }

                if (mShellfishBox.isChecked()) {
                    userDetails.getPreferences().setAllergy_shellfish(true);
                    mMilkBox.setChecked(false);
                    mNutsBox.setChecked(false);
                    mEggsBox.setChecked(false);
                    mSoyBox.setChecked(false);
                    mWheatBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }
                if (mSoyBox.isChecked()) {
                    userDetails.getPreferences().setAllergy_soya(true);
                    mMilkBox.setChecked(false);
                    mNutsBox.setChecked(false);
                    mShellfishBox.setChecked(false);
                    mEggsBox.setChecked(false);
                    mWheatBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }
                if (mWheatBox.isChecked()) {
                    userDetails.getPreferences().setAllergy_gluten(true);
                    mMilkBox.setChecked(false);
                    mNutsBox.setChecked(false);
                    mShellfishBox.setChecked(false);
                    mSoyBox.setChecked(false);
                    mEggsBox.setChecked(false);
                    mMilkBox.setChecked(false);
                }

                if (mPescatarianBox.isChecked()) {
                    userDetails.getPreferences().setPescatarian(true);
                    mVeganBox.setChecked(false);
                    mVegetarianBox.setChecked(false);
                }
                if (mVeganBox.isChecked()) {
                    userDetails.getPreferences().setVegan(true);
                    mPescatarianBox.setChecked(false);
                    mVegetarianBox.setChecked(false);
                }
                if (mVegetarianBox.isChecked()) {
                    userDetails.getPreferences().setVegetarian(true);
                    mVeganBox.setChecked(false);
                    mPescatarianBox.setChecked(false);
                }



                usersRef.update("preferences", preferences);
                userDetails.setPreferences(preferences);

            }

            private void finishActivity() {

                Log.e(TAG, "Initial User Customisation returning to main activity");

                // User data returned to main menu
                Intent returningIntent = new Intent(InitialUserCustomisation.this, MainActivity.class);
                returningIntent.putExtra("user", userDetails);
                setResult(RESULT_OK, returningIntent);

                finish();
            }


        });
    }
}