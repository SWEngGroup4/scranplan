package com.group4sweng.scranplan.RecipeInfo;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class recipeReviewRecyclerAdapter extends RecyclerView.Adapter <recipeReviewRecyclerAdapter.ViewHolder> {

    private List <reviewData> mData;
    private RecipeReviewFragment mRecipeReview;
    private UserInfoPrivate user;

    /**  Firebase **/
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    public static class reviewData{

        private String textBody;
        private String userID;
        private String postID;
        private String rating;
        private String recipePic;
        private String timeStamp;

        public reviewData(String textBody, String userID, String rating, Timestamp timeStampIn,String recipePic, String postID){

            this.textBody = textBody;
            this.userID = userID;
            this.rating = rating;
            this.recipePic = recipePic;
            String temp = timeStampIn.toDate().toString();
            this.timeStamp = temp;
            this.postID = postID;

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

            holder.postBody.setText(mData.get(position).textBody);
        }
        else{
            Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
            holder.postBody.setText("deleted");
        }

        if(mData.get(position).recipePic != null) {

            holder.recipeImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(mData.get(position).recipePic).into(holder.recipeImageView);
        }
        else{
            holder.recipeImageView.setVisibility(View.INVISIBLE);
            Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
        }

        holder.rating.setRating(Float.parseFloat(mData.get(position).rating));
        holder.timeStamp.setText(mData.get(position).timeStamp);

        mDatabase.collection("likes").document(mData.get(position).postID + "-" + user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().exists()){
                        holder.likedB4 = true;
                        holder.likedOrNot.setChecked((boolean)task.getResult().get("liked"));
                    }else{
                        holder.likedB4 = false;
                        holder.likedOrNot.setChecked(false);
                    }
                    holder.likedOrNot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (holder.likedOrNot.isChecked()) {
                                Log.e("FERC", "liked post");
                                if(holder.likedB4){
                                    mDatabase.collection("likes").document(mData.get(position).postID + "-" + user.getUID()).update("liked", true);
                                }else{
                                    holder.likedB4 = true;
                                    HashMap<String, Object> likePost = new HashMap<>();
                                    likePost.put("liked", true);
                                    likePost.put("user", user.getUID());
                                    likePost.put("post", mData.get(position).postID);
                                    mDatabase.collection("likes").document(mData.get(position).postID + "-" + user.getUID()).set(likePost);
                                }
                                mDatabase.collection("posts").document(mData.get(position).postID).update("likes", FieldValue.increment(1));
                                int newLiked = Integer.parseInt((String) holder.numLikes.getText())+1;
                                String test = String.valueOf(newLiked);
                                holder.numLikes.setText(test);

                            } else {
                                Log.e("FERC", "unliked post");
                                mDatabase.collection("likes").document(mData.get(position).postID + "-" + user.getUID()).update("liked", false);
                                mDatabase.collection("posts").document(mData.get(position).postID).update("likes", FieldValue.increment(-1));
                                int newLiked = Integer.parseInt((String) holder.numLikes.getText())-1;
                                String test = String.valueOf(newLiked);
                                holder.numLikes.setText(test);
                            }
                        }
                    });
                }else {
                    Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView postBody;
        private TextView timeStamp;
        private TextView author;
        private ImageView recipePic;
        private RatingBar rating;
        private CheckBox likedOrNot;
        private boolean likedB4;
        private TextView numLikes;
        private ImageView recipeImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postBody = itemView.findViewById(R.id.postBody);
            author = itemView.findViewById(R.id.postAuthor);
            timeStamp = itemView.findViewById(R.id.postTimeStamp);
            rating = itemView.findViewById(R.id.ratingBar2);
            likedOrNot = itemView.findViewById(R.id.likeIcon);
            numLikes = itemView.findViewById(R.id.postNumLike);
            cardView = itemView.findViewById(R.id.postCardView);
            recipeImageView = itemView.findViewById(R.id.postRecipeImageViewAdapter);

        }
    }

    public recipeReviewRecyclerAdapter(RecipeReviewFragment reviewFragment, List<reviewData> data, UserInfoPrivate user){

        mRecipeReview = reviewFragment;
        mData = data;
        this.user = user;

    }
}
