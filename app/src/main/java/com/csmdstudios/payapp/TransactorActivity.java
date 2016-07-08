package com.csmdstudios.payapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactorActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseUser mUser;
    public String currency;
    private String name;
    private String owedInfo;
    private String picURL;
    private double owed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactor);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        Intent intent = getIntent();
        ArrayList<String> transactor = intent.getStringArrayListExtra(LoggedInActivity.TRANSACTOR_EXTRA);
        name = transactor.get(0);
        owedInfo = transactor.get(1);
        owed = intent.getDoubleExtra(LoggedInActivity.OWED_EXTRA, 0);
        Log.d("OWED", Double.toString(owed));
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        CircleImageView transactorImage = (CircleImageView) myToolbar.findViewById(R.id.transactor_image);
        picURL = transactor.get(2);
        if (picURL != null) {
            Glide.with(this).load(picURL).into(transactorImage);
        }
        ab.setDisplayShowTitleEnabled(false);
        TextView transactorName = (TextView) myToolbar.findViewById(R.id.transactor_textview);
        transactorName.setText(name);
        ab.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.transaction_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // TODO: change currency in preferences
        currency = "\u20B9";

        // Firebase auth listener
        new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser == null) {
                    finish();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                //logout();
                return true;
            case R.id.settings:
                //showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
