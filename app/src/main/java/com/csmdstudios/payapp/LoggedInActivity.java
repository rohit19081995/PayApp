package com.csmdstudios.payapp;

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

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoggedInActivity extends AppCompatActivity {

    private static final String TAG = "LoggedInActivity";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String currency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // TODO: change currency in preferences
        currency = "\u20B9";

        // Firebase auth listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser == null) {
                    startActivity(new Intent(LoggedInActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        Log.d(TAG, mUser.getUid());

        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Transactor, TransactorViewHolder>(
                Transactor.class,
                R.layout.transactor_layout,
                TransactorViewHolder.class,
                FirebaseDatabase.getInstance().getReference(mUser.getUid() + "/transactors")) {

            @Override
            protected void populateViewHolder(TransactorViewHolder viewHolder, Transactor model, int position) {
                if (model.getPic_url() != null)
                    Glide.with(LoggedInActivity.this).load(model.getPic_url()).into(viewHolder.imageView);
                viewHolder.nameView.setText(model.getName());

                double owed = model.getOwed();
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
    }

    private static class TransactorViewHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView owedView;
        private TextView owedInfo;
        private CircleImageView imageView;

        public TransactorViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.transactor_name);
            owedInfo = (TextView) itemView.findViewById(R.id.owe_info);
            owedView = (TextView) itemView.findViewById(R.id.owed);
            imageView = (CircleImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
