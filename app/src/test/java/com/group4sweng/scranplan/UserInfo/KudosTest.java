package com.group4sweng.scranplan.UserInfo;

import com.group4sweng.scranplan.R;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Kudos Class tests.
 * Author: JButler
 * (c) CoDev 2020
 */
public class KudosTest {

    private static long KUDOS_DEFAULT_VALUE = 1000; // Initial testing Kudos value.
    private int[] logBaseFiveKudos = {1, 5, 25, 125, 625, 3125, 15625}; // Minimum value in Kudos for each individual chef level. Based on a log base 5 scaling.

    //  Set our initial Kudos.
    @Before
    public void setDefaultKudos(){
        Kudos.setKudos(KUDOS_DEFAULT_VALUE);
    }

    //  Test when kudos is set it updates the class.
    @Test
    public void testKudosSet(){
        assertEquals(Kudos.getKudos(), KUDOS_DEFAULT_VALUE);
    }

    //  Test Kudos can be incremented and decremented by varying amounts.
    @Test
    public void testKudosIncrementDecrement(){

        Kudos.incrementKudos(10);
        assertEquals(Kudos.getKudos(), KUDOS_DEFAULT_VALUE + 10);

        Kudos.decrementKudos(20);
        assertEquals(Kudos.getKudos(), KUDOS_DEFAULT_VALUE - 10);
    }

    //  Test when Kudos is updated the correct chef description is displayed
    @Test
    public void testUpdateKudosChefDescription() {
        //  Each chef level in ascending order in terms of Kudos required to reach this level.
        String[] chefDescriptions = {"Dishwasher", "Kitchen Porter", "Commis Chef", "Station Chef", "Sous Chef", "Head Chef", "Executive Chef"};

        //  Cycle through each set of Kudos values for each chef tier.
        for(int i = 0; i < logBaseFiveKudos.length; i++){
            Kudos.setKudos(logBaseFiveKudos[i]);
            Kudos.updateKudos(); // Update Kudos icons and descriptions.
            assertEquals(Kudos.chefLevel, chefDescriptions[i]); // Check The chefs level is updated appropriately.
        }
    }

    //  Test when Kudos is updated the correct chef icon is displayed.
    @Test
    public void testUpdateKudosIcons() {
        //  Each chef icon in ascending order.
        int[] kudosIconResources = {R.drawable.ic_dishwasher, R.drawable.ic_fishandveg, R.drawable.ic_grills, R.drawable.ic_buffet, R.drawable.ic_chefhat, R.drawable.ic_headchef, R.drawable.ic_legend};

        for (int i = 0; i < kudosIconResources.length; i++) {
            Kudos.setKudos(logBaseFiveKudos[i]);
            Kudos.updateKudos();
            assertEquals(Kudos.chefLevelIcon, kudosIconResources[i]);
        }
    }
}
