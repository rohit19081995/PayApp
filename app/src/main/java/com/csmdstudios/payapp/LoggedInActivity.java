package com.csmdstudios.payapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoggedInActivity extends AppCompatActivity {

    private static final String TAG = "LoggedInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        Button logoutButton = (Button) findViewById(R.id.logout_button);
        Button dataButton = (Button) findViewById(R.id.data_button);
        TextView name = (TextView) findViewById(R.id.name);
        TextView email = (TextView) findViewById(R.id.emailid);
        TextView dunno = (TextView) findViewById(R.id.dunno);

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
}
