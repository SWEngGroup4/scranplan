package com.group4sweng.scranplan;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URL;
import java.util.ArrayList;

public class ProfileSettings extends AppCompatActivity {

    enum filterType {
        ALLEGERNS,
        RELIGIOUS,
        DIETRY,
        HEALTH
    }

    private URL mImageURL;
    private Context mContext = this;

    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    StorageReference imageRef;

    // TAG for Profile Settings
    final String TAG = "ProfileSettings";

    UserInfoPrivate mUserProfile;

    ImageView mProfileImage;
    TextView mUsername;
    TextView mAboutMe;
    TextView mNumRecipes;
    ArrayList<View> mFilters;

    CheckBox mAllergy_nuts;
    CheckBox mAllergy_milk;
    CheckBox mAllergy_eggs;
    CheckBox mAllergy_shellfish;
    CheckBox mAllergy_soy;
    CheckBox mAllergy_wheat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        initPageItems();
    }

    protected void onStart(){
        super.onStart();

        mUserProfile = (UserInfoPrivate) getIntent().getSerializableExtra("user");
        if(mUserProfile != null){
            loadProfileData();
        }

        Log.i(TAG, "Context is: " + mContext.getClass().getName());
    }

    // TODO Make proper image download class.
    /*private void downloadImage(String UID){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getInstance().getReference()
                .child("user_profile_images")
                .child("megaMan.png");
        imageRef.getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        mProfileImage.setImageBitmap(bitmap);
                    }
                });
    }*/


    private void initPageItems(){
        mUsername = findViewById(R.id.settings_input_username);
        mAboutMe = findViewById(R.id.settings_input_about_me);
        mNumRecipes = findViewById(R.id.public_profile_recipes);

        mFilters = new ArrayList<>();
        findViewById(R.id.settings_privacy).addChildrenForAccessibility(mFilters);


    }

    private void loadProfileData(){
        mUsername.setText(mUserProfile.getDisplayName());
        mAboutMe.setText(mUserProfile.getAbout());
        mNumRecipes.setText(String.valueOf(mUserProfile.getNumRecipes()));

        //setFilters(filterType.ALLEGERNS);
    }

    private void setFilters(filterType type){
        switch(type){
            case ALLEGERNS:
                //TODO don't have a wheat allergy check yet.
                mAllergy_eggs.setChecked(mUserProfile.getPreferences().isAllergy_eggs());
                mAllergy_milk.setChecked(mUserProfile.getPreferences().isAllergy_milk());
                mAllergy_nuts.setChecked(mUserProfile.getPreferences().isAllergy_nuts());
                mAllergy_shellfish.setChecked(mUserProfile.getPreferences().isAllergy_shellfish());
                mAllergy_soy.setChecked(mUserProfile.getPreferences().isAllergy_soya());
            case RELIGIOUS:
                //TODO
            case DIETRY:
                //TODO
            case HEALTH:
                //TODO
        }
    }
}
