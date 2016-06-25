package com.csmdstudios.payapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoggedInActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "LoggedInActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        Button logoutButton = (Button) findViewById(R.id.logout_button);
        Button dataButton = (Button) findViewById(R.id.data_button);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        TextView name = (TextView) findViewById(R.id.name);
        TextView email = (TextView) findViewById(R.id.emailid);
        TextView dunno = (TextView) findViewById(R.id.dunno);

        fab.setOnClickListener(this);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        dunno.setText(user.getUid());

        if (logoutButton != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });
        }
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "setValue thing");
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(user.getUid()+"/details");
                mRef.child("email").setValue("bullschlak");
                Log.d(TAG, "setValue thing mail");
            }
        });
    }

    public void logout() {

        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(LoggedInActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Log.d(TAG, "FAB pressed");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                new AddTransactionFragment().show(fragmentTransaction, TAG);
//                Log.d(TAG, UserAutoCompleteAdapter.findUsers2(this, "Roh").toString()+" user");
                break;
        }
    }

    public void toggleVisibility(View... views) {
        for (View view : views) {
            boolean isVisible = view.getVisibility() == View.VISIBLE;
            view.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
