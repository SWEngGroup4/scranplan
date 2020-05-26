package com.group4sweng.scranplan.Social.Messenger;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.sentry.core.Sentry;

/***
 * Class to View active messages between people
 */
public class MessengerMenu extends AppCompatActivity {
    final String TAG = "Messenger MENU";


    UserInfoPrivate mUser;

    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColFollowers = mDatabase.collection("followers");
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;


    // Firebase user collection and storage references.
    CollectionReference mMessages;
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    StorageReference mStorageReference = mStorage.getReference();
    List<MessageMenuRecyclerAdapter.MessengerFeedPreviewData> data;
    Query query;


    FloatingActionButton mNewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Grabs serializable UserInfoPrivate data from main activity.
        mUser = (UserInfoPrivate) getIntent().getSerializableExtra("user");
        mMessages = mDatabase.collection("users").document(mUser.getUID()).collection("userInteractions");

        setContentView(R.layout.messenger);
        View view = findViewById(R.id.messageFrameLayout);
        addChats(view);

        //TODO open search for user to message
        mNewMessage = findViewById(R.id.newMessageButton);
        mNewMessage.setVisibility(View.VISIBLE);
        mNewMessage.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            openChat(null);
                mNewMessage.setVisibility(View.GONE);
            }})
        );
    }

    @Override
    protected void onStart(){
        super.onStart();
    }



    private void addChats(View view){
        final RecyclerView recyclerView = view.findViewById(R.id.messagesList);

        // Set out the layout of this horizontal view
        LinearLayoutManager rManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(rManager);
        //recyclerView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels));
        // Array to score downloaded data
        data = new ArrayList<>();
        final RecyclerView.Adapter rAdapter = new MessageMenuRecyclerAdapter(MessengerMenu.this, data, mUser, view);
        recyclerView.setAdapter(rAdapter);
        long numberOfChats = 5;
        query = mMessages.limit(numberOfChats);
        final boolean[] initalData = {true};
        // Ensure query exists and builds view with query
        if (query != null) {
            Log.e(TAG, "User is searching the following query: " + query.toString());
            // Query listener to add data to view
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "listen Failed", e);
                        Sentry.captureException(e);
                        return;
                    }
                    Log.e("Messanger", "UID = " + mUser.getUID());
                    Log.e("Messanger", "task success");
                    ArrayList<HashMap> posts = new ArrayList<>();
                    int size = queryDocumentSnapshots.size();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Log.e(TAG, "I have found a doc");
                        posts.add((HashMap) document.getData());
                    }
                    if (!initalData[0]) {
                        data.add(0, new MessageMenuRecyclerAdapter.MessengerFeedPreviewData(
                                posts.get(0)));
                    }
                    if (initalData[0]) {
                        for (int i = 0; i < posts.size(); i++) {
                            data.add(new MessageMenuRecyclerAdapter.MessengerFeedPreviewData(
                                    posts.get(i)));
                        }
                        initalData[0] = false;
                        if (queryDocumentSnapshots.size() != 0) {
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        } else {
                            isLastItemReached = true;
                        }
                    }

                    rAdapter.notifyDataSetChanged();

                    // Track users location to check if new data download is required
                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true;
                            }
                        }

                        // If scrolled to end then download new data and check if we are out of data
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();

                            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                isScrolling = false;
                                Query nextQuery = query.startAfter(lastVisible);
                                nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                        if (t.isSuccessful()) {
                                            ArrayList<HashMap> postsNext = new ArrayList<>();
                                            for (DocumentSnapshot d : t.getResult()) {
                                                Log.e("FEED", "I have found a doc");
                                                postsNext.add((HashMap) d.getData());
                                            }

                                            for (int i = 0; i < postsNext.size(); i++) {
                                                data.add(new MessageMenuRecyclerAdapter.MessengerFeedPreviewData(
                                                        postsNext.get(i)));
                                            }
                                            if (isLastItemReached) {
                                                // Add end here
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
            });

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                }
            });
        }
    }


    // Opens the chat to the specifed user
    void openChat(String messageRecipient){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new MessengerFeedFragment(mUser , messageRecipient);
        fragmentTransaction.replace(R.id.messageFrameLayout, fragment);
        fragmentTransaction.commit();
        mNewMessage.setVisibility(View.GONE);
    }







}
