package com.group4sweng.scranplan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private RecipeListFragment mRecipeListFragment;
    private List<RecipePreviewData> mDataset;

    public static class RecipePreviewData {

        private String recipeID;
        private String title;
        private String description;
        private String imageURL;

        RecipePreviewData(String recipeID, String title, String description, String imageURL) {
            this.recipeID = recipeID;
            this.title = title;
            this.description = description;
            this.imageURL = imageURL;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private ImageView imageView;
        private TextView title;
        private  TextView description;

        private ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.recipeListCardView);
            imageView = v.findViewById(R.id.recipeListImageView);
            title = v.findViewById(R.id.recipeListTitle);
            description = v.findViewById(R.id.recipeListDescription);
        }
    }

    public RecyclerAdapter (RecipeListFragment recipeListFragment, List<RecipePreviewData> dataset) {
        mRecipeListFragment = recipeListFragment;
        mDataset = dataset;
    }

    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyler, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.title.setText(mDataset.get(position).title);
        holder.description.setText(mDataset.get(position).description);
        Picasso.get().load(mDataset.get(position).imageURL).into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipeListFragment.recipeSelected(mDataset.get(holder.getAdapterPosition()).recipeID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}