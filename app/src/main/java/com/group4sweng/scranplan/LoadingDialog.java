package com.group4sweng.scranplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class LoadingDialog {

    Activity activity;
    //Fragment fragment;
    AlertDialog dialog;
    ImageView logo;

    public LoadingDialog(Activity thisActivity){
        activity = thisActivity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //AlertDialog.Builder builder = new AlertDialog.Builder(fragment);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.loading_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        logo = view.findViewById(R.id.loadingLogo);
        MainActivity.rotateImageClockwise(logo);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
