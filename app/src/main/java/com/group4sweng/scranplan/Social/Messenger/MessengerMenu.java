package com.group4sweng.scranplan.Social.Messenger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.SearchFunctions.SearchListFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.SearchFunctions.SearchQuery;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.sentry.core.Sentry;

/***
 * Class to View active messages between people
 */
public class MessengerMenu extends AppCompatActivity {
    final String TAG = "Messenger MENU";


    UserInfoPrivate mUser;

    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColFollowers = mDatabase.collection("followers");
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;


    // Firebase user collection and storage references.
    CollectionReference mMessages;
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    StorageReference mStorageReference = mStorage.getReference();
    List<MessageMenuRecyclerAdapter.MessengerFeedPreviewData> data;
    Query query;


    //Menu items
    private SearchView searchView;
    private MenuItem sortView;
    private SearchQuery searchQuery;
    SearchPrefs prefs;

    // Filter menu variables
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    //Menu check boxes
    CheckBox mPescatarianBox;
    CheckBox mVegetarianBox;
    CheckBox mVeganBox;
    CheckBox mNutsBox;
    CheckBox mMilkBox;
    CheckBox mEggsBox;
    CheckBox mWheatBox;
    CheckBox mShellfishBox;
    CheckBox mSoyBox;
    CheckBox mScoreBox;
    CheckBox mVoteBox;
    CheckBox mTimeBox;
    CheckBox mIngredientsBox;
    CheckBox mNameBox;
    CheckBox mChefBox;

    FragmentManager fragmentManager;



    FloatingActionButton mNewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Grabs serializable UserInfoPrivate data from main activity.
        mUser = (UserInfoPrivate) getIntent().getSerializableExtra("user");
        mMessages = mDatabase.collection("users").document(mUser.getUID()).collection("userInteractions");

        setContentView(R.layout.messenger);
        View view = findViewById(R.id.messageFrameLayout);
        fragmentManager = getSupportFragmentManager();
        initSearchMenu();
        addChats(view);

