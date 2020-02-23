package com.group4sweng.scranplan;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URL;

public class ProfileSettings extends AppCompatActivity {

    private URL mImageURL;


    ImageView mProfileImage;
    TextView mUsername;
    TextView mAboutMe;
    TextView mNumRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        initPageItems();


    }

    private void initPageItems(){
        mProfileImage = findViewById(R.id.settings_profile_image);
    }
}
