package com.csmdstudios.payapp;

import android.app.FragmentTransaction;
import android.content.Context;
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
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoggedInActivity extends AppCompatActivity {

    private static final String TAG = "LoggedInActivity";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseUser mUser;
    public static String currency;

    public static final String TRANSACTOR_EXTRA = "Transactor details";
    public static final String OWED_EXTRA = "Transactor owed double";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        mRecyclerView = (RecyclerView) findViewById(R.id.transactor_recycler_view);
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
                    startActivity(new Intent(LoggedInActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Transactor, TransactorViewHolder>(
                Transactor.class,
                R.layout.transactor_layout,
                TransactorViewHolder.class,
                FirebaseDatabase.getInstance().getReference(mUser.getUid() + "/transactors")) {

            @Override
            protected void populateViewHolder(TransactorViewHolder viewHolder, Transactor model, int position) {
                if (model.getPic_url() != null) {
                    Glide.with(LoggedInActivity.this).load(model.getPic_url()).into(viewHolder.imageView);
                    viewHolder.picUrl = model.getPic_url();
                }
                viewHolder.nameView.setText(model.getName());
                viewHolder.UID = getItemKey(position);

                double owed = model.getOwed();
                viewHolder.owed = owed;
                if (owed > 0) {
                    viewHolder.owedInfo.setTextColor(ContextCompat.getColor(LoggedInActivity.this, R.color.colorOwed));
                    viewHolder.owedInfo.setText(R.string.owes_you);
                    viewHolder.owedView.setTextColor(ContextCompat.getColor(LoggedInActivity.this, R.color.colorOwed));
                } else {
                    viewHolder.owedInfo.setTextColor(ContextCompat.getColor(LoggedInActivity.this, R.color.colorOwes));
                    viewHolder.owedInfo.setText(R.string.you_owe);
                    viewHolder.owedView.setTextColor(ContextCompat.getColor(LoggedInActivity.this, R.color.colorOwes));
                }
                viewHolder.owedView.setText(String.format(Locale.getDefault(),"%s %,.2f", currency, Math.abs(owed)));
            }
        };

        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "FAB pressed");
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    new AddTransactionFragment().show(fragmentTransaction, TAG);
                }
            });
        }
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(LoggedInActivity.this, LoginActivity.class));
        finish();
    }

    private static class TransactorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameView;
        private TextView owedView;
        private TextView owedInfo;
        private CircleImageView imageView;
        private String picUrl;
        private Context context;
        private double owed;
        public String UID;

        public TransactorViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.transactor_name);
            owedInfo = (TextView) itemView.findViewById(R.id.owe_info);
            owedView = (TextView) itemView.findViewById(R.id.owed);
            imageView = (CircleImageView) itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
            picUrl = null;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, TransactorActivity.class);
            ArrayList<String> transactorStrings = new ArrayList<>();
            transactorStrings.add(nameView.getText().toString());
            transactorStrings.add(owedInfo.getText().toString());
            transactorStrings.add(picUrl);
            transactorStrings.add(UID);
            intent.putStringArrayListExtra(TRANSACTOR_EXTRA, transactorStrings);
            intent.putExtra(OWED_EXTRA, owed);
            context.startActivity(intent);
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
                logout();
                return true;
            case R.id.settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSettings() {
    }
}
