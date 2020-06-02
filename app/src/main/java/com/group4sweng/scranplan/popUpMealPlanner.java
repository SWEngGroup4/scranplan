package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.Home;
import com.group4sweng.scranplan.InitialUserCustomisation;
import com.group4sweng.scranplan.Login;
import com.group4sweng.scranplan.MainActivity;
import com.group4sweng.scranplan.MealPlanner.PlannerFragment;
import com.group4sweng.scranplan.R;

import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;



public class popUpMealPlanner extends AppCompatActivity {

    final String TAG = "FirstpopUp";

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    UserInfoPrivate userDetails;

    Button mgoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        //sets the page to the initial filer page
        setContentView(R.layout.popup_mealplanner);

        userDetails = (UserInfoPrivate) getIntent().getSerializableExtra("user");
        if (userDetails != null) {
            initPageItems();

            getResources().getColor(R.color.colorBackground);

            initPageListeners();

            initFirebase();

        }
    }


    private void initPageItems() {
        mgoButton =  findViewById(R.id.goButton);
    }


    private void initPageListeners() {

        mgoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore database = FirebaseFirestore.getInstance();
                CollectionReference colRef = database.collection("users");
                DocumentReference usersRef = colRef.document(mAuth.getCurrentUser().getUid()); //gets the current user ID
                usersRef.update("firstMealPlannerLaunch", false); //first time login set to false so page wont show again

                Log.e(TAG,"Sending user to initial preference setup page");
                Intent BackToMain = new Intent(getApplicationContext(), Home.class);
                BackToMain.putExtra("user", userDetails);
                BackToMain.putExtra("fromPopUpMealPlanner" ,true);
                userDetails.setFirstMealPlannerLaunch(false);
                startActivity(BackToMain);
            }

        });

    }

    // Disable user from pressing back button on main activity page
    @Override
    public void onBackPressed() {
        //Do nothing
    }

    private void initFirebase() {
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }}