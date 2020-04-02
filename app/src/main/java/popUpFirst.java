import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;
import com.group4sweng.scranplan.Home;
import com.group4sweng.scranplan.InitialUserCustomisation;
import com.group4sweng.scranplan.Login;
import com.group4sweng.scranplan.MainActivity;
import com.group4sweng.scranplan.R;

import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.HashMap;

public class popUpFirst extends AppCompatActivity {

    final String TAG = "FirstpopUp";

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    UserInfoPrivate userDetails;
    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private UserInfoPrivate mUser;
    Button mgoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the page to the initial filer page
        setContentView(R.layout.popup_firstscreen);

        Log.e(TAG, "I GET TO THIS POINT");

            initPageItems();
            getResources().getColor(R.color.colorBackground);
            initPageListeners();
            initFirebase();
        }






    private void initFirebase() {
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initPageItems() {
    mgoButton =  findViewById(R.id.goButton);
    }



    private void initPageListeners() {
        mgoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //skip button takes user directly to the main page
                Log.e(TAG, "Initial user returning to main activity");

                Intent returningIntent = new Intent();
                setResult(RESULT_OK, returningIntent);

                finish();
            }

        });

        Log.e(TAG,"SignIn Returning to main activity");

        Intent returningIntent = new Intent(popUpFirst.class, MainActivity.class);

        returningIntent.putExtra("user", mUser);

        mUser = null;
        startActivity(returningIntent);

        finish();

    }


    private void finishActivity() {

        Log.e(TAG, "Initial User Customisation returning to main activity");

        // User data returned to main menu
        Intent returningIntent = new Intent();
        returningIntent.putExtra("user", userDetails);
        startActivity(returningIntent);

        finish();
    }

    // Disable user from pressing back button on main activity page
    @Override
    public void onBackPressed() {
        //Do nothing
    }

}