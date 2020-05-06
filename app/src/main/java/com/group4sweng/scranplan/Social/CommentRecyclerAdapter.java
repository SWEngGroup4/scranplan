package com.group4sweng.scranplan.Social;

        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.cardview.widget.CardView;
        import androidx.recyclerview.widget.RecyclerView;

        import com.bumptech.glide.Glide;
        import com.bumptech.glide.request.RequestOptions;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.Timestamp;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FieldValue;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.group4sweng.scranplan.Presentation.Presentation;
        import com.group4sweng.scranplan.R;
        import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

        import java.util.HashMap;
        import java.util.List;
/**
 *  Class holding the recycler adapter for the search functionality, each card will represent the view
 *  of one recipe. All recipe info is stored in this card.
 *  Creating a card view that hold the picture and the document which, the picture will be displayed
 *  in a button and the button will pass the document though for the recipe to be read
 */
public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder> {

    /**  Firebase **/
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    // Variables for database and fragment to be displayed in
    private UserInfoPrivate user;
    private Presentation presentation;
    private PostPage postPage;
    private List<CommentRecyclerAdapter.CommentData> mDataset;
    private String currentSlide;
    private String postID;

    public static class CommentData {
        private String authorID;
        private String likes;
        private String comment;
        private DocumentSnapshot document;
        private String timestamp;
        private String commentID;

        /**
         * The holder for the card with variables required
         */
        public CommentData(DocumentSnapshot doc, String authorID, String comment, Timestamp timestamp, String likes, String commentID) {
            this.document = doc;
            this.authorID = authorID;
            this.comment = comment;
            this.likes = likes;
            if(timestamp != null){
                this.timestamp = timestamp.toDate().toString();
            }
            this.commentID = commentID;



        }
    }

    /**
     * Building the card and image view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView author;
        private TextView comment;
        private TextView timestamp;
        private ImageView profilePic;
        private TextView numLikes;
        private CheckBox likedOrNot;
        private boolean likedB4;
        private LinearLayout bottomCommentBar;
        private ImageButton menu;


        private ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.commentListCardView);
            author = v.findViewById(R.id.commentName);
            comment = v.findViewById(R.id.commentMessage);
            timestamp = v.findViewById(R.id.commentTimeStamp);
            profilePic = v.findViewById(R.id.commentAuthorPic);
            numLikes = v.findViewById(R.id.commentNumLike);
            likedOrNot = v.findViewById(R.id.commentLikeIcon);
            bottomCommentBar = v.findViewById(R.id.bottomCommentBar);
            menu = v.findViewById(R.id.commentMenu);
        }
    }

    /**
     * Constructor to add all variables
     * @param presentation
     * @param dataset
     */
    public CommentRecyclerAdapter (Presentation presentation, List<CommentRecyclerAdapter.CommentData> dataset, String currentSlide, UserInfoPrivate user, String postID) {
        this.presentation = presentation;
        mDataset = dataset;
        this.currentSlide = currentSlide;
        this.user = user;
        this.postID = postID;
    }

    /**
     * Constructor to add all variables
     * @param postPage
     * @param dataset
     */
    public CommentRecyclerAdapter (PostPage postPage, List<CommentRecyclerAdapter.CommentData> dataset, UserInfoPrivate user, String postID) {
        this.postPage = postPage;
        mDataset = dataset;
        this.user = user;
        this.postID = postID;
    }

    /**
     * Building and inflating the view within its parent
     * @param parent
     * @param viewType
     * @return
     */
    public CommentRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment, parent, false);
        return new CommentRecyclerAdapter.ViewHolder(v);
    }

    /**
     * Getting the image with picasso and adding the on click functionality
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final CommentRecyclerAdapter.ViewHolder holder, int position) {
        holder.comment.setText(mDataset.get(position).comment);
        if(mDataset.get(position).likes != null){
            holder.menu.setVisibility(View.VISIBLE);
            holder.timestamp.setText(mDataset.get(position).timestamp);
            holder.numLikes.setText(mDataset.get(position).likes);


            mDatabase.collection("likes").document(postID + "-" + mDataset.get(position).commentID + "-" + user.getUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                    Log.e("CmRC", "liked post");
                                    if(holder.likedB4){
                                        mDatabase.collection("likes").document(postID + "-" + mDataset.get(position).commentID + "-" + user.getUID()).update("liked", true);
                                    }else{
                                        holder.likedB4 = true;
                                        HashMap<String, Object> likePost = new HashMap<>();
                                        likePost.put("liked", true);
                                        likePost.put("user", user.getUID());
                                        likePost.put("post", postID);
                                        likePost.put("comment", mDataset.get(position).commentID);
                                        mDatabase.collection("likes").document(postID + "-" + mDataset.get(position).commentID + "-" + user.getUID()).set(likePost);
                                    }
                                    mDataset.get(position).document.getReference().update("likes", FieldValue.increment(+1));
                                    int newLiked = Integer.parseInt((String) holder.numLikes.getText())+1;
                                    String test = String.valueOf(newLiked);
                                    holder.numLikes.setText(test);

                                } else {
                                    Log.e("CmRC", "unliked post");
                                    mDatabase.collection("likes").document(postID + "-" + mDataset.get(position).commentID + "-" + user.getUID()).update("liked", false);
                                    mDataset.get(position).document.getReference().update("likes", FieldValue.increment(-1));
                                    int newLiked = Integer.parseInt((String) holder.numLikes.getText())-1;
                                    String test = String.valueOf(newLiked);
                                    holder.numLikes.setText(test);
                                }
                            }
                        });
                    }else {
                        Log.e("CmRc", "User details retrieval : Unable to retrieve user document in Firestore ");
                    }
                }
            });

            holder.bottomCommentBar.setVisibility(View.VISIBLE);

        }
        if(mDataset.get(position).authorID != null){
            mDatabase.collection("users").document(mDataset.get(position).authorID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if((String)task.getResult().get("displayName") != null){
                            holder.author.setText((String)task.getResult().get("displayName"));
                            holder.author.setText((String)task.getResult().get("displayName"));
                            if(task.getResult().get("imageURL") != null || !((String) task.getResult().get("imageURL")).equals("")){
                                Glide.with(holder.profilePic.getContext())
                                        .load(task.getResult().get("imageURL"))
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(holder.profilePic);
                                holder.profilePic.setVisibility(View.VISIBLE);
                            }
                        }else{
                            holder.author.setText("Past user");
                        }
                    }else {
                        Log.e("FdRc", "User details retrieval : Unable to retrieve user document in Firestore ");
                        holder.author.setText("Past user");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    holder.author.setText("Past user");
                }
            });
        }else {
            Log.e("FdRc", "User UID null");
            holder.author.setVisibility(View.GONE);
        }
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presentation != null) {
                    if (mDataset.get(holder.getAdapterPosition()).document != null) {
                        presentation.commentSelected(mDataset.get(holder.getAdapterPosition()).document, holder.cardView, currentSlide);
                        Log.e("COMMENT RECYCLER", "Add send to profile on click");
                    }
                }else if(postPage != null){
                    if(mDataset.get(holder.getAdapterPosition()).document != null){
                        postPage.commentSelected(mDataset.get(holder.getAdapterPosition()).document, holder.cardView);
                        Log.e("COMMENT RECYCLER", "Add send to profile on click");
                    }
                }else{
                    Log.e("COMMENT RECYCLER", "Issue with no component in onBindViewHolder");
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