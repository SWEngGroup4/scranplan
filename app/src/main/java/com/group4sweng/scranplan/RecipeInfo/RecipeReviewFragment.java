package com.group4sweng.scranplan.RecipeInfo;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.Exceptions.ImageException;
import com.group4sweng.scranplan.LoadingDialog;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.SearchFunctions.HomeRecyclerAdapter;
import com.group4sweng.scranplan.SearchFunctions.SearchListFragment;
import com.group4sweng.scranplan.SearchFunctions.SearchRecyclerAdapter;
import com.group4sweng.scranplan.Social.FeedFragment;
import com.group4sweng.scranplan.Social.FeedRecyclerAdapter;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecipeReviewFragment extends FeedFragment {

    private RatingBar mStars;
    private CheckBox mImageIcon;
    private CheckBox mRecipeIcon;
    private CheckBox mReviewIcon;
    private TextView mRecipeRate;
    private String mRecipeID;
    private String mRecipeTitle;
    private String mRecipeDescription;
    private String docID;

    protected HashMap<String, Double> ratingMap;

    private List<recipeReviewRecyclerAdapter.reviewData> data;

    CollectionReference reviewRef, postRef;
    DocumentReference reviewDocRef;

    private double getNewRating;
    private double newOverallRating;
    private double newTotalRates;
    final String TAG = "Data";

    public RecipeReviewFragment() {
        super();
    }

    // Auto-generated super method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_feed, null);

        initPageItems(layout);
        displayItems(layout);
        checkReview();
        addPosts(layout);
        initPageListeners(layout);



        return layout;
    }

    private void checkReview() {

        mDatabase.collection("reviews").document(docID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Log.e(TAG, "exists ");

                        DocumentSnapshot document = task.getResult();
                        //mPostBodyInput.setText(document.get("overallReview").toString());

                    } else {
                        Log.e("FdRc", "Unable to retrieve user document in Firestore ");
                    }
                }
            }

        });
    }


    //Defining all relevant members of page
    public void initPageItems(View layout){
        super.initPageItems(layout);

        loadingDialog = new LoadingDialog(getActivity());

        ratingMap = (HashMap<String, Double>) getArguments().getSerializable("ratingMap");
        mRecipeID = getArguments().getString("recipeID");
        mRecipeTitle = getArguments().getString("recipeTitle");
        mRecipeDescription = getArguments().getString("recipeDescription");

        user = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) requireActivity().getIntent().getSerializableExtra("user");

        docID = user.getUID() + "-" + mRecipeID;

        postRef = mDatabase.collection("posts");
        reviewRef = mDatabase.collection("reviews");
        reviewDocRef = reviewRef.document(docID);
        mStars = layout.findViewById(R.id.postRecipeRating);
        mImageIcon = layout.findViewById(R.id.imageIcon);
        mRecipeIcon = layout.findViewById(R.id.recipeIcon);
        mReviewIcon = layout.findViewById(R.id.reviewIcon);
        mRecipeRate = layout.findViewById(R.id.recipeRate);

    }

    /**
     *  Setting up page listeners for when buttons are pressed
     */
    protected void initPageListeners(View layout) {
        super.initPageListeners();

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calculateRating();
                RecipeInfoFragment repInfo = (RecipeInfoFragment) getParentFragment();
                repInfo.updateStarRating(ratingMap.get("overallRating").toString());

//                mPostBodyInput.getText().clear();

                addingReviewFirestore(layout);


            }
        });

    }

    private void displayItems(View layout){

        //Setting background colour to match that of the fragment
        FrameLayout root = layout.findViewById(R.id.FrameLayoutid);
        root.setBackgroundColor(Color.parseColor("#bdb5c7"));

        //Makes the star rating visible and stores the value of the given rating
        mStars.setVisibility(View.VISIBLE);
        mStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                getNewRating = mStars.getRating();
            }
        });

        //Gets rid of recipe icon
        mRecipeIcon.setVisibility(View.INVISIBLE);

        //Gets rid of review icon
        mReviewIcon.setVisibility(View.INVISIBLE);

        //Sets the text view for rating to visible
        mRecipeRate.setVisibility(View.VISIBLE);

    }

    private void calculateRating(){


        Log.i(TAG, "Values: "+ ratingMap);

//        ratingMap.put("overallRating", 3f);
//        ratingMap.put("totalRates", 2f);

        newTotalRates = ratingMap.get("totalRates") + 1;
        newOverallRating = ((ratingMap.get("overallRating") * ratingMap.get("totalRates")) + getNewRating) / newTotalRates;

        ratingMap.put("overallRating", newOverallRating);
        ratingMap.put("totalRates", newTotalRates);

        HashMap<String, Object> updateMap = new HashMap<>();
        CollectionReference ref = mDatabase.collection("recipes");
        DocumentReference documentReference = ref.document(mRecipeID);
        updateMap.put("rating", ratingMap);
        documentReference.update(updateMap);

        Log.i(TAG, "Values: "+ ratingMap);


    }

    private void addingReviewFirestore(View layout){

        POST_IS_UPLOADING = true;
        loadingDialog.startLoadingDialog();

        String body = mPostBodyInput.getText().toString();

        // Creating map to store data in posts collection
        HashMap<String, Object> postsMap = new HashMap<>();
        postsMap.put("comments", 0);
        postsMap.put("likes", 0);
        postsMap.put("body", body);
        postsMap.put("overallRating", getNewRating);
        postsMap.put("timestamp", FieldValue.serverTimestamp());
        postsMap.put("author", user.getUID());
        postsMap.put("isPic", mPostPic.isChecked());
        postsMap.put("isRecipe", true);
        postsMap.put("isReview", true);
        postsMap.put("recipeDescription", mRecipeDescription);
        postsMap.put("recipeTitle", mRecipeTitle);
        postsMap.put("recipeID", mRecipeID);

        //Saving map to the firestore
        DocumentReference postRef = mDatabase.collection("posts").document();
        Log.e(TAG, "Added new post ");
        //On complete listener makes sure post has been saved on firestore before getting document ID and saving
        //in reviews collection
        postRef.set(postsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Document ID for review post
                docID = user.getUID() + "-" + mRecipeID;
                HashMap<String, Object> reviewMap = new HashMap<>();

                reviewMap.put("user", user.getUID());
                reviewMap.put("overallRating", getNewRating);
                reviewMap.put("post", postRef.getId());
                reviewMap.put("timestamp", FieldValue.serverTimestamp());

                //Saving review map to the firestore with custom document ID

                Log.e(TAG, "Added new post ");
                reviewDocRef.set(reviewMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        addPosts(layout);
                    }
                });

                // Update the UserInfoPrivate class with this new image URL.
                POST_IS_UPLOADING = false;// State we have finished uploading (a reference exists).
                loadingDialog.dismissDialog();
            }
        });

    }

    @Override
    protected void addPosts(View view) {

        // Creating a list of the data and building all variables to add to recycler view
        final RecyclerView recyclerView = view.findViewById(R.id.postsList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(rManager);
        data = new ArrayList<>();
        final RecyclerView.Adapter rAdapter = new recipeReviewRecyclerAdapter(RecipeReviewFragment.this, data);
        recyclerView.setAdapter(rAdapter);
        query = postRef.whereEqualTo("recipeID", recipeID).whereEqualTo("isReview", true).orderBy("timestamp", Query.Direction.DESCENDING).limit(10);

        // Once the data has been returned, dataset populated and components build
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // For each document a new recipe preview view is generated
                    if(task.getResult() != null)
                    {

                        for (DocumentSnapshot document : task.getResult()) {
                            //Pass all data from document
                            data.add(new recipeReviewRecyclerAdapter.reviewData(
                                    document.getId(),
                                    document.get("author").toString(),
                                    document.get("overallRating").toString(),
                                    document.get("timestamp").toString()
                            ));
                        }
                        rAdapter.notifyDataSetChanged();
                        // Set the last document as last user can see
                        if(task.getResult().size() != 0){
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }else{
                            // If no data returned, user notified
                            isLastItemReached = true;
//
                        }
                        // check if user has scrolled through the view
                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }
                            // If user is scrolling and has reached the end, more data is loaded
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                // Checking if user is at the end
                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();
                                // If found to have reached end, more data is requested from the server in the same manner
                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;
                                    Query nextQuery = query.startAfter(lastVisible);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    //Pass all data from document
                                                    data.add(new recipeReviewRecyclerAdapter.reviewData(
                                                            d.getId(),
                                                            d.get("author").toString(),
                                                            d.get("overallRating").toString(),
                                                            d.get("timestamp").toString()
                                                    ));
                                                }
                                                if(isLastItemReached){
                                                    // Last comment reached
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
            }
        });

    }

    /** Image checker.
     *  Used to reduce wait times for the user when uploading on a slow network.
     *  Also limits the data that has to be stored and queried from Firebase.
     *  @param uri - The unique uri of the image file location from the users storage.
     *  @throws ImageException - Throws if the image file is too large or the format isn't a supported image format.
     */
    protected void checkImage(Uri uri) throws ImageException {
        super.checkImage(uri);

    }

}
