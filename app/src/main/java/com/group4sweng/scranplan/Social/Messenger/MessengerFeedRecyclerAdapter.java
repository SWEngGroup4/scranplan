package com.group4sweng.scranplan.Social.Messenger;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.Administration.LoadingDialog;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.Social.ProfilePosts;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import io.sentry.core.Sentry;

/**
 * Class to display the data in the messanger feed
 */

public class MessengerFeedRecyclerAdapter extends RecyclerView.Adapter<MessengerFeedRecyclerAdapter.ViewHolder> {

    // Variables for database and fragment to be displayed in
    private MessengerFeedFragment mFeedFragment;
    private ProfilePosts mProfilePosts;
    private List<MessengerFeedRecyclerAdapter.FeedPostPreviewData> mDataset;
    private UserInfoPrivate user;
    private View view;



    /**  Firebase **/
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();


    MessengerFeedRecyclerAdapter(MessengerFeedFragment messengerFeedFragment, List<FeedPostPreviewData> dataset, UserInfoPrivate mUser, View view) {
            mFeedFragment = messengerFeedFragment;
            mDataset = dataset;
            this.user = mUser;
            this.view = view;
        }



    /**
     * The holder for the card with variables required
     */
    public static class FeedPostPreviewData {
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

        public FeedPostPreviewData(HashMap<String, Object> doc) {
            if (doc.get("timestamp") != null) {
                this.document = doc;
                this.postID = (String) document.get("docId");
                Timestamp time = (Timestamp) document.get("timestamp");
                try {
                    this.timeStamp = time.toDate().toString();
                } catch (Exception e) {
                    Sentry.captureException(e);
                }
                this.isRecipe = (boolean) document.get("isRecipe");
                this.isPic = (boolean) document.get("isPic");
                if (document.get("overallRating") != null) {
                    this.isReview = (boolean) document.get("isReview");
                }
                this.isReview = (boolean) document.get("isReview");
                this.authorUID = (String) document.get("author");
                this.body = (String) document.get("body");
                if (isPic) {
                    this.uploadedImageURL = (String) document.get("uploadedImageURL");
                }
                if (isRecipe) {
                    this.recipeID = (String) document.get("recipeID");
                    this.recipeImageURL = (String) document.get("recipeImageURL");
                    this.recipeTitle = (String) document.get("recipeTitle");
                    this.recipeDescription = (String) document.get("recipeDescription");
                    if (isReview) {
                        double toFloat = (double) document.get("overallRating");
                        this.review = (float) toFloat;
                    }
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
        private TextView author;
        private TextView body;
        private TextView timeStamp;
        private ImageView authorPic;
        private ImageView uploadedImageView;
        private ImageView commentIcon;

        private ImageView recipeImageView;
        private TextView recipeTitle;
        private TextView recipeDescription;

        private TextView numLikes;
        private TextView numComments;
        private CheckBox likedOrNot;

        private boolean likedB4;

        private RatingBar recipeRating;

        private ConstraintLayout recipeLayout;
        private ConstraintLayout picLayout;

        private ImageButton menu;

        private ViewHolder(View v) {
            super(v);
                authorPic = v.findViewById(R.id.postAuthorPic);
                cardView = v.findViewById(R.id.postCardView);
                author = v.findViewById(R.id.postAuthor);
                body = v.findViewById(R.id.postBody);
                uploadedImageView = v.findViewById(R.id.userUploadedImageViewAdapter);
                recipeImageView = v.findViewById(R.id.postRecipeImageViewAdapter);
                recipeTitle = v.findViewById(R.id.postRecipeTitleAdapter);
                recipeDescription = v.findViewById(R.id.postRecipeDescriptionAdapter);
                timeStamp = v.findViewById(R.id.postTimeStamp);
                recipeRating = v.findViewById(R.id.recipeRatingFeed);
                numLikes = v.findViewById(R.id.postNumLike);
                numComments = v.findViewById(R.id.postNumComments);
                likedOrNot = v.findViewById(R.id.likeIcon);
                recipeLayout = v.findViewById(R.id.postRecipeImageViewAdapterLayout);
                picLayout = v.findViewById(R.id.userUploadedImageViewAdapterLayout);
                menu = v.findViewById(R.id.postMenu);
                commentIcon = v.findViewById(R.id.commentIcon);
            }
    }








    /**
     * Building and inflating the view within its parent
     * @param parent
     * @param viewType
     * @return
     */
    public MessengerFeedRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_recyler, parent, false);
        return new MessengerFeedRecyclerAdapter.ViewHolder(v);
    }

