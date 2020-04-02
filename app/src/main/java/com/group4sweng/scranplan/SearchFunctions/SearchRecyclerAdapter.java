package com.group4sweng.scranplan.SearchFunctions;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.group4sweng.scranplan.R;
import com.squareup.picasso.Picasso;

import java.util.List;
/**
 *  Class holding the recycler adapter for the search functionality, each card will represent the view
 *  of one recipe. All recipe info is stored in this card.
 *  Creating a card view that hold the picture and the document which, the picture will be displayed
 *  in a button and the button will pass the document though for the recipe to be read
 */
public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> {

    // Variables for database and fragment to be displayed in
    private SearchListFragment mSearchListFragment;
    private List<SearchRecipePreviewData> mDataset;

    public static class SearchRecipePreviewData {

        private String recipeID;
        private String title;
        private String description;
        private String imageURL;
        private DocumentSnapshot document;

        /**
         * The holder for the card with variables required
         */
        public SearchRecipePreviewData(DocumentSnapshot doc, String recipeID, String title, String description, String imageURL) {
            this.document = doc;
            this.recipeID = recipeID;
            this.title = title;
            this.description = description;
            this.imageURL = imageURL;
        }
    }

    /**
     * Building the card and image view
     */
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

    /**
     * Constructor to add all variables
     * @param searchListFragment
     * @param dataset
     */
    public SearchRecyclerAdapter (SearchListFragment searchListFragment, List<SearchRecipePreviewData> dataset) {
        mSearchListFragment = searchListFragment;
        mDataset = dataset;
    }

    /**
     * Building and inflating the view within its parent
     * @param parent
     * @param viewType
     * @return
     */
    public SearchRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyler, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Getting the image with picasso and adding the on click functionality
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String cap = mDataset.get(position).title;
        holder.title.setText(cap.substring(0, 1).toUpperCase() + cap.substring(1));
        holder.description.setText(mDataset.get(position).description);
        Picasso.get().load(mDataset.get(position).imageURL).into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSearchListFragment != null){
                    mSearchListFragment.recipeSelected(mDataset.get(holder.getAdapterPosition()).document);
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