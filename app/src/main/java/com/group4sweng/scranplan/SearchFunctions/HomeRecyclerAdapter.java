package com.group4sweng.scranplan.SearchFunctions;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.group4sweng.scranplan.Adverts.AdViewHolder;
import com.group4sweng.scranplan.MealPlanner.MealTimescaleFragment;
import com.group4sweng.scranplan.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 *  Class holding the recycler adapter for the home page, each card will represent the view
 *  of one recipe. All recipe info is stored in this card.
 *  Creating a card view that hold the picture and the document which, the picture will be displayed
 *  in a button and the button will pass the document though for the recipe to be read
 *
 *  Class also deals with displaying adverts
 */
public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    // Variables for database and fragment to be displayed in
    private RecipeFragment mRecipeFragment;
    private MealTimescaleFragment mMealTimescaleFragment;


    private List<HomeRecipePreviewData> mDataset;

    private static final int RECIPE_PREVIEW_ITEM_VIEW_TYPE = 0;
    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;
    /**
     * The holder for the card with variables required
     */
    public static class HomeRecipePreviewData {

        private String recipeID;
        private String title;
        private Float rating;
        private String imageURL;
        private DocumentSnapshot document;
        private String ratingResults;

        public HomeRecipePreviewData(DocumentSnapshot doc, String recipeID, String title, String imageURL, HashMap<String, Double> ratingResults) {
            this.document = doc;
            this.recipeID = recipeID;
            this.title = title;
            this.imageURL = imageURL;
            this.ratingResults = ratingResults.get("overallRating").toString();
            this.rating = Float.parseFloat(this.ratingResults);
        }
    }

    /**
     * Building the card and image view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private CardView cardView;
        private TextView textView;
        private RatingBar ratingBar;
        private ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            linearLayout = v.findViewById(R.id.recipeListContainer);
            cardView = v.findViewById(R.id.recipeListCardView);
            textView = v.findViewById(R.id.recipeListTitle);
            ratingBar = v.findViewById(R.id.recipeListRating);
            imageView = v.findViewById(R.id.recipeListImageView);
        }
    }

    /**
     * Constructor to add all variables
     * @param recipeFragment
     * @param dataset
     */
    public HomeRecyclerAdapter (RecipeFragment recipeFragment, List<Object> dataset) {
        mRecipeFragment = recipeFragment;
        mDataset = dataset;
    }

    /**
            * Constructor to add all variables
     * @param mealTimescaleFragment
     * @param dataset
     */
    public HomeRecyclerAdapter (MealTimescaleFragment mealTimescaleFragment, List<HomeRecyclerAdapter.HomeRecipePreviewData> dataset) {
        mMealTimescaleFragment = mealTimescaleFragment;
        mDataset = dataset;
    }

    /**
     * Building and inflating the view within its parent
     * @param parent
     * @param viewType
     * @return
     */
    public HomeRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View nativeAdLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_unified, parent, false);
                return new AdViewHolder(nativeAdLayout);
            case RECIPE_PREVIEW_ITEM_VIEW_TYPE:
                // do nothing
            default:
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_image_recycler, parent, false);
                return new ViewHolder(v);
        }
    }

    /**
     * Getting the image with picasso and adding the on click functionality
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Integer padding = 10;
        Float density = holder.cardView.getContext().getResources().getDisplayMetrics().density;
        Integer paddingDP = (int)(padding * density);

        //Load image into ImageView
        Picasso.get().load(mDataset.get(position).imageURL).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                //Once image has been loaded, set up info for recipe display
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayout.setPadding(paddingDP, 0, paddingDP, 0);

                holder.textView.setText(mDataset.get(position).title);
                holder.textView.setBackgroundColor(Color.parseColor("#80FFFFFF"));

                holder.ratingBar.setRating(mDataset.get(position).rating);
                holder.ratingBar.setBackgroundColor(Color.parseColor("#80FFFFFF"));
            }

            @Override
            public void onError(Exception e) {

            }
        });

        holder.cardView.setOnClickListener(v -> {
            if (mRecipeFragment != null){
                mRecipeFragment.recipeSelected(mDataset.get(holder.getAdapterPosition()).document);
            }else{
                Log.e("SEARCH RECYCLER ADAPTER", "Issue with no component in onBindViewHolder");
            }

            if (mMealTimescaleFragment != null){
                mMealTimescaleFragment.recipeSelected(mDataset.get(holder.getAdapterPosition()).document);
            }else{
                Log.e("SEARCH RECYCLER ADAPTER", "Issue with no component in onBindViewHolder");
            }
        });
    }

    // Getting dataset size
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position){
        Object recyclerViewItem = mDataset.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return RECIPE_PREVIEW_ITEM_VIEW_TYPE;
    }
}
