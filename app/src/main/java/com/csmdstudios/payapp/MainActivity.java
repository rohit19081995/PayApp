package com.csmdstudios.payapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {

    private static final String myPreferences = "login details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        Boolean loggedIn = sharedPreferences.getBoolean("LOGGED_IN", false);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(!loggedIn) {
            //load the login screen
            LoginFragment loginFragment = new LoginFragment();
            fragmentTransaction.add(R.id.plain_layout, loginFragment, "LOGIN_FRAGMENT");
        }
        else {
            //load the user's profile
        }

        fragmentTransaction.commit();
    }
}
