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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.TimelinePlanner.BreakfastFragment;
import com.group4sweng.scranplan.RecipeFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BreakfastRecyclerAdapter extends RecyclerView.Adapter<BreakfastRecyclerAdapter.ViewHolder> {

    // Variables for database and fragment to be displayed in
    private BreakfastFragment mBreakfastFragment;
    private List<BreakfastRecyclerAdapter.BreakfastRecipePreviewData> mDataset;

    /**
     * The holder for the card with variables required
     */
    public static class BreakfastRecipePreviewData {

        private String recipeID;
        private String imageURL;
        private DocumentSnapshot document;

        public BreakfastRecipePreviewData(DocumentSnapshot doc, String recipeID, String imageURL) {
            this.document = doc;
            this.recipeID = recipeID;
            this.imageURL = imageURL;
        }
    }

    /**
     * Building the card and image view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private ImageView imageView;

        private ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.recipeListCardView);
            imageView = v.findViewById(R.id.recipeListImageView);
        }
    }

    /**
     * Constructor to add all variables
     * @param breakfastFragment
     * @param dataset
     */
    public BreakfastRecyclerAdapter (BreakfastFragment breakfastFragment, List<BreakfastRecyclerAdapter.BreakfastRecipePreviewData> dataset) {
        mBreakfastFragment = breakfastFragment;
        mDataset = dataset;
    }


    /**
     * Building and inflating the view within its parent
     * @param parent
     * @param viewType
     * @return
     */
    public BreakfastRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_image_recycler, parent, false);
        return new BreakfastRecyclerAdapter.ViewHolder(v);
    }

    /**
     * Getting the image with picasso and adding the on click functionality
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final BreakfastRecyclerAdapter.ViewHolder holder, int position) {
        Picasso.get().load(mDataset.get(position).imageURL).into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBreakfastFragment != null){
                    mBreakfastFragment.recipeSelected(mDataset.get(holder.getAdapterPosition()).document);
                }else{
                    Log.e("SEARCH RECYCLER ADAPTER", "Issue with no component in onBindViewHolder");
                }

            }
        });
    }

    // Getting dataset size
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
