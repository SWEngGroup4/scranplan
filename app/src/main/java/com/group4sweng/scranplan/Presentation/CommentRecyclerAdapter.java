package com.group4sweng.scranplan.Presentation;

        import android.content.Context;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.PopupMenu;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.cardview.widget.CardView;
        import androidx.recyclerview.widget.RecyclerView;

        import com.google.firebase.firestore.DocumentSnapshot;
        import com.group4sweng.scranplan.R;

        import java.util.List;
/**
 *  Class holding the recycler adapter for the search functionality, each card will represent the view
 *  of one recipe. All recipe info is stored in this card.
 *  Creating a card view that hold the picture and the document which, the picture will be displayed
 *  in a button and the button will pass the document though for the recipe to be read
 */
public class CommentRecyclerAdapter extends RecyclerView.Adapter<com.group4sweng.scranplan.Presentation.CommentRecyclerAdapter.ViewHolder> {

    // Variables for database and fragment to be displayed in
    private Presentation presentation;
    private List<com.group4sweng.scranplan.Presentation.CommentRecyclerAdapter.CommentData> mDataset;
    private  String currentSlide;

    public static class CommentData {
        private String docID;
        private String author;
        private String comment;
        private DocumentSnapshot document;

        /**
         * The holder for the card with variables required
         */
        public CommentData(DocumentSnapshot doc, String authorID, String author, String comment) {
            this.document = doc;
            this.docID = authorID;
            this.author = author;
            this.comment = comment;

        }
    }

    /**
     * Building the card and image view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView author;
        private TextView comment;


        private ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.commentListCardView);
            author = v.findViewById(R.id.commentName);
            comment = v.findViewById(R.id.commentMessage);
        }
    }

    /**
     * Constructor to add all variables
     * @param presentation
     * @param dataset
     */
    public CommentRecyclerAdapter (Presentation presentation, List<com.group4sweng.scranplan.Presentation.CommentRecyclerAdapter.CommentData> dataset, String currentSlide) {
        this.presentation = presentation;
        mDataset = dataset;
        this.currentSlide = currentSlide;
    }

    /**
     * Building and inflating the view within its parent
     * @param parent
     * @param viewType
     * @return
     */
    public com.group4sweng.scranplan.Presentation.CommentRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment, parent, false);
        return new com.group4sweng.scranplan.Presentation.CommentRecyclerAdapter.ViewHolder(v);
    }

    /**
     * Getting the image with picasso and adding the on click functionality
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final com.group4sweng.scranplan.Presentation.CommentRecyclerAdapter.ViewHolder holder, int position) {
        holder.author.setText(mDataset.get(position).author);
        holder.comment.setText(mDataset.get(position).comment);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(presentation != null){
                   presentation.commentSelected(mDataset.get(holder.getAdapterPosition()).document, holder.cardView, currentSlide);
                    Log.e("COMMENT RECYCLER", "Add send to profile on click");
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