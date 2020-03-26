package com.group4sweng.scranplan.Social;

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
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeInfoFragment;
import com.group4sweng.scranplan.SearchFunctions.HomeQueries;
import com.group4sweng.scranplan.SearchFunctions.HomeRecyclerAdapter;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class builds the horizontal scrolls of custom preference recipe selection for the user on the
 * home screen. Each of these scrolls is infinite in length, loading 5 recipes at a time to minimise
 * reads from the Firestore yet still giving the user an infinite and responsive experience with
 * scroll listeners to check where the user is interacting with these scrolls.
 */
public class FeedFragment extends Fragment {

    final String TAG = "Home horizontal queries";
    // User preferences passed into scroll views via constructor
    UserInfoPrivate user;
    public FeedFragment(UserInfoPrivate userSent){
        user = userSent;
    }

    // Width size of each scroll view, dictating size of images on home screen
    final int scrollViewSize = 5;

    //Score scroll info
    List<FeedRecyclerAdapter.FeedPostPreviewData> data;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;



    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColRef = mDatabase.collection("followers");





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
            //TODO make new query





            final RecyclerView recyclerView = new RecyclerView(view.getContext());
            // Set out the layout of this horizontal view
            RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(rManager);
            recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
            // Array to score downloaded data
            data = new ArrayList<>();
            final RecyclerView.Adapter rAdapter = new FeedRecyclerAdapter(FeedFragment.this, data);
            recyclerView.setAdapter(rAdapter);
            final Query query = mColRef.whereArrayContains("users", user.getUID()).orderBy("timestamp").limit(10);
            // Ensure query exists and builds view with query
            if (query != null) {
                Log.e(TAG, "User is searching the following query: " + query.toString());
//                // Give the view a title
//                TextView textView = new TextView(view.getContext());
//                String testString = "Top picks";
//                textView.setTextSize(25);
//                textView.setPadding(20, 5, 5, 5);
//                textView.setTextColor(Color.WHITE);
//                textView.setShadowLayer(4, 0, 0, Color.BLACK);
//                textView.setText(testString);
                // Query listener to add data to view
                query
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<HashMap> posts = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                posts.add((HashMap)document.get("recent"));
                            }
                            Collections.sort(posts, new MapComparator("timestamp"));
                            for(int i = 0; i < posts.size(); i++){
                                data.add(new FeedRecyclerAdapter.FeedPostPreviewData(
                                        posts.get(i)));
                            }
                            rAdapter.notifyDataSetChanged();
                            if(task.getResult().size() != 0){
                                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            }else{
                                isLastItemReached = true;
                            }
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
//                                                    for (DocumentSnapshot d : t.getResult()) {
//                                                        data.add(new FeedRecyclerAdapter.FeedPostPreviewData(
//                                                                d,
//                                                                d.getId(),
//                                                                d.get("imageURL").toString()
//                                                        ));
//                                                    }
                                                    ArrayList<HashMap> posts = new ArrayList<>();
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        posts.add((HashMap)document.get("recent"));
                                                    }
                                                    Collections.sort(posts, new Comparator<Map<String, Object>>() {
                                                        @Override
                                                        public int compare(Map<String, Object> map1, Map<String, Object> map2) {

                                                            //TODO compare the two doubles or its from the map and then return the correct one.
                                                            return 0;
                                                        }
                                                    });



                                                    for(int i = 0; i < posts.size(); i++){
                                                        data.add(new FeedRecyclerAdapter.FeedPostPreviewData(
                                                                posts.get(i)));
                                                    }
                                                    if(isLastItemReached){
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
                    }
                });
                // Add view to page
//                topLayout.addView(textView);
                topLayout.addView(recyclerView);
                Log.e(TAG, "Social feed added");
            }

        }else{
            // If scroll views fail due to no user, this error is reported
            Log.e(TAG, "ERROR: Loading social feed - We were unable to find user.");
        }
        return view;
    }

    /**
     * Sort the ArrayList of maps
     */
    class MapComparator implements Comparator<Map<String, String>>
    {
        private final String key;

        public MapComparator(String key)
        {
            this.key = key;
        }

        public int compare(Map<String, String> first,
                           Map<String, String> second)
        {
            String firstValue = first.get(key);
            String secondValue = second.get(key);
            return firstValue.compareTo(secondValue);
        }
    }

    /**
     * On click of a recipe a new recipe info fragment is opened and the document is sent through
     * This saves on downloading the data again from the database
     */
    public void itemSelected(Map<String, Object> document) {

        //Takes ingredient array from snap shot and reformats before being passed through to fragment
//        ArrayList<String> ingredientArray = new ArrayList<>();
//
//        Map<String, Map<String, Object>> test = (Map) document.getData().get("Ingredients");
//        Iterator hmIterator = test.entrySet().iterator();
//
//        while (hmIterator.hasNext()) {
//            Map.Entry mapElement = (Map.Entry) hmIterator.next();
//            String string = mapElement.getKey().toString() + ": " + mapElement.getValue().toString();
//            ingredientArray.add(string);
//        }
//
//        //Creating a bundle so all data needed from firestore query snapshot can be passed through into fragment class
//        Bundle bundle = new Bundle();
//        bundle.putStringArrayList("ingredientList", ingredientArray);
//        bundle.putString("recipeID", document.getId());
//        bundle.putString("xmlURL", document.get("xml_url").toString());
//        bundle.putString("recipeTitle", document.get("Name").toString());
//        bundle.putString("rating", document.get("score").toString());
//        bundle.putString("imageURL", document.get("imageURL").toString());
//        bundle.putString("recipeDescription", document.get("Description").toString());
//        bundle.putString("chefName", document.get("Chef").toString());
//        bundle.putSerializable("user", user);
//
//
//        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
//        recipeDialogFragment.setArguments(bundle);
//        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }
}