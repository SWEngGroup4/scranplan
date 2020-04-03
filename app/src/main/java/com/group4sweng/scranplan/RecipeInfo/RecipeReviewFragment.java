package com.group4sweng.scranplan.RecipeInfo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.Social.FeedFragment;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;


public class RecipeReviewFragment extends FeedFragment {

    private RatingBar mStars;
    private CheckBox mImageIcon;
    private CheckBox mRecipeIcon;
    private CheckBox mReviewIcon;
    private TextView mRecipeRate;

    private float getRating;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_feed, null);

        initPageItems(layout);
        displayItems(layout);



        return layout;
    }

    //Defining all relevant members of page
    public void initPageItems(View layout){
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
                getRating = mStars.getRating();
            }
        });

        //Gets rid of recipe icon
        mRecipeIcon.setVisibility(View.INVISIBLE);

        //Gets rid of review icon
        mReviewIcon.setVisibility(View.INVISIBLE);

        //Sets the text view for rating to visible
        mRecipeRate.setVisibility(View.VISIBLE);

    }
}
