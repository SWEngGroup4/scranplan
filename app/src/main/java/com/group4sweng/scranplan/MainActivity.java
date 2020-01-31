package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.widget.FrameLayout;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;

public class MainActivity extends AppCompatActivity {

    final String TAG = "FirebaseTest";

    FirebaseAuth mAuth;
    FirebaseApp mApp;

    FirebaseAuth.AuthStateListener mAuthListener;
    String mDisplayName;

    TabLayout tabLayout;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFirebase();

        tabLayout = findViewById(R.id.tabLayout);
        frameLayout = findViewById(R.id.frameLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Recipes"));
        tabLayout.addTab(tabLayout.newTab().setText("Meal Planner"));
        tabLayout.addTab(tabLayout.newTab().setText("Timeline"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new RecipeFragment();
                        break;
                    case 1:
                        fragment = new PlannerFragment();
                        break;
                    case 2:
                        fragment = new TimelinePlanner();
                        break;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
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
                    Log.e(TAG, "AUTHENTICATION STATE UPDATE : Valid user logged in : email [" + user.getEmail() + "] and display name [" + user.getDisplayName() + "]");
                    mDisplayName = user.getDisplayName();
                }else{
                    Log.e(TAG,"AUTHENTICATION STATE UPDATE : No Valid current user logged in");
                    mDisplayName = "No Valid User";

                    Intent signIn = new Intent(getApplicationContext(), Login.class);
                    startActivity(signIn);
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);


    }


}
