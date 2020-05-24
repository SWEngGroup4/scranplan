package com.group4sweng.scranplan.Social.Messenger;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4sweng.scranplan.R;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

/***
 * Class to View active messages between people
 */
public class MessengerMenu extends AppCompatActivity {

    UserInfoPrivate mUser;

    // Database objects for accessing recipes
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private CollectionReference mColFollowers = mDatabase.collection("followers");

    // Firebase user collection and storage references.
    CollectionReference mMessages = mDatabase.collection("posts");
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    StorageReference mStorageReference = mStorage.getReference();

    FloatingActionButton mNewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messenger);

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
        //Grabs serializable UserInfoPrivate data from main activity.
        mUser = (UserInfoPrivate) getIntent().getSerializableExtra("user");
    }

    // Opens the chat to the specifed user
    private void openChat(UserInfoPrivate messageRecipient){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new MessengerFeedFragment(mUser , messageRecipient);
        fragmentTransaction.replace(R.id.messageFrameLayout, fragment);
        fragmentTransaction.commit();
    }







}
