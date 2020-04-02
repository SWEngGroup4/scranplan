package com.group4sweng.scranplan.UserInfo;

import com.group4sweng.scranplan.R;

public class Kudos {
    private static long kudos;
    public static String chefLevel;
    public static int chefLevelIcon;

    public static void updateKudos(){
        if(kudos < 5){
            chefLevel = "Dishwasher";
            chefLevelIcon = R.drawable.ic_dishwasher;
            return;
        }

        int kudosScaled = (int) (Math.log(kudos) / Math.log(5));

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

    public static long getKudos() { return Kudos.kudos; }

    public static void setKudos(long kudos){
        if(kudos < 0){
            kudos = 0;
        }
        Kudos.kudos = kudos;
    }

    public static void incrementKudos(long incrementAmount) { kudos = kudos + incrementAmount; }

    public static void decrementKudos(long decrementAmount) {
        kudos = kudos - decrementAmount;

        if(kudos < 0){
            kudos = 0;
        }
    }
}
