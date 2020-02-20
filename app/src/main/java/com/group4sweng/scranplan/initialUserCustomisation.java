package com.group4sweng.scranplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class initialUserCustomisation extends AppCompatActivity {
    final String TAG = "userCustomisation";

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    UserInfo userDetails;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();

    Preferences preferences = userDetails.getPreferences();



    Button mSkipButton;
    CheckBox mVegetarianBox;
    CheckBox mVeganBox;
    CheckBox mNutsBox;
    CheckBox mMilkBox;
    CheckBox mEggsBox;
    CheckBox mWheatBox;
    CheckBox mShellfishBox;
    CheckBox mSoyBox;

    Boolean mVegBox = false;
    Boolean mVveganBoxBool = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        Log.e(TAG, "Starting Login activity");

        initPageItems();

        initPageListeners();
    }

    private void initFirebase(){
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();



    }

    private void initPageItems(){
        //Defining all relevant members of signin & register page
        mSkipButton = (Button) findViewById(R.id.skipButton);
        mVegetarianBox = (CheckBox) findViewById(R.id.VegCheckBox);
        mVeganBox = (CheckBox) findViewById(R.id.veganCheckBox);
        mNutsBox = (CheckBox) findViewById(R.id.nutCheckBox);
        mEggsBox = (CheckBox) findViewById(R.id.eggCheckBox);
        mMilkBox = (CheckBox) findViewById(R.id.milkCheckBox);
        mWheatBox = (CheckBox) findViewById(R.id.wheatCheckBox);
        mShellfishBox = (CheckBox) findViewById(R.id.shellfishCheckBox);
        mSoyBox = (CheckBox) findViewById(R.id.soyCheckBox);

    }

    private void initPageListeners(){
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"Initial user returning to main activity");

                Intent returningIntent = new Intent();
                setResult(RESULT_OK, returningIntent);

                finish();
            }
        });

    }

    //make function to add info to firestore
    private void savePref(){
        DocumentReference usersRef =  database.document(mAuth.getCurrentUser().getUid());
        HashMap<String, Object> preferences = new HashMap<>();

        // Default user food preferences
        preferences.put("allergy_celery", false);
        preferences.put("allergy_crustacean", false);
        preferences.put("allergy_eggs", false);
        preferences.put("allergy_fish", false);
        preferences.put("allergy_gluten", false);
        preferences.put("allergy_milk", false);
        preferences.put("allergy_mustard", false);
        preferences.put("allergy_nuts", false);
        preferences.put("allergy_peanuts", false);
        preferences.put("allergy_sesame", false);
        preferences.put("allergy_shellfish", false);
        preferences.put("allergy_soya", false);
        preferences.put("allergy_sulphide", false);
        preferences.put("diabetic", false);
        preferences.put("halal", false);
        preferences.put("high_protein", false);
        preferences.put("kosher", false);
        preferences.put("lactose_free", false);
        preferences.put("lactovegetarian", false);
        preferences.put("low_carb", false);
        preferences.put("low_sodium", false);
        preferences.put("no_alcohol", false);
        preferences.put("no_pork", false);
        preferences.put("ovovegetarian", false);
        preferences.put("pescatarian", false);
        preferences.put("vegan", false);
        preferences.put("vegetarian", false);


        usersRef.update("preferences", preferences);
        userDetails.updatePreferences(preferences);
    }


    //TODO need to add opening intent to this page after user is logged in or has returned from the user registration page

    private void finishActivity() {

        Log.e(TAG,"Initial User Customisation returning to main activity");

        // User data returned to main menu
        Intent returningIntent = new Intent();
        returningIntent.putExtra("user", userDetails);
        setResult(RESULT_OK, returningIntent);

        finish();
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        if (mVegetarianBox.isChecked()){
            result.put("vegetarian", true);
        }
        if (mVeganBox.isChecked()){
            result.put("vegetarian", true);
        }
    }

}
