package com.group4sweng.scranplan;

import android.widget.ImageView;

import java.io.Serializable;

public abstract class UserBase implements Serializable {


    private String UID;
    private String displayName;
    private String imageURL;
    private double chefRating;
    private long numRecipes;
    private ImageView[] badges;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public double getChefRating() {
        return chefRating;
    }

    public void setChefRating(int chefRating) {
        this.chefRating = chefRating;
    }

    public long getNumRecipes() {
        return numRecipes;
    }

    public void setNumRecipes(int numRecipes) {
        this.numRecipes = numRecipes;
    }

}
