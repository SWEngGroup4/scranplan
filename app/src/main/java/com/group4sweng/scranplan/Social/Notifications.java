package com.group4sweng.scranplan.Social;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for displaying notifications.
 * Author(s): LNewman
 * (c) CoDev 2020
 *
 * Page displaying a full post with max sized pictures and an area for users to comment and for all comments to be displayed in an infinite scroll
 */
public class Notifications extends AppCompatDialogFragment {

    final static String TAG = "Notification"; //Log tag.
    int numLoad = 15;

    /**  Firebase **/
    FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    public Notifications(UserInfoPrivate user){
        this.mUser = user;
    }
    List<NotificationRecyclerAdapter.NotificationData> data;


    /**  Comment additions **/
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private DocumentSnapshot lastVisible;
    private RecyclerView notificationList;
    private Query query;
    com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;


    // Auto-generated super method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View layout = inflater.inflate(R.layout.fragment_notifications, null);
        notificationList = layout.findViewById(R.id.notificationList);

        builder.setView(layout);



        addFirestoreComments();

        return layout;
    }

    /**
     * Method that scales the pop up dialog box to fill the majority of the screen
     */
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }


    /**
     * Function to set up a new recycler view that takes all comments and downloads them from the server
     * when there is more than 5 comments, the data is downloaded 5 items at a time and loads new comments
     * as the user scrolls down through the comments
     */
    private void addFirestoreComments(){


        data = new ArrayList<>();

        // Creating a list of the data and building all variables to add to recycler view
        final RecyclerView recyclerView = notificationList;
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(rManager);
        final RecyclerView.Adapter rAdapter = new NotificationRecyclerAdapter(data, mUser);
        recyclerView.setAdapter(rAdapter);
        query = mDatabase.collection("users").document(mUser.getUID()).collection("notifications").limit(numLoad).orderBy("timestamp");

        // Once the data has been returned, dataset populated and components build
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // For each document a new recipe preview view is generated
                    if(task.getResult() != null)
                    {
                        for (DocumentSnapshot document : task.getResult()) {
                            data.add(new NotificationRecyclerAdapter.NotificationData(
                                    (boolean) document.get("ifRequested"),
                                    document,
                                    document.get("senderID").toString(),
                                    document.get("body").toString(),
                                    (Timestamp) document.getTimestamp("timestamp"),
                                    (String) document.get("relatedPostID")
                            ));
                        }
                        rAdapter.notifyDataSetChanged();
                        // Set the last document as last user can see
                        if(task.getResult().size() != 0){
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }else{
                            // If no data returned, user notified
                            isLastItemReached = true;
                            data.add(new NotificationRecyclerAdapter.NotificationData(
                                    false,
                                    null,
                                    null,
                                    "No notifications yet!",
                                    null,
                                    null
                            ));
                        }
                        // check if user has scrolled through the view
                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }
                            // If user is scrolling and has reached the end, more data is loaded
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                // Checking if user is at the end
                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();
                                // If found to have reached end, more data is requested from the server in the same manner
                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;
                                    Query nextQuery = query.startAfter(lastVisible);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    data.add(new NotificationRecyclerAdapter.NotificationData(
                                                            (boolean) d.get("ifRequested"),
                                                            d,
                                                            d.get("senderID").toString(),
                                                            d.get("body").toString(),
                                                            (Timestamp) d.getTimestamp("timestamp"),
                                                            d.get("relatedPostID").toString()
                                                    ));
                                                }
                                                if(isLastItemReached){
                                                    // Last comment reached
                                                }
                                                rAdapter.notifyDataSetChanged();
                                                if (t.getResult().size() != 0) {
                                                    lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                }

                                                if (t.getResult().size() < 5) {
                                                    isLastItemReached = true;
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        };
                        recyclerView.addOnScrollListener(onScrollListener);

                    }
                }
            }
        });
    }
    

}
