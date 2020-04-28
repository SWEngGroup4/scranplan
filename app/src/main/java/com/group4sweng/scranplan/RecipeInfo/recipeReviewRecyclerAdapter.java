package com.group4sweng.scranplan.RecipeInfo;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.R;

import java.sql.Timestamp;
import java.util.List;

public class recipeReviewRecyclerAdapter extends RecyclerView.Adapter <recipeReviewRecyclerAdapter.ViewHolder> {

    private List <reviewData> mData;
    private RecipeReviewFragment mRecipeReview;

    /**  Firebase **/
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    public static class reviewData{

        private String textBody;
        private String userID;
        private String rating;
        private Uri recipePic;
        private String timeStamp;

        public reviewData(String textBody, String userID, String rating, String timeStamp){

            this.textBody = textBody;
            this.userID = userID;
            this.rating = rating;
            //this.recipePic = recipePic;
            this.timeStamp = timeStamp;

        }

    }

    @Override
    public recipeReviewRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_recycler, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull recipeReviewRecyclerAdapter.ViewHolder holder, int position) {

        if(mData.get(position).userID != null) {
            mDatabase.collection("users").document(mData.get(position).userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        holder.author.setText((String) task.getResult().get("displayName"));

                    }
                }
            });
        }else{
            Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
            holder.author.setText("past user");
        }

        if(mData.get(position).textBody != null) {
            mDatabase.collection("posts").document(mData.get(position).textBody).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    holder.postBody.setText((String) task.getResult().get("body"));

                }
            });
        }else{
            Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
            holder.postBody.setText("deleted");
        }


        holder.rating.setRating(Float.parseFloat(mData.get(position).rating));
        holder.timeStamp.setText(mData.get(position).timeStamp);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView postBody;
        private TextView timeStamp;
        private TextView author;
        private ImageView recipePic;
        private RatingBar rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postBody = itemView.findViewById(R.id.postBody);
            author = itemView.findViewById(R.id.postAuthor);
            timeStamp = itemView.findViewById(R.id.postTimeStamp);
            rating = itemView.findViewById(R.id.ratingBar2);

        }
    }

    public recipeReviewRecyclerAdapter(RecipeReviewFragment reviewFragment, List<reviewData> data){

        mRecipeReview = reviewFragment;
        mData = data;

    }
}
