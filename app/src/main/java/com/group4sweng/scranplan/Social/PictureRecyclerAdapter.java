package com.group4sweng.scranplan.Social;

import android.graphics.Color;
import android.os.Bundle;
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

/**
 * Class for the profile picture feed recycler adapter.
 * Author(s): LNewman
 * (c) CoDev 2020
 *
 *  Class holding the recycler adapter for the home page, each card will represent the view
 *  of one recipe. All recipe info is stored in this card.
 *  Creating a card view that hold the picture and the document which, the picture will be displayed
 *  in a button and the button will pass the document though for the recipe to be read
 */
public class PictureRecyclerAdapter extends RecyclerView.Adapter<PictureRecyclerAdapter.ViewHolder> {

    // Variables for database and fragment to be displayed in
    private ProfilePictures mProfilePictures;
    private List<PicturePostPreviewData> mDataset;
    private UserInfoPrivate user;
    private String authorName;



    /**  Firebase **/
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    /**
     * The holder for the card with variables required
     */
    public static class PicturePostPreviewData {
        private String postID;
        private String authorUID;
        private String timeStamp;
        private String body;
        private boolean isPic;
        private String uploadedImageURL;
        private boolean isRecipe;
        private String recipeID;
        private String recipeImageURL;
        private String recipeTitle;
        private String recipeDescription;
        private boolean isReview;
        private float review;
        private HashMap<String, Object> document;

        public PicturePostPreviewData(HashMap<String, Object> doc) {
            this.document = doc;
            this.postID = (String) document.get("docID");
            Timestamp time = (Timestamp) document.get("timestamp");
            this.timeStamp = time.toDate().toString();
            this.isRecipe = (boolean) document.get("isRecipe");
            this.isPic = (boolean) document.get("isPic");
            this.isReview = (boolean) document.get("isReview");
            this.authorUID = (String) document.get("author");
            this.body = (String) document.get("body");
            if(isPic){
                this.uploadedImageURL = (String) document.get("uploadedImageURL");
            }
            if(isRecipe){
                this.recipeID = (String) document.get("recipeID");
                this.recipeImageURL = (String) document.get("recipeImageURL");
                this.recipeTitle = (String) document.get("recipeTitle");
                this.recipeDescription = (String) document.get("recipeDescription");
                if(isReview){
                    double toFloat = (double) document.get("overallRating");
                    this.review = (float)toFloat;
                }
            }

        }
    }

    /**
     * Building the card and image view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private String authorName;
        private String authorPicURL;
        private CardView cardView;
        private ImageView uploadedImageView;
        private TextView recipeTitle;
        private RatingBar recipeRating;

        private TextView numLikes;
        private TextView numComments;
        private CheckBox likedOrNot;

        private boolean likedB4;

        private ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.recipeListCardView);
            uploadedImageView = v.findViewById(R.id.recipeListImageView);
            recipeTitle = v.findViewById(R.id.recipeListTitle);
            recipeRating = v.findViewById(R.id.recipeListRating);
            numLikes = v.findViewById(R.id.postNumLike);
            numComments = v.findViewById(R.id.postNumComments);
            likedOrNot = v.findViewById(R.id.likeIcon);
        }
    }




    /**
     * Constructor to add all variables
     * @param profilePictures
     * @param dataset
     */
    public PictureRecyclerAdapter(ProfilePictures profilePictures, List<PicturePostPreviewData> dataset ,UserInfoPrivate user, String author) {
        mProfilePictures = profilePictures;
        this.authorName = author;
        mDataset = dataset;
        this.user = user;
    }


