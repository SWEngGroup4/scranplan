package com.group4sweng.scranplan.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//  A class for including various static helper functions that check the overall status of the device being used.
public class CheckAndroidServices {

    /** Check that we have a valid wifi/mobile data connection.
     * @param context - The context of the Activity screen being sent from.
     * @return - True if a connection is established or False if not.
     */
    public static boolean checkNetworkConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        boolean connectionEstablished = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return connectionEstablished;
    }
}
