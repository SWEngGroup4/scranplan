package com.group4sweng.scranplan;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import static com.google.firebase.firestore.FieldValue.delete;

public class MainActivity extends AppCompatActivity {

    final String TAG = "ScranPlanHome";

    UserInfo userDetails;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();

    FirebaseAuth mAuth;
    FirebaseApp mApp;

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    FirebaseAuth.AuthStateListener mAuthListener;
    String mDisplayName;

    Button mLogoutButton;
    TabLayout tabLayout;
    FrameLayout frameLayout;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Drawer setup and and synchronising the states
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        initFirebase();
        initPageItems();
        initPageListeners();



        Fragment fragment = new RecipeFragment();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();




//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // add search function here
                SearchListFragment searchListFragment = new SearchListFragment();
                searchListFragment.setValue(query);
                Log.e(TAG, "I'm pressing it boss");
                searchListFragment.show(fragmentManager, "test");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        if(null!=searchManager ) {
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        }
//        searchView.setIconifiedByDefault(false);

        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuSortButton) {

            // do something here
            AlertDialog.Builder builder;
            AlertDialog alertDialog;

            LayoutInflater inflater = (LayoutInflater)
                    getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.filter_tab_dialog,
                    (ViewGroup) findViewById(R.id.tabhost));



            TabHost tabs = (TabHost) layout.findViewById(R.id.tabhost);
            tabs.setup();
            TabHost.TabSpec tabpage1 = tabs.newTabSpec("type");
            tabpage1.setContent(R.id.ScrollView01);
            tabpage1.setIndicator("Type");
            TabHost.TabSpec tabpage2 = tabs.newTabSpec("type");
            tabpage2.setContent(R.id.ScrollView02);
            tabpage2.setIndicator("Diet");
            TabHost.TabSpec tabpage3 = tabs.newTabSpec("sort");
            tabpage3.setContent(R.id.ScrollView03);
            tabpage3.setIndicator("Sort");
            tabs.addTab(tabpage1);
            tabs.addTab(tabpage2);
            tabs.addTab(tabpage3);
            builder = new AlertDialog.Builder(MainActivity.this);

            builder
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    query = new Query();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            builder.setTitle("Filter options");
            builder.setView(layout);
            alertDialog = builder.create();
            alertDialog.show();

            TextView tv;
            tv = (TextView)tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
            tv.setTextColor(Color.GRAY);
            tv = (TextView)tabs.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
            tv.setTextColor(Color.GRAY);
            tv = (TextView)tabs.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
            tv.setTextColor(Color.GRAY);

        }
        return super.onOptionsItemSelected(item);
    }



    private void initFirebase(){
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null){
                    if(mAuth.getCurrentUser().isEmailVerified()){
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
                        mAuth.getCurrentUser().sendEmailVerification();
                        Log.e(TAG, "SignIn : Email authentication sent for user trying to log in with unverified email, user logged out.");
                        Toast.makeText(getApplicationContext(),"Email is not yet verified, a new verification email has been sent, please verify email and try again.",Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                }else{
                    Log.e(TAG,"AUTHENTICATION STATE UPDATE : No Valid current user logged in");
                    mDisplayName = "No Valid User";
                    mAuth.removeAuthStateListener(mAuthListener);
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
        tabLayout = findViewById(R.id.tabLayout);
        frameLayout = findViewById(R.id.frameLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Recipes"));
        tabLayout.addTab(tabLayout.newTab().setText("Meal Planner"));
        tabLayout.addTab(tabLayout.newTab().setText("Timeline"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
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
      
      /*TODO Clean up temporary profile settings page listener*/
        final Button tempProfileSettings = findViewById(R.id.profile_settings_button);
        tempProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                tempOpenProfileSettings();
            }
        });
    }

    public void tempOpenProfileSettings() {
        Intent intent = new Intent(this, ProfileSettings.class);
        startActivity(intent);
    }

    /**
     * deleteAccount
     * called when user wants to delete their account, needs users password to be re-entered
     * FireBase Auth is deleted along with all local data and their personal account in the database
     * @param rePassword
     */
    private void deleteAccount(String rePassword){
        final String password = rePassword;
        userDetails = null;
        database.collection("users").document(mAuth.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(mAuth.getCurrentUser().getEmail(), password);

                // Prompt the user to re-provide their sign-in credentials
                mAuth.getCurrentUser().reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
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

    /**
     * resetPassword
     * taked both the old password and the new password and resets the users password to the new one
     * @param rePassword
     * @param newPassword
     */
    private void resetPassword(final String rePassword, final String newPassword){
        final String password = rePassword;
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

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
                            }
                        });
                    }
                });
    }


}
