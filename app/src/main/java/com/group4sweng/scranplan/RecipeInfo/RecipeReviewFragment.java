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
    private String mrecipeID;

    protected HashMap<String, Double> ratingMap;

    private double getNewRating;
    private double newOverallRating;
    private double newTotalRates;


    public RecipeReviewFragment(UserInfoPrivate userSent) {
        super(userSent);
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
        mrecipeID = getArguments().getString("recipeID");


        initPageItems(layout);
        displayItems(layout);
        calculateRating();
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

                calculateRating();

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

        final String TAG = "Data";
        Log.i(TAG, "Values: "+ ratingMap);

        newTotalRates = ratingMap.get("totalRates") + 1;

        newOverallRating = ((ratingMap.get("overallRating") * ratingMap.get("totalRates")) + getNewRating) / newTotalRates;

        ratingMap.put("overallRating", newOverallRating);
        ratingMap.put("totalRates", newTotalRates);

        HashMap<String, Object> updateMap = new HashMap<>();
        CollectionReference ref = mDatabase.collection("recipes");
        DocumentReference documentReference = ref.document(mrecipeID);
        updateMap.put("rating", ratingMap);
        documentReference.update(updateMap);

        Log.i(TAG, "Values: "+ ratingMap);


    }

}
