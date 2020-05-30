package com.group4sweng.scranplan.MealPlanner.Ingredients;

/** Custom ingredient class. Holds all variables required to display ingredients.
 *  Author: JButler, Date: 1/5/20
 * (c) CoDev 2020    **/
public class Ingredient {

    private String mName;
    private String mPortion; // Amount of each ingredient.
    private int mIcon = -1;

    //  Warning if ingredient isn't converted properly.
    private String mWarning = null;

    /** Basic ingredient creation.
     * @param mName - Ingredient name.
     * @param mPortion - Portion amount.
     */
    public Ingredient(String mName, String mPortion){
        this.mName = mName;
        this.mPortion = mPortion;
    }

    /** Mealplanner ingredient creation.
     * @param mName - Ingredient name.
     * @param mPortion - Portion amount.
     * @param mIcon - Ingredient Icon.
     * @param mWarning - Warning if ingredient isn't converted properly.
     */
    Ingredient(String mName, String mPortion, int mIcon, String mWarning){
        this(mName, mPortion);
        setWarning(mWarning);
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

    void setWarning(String mWarning){ this.mWarning = mWarning;}

    String getWarning(){
        return mWarning;
    }
}