    /**
     * Building and inflating the view within its parent
     * @param parent
     * @param viewType
     * @return
     */
    public PictureRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_picture_recycler, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Getting the image with picasso and adding the on click functionality
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mDataset.get(position).uploadedImageURL != null){
            holder.uploadedImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(mDataset.get(position).uploadedImageURL).into(holder.uploadedImageView);
        }
        if(mDataset.get(position).isRecipe){
            holder.recipeTitle.setVisibility(View.VISIBLE);
            holder.recipeTitle.setBackgroundColor(Color.parseColor("#80FFFFFF"));
            holder.recipeTitle.setText(mDataset.get(position).recipeTitle);
            if(mDataset.get(position).isReview){
                holder.recipeRating.setVisibility(View.VISIBLE);
                holder.recipeRating.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                holder.recipeRating.setRating(mDataset.get(position).review);
            }else{
                holder.recipeRating.setVisibility(View.GONE);
            }
        }else{
            holder.recipeTitle.setVisibility(View.GONE);
        }
        Log.e("FdRc", "searching for post: " + mDataset.get(position).postID);
        mDatabase.collection("posts").document(mDataset.get(position).postID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    holder.numLikes.setText(task.getResult().get("likes").toString());
                    holder.numComments.setText(task.getResult().get("comments").toString());
                }else {
                    Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
                }
            }
        });
        mDatabase.collection("likes").document(mDataset.get(position).postID + "-" + user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                    mDatabase.collection("likes").document(mDataset.get(position).postID + "-" + user.getUID()).update("liked", true);
                                }else{
                                    holder.likedB4 = true;
                                    HashMap<String, Object> likePost = new HashMap<>();
                                    likePost.put("liked", true);
                                    likePost.put("user", user.getUID());
                                    likePost.put("post", mDataset.get(position).postID);
                                    mDatabase.collection("likes").document(mDataset.get(position).postID + "-" + user.getUID()).set(likePost);
                                }
                                mDatabase.collection("posts").document(mDataset.get(position).postID).update("likes", FieldValue.increment(+1));
                                int newLiked = Integer.parseInt((String) holder.numLikes.getText())+1;
                                String test = String.valueOf(newLiked);
                                holder.numLikes.setText(test);

                            } else {
                                Log.e("FERC", "unliked post");
                                mDatabase.collection("likes").document(mDataset.get(position).postID + "-" + user.getUID()).update("liked", false);
                                mDatabase.collection("posts").document(mDataset.get(position).postID).update("likes", FieldValue.increment(-1));
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
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProfilePictures != null){
                    Bundle mBundle = new Bundle();
                    mBundle.putString("authorID", mDataset.get(position).authorUID);
                    mBundle.putString("postID", mDataset.get(position).postID);
                    mBundle.putString("authorName", authorName);
                    mBundle.putString("authorPicURL", holder.authorPicURL);
                    mBundle.putBoolean("likedB4", holder.likedB4);
                    mBundle.putString("body", mDataset.get(position).body);
                    mBundle.putBoolean("isRecipe", mDataset.get(position).isRecipe);
                    if(mDataset.get(position).isRecipe){
                        mBundle.putString("recipeTitle", mDataset.get(position).recipeTitle);
                        mBundle.putString("recipeDescription", mDataset.get(position).recipeDescription);
                        mBundle.putString("recipeImageURL", mDataset.get(position).recipeImageURL);
                        mBundle.putBoolean("isReview", mDataset.get(position).isReview);
                        mBundle.putString("recipeID", mDataset.get(position).recipeID);
                        if(mDataset.get(position).isReview){
                            mBundle.putFloat("overallRating", mDataset.get(position).review);
                        }
                    }
                    if(mDataset.get(position).isPic) {
                        mBundle.putString("uploadedImageURL", mDataset.get(position).uploadedImageURL);
                    }
                    mBundle.putString("timestamp", mDataset.get(position).timeStamp);


                    mProfilePictures.itemSelected(mDataset.get(holder.getAdapterPosition()).document, mBundle, holder.numLikes, holder.likedOrNot, holder.numComments, holder.cardView);




                    mDatabase.collection("likes").document(mDataset.get(position).postID + "-" + user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                            }else {
                                Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
                            }
                        }
                    });

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