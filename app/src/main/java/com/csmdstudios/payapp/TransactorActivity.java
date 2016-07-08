package com.csmdstudios.payapp;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactorActivity extends AppCompatActivity {

    private static final String TAG = "TransactorActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseUser mUser;
    public String currency;
    private String name;
    private String owedInfo;
    private String picURL;
    private double owed;
    private String UID;

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
        UID = transactor.get(3);
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

//        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Transaction, TransactionViewHolder>(
//                Transaction.class,
//                R.layout.transaction_layout,
//                TransactionViewHolder.class,
//                FirebaseDatabase.getInstance().getReference(mUser.getUid() + "/transactors")) {
//        };
//        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "FAB pressed");
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AddTransactionFragment.USER_SELECTED_STATE, true);
                    bundle.putDouble(AddTransactionFragment.USER_OWED, owed);
                    String[] userString = {UID, name, picURL};
                    bundle.putStringArray(AddTransactionFragment.USER_SELECTED, userString);
                    DialogFragment fragment = AddTransactionFragment.newInstance(bundle);
                    fragment.show(fragmentTransaction, TAG);
                }
            });
        }
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