    /**
     * Getting the image with picasso and adding the on click functionality
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.numLikes.setVisibility(View.GONE);
        holder.likedOrNot.setVisibility(View.GONE);
        holder.numComments.setVisibility(View.GONE);
        holder.commentIcon.setVisibility(View.GONE);

        if(mDataset.get(position).timeStamp != null){
        if (mDataset.get(position).authorUID != null) {
            mDatabase.collection("users").document(mDataset.get(position).authorUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        holder.authorName = (String) task.getResult().get("displayName");
                        holder.authorPicURL = (String) task.getResult().get("imageURL");

                        if(task.getResult().get("displayName") != null){
                            holder.author.setText(holder.authorName);
                        }else {
                            holder.author.setText(R.string.deleted_user);
                        }
                        if (task.getResult().get("imageURL") != null) {
                            Glide.with(holder.authorPic.getContext())
                                    .load(task.getResult().get("imageURL"))
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(holder.authorPic);
                            holder.authorPic.setVisibility(View.VISIBLE);
                        }
                        if (task.getResult().get("imageURL") == null || task.getResult().get("imageURL") == "") {
                            Glide.with(holder.authorPic.getContext())
                                    .load(R.drawable.temp_settings_profile_image)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(holder.authorPic);
                            holder.authorPic.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
                        holder.author.setText("");
                    }
                }
            });
        } else {
            Log.e("FdRc", "User UID null");
            holder.author.setText("");
        }

        holder.timeStamp.setText(mDataset.get(position).timeStamp);
        holder.body.setText(mDataset.get(position).body);
        if (mDataset.get(position).uploadedImageURL != null) {
            holder.picLayout.setVisibility(View.VISIBLE);
            holder.uploadedImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(mDataset.get(position).uploadedImageURL).into(holder.uploadedImageView);
        }
        if (mDataset.get(position).uploadedImageURL == null) {
                holder.picLayout.setVisibility(View.GONE);
                holder.uploadedImageView.setVisibility(View.GONE);
            }
        if (mDataset.get(position).isRecipe) {
            holder.recipeLayout.setVisibility(View.VISIBLE);
            holder.recipeTitle.setVisibility(View.VISIBLE);
            holder.recipeDescription.setVisibility(View.VISIBLE);
            holder.recipeImageView.setVisibility(View.VISIBLE);
            holder.recipeTitle.setText(mDataset.get(position).recipeTitle);
            holder.recipeDescription.setText(mDataset.get(position).recipeDescription);
            Picasso.get().load(mDataset.get(position).recipeImageURL).into(holder.recipeImageView);
//            Glide.with(holder.recipeImageView.getContext())
//                    .load(mDataset.get(position).recipeImageURL)
//                    .apply(RequestOptions.centerCropTransform())
//                    .into(holder.recipeImageView);
            if (mDataset.get(position).isReview) {
                holder.recipeRating.setVisibility(View.VISIBLE);
                holder.recipeRating.setRating(mDataset.get(position).review);
            }
        }
            if (!mDataset.get(position).isRecipe) {
                holder.recipeLayout.setVisibility(View.GONE);
                holder.recipeTitle.setVisibility(View.GONE);
                holder.recipeDescription.setVisibility(View.GONE);
                holder.recipeImageView.setVisibility(View.GONE);
                if (!mDataset.get(position).isReview) {
                    holder.recipeRating.setVisibility(View.GONE);
                }
            }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only open up post page if it's got an image or recipie or review
                if(mDataset.get(position).isPic ||mDataset.get(position).isRecipe || mDataset.get(position).isReview ){
                if (mFeedFragment != null || mProfilePosts != null){
                    Bundle mBundle = new Bundle();
                    mBundle.putString("authorID", mDataset.get(position).authorUID);
                    mBundle.putString("docId", mDataset.get(position).postID);
                    mBundle.putString("authorName", holder.authorName);
                    mBundle.putString("authorPicURL", holder.authorPicURL);
                    mBundle.putBoolean("likedB4", holder.likedB4);
                    mBundle.putString("body", mDataset.get(position).body);
                    mBundle.putBoolean("isRecipe", mDataset.get(position).isRecipe);
                    if(mDataset.get(position).isRecipe){
                        mBundle.putString("recipeTitle", mDataset.get(position).recipeTitle);
                        mBundle.putString("recipeDescription", mDataset.get(position).recipeDescription);
                        mBundle.putString("recipeImageURL", mDataset.get(position).recipeImageURL);
                        mBundle.putBoolean("isReview", mDataset.get(position).isReview);
                        if(mDataset.get(position).isReview){
                            mBundle.putFloat("overallRating", mDataset.get(position).review);
                        }
                    }
                    if(mDataset.get(position).isPic) {
                        mBundle.putString("uploadedImageURL", mDataset.get(position).uploadedImageURL);
                    }
                    mBundle.putString("timestamp", mDataset.get(position).timeStamp);

                    if(mFeedFragment != null){
                        mFeedFragment.itemSelected(mDataset.get(holder.getAdapterPosition()).document, mBundle, holder.numLikes, holder.likedOrNot, holder.numComments, holder.cardView);
                    }else{
                        mProfilePosts.itemSelected(mDataset.get(holder.getAdapterPosition()).document, mBundle, holder.numLikes, holder.likedOrNot, holder.numComments, holder.cardView);
                    }

                }else{
                    Log.e("FEED RECYCLER ADAPTER", "Issue with no component in onBindViewHolder");
                }

            }}
        });

            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFeedFragment != null) {
                        if (mDataset.get(holder.getAdapterPosition()).document != null) {
                            mFeedFragment.menuSelected(mDataset.get(holder.getAdapterPosition()).document, holder.menu, position);
                            Log.e("COMMENT RECYCLER", "Add send to profile on click");
                        }
                    }else if(mProfilePosts != null){
                        if(mDataset.get(holder.getAdapterPosition()).document != null){
                            mProfilePosts.menuSelected(mDataset.get(holder.getAdapterPosition()).document, holder.menu);
                            Log.e("COMMENT RECYCLER", "Add send to profile on click");
                        }
                    }else{
                        Log.e("COMMENT RECYCLER", "Issue with no component in onBindViewHolder");
                    }

                }
            });

            holder.recipeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LoadingDialog loadingDialog = new LoadingDialog(mFeedFragment.getActivity());
                    loadingDialog.startLoadingDialog();

                    if(mDataset.get(position).recipeID != null) {
                        mDatabase.collection("recipes").document(mDataset.get(position).recipeID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (mFeedFragment != null) {
                                            mFeedFragment.recipeSelected(document, user.getUID());
                                        } else {
                                            mProfilePosts.recipeSelected(document, user.getUID());
                                        }

                                        loadingDialog.dismissDialog();
                                    }
                                }
                            }
                        });
                    }
                }
            });
    }
    }



    // Getting dataset size
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
