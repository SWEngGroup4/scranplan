package com.group4sweng.scranplan.SearchFunctions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.Home;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeInfo.RecipeInfoFragment;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.sentry.core.Sentry;

import static com.group4sweng.scranplan.SearchFunctions.QueryRequestCode.QueryRequestCodes;

/**
 * Class for the home page fragment containing horizontal meals to scroll though.
 * Author(s): LNewman
 * (c) CoDev 2020
 *
 * This class builds the horizontal scrolls of custom preference recipe selection for the user on the
 * home screen. Each of these scrolls is infinite in length, loading 5 recipes at a time to minimise
 * reads from the Firestore yet still giving the user an infinite and responsive experience with
 * scroll listeners to check where the user is interacting with these scrolls.
 */
public class RecipeFragment extends Fragment implements QueryRequestCodes {

    final String TAG = "Home horizontal queries";
    // User preferences passed into scroll views via constructor
    UserInfoPrivate user;
    private int requestCode = 3;

    public RecipeFragment(UserInfoPrivate mUser){
        this.user = mUser;
    }

    public RecipeFragment(UserInfoPrivate mUser, int requestCode){
        this(mUser);
        this.requestCode = requestCode;
    }

    // Width size of each scroll view, dictating size of images on home screen
    final int scrollViewSize = 5;

    //Score scroll info
    private List<Object>  dataScore = new ArrayList<>();
    private DocumentSnapshot lastVisibleScore;
    private boolean isScrollingScore = false;
    private boolean isLastItemReachedScore = false;
    private final RecyclerView.Adapter rAdapterScore = new HomeRecyclerAdapter(RecipeFragment.this, dataScore);


    //Votes scroll info
    private List<Object> dataVotes  = new ArrayList<>();;
    private DocumentSnapshot lastVisibleVotes;
    private boolean isScrollingVotes = false;
    private boolean isLastItemReachedVotes = false;
    private final RecyclerView.Adapter rAdapterVotes = new HomeRecyclerAdapter(RecipeFragment.this, dataVotes);

    //Timestamp scroll info
    private List<Object>  dataTime = new ArrayList<>();
    private DocumentSnapshot lastVisibleTime;
    private boolean isScrollingTime = false;
    private boolean isLastItemReachedTime = false;
    private final RecyclerView.Adapter rAdapterTime = new HomeRecyclerAdapter(RecipeFragment.this, dataTime);


    //Favourites scroll info
    List<Object> dataFave;
    private DocumentSnapshot lastVisibleFave;
    private boolean isScrollingFave = false;
    private boolean isLastItemReachedFave = false;

    // Adverts
    // NUMBER OF ADS = number of ads to display at one time
    private static final int NUMBER_OF_ADS = 1;

    // used to work out where the ads should be placed
    private static final int SCORE_ADS = 1;
    private static final int VOTES_ADS = 2;
    private static final int TIME_ADS = 3;
    private AdLoader adLoader;

    // List of native ads that have been successfully loaded.
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("recipes");
    public static final int NUMBER_OF_RECIPES = 5;


    private SearchView searchView;
    private MenuItem sortView;
    private SearchPrefs prefs;
    private Home home;


    private Bundle mBundle;
    private Boolean planner = false;

    // Auto-generated super method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Auto-generated onCreate method (everything happens here)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);


