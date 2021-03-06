package com.group4sweng.scranplan.RecipeInfo;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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

import java.sql.Time;
import java.util.HashMap;
import java.util.List;

public class recipeReviewRecyclerAdapter extends RecyclerView.Adapter <recipeReviewRecyclerAdapter.ViewHolder> {

    private List <reviewData> mData;
    private RecipeReviewFragment mRecipeReview;
    private UserInfoPrivate user;
    private String mRecipeID;

    /**  Firebase **/
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    public static class reviewData{

        private String textBody;
        private String userID;
        private String postID;
        private String rating;
        private String recipePic;
        private String timeStamp;
        private HashMap<String, Object> document;

        public reviewData(HashMap<String, Object> doc){

            this.document = doc;
            this.textBody = document.get("body").toString();
            this.userID = document.get("author").toString();
            this.rating = document.get("overallRating").toString();
            if(document.get("userImage") != null){
                this.recipePic = document.get("userImage").toString();
            }
            Timestamp time = (Timestamp) document.get("timeStamp");
            this.timeStamp = time.toDate().toString();
            this.postID = document.get("docID").toString();


        }

    }

    @Override
    public recipeReviewRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_recycler, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull recipeReviewRecyclerAdapter.ViewHolder holder, int position) {

        //Checks to see if the author of the review still has an active account before displaying name
        if(mData.get(position).userID != null) {
            mDatabase.collection("users").document(mData.get(position).userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        holder.author.setText((String) task.getResult().get("displayName"));
                        holder.authorPicURL = (String)task.getResult().get("imageURL");
                        if(task.getResult().get("imageURL") != null){
                            Glide.with(holder.authorPic.getContext())
                                    .load(task.getResult().get("imageURL"))
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(holder.authorPic);
                            holder.authorPic.setVisibility(View.VISIBLE);
                        }

                    }
                }
            });
        }else{
            Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
            holder.author.setText("past user"); //Replaces authors name in event of deletion
        }

        //Checks to see if the text of the review is not null and displays it
        if(mData.get(position).textBody != null) {
            holder.postBody.setText(mData.get(position).textBody);
        }
        else{
            Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
            holder.postBody.setText("deleted");
        }

        //Checks to see if there is a image url present. If one is present then the picture is displayed.
        // If the image url is null then the imageView is set to gone
        if(mData.get(position).recipePic != null) {

            holder.recipeImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(mData.get(position).recipePic).into(holder.recipeImageView);
        }
        else{
            holder.recipeImageView.setVisibility(View.GONE);
            Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
        }

        //Displays the rating and the timestamp associated to the review
        holder.rating.setRating(Float.parseFloat(mData.get(position).rating));
        holder.timeStamp.setText(mData.get(position).timeStamp);


        //Functionality for the like system on posts
        mDatabase.collection("likes").document(mData.get(position).postID + "-" + user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().exists()){
                        holder.likedB4 = true;
                        holder.likedOrNot.setChecked((boolean)task.getResult().get("liked"));
                        mDatabase.collection("posts").document(mData.get(position).postID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> nextTask) {
                                DocumentSnapshot d = nextTask.getResult();
                                holder.numLikes.setText(d.get("likes").toString());
                            }
                        });
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

        //calls the menu for the reviews once clicked. From here, if it is the users post they can delete
        holder.mReviewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecipeReview != null) {
                    if (mData.get(holder.getAdapterPosition()).document != null) {
                        mRecipeReview.menuSelected(mData.get(holder.getAdapterPosition()).document, holder.mReviewMenu);
                        Log.e("COMMENT RECYCLER", "Add send to profile on click");
                    }
                }else{
                    Log.e("COMMENT RECYCLER", "Issue with no component in onBindViewHolder");
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public String authorPicURL;
        private ImageView authorPic;
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
        private ImageButton mReviewMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            authorPic = itemView.findViewById(R.id.postAuthorPic);
            postBody = itemView.findViewById(R.id.postBody);
            author = itemView.findViewById(R.id.postAuthor);
            timeStamp = itemView.findViewById(R.id.postTimeStamp);
            rating = itemView.findViewById(R.id.ratingBar2);
            likedOrNot = itemView.findViewById(R.id.likeIcon);
            numLikes = itemView.findViewById(R.id.postNumLike);
            cardView = itemView.findViewById(R.id.postCardView);
            recipeImageView = itemView.findViewById(R.id.postRecipeImageViewAdapter);
            mReviewMenu = itemView.findViewById(R.id.postMenu);

        }
    }

    public recipeReviewRecyclerAdapter(RecipeReviewFragment reviewFragment, List<reviewData> data, UserInfoPrivate user, String mRecipeID){

        mRecipeReview = reviewFragment;
        this.mRecipeID = mRecipeID;
        mData = data;
        this.user = user;

    }
}
