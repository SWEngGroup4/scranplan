package com.group4sweng.scranplan;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
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
import com.group4sweng.scranplan.SearchFunctions.HomeQueries;
import com.group4sweng.scranplan.SearchFunctions.HomeRecyclerAdapter;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class builds the horizontal scrolls of custom preference recipe selection for the user on the
 * home screen. Each of these scrolls is infinite in length, loading 5 recipes at a time to minimise
 * reads from the Firestore yet still giving the user an infinite and responsive experience with
 * scroll listeners to check where the user is interacting with these scrolls.
 */
public class RecipeFragment extends Fragment {

    final String TAG = "Home horizontal queries";
    // User preferences passed into scroll views via constructor
    UserInfoPrivate user;
    public RecipeFragment(UserInfoPrivate userSent){
        user = userSent;
    }

    // Width size of each scroll view, dictating size of images on home screen
    final int scrollViewSize = 5;

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


    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("recipes");





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

        // Grabs screen size for % layout TODO - change to density pixels + NullPointerException check
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        // Procedurally fills topLayout with imageButton content
        LinearLayout topLayout = view.findViewById(R.id.topLayout);

        // Checks users details have been provided
        if(user != null){
            // Build the first horizontal scroll built around organising the recipes via highest rated
            HomeQueries horizontalScrollQueries = new HomeQueries(user);
            final RecyclerView recyclerViewScore = new RecyclerView(view.getContext());
            // Set out the layout of this horizontal view
            RecyclerView.LayoutManager rManagerScore = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewScore.setLayoutManager(rManagerScore);
            recyclerViewScore.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            // Array to score downloaded data
            dataScore = new ArrayList<>();
            final RecyclerView.Adapter rAdapterScore = new HomeRecyclerAdapter(RecipeFragment.this, dataScore);
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
                                        document.get("imageURL").toString()
                                ));
                            }
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
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if(isLastItemReachedScore){
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
            final RecyclerView.Adapter rAdapterVotes = new HomeRecyclerAdapter(RecipeFragment.this, dataVotes);
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
                                        document.get("imageURL").toString()
                                ));
                            }
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
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if(isLastItemReachedVotes){
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
            final RecyclerView.Adapter rAdapterTime = new HomeRecyclerAdapter(RecipeFragment.this, dataTime);
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
                                        document.get("imageURL").toString()
                                ));
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
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if(isLastItemReachedTime){
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

            /* Adding the save view as score but with user favourited recipes as a new query
            /  This has been done in the same manner but as there are too many variables to track
            /  this is not workable in any kind of loop. */
            final RecyclerView recyclerViewFave = new RecyclerView(view.getContext());
            RecyclerView.LayoutManager rManagerFave = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewFave.setLayoutManager(rManagerFave);
            recyclerViewFave.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataFave = new ArrayList<>();
            final RecyclerView.Adapter rAdapterFave = new HomeRecyclerAdapter(RecipeFragment.this, dataFave);
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
                                        document.get("imageURL").toString()
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
                                                                d.get("imageURL").toString()
                                                        ));
                                                    }
                                                    if(isLastItemReachedFave){
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
    public void recipeSelected(DocumentSnapshot document) {

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
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("ingredientList", ingredientArray);
        bundle.putString("recipeID", document.getId());
        bundle.putString("xmlURL", document.get("xml_url").toString());
        bundle.putString("recipeTitle", document.get("Name").toString());
        bundle.putString("rating", document.get("score").toString());
        bundle.putString("imageURL", document.get("imageURL").toString());
        bundle.putString("recipeDescription", document.get("Description").toString());
        bundle.putString("chefName", document.get("Chef").toString());


        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
        recipeDialogFragment.setArguments(bundle);
        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }
}