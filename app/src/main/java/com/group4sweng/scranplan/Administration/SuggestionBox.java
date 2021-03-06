package com.group4sweng.scranplan.Administration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SuggestionBox {

    Context activity;
    private String userID;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private HashMap<String, Object> map;

    public SuggestionBox(Context thisActivity, String user){
        activity = thisActivity;
        this.userID = user;
    }

    public void startSuggestionDialog(){
        AlertDialog.Builder report = new AlertDialog.Builder(activity);

        //Alert dialog box appears on screen and allows the user to type in their issue
        final EditText edittext = new EditText(activity);
        report.setMessage("What could be done to improve your experience?");
        report.setTitle("Suggestion Box");

        report.setView(edittext); //users can type in their issue

        //Create the submit button
        report.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        //Create the cancel button
        report.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });


        AlertDialog alertDialog = report.create();
        alertDialog.show();

        //Make sure the user cannot submit a null string
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usersSuggestion = edittext.getText().toString(); // gets issue as string to be passed into map

                if (usersSuggestion.length() > 3) {
                    map = new HashMap<>();
                    map.put("suggestion", usersSuggestion);
                    map.put("userID", userID);

                    //takes the map and puts it onto the firebase
                    CollectionReference reportRef = mDatabase.collection("suggestions");
                    DocumentReference documentReference = reportRef.document();
                    documentReference.set(map);

                    //Toast to thank the user for their feedback
                    Toast.makeText(activity, "Thank you for your feedback",
                            Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(activity, "Please leave some feedback, thank you", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

}
