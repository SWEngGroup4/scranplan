package com.group4sweng.scranplan.Administration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.R;

import java.util.HashMap;


public class ContentReporting {

    Activity activity;
    private HashMap<String, Object> document;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private String location;

    private EditText editText;
    public TextView title;
    public TextView message;

    public ContentReporting(Activity thisActivity, HashMap<String, Object> map, String location){
        activity = thisActivity;
        this.document = map;
        this.location = location;
    }

    public void startReportingDialog(){
        AlertDialog.Builder report = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.content_reporting, null);

        editText = view.findViewById(R.id.editText);
        title = view.findViewById(R.id.reporting_title);
        message = view.findViewById(R.id.reporting_message);

        report.setView(view);
        report.setCancelable(false);

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
                String usersContentReport = editText.getText().toString(); // gets issue as string to be passed into map

                if(usersContentReport.length() > 3){
                    document.put("report", usersContentReport);

                    //takes the map and puts it onto the firebase
                    CollectionReference reportRef = mDatabase.collection(location);
                    DocumentReference documentReference = reportRef.document();
                    documentReference.set(document);

                    //Toast to thank the user for their feedback
                    Toast.makeText(activity, "Thank you for your feedback",
                            Toast.LENGTH_SHORT).show();

                    alertDialog.dismiss();
                }else{
                    Toast.makeText(activity, "Please explain the issue, thank you", Toast.LENGTH_SHORT).show();
                }


            }

        });

    }

}
