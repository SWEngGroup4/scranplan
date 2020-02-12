package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    final String TAG = "FirebaseTest";

    UserInfo userDetails;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();

    FirebaseAuth mAuth;
    FirebaseApp mApp;

    FirebaseAuth.AuthStateListener mAuthListener;
    String mDisplayName;

    Button mLogoutButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFirebase();
        initPageItems();
        initPageListeners();




//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void initFirebase(){
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null){
                    Log.e(TAG, "AUTHENTICATION STATE UPDATE : Valid user logged in : email [" + user.getEmail() + "]");

                    DocumentReference usersRef = database.collection("users").document(mAuth.getCurrentUser().getUid());
                    usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() == null) Log.d(TAG, "getResult is null");
                                Log.d(TAG, "getResult: " + task.getResult());
                                DocumentSnapshot document = task.getResult();
                                HashMap<String, Object> map = new HashMap<>();

                                map.put("UID", document.get("UID"));
                                map.put("email", document.get("email"));
                                map.put("displayName", document.get("displayName"));
                                map.put("imageURL", document.get("imageURL"));
                                map.put("chefRating", document.get("chefRating"));
                                map.put("numRecipes", document.get("numRecipes"));
                                map.put("preferences", document.get("preferences"));
                                userDetails = new UserInfo(map, (HashMap<String, Object>) document.get("preferences"));
                            }else {
                                Log.e(TAG, "User details retrieval : Unable to retrieve user document in Firestore ");
                                Toast.makeText(getApplicationContext(),"Unable to retrieve current user details, please sign in again.",Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }
                        }
                    });
                }else{
                    Log.e(TAG,"AUTHENTICATION STATE UPDATE : No Valid current user logged in");
                    mDisplayName = "No Valid User";

                    Intent signIn = new Intent(getApplicationContext(), Login.class);
                    startActivity(signIn);
                    userDetails = (UserInfo)signIn.getSerializableExtra("user");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);


    }

    private void initPageItems(){
        //Defining all relevant members of signin & register page
        mLogoutButton = (Button) findViewById(R.id.logoutButton);
    }

    private void initPageListeners() {
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Logout button has been pressed and user has been logged out.");
                userDetails = null;
                mAuth.signOut();
            }
        });

    }


}