        mNewMessage = findViewById(R.id.newMessageButton);
        mNewMessage.setVisibility(View.VISIBLE);
        mNewMessage.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Messenger NEW CHAT Has been entered");
                Intent intentMessenger = new Intent(getApplicationContext(), MessengerNewChat.class);
                intentMessenger.putExtra("user", mUser);
                startActivity(intentMessenger);
            }})
        );
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    /**
     *  Setting up the search menu within the action bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Building the search bar within the action button
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView = (SearchView)item.getActionView();
        sortView = menu.findItem(R.id.menuSortButton);

        // Adding the listener to search for string provided by user
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Search function
                searchQuery = new SearchQuery( s, prefs);
                SearchListFragment searchListFragment = new SearchListFragment(mUser);
                searchListFragment.setValue(searchQuery.getQuery());
                searchListFragment.setIndex(searchQuery.getIndex());
                Log.e(TAG, "User opening search");
                searchListFragment.show(fragmentManager, "search");
                return false;
            }

            // Change in text function currently not used as the recipe fragment is extended to
            // cover the screen, this minimised firebase reads through any changes.
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        item.setVisible(false);
        sortView.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *  Setting open menu button on action bar to open filter menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Opening the filter menu
        int id = item.getItemId();
        if (id == R.id.menuSortButton) {
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  Initial set up of the search menu that enables the user to select search filters and
     *  sorting.
     */
    public void initSearchMenu(){
        // Build the inflater alert dialog
        LayoutInflater inflater = (LayoutInflater)
                getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        // fill the alter dialogue with tabs to enable the user to switch between the filters,
        // sorting and what to search for
        View layout = inflater.inflate(R.layout.filter_tab_dialog,
                (ViewGroup) findViewById(R.id.tabhost));
        // Setting up the tabs and giving them names
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


        // Adding the XML for each tab
        tabs.addTab(tabpage1);
        tabs.addTab(tabpage2);
        tabs.addTab(tabpage3);

        // Connecting variables up to each component within the tabs
        mPescatarianBox = layout.findViewById(R.id.menuPescatarianCheckBox);
        mVegetarianBox = layout.findViewById(R.id.menuVegCheckBox);
        mVeganBox = layout.findViewById(R.id.menuVeganCheckBox);
        mNutsBox = layout.findViewById(R.id.menuNutCheckBox);
        mEggsBox = layout.findViewById(R.id.menuEggCheckBox);
        mMilkBox = layout.findViewById(R.id.menuMilkCheckBox);
        mWheatBox = layout.findViewById(R.id.menuWheatCheckBox);
        mShellfishBox = layout.findViewById(R.id.menuShellfishCheckBox);
        mSoyBox = layout.findViewById(R.id.menuSoyCheckBox);
        mScoreBox = layout.findViewById(R.id.scoreCheckBox);
        mVoteBox = layout.findViewById(R.id.voteCheckBox);
        mTimeBox = layout.findViewById(R.id.timestampCheckBox);
        mIngredientsBox = layout.findViewById(R.id.ingredientCheckBox);
        mNameBox = layout.findViewById(R.id.nameCheckBox);
        mChefBox = layout.findViewById(R.id.chefCheckBox);

        // Initialise the check boxes by filling them with users current preferences
        initMenuCheckBoxes(tabs);

        // add the alert dialogue to the current context
        builder = new AlertDialog.Builder(MessengerMenu.this);
        // Set listeners for the button presses for the dialogue
        builder
                .setCancelable(false)
                // if positive picked then create a new preferences variable to reflect what the user has selected
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                prefs = new SearchPrefs(mPescatarianBox.isChecked(), mVegetarianBox.isChecked(), mVeganBox.isChecked(), mNutsBox.isChecked(),
                                        mMilkBox.isChecked(), mEggsBox.isChecked(), mWheatBox.isChecked(), mShellfishBox.isChecked(), mSoyBox.isChecked(),
                                        mScoreBox.isChecked(), mVoteBox.isChecked(), mTimeBox.isChecked(),mIngredientsBox.isChecked(), mNameBox.isChecked(),
                                        mChefBox.isChecked());

                            }
                        })
                .setNegativeButton("Cancel",
                        // If negative button clicked then cancel the action of changing user prefs
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.setTitle("Search options");
        builder.setView(layout);
        alertDialog = builder.create();

        // Change the colour of the tabs to grey
        TextView tv;
        tv = (TextView)tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tv.setTextColor(Color.GRAY);
        tv = (TextView)tabs.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        tv.setTextColor(Color.GRAY);
        tv = (TextView)tabs.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
        tv.setTextColor(Color.GRAY);

    }

    /**
     *  Initialise all check boxes to user preferences and ensure that queries are only query that is allowed
     */
    public void initMenuCheckBoxes(TabHost tabs){
        // Ensure that only the correct boxes are ticked at any one time
        mPescatarianBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mPescatarianBox.isChecked()) {
                    mVegetarianBox.setChecked(false);
                    mVeganBox.setChecked(false);
                }
            }
        });
        mVeganBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mVeganBox.isChecked()) {
                    mVegetarianBox.setChecked(false);
                    mPescatarianBox.setChecked(false);
                }
            }
        });
        mVegetarianBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mVegetarianBox.isChecked()) {
                    mVeganBox.setChecked(false);
                    mPescatarianBox.setChecked(false);
                }
            }
        });

        mScoreBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mScoreBox.isChecked()) {
                    mVoteBox.setChecked(false);
                    mTimeBox.setChecked(false);
                }else if(!mVoteBox.isChecked() && !mTimeBox.isChecked()){
                    mScoreBox.setChecked(true);
                }
            }
        });

        mVoteBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mVoteBox.isChecked()) {
                    mScoreBox.setChecked(false);
                    mTimeBox.setChecked(false);
                }else if(!mScoreBox.isChecked() && !mTimeBox.isChecked()){
                    mVoteBox.setChecked(true);
                }
            }
        });

        mTimeBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mTimeBox.isChecked()) {
                    mVoteBox.setChecked(false);
                    mScoreBox.setChecked(false);
                }else if(!mVoteBox.isChecked() && !mScoreBox.isChecked()){
                    mTimeBox.setChecked(true);
                }
            }
        });

        mChefBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mChefBox.isChecked()) {
                    mIngredientsBox.setChecked(false);
                    mNameBox.setChecked(false);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.GONE);
                    tabs.setCurrentTab(1);
                    tabs.getCurrentTabView().setVisibility(View.GONE);
                    tabs.setCurrentTab(0);
                }else if(!mNameBox.isChecked() && !mIngredientsBox.isChecked()){
                    mChefBox.setChecked(true);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.GONE);
                    tabs.setCurrentTab(0);
                }
            }
        });

        mIngredientsBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mIngredientsBox.isChecked()) {
                    mChefBox.setChecked(false);
                    mNameBox.setChecked(false);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.VISIBLE);
                    tabs.setCurrentTab(1);
                    tabs.getCurrentTabView().setVisibility(View.VISIBLE);
                    tabs.setCurrentTab(0);
                }else if(!mNameBox.isChecked() && !mChefBox.isChecked()){
                    mIngredientsBox.setChecked(true);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.VISIBLE);
                    tabs.setCurrentTab(0);
                }
            }
        });

        mNameBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mNameBox.isChecked()) {
                    mChefBox.setChecked(false);
                    mIngredientsBox.setChecked(false);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.VISIBLE);
                    tabs.setCurrentTab(1);
                    tabs.getCurrentTabView().setVisibility(View.VISIBLE);
                    tabs.setCurrentTab(0);
                }else if(!mChefBox.isChecked() && !mIngredientsBox.isChecked()){
                    mNameBox.setChecked(true);
                    tabs.setCurrentTab(2);
                    tabs.getCurrentTabView().setVisibility(View.GONE);
                    tabs.setCurrentTab(0);
                }
            }
        });
        if(mUser != null){
            mPescatarianBox.setChecked(mUser.getPreferences().isPescatarian());
            mVegetarianBox.setChecked(mUser.getPreferences().isVegetarian());
            mVeganBox.setChecked(mUser.getPreferences().isVegan());
            mNutsBox.setChecked(mUser.getPreferences().isAllergy_nuts());
            mEggsBox.setChecked(mUser.getPreferences().isAllergy_eggs());
            mMilkBox.setChecked(mUser.getPreferences().isAllergy_milk());
            mWheatBox.setChecked(mUser.getPreferences().isAllergy_gluten());
            mShellfishBox.setChecked(mUser.getPreferences().isAllergy_shellfish());
            mSoyBox.setChecked(mUser.getPreferences().isAllergy_soya());
        }
        // Set up user preferences
        mScoreBox.setChecked(true);
        mIngredientsBox.setChecked(true);

    }

    // Quick function for getting the search menu in other fragments
    public SearchView getSearchView() {
        return searchView;
    }

    public MenuItem getSortView() {
        return sortView;
    }

    // Quick function for getting the search prefs in other fragments
    public SearchPrefs getSearchPrefs() {
        return prefs;
    }

    private void addChats(View view){
        final RecyclerView recyclerView = view.findViewById(R.id.messagesList);

        // Set out the layout of this horizontal view
        LinearLayoutManager rManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(rManager);
        //recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
        // Array to score downloaded data
        data = new ArrayList<>();
        final RecyclerView.Adapter rAdapter = new MessageMenuRecyclerAdapter(MessengerMenu.this, data, mUser, view);
        recyclerView.setAdapter(rAdapter);
        long numberOfChats = 5;
        query = mMessages.orderBy("latestMessage", Query.Direction.DESCENDING).limit(numberOfChats);
        final boolean[] initalData = {true};
        // Ensure query exists and builds view with query
        if (query != null) {
            Log.e(TAG, "User is searching the following query: " + query.toString());
            // Query listener to add data to view
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "listen Failed", e);
                        Sentry.captureException(e);
                        return;
                    }
                    Log.e("Messanger", "UID = " + mUser.getUID());
                    Log.e("Messanger", "task success");
                    ArrayList<HashMap> posts = new ArrayList<>();
                    int size = queryDocumentSnapshots.size();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Log.e(TAG, "I have found a doc");
                        posts.add((HashMap) document.getData());
                    }
                    if (!initalData[0]) {
                        data.add(0, new MessageMenuRecyclerAdapter.MessengerFeedPreviewData(
                                posts.get(0)));
                    }
                    if (initalData[0]) {
                        for (int i = 0; i < posts.size(); i++) {
                            data.add(new MessageMenuRecyclerAdapter.MessengerFeedPreviewData(
                                    posts.get(i)));
                        }
                        initalData[0] = false;
                        if (queryDocumentSnapshots.size() != 0) {
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        } else {
                            isLastItemReached = true;
                        }
                    }

                    rAdapter.notifyDataSetChanged();

                    // Track users location to check if new data download is required
                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true;
                            }
                        }

                        // If scrolled to end then download new data and check if we are out of data
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();

                            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                isScrolling = false;
                                Query nextQuery = query.startAfter(lastVisible);
                                nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                        if (t.isSuccessful()) {
                                            ArrayList<HashMap> postsNext = new ArrayList<>();
                                            for (DocumentSnapshot d : t.getResult()) {
                                                Log.e("FEED", "I have found a doc");
                                                postsNext.add((HashMap) d.getData());
                                            }

                                            for (int i = 0; i < postsNext.size(); i++) {
                                                data.add(new MessageMenuRecyclerAdapter.MessengerFeedPreviewData(
                                                        postsNext.get(i)));
                                            }
                                            if (isLastItemReached) {
                                                // Add end here
                                            }
                                            rAdapter.notifyDataSetChanged();
                                            if (t.getResult().size() != 0) {
                                                lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                            }

                                            if (t.getResult().size() < 5) {
                                                isLastItemReached = true;
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    };
                    recyclerView.addOnScrollListener(onScrollListener);


                }
            });

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                }
            });
        }
    }


    // Opens the chat to the specifed user
    protected void openChat(String messageRecipient){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new MessengerFeedFragment(mUser , messageRecipient);
        fragmentTransaction.add(R.id.messageFrameLayout, fragment); //Overlays fragment on existing one
        fragmentTransaction.commitNow(); //Waits for fragment transaction to be completed
        mNewMessage.setVisibility(View.INVISIBLE);
    }

}
