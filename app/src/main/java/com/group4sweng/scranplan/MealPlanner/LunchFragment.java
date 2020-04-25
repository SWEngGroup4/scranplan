package com.group4sweng.scranplan.MealPlanner;

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
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.group4sweng.scranplan.SearchFunctions.HomeQueries;
import com.group4sweng.scranplan.SearchFunctions.HomeRecyclerAdapter;
import com.group4sweng.scranplan.SearchFunctions.SearchListFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchPrefs;
import com.group4sweng.scranplan.SearchFunctions.SearchQuery;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class LunchFragment extends Fragment {

    final String TAG = "Lunch horizontal queries";
    UserInfoPrivate user;
    public LunchFragment(UserInfoPrivate userSent){
        user = userSent;
    }

    List<HomeRecyclerAdapter.HomeRecipePreviewData> dataLunch;
    private DocumentSnapshot lastVisibleLunch;
    private boolean isScrollingLunch = false;
    private boolean isLastItemReachedLunch = false;

    //Score scroll info
    List<HomeRecyclerAdapter.HomeRecipePreviewData> dataScore;
    private DocumentSnapshot lastVisibleScore;
    private boolean isScrollingScore = false;
    private boolean isLastItemReachedScore = false;


    //Votes scroll info
    List<HomeRecyclerAdapter.HomeRecipePreviewData> dataVotes;
    private DocumentSnapshot lastVisibleVotes;
    private boolean isScrollingVotes = false;
    private boolean isLastItemReachedVotes = false;

    //Timestamp scroll info
    List<HomeRecyclerAdapter.HomeRecipePreviewData> dataTime;
    private DocumentSnapshot lastVisibleTime;
    private boolean isScrollingTime = false;
    private boolean isLastItemReachedTime = false;

    //Favourites scroll info
    List<HomeRecyclerAdapter.HomeRecipePreviewData> dataFave;
    private DocumentSnapshot lastVisibleFave;
    private boolean isScrollingFave = false;
    private boolean isLastItemReachedFave = false;

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("recipes");

    private SearchView searchView;
    private MenuItem sortView;
    private SearchPrefs prefs;

    private Bundle mBundle;
    private Boolean planner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null)
            planner = getArguments().getBoolean("planner");
        else planner = false;

        Home home = (Home) getActivity();
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
                        SearchQuery query = new SearchQuery(s, prefs);
                        SearchListFragment searchListFragment = new SearchListFragment(user);
                        searchListFragment.setValue(query.getQuery());
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

        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        // Grabs screen size for % layout TODO - change to density pixels + NullPointerException check
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        // Procedurally fills topLayout with imageButton content
        LinearLayout topLayout = view.findViewById(R.id.topLayout);

        final int scrollViewSize = 5;

        if (user != null) {
            HomeQueries horizontalScrollQueries = new HomeQueries(user);

            /* Adding the save view as score but with lunch as a new query
            /  This has been done in the same manner but as there are too many variables to track
            /  this is not workable in any kind of loop. */
            final RecyclerView recyclerViewLunch = new RecyclerView(view.getContext());
            RecyclerView.LayoutManager rType = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewLunch.setLayoutManager(rType);
            recyclerViewLunch.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataLunch = new ArrayList<>();
            final HomeRecyclerAdapter rAdapterLunch = new HomeRecyclerAdapter(LunchFragment.this, dataLunch);
            recyclerViewLunch.setAdapter(rAdapterLunch);
            final Query queryLunch = (Query) horizontalScrollQueries.getQueries().get("lunch");
            if (queryLunch != null) {
                Log.e(TAG, "User is searching the following query: " + queryLunch.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "Lunch";
                textView.setTextSize(25);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(4, 0, 0, Color.BLACK);
                textView.setText(testString);

                queryLunch
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                dataLunch.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        Float.valueOf(document.get("score").toString()),
                                        document.get("imageURL").toString()
                                ));
                            }
                            rAdapterLunch.notifyDataSetChanged();
                            if (task.getResult().size() != 0) {
                                lastVisibleLunch = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            } else {
                                isLastItemReachedLunch = true;
                            }

                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                        isScrollingLunch = true;
                                    }
                                }

                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                    int visibleItemCount = linearLayoutManager.getChildCount();
                                    int totalItemCount = linearLayoutManager.getItemCount();

                                    if (isScrollingLunch && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReachedLunch) {
                                        isScrollingLunch = false;
                                        Query nextQuery = queryLunch.startAfter(lastVisibleLunch);
                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                                if (t.isSuccessful()) {
                                                    for (DocumentSnapshot d : t.getResult()) {
                                                        dataLunch.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                                                d,
                                                                d.getId(),
                                                                d.get("Name").toString(),
                                                                Float.valueOf(d.get("score").toString()),
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if (isLastItemReachedLunch) {
                                                        // Add end here
                                                    }
                                                    rAdapterLunch.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleLunch = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < 10) {
                                                        isLastItemReachedLunch = true;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            };
                            recyclerViewLunch.addOnScrollListener(onScrollListener);
                        }
                    }
                });
                topLayout.addView(textView);
                topLayout.addView(recyclerViewLunch);
                Log.e(TAG, "Lunch horizontal view added");
            }
            // Build a horizontal scroll built around organising the recipes via highest rated
            final RecyclerView recyclerViewScore = new RecyclerView(view.getContext());
            // Set out the layout of this horizontal view
            RecyclerView.LayoutManager rManagerScore = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewScore.setLayoutManager(rManagerScore);
            recyclerViewScore.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            // Array to score downloaded data
            dataScore = new ArrayList<>();
            final RecyclerView.Adapter rAdapterScore = new HomeRecyclerAdapter(LunchFragment.this, dataScore);
            recyclerViewScore.setAdapter(rAdapterScore);
            final Query queryScore = (Query) horizontalScrollQueries.getQueries().get("score");
            // Ensure query exists and builds view with query
            if (queryScore != null) {
                Log.e(TAG, "User is searching the following query: " + queryScore.toString());
                // Give the view a title
                TextView textView = new TextView(view.getContext());
                String testString = "Top picks";
                textView.setTextSize(25);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(4, 0, 0, Color.BLACK);
                textView.setText(testString);
                // Query listener to add data to view
                queryScore
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                dataScore.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        Float.valueOf(document.get("score").toString()),
                                        document.get("imageURL").toString()
                                ));
                            }
                            rAdapterScore.notifyDataSetChanged();
                            if (task.getResult().size() != 0) {
                                lastVisibleScore = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            } else {
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
                                                                Float.valueOf(d.get("score").toString()),
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if (isLastItemReachedScore) {
                                                        // Add end here
                                                    }
                                                    rAdapterScore.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleScore = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < 5) {
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
            dataVotes = new ArrayList<>();
            final RecyclerView.Adapter rAdapterVotes = new HomeRecyclerAdapter(LunchFragment.this, dataVotes);
            recyclerViewVotes.setAdapter(rAdapterVotes);
            final Query queryVotes = (Query) horizontalScrollQueries.getQueries().get("votes");
            if (queryVotes != null) {
                Log.e(TAG, "User is searching the following query: " + queryVotes.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "Trending";
                textView.setTextSize(25);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(4, 0, 0, Color.BLACK);
                textView.setText(testString);

                queryVotes
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                dataVotes.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        Float.valueOf(document.get("score").toString()),
                                        document.get("imageURL").toString()
                                ));
                            }
                            rAdapterVotes.notifyDataSetChanged();
                            if (task.getResult().size() != 0) {
                                lastVisibleVotes = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            } else {
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
                                                                Float.valueOf(d.get("score").toString()),
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if (isLastItemReachedVotes) {
                                                        // Add end here
                                                    }
                                                    rAdapterVotes.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleVotes = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < 5) {
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
            dataTime = new ArrayList<>();
            final RecyclerView.Adapter rAdapterTime = new HomeRecyclerAdapter(LunchFragment.this, dataTime);
            recyclerViewTime.setAdapter(rAdapterTime);
            final Query queryTime = (Query) horizontalScrollQueries.getQueries().get("timestamp");
            if (queryTime != null) {
                Log.e(TAG, "User is searching the following query: " + queryTime.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "New tastes";
                textView.setTextSize(25);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(4, 0, 0, Color.BLACK);
                textView.setText(testString);

                queryTime
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                dataTime.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        Float.valueOf(document.get("score").toString()),
                                        document.get("imageURL").toString()
                                ));
                            }
                            rAdapterTime.notifyDataSetChanged();
                            if (task.getResult().size() != 0) {
                                lastVisibleTime = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            } else {
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
                                                                Float.valueOf(d.get("score").toString()),
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if (isLastItemReachedTime) {
                                                        // Add end here
                                                    }
                                                    rAdapterTime.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleTime = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < 5) {
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
            final RecyclerView.Adapter rAdapterFave = new HomeRecyclerAdapter(LunchFragment.this, dataFave);
            recyclerViewFave.setAdapter(rAdapterFave);
            final Query queryFave = (Query) horizontalScrollQueries.getQueries().get("favourite");
            if (queryFave != null) {
                Log.e(TAG, "User is searching the following query: " + queryFave.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "My favourites";
                textView.setTextSize(25);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(4, 0, 0, Color.BLACK);
                textView.setText(testString);

                queryFave
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                dataFave.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
                                        document,
                                        document.getId(),
                                        document.get("Name").toString(),
                                        Float.valueOf(document.get("score").toString()),
                                        document.get("imageURL").toString()
                                ));
                            }
                            rAdapterFave.notifyDataSetChanged();
                            if (task.getResult().size() != 0) {
                                lastVisibleFave = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            } else {
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
                                                                Float.valueOf(d.get("score").toString()),
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if (isLastItemReachedFave) {
                                                        // Add end here
                                                    }
                                                    rAdapterFave.notifyDataSetChanged();
                                                    if (t.getResult().size() != 0) {
                                                        lastVisibleFave = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                    }

                                                    if (t.getResult().size() < 5) {
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
    public void recipeSelected (DocumentSnapshot document){

        //Takes ingredient array from snap shot and reformats before being passed through to fragment
        ArrayList<String> ingredientArray = new ArrayList<>();

        Map<String, Map<String, Object>> test = (Map) document.getData().get("Ingredients");
        Iterator hmIterator = test.entrySet().iterator();

        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) hmIterator.next();
            String string = mapElement.getKey().toString() + ": " + mapElement.getValue().toString();
            ingredientArray.add(string);
        }

        //Creating a bundle so all data needed from firestore query snapshot can be passed through into fragment class
        mBundle = new Bundle();
        mBundle.putStringArrayList("ingredientList", ingredientArray);
        mBundle.putString("recipeID", document.getId());
        mBundle.putString("xmlURL", document.get("xml_url").toString());
        mBundle.putString("recipeTitle", document.get("Name").toString());
        mBundle.putString("rating", document.get("score").toString());
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

        ArrayList faves = (ArrayList) document.get("favourite");
        mBundle.putBoolean("isFav", faves.contains(user.getUID().hashCode()));


        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
        recipeDialogFragment.setArguments(mBundle);
        recipeDialogFragment.setTargetFragment(this, 1);
        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
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
