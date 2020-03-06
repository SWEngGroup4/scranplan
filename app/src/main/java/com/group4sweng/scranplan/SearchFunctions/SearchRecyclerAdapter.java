package com.group4sweng.scranplan.SearchFunctions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.group4sweng.scranplan.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> {

    private SearchListFragment mSearchListFragment;
    private List<SearchRecipePreviewData> mDataset;

    public static class SearchRecipePreviewData {

        private String recipeID;
        private String title;
        private String description;
        private String imageURL;

        SearchRecipePreviewData(String recipeID, String title, String description, String imageURL) {
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

    public SearchRecyclerAdapter (SearchListFragment searchListFragment, List<SearchRecipePreviewData> dataset) {
        mSearchListFragment = searchListFragment;
        mDataset = dataset;
    }


    public SearchRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
                mSearchListFragment.recipeSelected(mDataset.get(holder.getAdapterPosition()).recipeID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}