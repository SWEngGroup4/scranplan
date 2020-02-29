package com.group4sweng.scranplan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    TabLayout tabLayout;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Drawer setup and and synchronising the states
        SideMenu mSideMenu = new SideMenu();
        mSideMenu.mMenuToolbar = findViewById(R.id.toolbar);
        mSideMenu.mMenuDrawer = findViewById(R.id.drawer_layout);
        mSideMenu.mNavigationView = findViewById(R.id.side_menu);
        setSupportActionBar(mSideMenu.mMenuToolbar);
        mSideMenu.init(this, this);


        initFirebase();
        initPageItems();
        initPageListeners();


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