        if (getArguments() != null) {
            planner = getArguments().getBoolean("planner");
            if(planner){
                view.findViewById(R.id.recipeFragmentReturnButton).setVisibility(View.VISIBLE);
                view.findViewById(R.id.recipeFragmentReturnButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment = getTargetFragment();
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
                    }
                });
            }
        }
        else planner = false;

        if (planner) {

            TextView title = view.findViewById(R.id.recipeFragmentTitle);
            ImageButton returnButton = view.findViewById(R.id.recipeFragmentReturnButton);
            title.setVisibility(View.VISIBLE);
            returnButton.setVisibility(View.VISIBLE);
            returnButton.setOnClickListener(v ->
                    getTargetFragment().onActivityResult(getTargetRequestCode(),
                            Activity.RESULT_CANCELED, null));
        }

       try{ home = (Home) getActivity();}
       catch (Exception e){
           home = null;
           Sentry.captureException(e);
       }

        if (home != null) {
            searchView = home.getSearchView();
            sortView = home.getSortView();
            if (sortView != null) sortView.setVisible(true);
            if (searchView != null) searchView.setVisibility(View.VISIBLE);
            prefs = home.getSearchPrefs();
            if (searchView != null && prefs != null) {

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        // Search function
                        prefs = home.getSearchPrefs();
                        SearchQuery query = new SearchQuery(s, prefs);
                        SearchListFragment searchListFragment = new SearchListFragment(user);
                        searchListFragment.setValue(query.getQuery());
                        searchListFragment.setIndex(query.getIndex());
                        Log.e(TAG, "User opening search");
                        searchListFragment.show(getFragmentManager(), "search");
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
            }
        }

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        // Procedurally fills topLayout with imageButton content
        LinearLayout topLayout = view.findViewById(R.id.topLayout);

        // Checks users details have been provided
        if(user != null){

            String mealTimescaleScore = "score";
            String mealTimescaleVotes = "votes";
            String mealTimescaleTimestamp = "timestamp";
            String mealTimescaleFavourite = "favourite";

            switch (requestCode) {
                case BREAKFAST:
                    mealTimescaleScore = "breakfastScore";
                    mealTimescaleVotes = "breakfastVotes";
                    mealTimescaleTimestamp = "breakfastTimestamp";
                    mealTimescaleFavourite = "breakfastFavourite";
                    break;
                case LUNCH:
                    mealTimescaleScore = "lunchScore";
                    mealTimescaleVotes = "lunchVotes";
                    mealTimescaleTimestamp = "lunchTimestamp";
                    mealTimescaleFavourite = "lunchFavourite";
                    break;
                case DINNER:
                    mealTimescaleScore = "dinnerScore";
                    mealTimescaleVotes = "dinnerVotes";
                    mealTimescaleTimestamp = "dinnerTimestamp";
                    mealTimescaleFavourite = "dinnerFavourite";
                    break;
                case NORMAL:
                    mealTimescaleScore = "score";
                    mealTimescaleVotes = "votes";
                    mealTimescaleTimestamp = "timestamp";
                    mealTimescaleFavourite = "favourite";
                    break;
            }

            // Build the first horizontal scroll built around organising the recipes via highest rated
            HomeQueries horizontalScrollQueries = new HomeQueries(user);
            final RecyclerView recyclerViewScore = new RecyclerView(view.getContext());
            // Set out the layout of this horizontal view
            RecyclerView.LayoutManager rManagerScore = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewScore.setLayoutManager(rManagerScore);
            recyclerViewScore.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            recyclerViewScore.setAdapter(rAdapterScore);
            final int[] scoreAdIndex = {0};
            final Query queryScore = (Query) horizontalScrollQueries.getQueries().get(mealTimescaleScore);
            // Ensure query exists and builds view with query
            if (queryScore != null) {
                Log.e(TAG, "User is searching the following query: " + queryScore.toString());
                // Give the view a title
                TextView textView = new TextView(view.getContext());
                String testString = "Top picks";
                textView.setTextSize(20);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.BLACK);
                textView.setText(testString);
                // Query listener to add data to view
                queryScore
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dataScore.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                dataScore.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        document.get("imageURL").toString(),
                                        (HashMap) document.getData().get("rating")
                                ));
                            }
                            if(dataScore != null){ try{
                                loadNativeAds(scoreAdIndex[0], SCORE_ADS);}
                                catch (Exception e) {
                                Sentry.captureException(e);
                            }}
                            rAdapterScore.notifyDataSetChanged();
                            if(task.getResult().size() != 0){
                                lastVisibleScore = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            }else{
                                isLastItemReachedScore = true;
                            }
                            // Track users location to check if new data download is required
                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                        isScrollingScore = true;
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

                                    if (isScrollingScore && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReachedScore) {
                                        isScrollingScore = false;
                                        Query nextQuery = queryScore.startAfter(lastVisibleScore);
                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                if (t.isSuccessful()) {
                                                    for (DocumentSnapshot d : t.getResult()) {
                                                        dataScore.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                                                d,
                                                                d.getId(),
                                                                d.get("Name").toString(),
                                                                d.get("imageURL").toString(),
                                                                (HashMap) d.getData().get("rating")
                                                        ));
                                                    }
                                                    if(dataScore != null){
                                                        try {
                                                            scoreAdIndex[0] = totalItemCount;
                                                            loadNativeAds(scoreAdIndex[0], SCORE_ADS);
                                                        }                                                        catch (Exception e){
                                                            Sentry.captureException(e);
                                                        }
                                                    }
                                                    if(isLastItemReachedScore){
                                                        // Add end here
                                                    }
                                                    rAdapterScore.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleScore = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < NUMBER_OF_RECIPES) {
                                                        isLastItemReachedScore = true;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            };
                            recyclerViewScore.addOnScrollListener(onScrollListener);
                        }
                    }
                });
                // Add view to page
                topLayout.addView(textView);
                topLayout.addView(recyclerViewScore);
                Log.e(TAG, "Score horizontal row added");
            }
            /* Adding the save view as score but with highest votes as a new query
            /  This has been done in the same manner but as there are too many variables to track
            /  this is not workable in any kind of loop. */
            final RecyclerView recyclerViewVotes = new RecyclerView(view.getContext());
            RecyclerView.LayoutManager rManagerVotes = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewVotes.setLayoutManager(rManagerVotes);
            recyclerViewVotes.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            final int[] votesAdIndex = {0};
            recyclerViewVotes.setAdapter(rAdapterVotes);
            final Query queryVotes = (Query) horizontalScrollQueries.getQueries().get(mealTimescaleVotes);
            if (queryVotes != null) {
                Log.e(TAG, "User is searching the following query: " + queryVotes.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "Trending";
                textView.setTextSize(20);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.BLACK);
                textView.setText(testString);

                queryVotes
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dataVotes.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                dataVotes.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        document.get("imageURL").toString(),
                                        (HashMap) document.getData().get("rating")
                                ));
                            }
                            if(dataVotes != null){ try{
                                loadNativeAds(votesAdIndex[0], VOTES_ADS);}
                                catch (Exception e){
                                Sentry.captureException(e);
                            }}
                            rAdapterVotes.notifyDataSetChanged();
                            if(task.getResult().size() != 0){
                                lastVisibleVotes = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            }else{
                                isLastItemReachedVotes = true;
                            }

                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                        isScrollingVotes = true;
                                    }
                                }

                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);
                                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                    int visibleItemCount = linearLayoutManager.getChildCount();
                                    int totalItemCount = linearLayoutManager.getItemCount();

                                    if (isScrollingVotes && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReachedVotes) {
                                        isScrollingVotes = false;
                                        Query nextQuery = queryVotes.startAfter(lastVisibleVotes);
                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                if (t.isSuccessful()) {
                                                    for (DocumentSnapshot d : t.getResult()) {
                                                        dataVotes.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                                                d,
                                                                d.getId(),
                                                                d.get("Name").toString(),
                                                                d.get("imageURL").toString(),
                                                                (HashMap) d.getData().get("rating")
                                                        ));
                                                    }
                                                    if(dataVotes != null){
                                                        try{
                                                        votesAdIndex[0] = totalItemCount;
                                                        loadNativeAds(votesAdIndex[0],VOTES_ADS);}
                                                        catch (Exception e){
                                                            Sentry.captureException(e);
                                                        }
                                                    }
                                                    if(isLastItemReachedVotes){
                                                        // Add end here
                                                    }
                                                    rAdapterVotes.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleVotes = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < NUMBER_OF_RECIPES) {
                                                        isLastItemReachedVotes = true;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            };
                            recyclerViewVotes.addOnScrollListener(onScrollListener);
                        }
                    }
                });
                topLayout.addView(textView);
                topLayout.addView(recyclerViewVotes);
                Log.e(TAG, "Votes horizontal view added");
            }


            /* Adding the save view as score but with newest recipes added as a new query
            /  This has been done in the same manner but as there are too many variables to track
            /  this is not workable in any kind of loop. */
            final RecyclerView recyclerViewTime = new RecyclerView(view.getContext());
            RecyclerView.LayoutManager rManagerTime = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewTime.setLayoutManager(rManagerTime);
            recyclerViewTime.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            final int[] timeAdIndex = {0};
            recyclerViewTime.setAdapter(rAdapterTime);
            final Query queryTime = (Query) horizontalScrollQueries.getQueries().get(mealTimescaleTimestamp);
            if (queryTime != null) {
                Log.e(TAG, "User is searching the following query: " + queryTime.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "New tastes";
                textView.setTextSize(20);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.BLACK);
                textView.setText(testString);

                queryTime
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dataTime.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                dataTime.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        document.get("imageURL").toString(),
                                        (HashMap) document.getData().get("rating")
                                ));
                            }
                            if(dataTime != null){ try{loadNativeAds(timeAdIndex[0], TIME_ADS);}
                            catch(Exception e){
                            Sentry.captureException(e);}
                            }
                            rAdapterTime.notifyDataSetChanged();
                            if(task.getResult().size() != 0){
                                lastVisibleTime = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            }else{
                                isLastItemReachedTime = true;
                            }

                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                        isScrollingTime = true;
                                    }
                                }

                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                    int visibleItemCount = linearLayoutManager.getChildCount();
                                    int totalItemCount = linearLayoutManager.getItemCount();

                                    if (isScrollingTime && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReachedTime) {
                                        isScrollingTime = false;
                                        Query nextQuery = queryTime.startAfter(lastVisibleTime);
                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                if (t.isSuccessful()) {
                                                    for (DocumentSnapshot d : t.getResult()) {
                                                        dataTime.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                                                d,
                                                                d.getId(),
                                                                d.get("Name").toString(),
                                                                d.get("imageURL").toString(),
                                                                (HashMap) d.getData().get("rating")
                                                        ));}
                                                    if(dataTime != null){
                                                        try{
                                                        timeAdIndex[0] = totalItemCount;
                                                        loadNativeAds(timeAdIndex[0],TIME_ADS);}
                                                        catch (Exception e){
                                                            Sentry.captureException(e);
                                                        }
                                                    }
                                                    if(isLastItemReachedTime){
                                                        // Add end here
                                                    }
                                                    rAdapterTime.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleTime = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < NUMBER_OF_RECIPES) {
                                                        isLastItemReachedTime = true;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            };
                            recyclerViewTime.addOnScrollListener(onScrollListener);
                        }
                    }
                });
                topLayout.addView(textView);
                topLayout.addView(recyclerViewTime);
                Log.e(TAG, "Time horizontal view added");
            }

            /* Adding the save view as score but with user favourite recipes as a new query
            /  This has been done in the same manner but as there are too many variables to track
            /  this is not workable in any kind of loop. */
            final RecyclerView recyclerViewFave = new RecyclerView(view.getContext());
            RecyclerView.LayoutManager rManagerFave = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewFave.setLayoutManager(rManagerFave);
            recyclerViewFave.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataFave = new ArrayList<>();
            final RecyclerView.Adapter rAdapterFave = new HomeRecyclerAdapter(RecipeFragment.this, dataFave);
            recyclerViewFave.setAdapter(rAdapterFave);
            final Query queryFave = (Query) horizontalScrollQueries.getQueries().get(mealTimescaleFavourite);
            if (queryFave != null) {
                Log.e(TAG, "User is searching the following query: " + queryFave.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "My favourites";
                textView.setTextSize(20);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.BLACK);
                textView.setText(testString);

                queryFave
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dataFave.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                dataFave.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        document.get("imageURL").toString(),
                                        (HashMap) document.getData().get("rating")
                                ));
                            }
                            rAdapterFave.notifyDataSetChanged();
                            if(task.getResult().size() != 0){
                                lastVisibleFave = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            }else{
                                isLastItemReachedFave = true;
                            }

                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                        isScrollingFave = true;
                                    }
                                }

                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                    int visibleItemCount = linearLayoutManager.getChildCount();
                                    int totalItemCount = linearLayoutManager.getItemCount();

                                    if (isScrollingFave && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReachedFave) {
                                        isScrollingFave = false;
                                        Query nextQuery = queryFave.startAfter(lastVisibleFave);
                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                if (t.isSuccessful()) {
                                                    for (DocumentSnapshot d : t.getResult()) {
                                                        dataFave.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                                                d,
                                                                d.getId(),
                                                                d.get("Name").toString(),
                                                                d.get("imageURL").toString(),
                                                                (HashMap) d.getData().get("rating")
                                                        ));
                                                    }
                                                    if(isLastItemReachedFave){
                                                        // Add end here
                                                    }
                                                    rAdapterFave.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleFave = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < NUMBER_OF_RECIPES) {
                                                        isLastItemReachedFave = true;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            };
                            recyclerViewFave.addOnScrollListener(onScrollListener);
                        }
                    }
                });
                topLayout.addView(textView);
                topLayout.addView(recyclerViewFave);
                Log.e(TAG, "Time horizontal view added");
            }

        }else{
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading scroll views - We were unable to find user.");
        }
        return view;
    }

    /**
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void recipeSelected(DocumentSnapshot document) {

        //Takes ingredient and recipe rating array from snap shot and reformats before being passed through to fragment
        ArrayList<String> ingredientArray = new ArrayList<>();

        Map<String, Map<String, Object>> ingredients = (Map) document.getData().get("Ingredients");
        Iterator hmIterator = ingredients.entrySet().iterator();

        HashMap<String, Double> ratingResults = (HashMap) document.getData().get("rating");

        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) hmIterator.next();
            String string = mapElement.getKey().toString() + ": " + mapElement.getValue().toString();
            ingredientArray.add(string);
        }
        //Takes ingredient HashMap from the snapshot.
        HashMap<String, String> ingredientHashMap = (HashMap<String, String>) document.getData().get("Ingredients");



        //Creating a bundle so all data needed from firestore query snapshot can be passed through into fragment class
        mBundle = new Bundle();
        mBundle.putSerializable("ingredientHashMap", ingredientHashMap);
        mBundle.putStringArrayList("ingredientList", ingredientArray);
        mBundle.putSerializable("ratingMap", ratingResults);
        mBundle.putString("recipeID", document.getId());
        mBundle.putString("xmlURL", document.get("xml_url").toString());
        mBundle.putString("recipeTitle", document.get("Name").toString());
        mBundle.putString("imageURL", document.get("imageURL").toString());
        mBundle.putString("recipeDescription", document.get("Description").toString());
        mBundle.putString("chefName", document.get("Chef").toString());
        mBundle.putBoolean("planner", planner);
        mBundle.putBoolean("canFreeze", document.getBoolean("freezer"));
        mBundle.putString("peopleServes", document.get("serves").toString());
        mBundle.putString("fridgeDays", document.get("fridge").toString());
        mBundle.putString("reheat", document.get("reheat").toString());
        mBundle.putBoolean("noEggs", document.getBoolean("noEggs"));
        mBundle.putBoolean("noMilk", document.getBoolean("noMilk"));
        mBundle.putBoolean("noNuts", document.getBoolean("noNuts"));
        mBundle.putBoolean("noShellfish", document.getBoolean("noShellfish"));
        mBundle.putBoolean("noSoy", document.getBoolean("noSoy"));
        mBundle.putBoolean("noWheat", document.getBoolean("noWheat"));
        mBundle.putBoolean("pescatarian", document.getBoolean("pescatarian"));
        mBundle.putBoolean("vegan", document.getBoolean("vegan"));
        mBundle.putBoolean("vegetarian", document.getBoolean("vegetarian"));

        ArrayList<Integer> faves = (ArrayList) document.get("favourite");
        mBundle.putBoolean("isFav", faves.contains(user.getUID()));

        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
        recipeDialogFragment.setArguments(mBundle);
        recipeDialogFragment.setTargetFragment(this, 1);
        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }

    /**
     * Inset the ad in the recipe list
     * Un-comment premium/gold member features to enable and
     * un comment switch case statments to ad adverts there
     * @param index index where to intially put the advert in (the start of the list usually)
     * @param adType Location where the ad goes which list it should be placed into.
     */
    private void insertAdsInRecipeItems(int index, int adType) {
        /* TODO implement check for premium membership
        if(premium member){
        return;
        } else {
         */
        int offset = 0;
        if (mNativeAds.size() <= 0 || dataScore.size() <= 0) {
            return;
        }


        offset = NUMBER_OF_RECIPES/2 + 1;

        switch (adType){
//            case SCORE_ADS:
//                if(index + offset < dataScore.size()){
//                    dataScore.add(index + offset + 1,mNativeAds.get(mNativeAds.size() - 1));
//                }
//                break;
            case VOTES_ADS:
                if(index + offset < dataVotes.size()){
                    dataVotes.add(index + offset + 1,mNativeAds.get(mNativeAds.size() - 1));
                }
                break;
            case TIME_ADS:
                if(index + offset < dataTime.size()){
                    dataTime.add(index + offset + 1,mNativeAds.get(mNativeAds.size() - 1));
                }
                break;
            default:
                break;
        }

        switch (adType) {
//            case SCORE_ADS:
//                rAdapterScore.notifyDataSetChanged();
//                break;
            case VOTES_ADS:
                rAdapterVotes.notifyDataSetChanged();
                break;
            case TIME_ADS:
                 rAdapterTime.notifyDataSetChanged();
             break;
            default:
                break;
        }


        /* TODO remove for premium membership
        }
         */
    }

    private void loadNativeAds(int index, int adType) {

        AdLoader.Builder builder = new AdLoader.Builder(getContext(), getString(R.string.ad_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            insertAdsInRecipeItems(index , adType);
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertAdsInRecipeItems(index, adType);
                        }
                    }
                }).build();

        // Load the Native ads.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Intent i = new Intent();
            i.putExtras(mBundle);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        }
    }
}
