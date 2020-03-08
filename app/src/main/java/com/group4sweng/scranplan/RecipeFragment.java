package com.group4sweng.scranplan;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import com.group4sweng.scranplan.SearchFunctions.HomeQueries;
import com.group4sweng.scranplan.SearchFunctions.HomeRecyclerAdapter;
import com.group4sweng.scranplan.SearchFunctions.SearchListFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchRecyclerAdapter;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;


public class RecipeFragment extends Fragment {

    final String TAG = "Home horizontal queries";

    UserInfoPrivate user;
    public RecipeFragment(UserInfoPrivate userSent){
        user = userSent;
    }

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

        if(user != null){
            HomeQueries horizontalScrollQueriesScore = new HomeQueries(user);

            final RecyclerView recyclerViewScore = new RecyclerView(view.getContext());

            RecyclerView.LayoutManager rManagerScore = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

            recyclerViewScore.setLayoutManager(rManagerScore);

            recyclerViewScore.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataScore = new ArrayList<>();

            final RecyclerView.Adapter rAdapterScore = new HomeRecyclerAdapter(RecipeFragment.this, dataScore);
            recyclerViewScore.setAdapter(rAdapterScore);

            final Query queryScore = (Query) horizontalScrollQueriesScore.getQueries().get("score");


            if (queryScore != null) {
                Log.e(TAG, "User is searching the following query: " + queryScore.toString());

                TextView textView = new TextView(view.getContext());
                String testString = "Top picks";
                textView.setTextSize(25);
                textView.setPadding(20, 5, 5, 5);
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(4, 0, 0, Color.BLACK);
                textView.setText(testString);

                queryScore
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                dataScore.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
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

                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                        isScrollingScore = true;
                                    }
                                }

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
                topLayout.addView(textView);
                topLayout.addView(recyclerViewScore);
                Log.e(TAG, "Score horizontal row added");
            }

            HomeQueries horizontalScrollQueriesVotes = new HomeQueries(user);

            final RecyclerView recyclerViewVotes = new RecyclerView(view.getContext());

            RecyclerView.LayoutManager rManagerVotes = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

            recyclerViewVotes.setLayoutManager(rManagerVotes);

            recyclerViewVotes.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataVotes = new ArrayList<>();

            final RecyclerView.Adapter rAdapterVotes = new HomeRecyclerAdapter(RecipeFragment.this, dataVotes);
            recyclerViewVotes.setAdapter(rAdapterVotes);

            final Query queryVotes = (Query) horizontalScrollQueriesVotes.getQueries().get("votes");


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



            HomeQueries horizontalScrollQueriesTime = new HomeQueries(user);

            final RecyclerView recyclerViewTime = new RecyclerView(view.getContext());

            RecyclerView.LayoutManager rManagerTime = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

            recyclerViewTime.setLayoutManager(rManagerTime);

            recyclerViewTime.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataTime = new ArrayList<>();

            final RecyclerView.Adapter rAdapterTime = new HomeRecyclerAdapter(RecipeFragment.this, dataTime);
            recyclerViewTime.setAdapter(rAdapterTime);

            final Query queryTime = (Query) horizontalScrollQueriesTime.getQueries().get("timestamp");


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

            HomeQueries horizontalScrollQueriesFave = new HomeQueries(user);

            final RecyclerView recyclerViewFave = new RecyclerView(view.getContext());

            RecyclerView.LayoutManager rManagerFave = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

            recyclerViewFave.setLayoutManager(rManagerFave);

            recyclerViewFave.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / scrollViewSize));
            dataFave = new ArrayList<>();

            final RecyclerView.Adapter rAdapterFave = new HomeRecyclerAdapter(RecipeFragment.this, dataFave);
            recyclerViewFave.setAdapter(rAdapterFave);

            final Query queryFave = (Query) horizontalScrollQueriesFave.getQueries().get("favourite");


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
                                dataScore.add(new HomeRecyclerAdapter.HomeRecipePreviewData(
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
            Log.e(TAG, "ERROR: Loading scroll views - We were unable to find user.");
        }





//        for (int i = 0; i < 10; i++) {
//
//            // Placeholder text TODO - change to query type
//            TextView textView = new TextView(view.getContext());
//            String testString = "Test" + i;
//            textView.setText(testString);
//
//            // Generates single horizontal scroll view for query
//            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(view.getContext());
//
//            // Generates linear layout for holding content with % size according to screen
//            LinearLayout linearLayout = new LinearLayout(view.getContext());
//            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / 5));
//
//            for (int j =0; j < 10; j++) {
//
//                // Generates imageButton, adds padding and sizes accordingly
//                ImageButton imageButton = new ImageButton(view.getContext());
//                imageButton.setAdjustViewBounds(true);
//                imageButton.setPadding(10,10,10,10);
//                imageButton.setBackground(null);
//                imageButton.setScaleType(ImageButton.ScaleType.FIT_XY);
//
//                // Function to add image to button from database
//                loadImage(imageButton);
//
//                // Button functionality
//                imageButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        openRecipeDialog();
//                    }
//                });
//
//                linearLayout.addView(imageButton);
//            }
//
//            // Adds generated content to appropriate views
//            horizontalScrollView.addView(linearLayout);
//            topLayout.addView(textView);
//            topLayout.addView(horizontalScrollView);
//        }
        return view;
    }

//    // Private function to get random image from database and load it into an imageButton
//    // TODO - Change to structured query search instead of random selection
//    // TODO - Edit function so that we don't have to call the entire collection for each image?
//    private void loadImage(final ImageButton imageButton) {
//        final Random random = new Random();
//
//        /* Adds onSuccessListener - will not run until query is reported successful
//           Stops async task from throwing errors */
//        mColRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot querySnapshot) {
//                // Not necessary but worth checking
//                if (!querySnapshot.isEmpty()) {
//                    List<DocumentSnapshot> docs = querySnapshot.getDocuments(); // Get documents from queried collection
//                    int n = random.nextInt(docs.size() - 1); // Random number generated
//                    Picasso.get().load(docs.get(n).get("imageURL").toString()).into(imageButton); //Loads image using picasso library TODO - NullPointerException check
//
//                }
//            }
//        });
//    }

    public void openRecipeDialog(){

        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");

    }

    public void recipeSelected(String recipeID) {
        RecipeInfoFragment recipeDialogFragment = new RecipeInfoFragment();
        recipeDialogFragment.show(getFragmentManager(), "Show recipe dialog fragment");
    }
}