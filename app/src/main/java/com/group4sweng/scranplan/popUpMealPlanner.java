package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;



public class popUpMealPlanner extends AppCompatActivity {
    //initial page that introduces the meal planner and shows the user how to use it

    final String TAG = "FirstpopUp";

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    UserInfoPrivate userDetails;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    CollectionReference colRef = database.collection("users");

    Button mgoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        //sets the page to the information screen
        setContentView(R.layout.popup_mealplanner);

        userDetails = (UserInfoPrivate) getIntent().getSerializableExtra("user");
        if (userDetails != null) {
            initPageItems();

            getResources().getColor(R.color.colorBackground);

            initPageListeners();

        }
    }


    private void initPageItems() {
        mgoButton =  findViewById(R.id.goButton);
    }


    private void initPageListeners() {

        mgoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference usersRef = colRef.document(userDetails.getUID()); //gets the current user ID
                usersRef.update("firstMealPlannerLaunch", false); //first time seeing the meal planner set to false so page wont show again

                Log.e(TAG,"Sending user to home page");
                Intent BackToMain = new Intent(getApplicationContext(), Home.class);//sends user back to home oage
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

}