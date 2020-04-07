package com.group4sweng.scranplan.SearchFunctions;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.group4sweng.scranplan.MealPlanner.BreakfastFragment;
import com.group4sweng.scranplan.MealPlanner.DinnerFragment;
import com.group4sweng.scranplan.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DinnerRecyclerAdapter extends RecyclerView.Adapter<DinnerRecyclerAdapter.ViewHolder> {
    // Variables for database and fragment to be displayed in
    private DinnerFragment mDinnerFragment;
    private List<DinnerRecyclerAdapter.DinnerRecipePreviewData> mDataset;

    /**
     * The holder for the card with variables required
     */
    public static class DinnerRecipePreviewData {

        private String recipeID;
        private String title;
        private Float rating;
        private String imageURL;
        private DocumentSnapshot document;

        public DinnerRecipePreviewData(DocumentSnapshot doc, String recipeID, String title, Float rating, String imageURL) {
            this.document = doc;
            this.recipeID = recipeID;
            this.title = title;
            this.rating = rating;
            this.imageURL = imageURL;
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

        private ViewHolder(View v) {
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
     * @param dinnerFragment
     * @param dataset
     */
    public DinnerRecyclerAdapter (DinnerFragment dinnerFragment, List<DinnerRecyclerAdapter.DinnerRecipePreviewData> dataset) {
        mDinnerFragment = dinnerFragment;
        mDataset = dataset;
    }


    /**
     * Building and inflating the view within its parent
     * @param parent
     * @param viewType
     * @return
     */
    public DinnerRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_image_recycler, parent, false);
        return new DinnerRecyclerAdapter.ViewHolder(v);
    }

    /**
     * Getting the image with picasso and adding the on click functionality
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final DinnerRecyclerAdapter.ViewHolder holder, int position) {
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
            if (mDinnerFragment != null){
                mDinnerFragment.recipeSelected(mDataset.get(holder.getAdapterPosition()).document);
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
}
