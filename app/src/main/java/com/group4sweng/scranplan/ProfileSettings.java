package com.group4sweng.scranplan;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URL;

public class ProfileSettings extends AppCompatActivity {

    private URL mImageURL;
    private Context mContext = this;

    // TAG for Profile Settings
    final String TAG = "ProfileSettings";

    ImageView mProfileImage;
    TextView mUsername;
    TextView mAboutMe;
    TextView mNumRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        Log.i(TAG, "Context is: " + mContext.getClass().getName());
        //com.group4sweng.scranplan.ProfileSettings
    }

    private void initPageItems(){
        mProfileImage = findViewById(R.id.settings_profile_image);
    }
}
