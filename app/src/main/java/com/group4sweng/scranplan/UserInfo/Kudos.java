package com.group4sweng.scranplan.UserInfo;

import android.util.Log;

import com.group4sweng.scranplan.R;

import java.io.Serializable;

public class Kudos implements Serializable {
    private long kudos;
    private String chefLevel = "dishwasher";
    private int chefLevelIcon = R.drawable.ic_dishwasher;

    public Kudos(long initialKudos){
        kudos = initialKudos;
    }

    public void updateKudos(){
        int kudosScaled = (int) (Math.log(kudos) / Math.log(5));


        Log.e("Kudos", "New kudos scaled value is: " + kudosScaled);

        switch(kudosScaled) {
            case 0:
                chefLevel = "Dishwasher";
                chefLevelIcon = R.drawable.ic_dishwasher;
                break;
            case 1:
                chefLevel = "Kitchen Porter";
                chefLevelIcon = R.drawable.ic_fishandveg;
                break;
            case 2:
                chefLevel = "Commis Chef";
                chefLevelIcon = R.drawable.ic_grills;
                break;
            case 3:
                chefLevel = "Station Chef";
                chefLevelIcon = R.drawable.ic_buffet;
                break;
            case 4:
                chefLevel = "Sous Chef";
                chefLevelIcon = R.drawable.ic_chefhat;
                break;
            case 5:
                chefLevel = "Head Chef";
                chefLevelIcon = R.drawable.ic_headchef;
                break;
        }

        if(kudosScaled > 5){
            chefLevel = "Executive Chef";
            chefLevelIcon = R.drawable.ic_legend;
        }

    }

    public long getKudos(){ return kudos; }

    public String getChefLevel() { return chefLevel; }

    public int getChefLevelIconResource() { return chefLevelIcon; }

    public void setKudos(int kudos){
        if(kudos < 0){
            kudos = 0;
        }
        this.kudos = kudos;
    }

    public void incrementKudos(int incrementAmount) { kudos = kudos + incrementAmount; }

    public void decrementKudos(int decrementAmount) {
        kudos = kudos - decrementAmount;

        if(kudos < 0){
            kudos = 0;
        }
    }
}
