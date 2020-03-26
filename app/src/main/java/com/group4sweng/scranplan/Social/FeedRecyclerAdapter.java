package com.group4sweng.scranplan.Social;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.group4sweng.scranplan.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 *  Class holding the recycler adapter for the home page, each card will represent the view
 *  of one recipe. All recipe info is stored in this card.
 *  Creating a card view that hold the picture and the document which, the picture will be displayed
 *  in a button and the button will pass the document though for the recipe to be read
 */
public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {

    // Variables for database and fragment to be displayed in
    private FeedFragment mFeedFragment;
    private List<FeedPostPreviewData> mDataset;

    /**
     * The holder for the card with variables required
     */
    public static class FeedPostPreviewData {

        private String postID;
        private String authorUID;

        private String title;
        private String body;
        private boolean isPic;
        private String uploadedImageURL;
        private boolean isRecipe;
        private String recipeID;
        private String recipeImageURL;
        private String recipeTitle;
        private String recipeDescription;
        private HashMap<String, Object> document;

        public FeedPostPreviewData(HashMap<String, Object> doc) {
            this.document = doc;
            this.isRecipe = (boolean) document.get("isRecipe");
            this.isPic = (boolean) document.get("isPic");
            this.title = (String) document.get("title");
            this.body = (String) document.get("body");
            if(isPic){
                this.uploadedImageURL = (String) document.get("uploadedImageURL");
            }
            if(isRecipe){
                this.recipeID = (String) document.get("recipeID");
                this.recipeImageURL = (String) document.get("recipeImageURL");
                this.recipeTitle = (String) document.get("recipeTitle");
                this.recipeDescription = (String) document.get("recipeDescription");
            }

        }
    }

    /**
     * Building the card and image view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {


        private CardView cardView;
        private TextView title;
        private TextView body;
        private ImageView uploadedImageView;

        private LinearLayout recipeLayout;

        private ImageView recipeImageView;
        private TextView recipeTitle;
        private TextView RecipeDescription;

        private ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.postCardView);
            title = v.findViewById(R.id.postTitle);
            body = v.findViewById(R.id.postBody);
            uploadedImageView = v.findViewById(R.id.uploadedImageView);
            recipeLayout = v.findViewById(R.id.recipePostInput);
            recipeImageView = v.findViewById(R.id.recipeListImageView);
            recipeTitle = v.findViewById(R.id.recipeListTitle);
            RecipeDescription = v.findViewById(R.id.recipeListDescription);

        }
    }

    /**
     * Constructor to add all variables
     * @param feedFragment
     * @param dataset
     */
    public FeedRecyclerAdapter(FeedFragment feedFragment, List<FeedPostPreviewData> dataset) {
        mFeedFragment = feedFragment;
        mDataset = dataset;
    }


    /**
     * Building and inflating the view within its parent
     * @param parent
     * @param viewType
     * @return
     */
    public FeedRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_recyler, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Getting the image with picasso and adding the on click functionality
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.title.setText(mDataset.get(position).title);
        holder.body.setText(mDataset.get(position).body);
        if(mDataset.get(position).uploadedImageURL != null){
            holder.uploadedImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(mDataset.get(position).uploadedImageURL).into(holder.uploadedImageView);
        }
        if(mDataset.get(position).isRecipe){
            holder.recipeLayout.setVisibility(View.VISIBLE);
            holder.recipeTitle.setText(mDataset.get(position).recipeTitle);
            holder.RecipeDescription.setText(mDataset.get(position).recipeDescription);
            Picasso.get().load(mDataset.get(position).recipeImageURL).into(holder.recipeImageView);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFeedFragment != null){
                    mFeedFragment.itemSelected(mDataset.get(holder.getAdapterPosition()).document);
                }else{
                    Log.e("FEED RECYCLER ADAPTER", "Issue with no component in onBindViewHolder");
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