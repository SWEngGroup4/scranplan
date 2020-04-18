package com.group4sweng.scranplan.RecipeInfo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.Social.FeedFragment;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RecipeReviewFragment extends FeedFragment {

    private RatingBar mStars;
    private CheckBox mImageIcon;
    private CheckBox mRecipeIcon;
    private CheckBox mReviewIcon;
    private TextView mRecipeRate;
    private String mRecipeID;

    protected HashMap<String, Double> ratingMap;

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

        ratingMap = (HashMap<String, Double>) getArguments().getSerializable("ratingMap");
        mRecipeID = getArguments().getString("recipeID");

        user = (com.group4sweng.scranplan.UserInfo.UserInfoPrivate) requireActivity().getIntent().getSerializableExtra("user");

        Log.e(TAG, "id name " + user.getUID());
        Log.e(TAG, "id name " + mRecipeID);
        Log.e(TAG, "id name " + user.getUID()+ mRecipeID);

        initPageItems(layout);
        displayItems(layout);
        initPageListeners();


        return layout;
    }

    //Defining all relevant members of page
    protected void initPageItems(View layout){
        super.initPageItems(layout);

        mStars = layout.findViewById(R.id.rating);
        mImageIcon = layout.findViewById(R.id.imageIcon);
        mRecipeIcon = layout.findViewById(R.id.recipeIcon);
        mReviewIcon = layout.findViewById(R.id.reviewIcon);
        mRecipeRate = layout.findViewById(R.id.recipeRate);

    }

    /**
     *  Setting up page listeners for when buttons are pressed
     */
    protected void initPageListeners() {
        super.initPageListeners();

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String title = mPostTitleInput.getText().toString();
                String body = mPostBodyInput.getText().toString();
                boolean isPic = false;
                //TODO set these variables from the addition of these items


                //mPostTitleInput.getText().clear();
                mPostBodyInput.getText().clear();

                CollectionReference ref = mDatabase.collection("reviews");
                Log.e(TAG, "Added new post ");
                // Saving the comment as a new document

                HashMap<String, Object> map = new HashMap<>();
                map.put("reviewID", user.getUID()+mRecipeID);
                //map.put("title", title);
                map.put("body", body);
                map.put("timestamp", FieldValue.serverTimestamp());
                map.put("isPic", isPic);
                if(isPic){

                }
                // Saving default user to Firebase Firestore database
                ref.add(map);


                calculateRating();
                RecipeInfoFragment repInfo = (RecipeInfoFragment) getParentFragment();
                repInfo.updateStarRating(ratingMap.get("overallRating").toString());

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

}
