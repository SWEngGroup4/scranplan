package com.group4sweng.scranplan.MealPlanner.Ingredients;

public class Ingredient {

    private String mName;
    private String mPortion;
    private int mIcon = -1;

    private String mWarning = null;
    private boolean SHOW_PORTION_CONVERT_WARNING = false;

    public Ingredient(String mName, String mPortion){
        this.mName = mName;
        this.mPortion = mPortion;
    }

    public Ingredient(String mName, String mPortion, String mWarning){
        this(mName, mPortion);
        setWarning(mWarning);
    }

    public Ingredient(String mName, String mPortion, int mIcon, String mWarning){
        this(mName, mPortion, mWarning);
        this.mIcon = mIcon;
    }

    public void setName(String mName){
        this.mName = mName;
    }

    public String getName() {
        return mName;
    }

    public void setPortion(String mPortion) { this.mPortion = mPortion; }

    public String getPortion() {
        return mPortion;
    }

    public void setIcon(int mIcon) {
        this.mIcon = mIcon;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setWarning(String mWarning){ this.mWarning = mWarning;}

    public String getWarning(){
        return mWarning;
    }
}
