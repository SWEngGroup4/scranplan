package com.group4sweng.scranplan.UserInfo;

import android.app.Application;

import java.io.Serializable;

public class UserInfoPublic extends Application implements Serializable{

    final static String TAG = "UserInfo";

    private static final String CONTEXT_PROFILE_SETTINGS = "com.group4sweng.scranplan.ProfileSettings";
    private static final String CONTEXT_LOGIN_SETTINGS = "com.group4sweng.scranplan.Login";
    private static final String CONTEXT_MAIN_ACTIVITY_SETTINGS = "com.group4sweng.scranplan.MainActivity";

    //  User information
    private String mUID;
    private String mDisplayName;
    private String mImageURL;
    private String mAbout;
    private double mChefRating;
    private long mNumRecipes;
    private Preferences mPreferences;

    
}