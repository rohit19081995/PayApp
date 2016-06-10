package com.csmdstudios.payapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static String getMyPreferences() {
        return myPreferences;
    }

    private static final String myPreferences = "login details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        Boolean loggedIn = sharedPreferences.getBoolean(LoginFragment.getUserLoggedIn(), false);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(!loggedIn) {
            //load the login screen
            LoginFragment loginFragment = new LoginFragment();
            fragmentTransaction.add(R.id.plain_layout, loginFragment, "LOGIN_FRAGMENT");
            fragmentTransaction.commit();
        }
        else {
            //Check if login details provided are still valid
            new AsyncLogin(this, getString(R.string.login_url), fragmentTransaction, null, AsyncLogin.ActionToPerform.CHECK).execute(sharedPreferences.getString("USERNAME", ""), sharedPreferences.getString("PASSWORD", ""));
            //load the user's profile
        }
    }
}
