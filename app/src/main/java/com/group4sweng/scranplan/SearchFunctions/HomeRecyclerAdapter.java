package com.group4sweng.scranplan.SearchFunctions;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.RecipeFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    private RecipeFragment mRecipeFragment;
    private List<HomeRecipePreviewData> mDataset;

    public static class HomeRecipePreviewData {

        private String recipeID;
        private String imageURL;

        public HomeRecipePreviewData(String recipeID, String imageURL) {
            this.recipeID = recipeID;
            this.imageURL = imageURL;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private ImageView imageView;

        private ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.recipeListCardView);
            imageView = v.findViewById(R.id.recipeListImageView);
        }
    }

    public HomeRecyclerAdapter (RecipeFragment recipeFragment, List<HomeRecipePreviewData> dataset) {
        mRecipeFragment = recipeFragment;
        mDataset = dataset;
    }



    public HomeRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_image_recycler, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Picasso.get().load(mDataset.get(position).imageURL).into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecipeFragment != null){
                    mRecipeFragment.recipeSelected(mDataset.get(holder.getAdapterPosition()).recipeID);
                }else{
                    Log.e("SEARCH RECYCLER ADAPTER", "Issue with no component in onBindViewHolder");
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}