package com.csmdstudios.payapp;

import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static boolean persistenceEnabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!persistenceEnabled) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            persistenceEnabled = true;
        }
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //start activity after 2000 ms
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            }
//        }, 2000);

        // Initialize Firebase Auth
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            // Animate this
            startActivity(new Intent(this, LoggedInActivity.class));
            finish();
        }

    }
}
