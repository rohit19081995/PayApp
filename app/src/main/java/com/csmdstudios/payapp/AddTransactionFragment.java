package com.csmdstudios.payapp;


import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTransactionFragment extends DialogFragment {


    public static final int THRESHOLD = 3;
    public static final String TAG = "AddTransactionFragment";
    public static final String USER_SELECTED_STATE = "User Selected";
    public static final String USER_SELECTED = "User Selected Array";
    public static final String USER_OWED = "User Selected Owed";
    private User user;
    private double owed = 0;
    private boolean itemClickedState;

    private FirebaseUser mUser;
    private View fragmentLayout;
    private TextInputLayout inputLayout;
    private EditText amount;
    private RadioGroup radioGroup;
    private RadioButton borrowButton;
    private RadioButton lendButton;

    public AddTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            itemClickedState = bundle.getBoolean(USER_SELECTED_STATE, false);
            owed = bundle.getDouble(USER_OWED);
            if (itemClickedState) {
                String[] userString = bundle.getStringArray(USER_SELECTED);
                user = new User(userString);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            itemClickedState = savedInstanceState.getBoolean(USER_SELECTED_STATE, false);
            owed = savedInstanceState.getDouble(USER_OWED);
            if (itemClickedState) {
                String[] userString = savedInstanceState.getStringArray(USER_SELECTED);
                user = new User(userString);
            }
        }

        Log.d(TAG, Boolean.toString(savedInstanceState != null));
        // Inflate the layout for this fragment
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        fragmentLayout = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        inputLayout = (TextInputLayout) fragmentLayout.findViewById(R.id.email_login_layout);
        radioGroup = (RadioGroup) fragmentLayout.findViewById(R.id.radio_group);
        amount = (EditText) fragmentLayout.findViewById(R.id.amount);
        borrowButton = (RadioButton) radioGroup.findViewById(R.id.borrow);
        lendButton = (RadioButton) radioGroup.findViewById(R.id.lend);

        if (itemClickedState) {
            loadItemClickedState();
        } else {
            loadUserSelectState();
        }

        return fragmentLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(USER_SELECTED_STATE, itemClickedState);
        savedInstanceState.putDouble(USER_OWED, owed);
        if (itemClickedState) {
            String[] userString = {user.getUID(), user.getName(),  user.getPic_url()};
            savedInstanceState.putStringArray(USER_SELECTED, userString);
        }
    }

    public void loadItemClickedState() {
        final LinearLayout nameLayout = (LinearLayout) fragmentLayout.findViewById(R.id.name_layout);
        inputLayout.setVisibility(View.INVISIBLE);
        inputLayout.setClickable(false);
        nameLayout.setVisibility(View.VISIBLE);
        TextView name = (TextView) nameLayout.findViewById(R.id.text1);
        CircleImageView image = (CircleImageView) nameLayout.findViewById(R.id.imageView);
        name.setText(user.getName());
        if (user.getPic_url() != null) {
            Glide.with(getActivity()).load(user.getPic_url()).into(image);
        }

        if (owed > 0) {
            borrowButton.setText(R.string.paid_me_back);
            lendButton.setText(R.string.lend_more);
        } else if (owed < 0) {
            borrowButton.setText(R.string.borrow_more);
            lendButton.setText(R.string.pay_back);
        } else {
            borrowButton.setText(R.string.borrow);
            lendButton.setText(R.string.lend);
        }

        radioGroup.setVisibility(View.VISIBLE);
        final View amountLayout = fragmentLayout.findViewById(R.id.amount_layout);
        amountLayout.setVisibility(View.VISIBLE);
        amount.setVisibility(View.VISIBLE);
        final TextInputLayout descriptionLayout = (TextInputLayout) fragmentLayout.findViewById(R.id.description_layout);
        descriptionLayout.setVisibility(View.VISIBLE);
        Button addButton = (Button) fragmentLayout.findViewById(R.id.add_button);
        addButton.setVisibility(View.VISIBLE);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add transaction to database
                double newAmount = 0;
                try {
                    newAmount = NumberFormat.getInstance(Locale.getDefault()).parse(amount.getText().toString()).doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //double newAmount = Double.parseDouble(amount.getText().toString());
                if (newAmount > 0) {
                    DatabaseReference mRef = FirebaseDatabase.getInstance()
                            .getReference(mUser.getUid() + "/transactions/" + user.getUID());
                    DatabaseReference nRef = FirebaseDatabase.getInstance()
                            .getReference(user.getUID() + "/transactions/" + mUser.getUid());
                    String key = mRef.push().getKey();
                    mRef = mRef.child(key);
                    nRef = nRef.child(key);
                    DatabaseReference mRef2 = FirebaseDatabase.getInstance()
                            .getReference(mUser.getUid() + "/transactors/" + user.getUID());
                    DatabaseReference nRef2 = FirebaseDatabase.getInstance()
                            .getReference(user.getUID() + "/transactors/" + mUser.getUid());
                    mRef2.child("name").setValue(user.getName());
                    nRef2.child("name").setValue(mUser.getDisplayName());
                    mRef2.child("pic_url").setValue(user.getPic_url());
                    if (mUser.getPhotoUrl() != null)
                        nRef2.child("pic_url").setValue(mUser.getPhotoUrl().toString());
                    double newOwed;
                    if (borrowButton.isChecked()) {
                        //Do not validate
                        newOwed = owed - newAmount;
                        if (newOwed == 0) {
                            mRef2.setValue(null);
                            nRef2.setValue(null);
                        } else {
                            mRef2.child("owed").setValue(owed - newAmount);
                            nRef2.child("owed").setValue(-owed + newAmount);
                        }
                        mRef.child("owed").setValue(-newAmount);
                        nRef.child("owed").setValue(newAmount);
                    } else {
                        //validate
                        newOwed = owed + newAmount;
                        if (newOwed == 0) {
                            mRef2.setValue(null);
                            nRef2.setValue(null);
                        } else {
                            mRef2.child("owed").setValue(owed + newAmount);
                            mRef2.child("unvalidated").setValue(true);
                            nRef2.child("owed").setValue(-owed - newAmount);
                            nRef2.child("unvalidated").setValue(true);
                        }
                        mRef.child("owed").setValue(newAmount);
                        mRef.child("unvalidated").setValue(true);
                        nRef.child("owed").setValue(-newAmount);
                        nRef.child("unvalidated").setValue(true);
                    }
                    if (newOwed != 0) {
                        mRef2.child("timestamp").setValue((ServerValue.TIMESTAMP));
                        nRef2.child("timestamp").setValue((ServerValue.TIMESTAMP));
                    }
                    String description = descriptionLayout.getEditText().getText().toString();
                    if ( description.length() > 0) {
                        mRef.child("description").setValue(description);
                        nRef.child("description").setValue(description);
                    }
                    mRef.child("timestamp").setValue(ServerValue.TIMESTAMP);
                    nRef.child("timestamp").setValue(ServerValue.TIMESTAMP);
                }
                dismiss();
            }
        });
    }

    public void loadUserSelectState() {
        final DelayAutoCompleteTextView textView = (DelayAutoCompleteTextView) fragmentLayout.findViewById(R.id.name_search);
        textView.setThreshold(THRESHOLD);
        ProgressBar mLoadingIndicator = ((ProgressBar) fragmentLayout.findViewById(R.id.pb_loading_indicator));
        final FirebaseSearchAdapter<User> searchAdapter = new FirebaseSearchAdapter<User>(getActivity(), User.class, R.layout.image_dropdown) {
            @Override
            protected void populateView(View v, User model, int position) {
                Log.d(TAG, "child for filter");
                User user = (User) getItem(position);
                ((TextView) v.findViewById(R.id.text1)).setText(user.getName());
                if (user.getPic_url() != null) {
                    ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
                    Glide.with(getActivity()).load(user.getPic_url()).into(imageView);
                }
            }
        };
        textView.setAdapter(searchAdapter);
        textView.setLoadingIndicator(mLoadingIndicator);
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Item" + position + "selected");
                // Animate this
                itemClickedState = true;
                user = searchAdapter.getItem(position);
                user.setUID(searchAdapter.getUID(position));
                borrowButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if ((isChecked && owed > 0) || (!isChecked && owed < 0))
                            amount.setText(String.format(Locale.getDefault(),"%,.2f", Math.abs(owed)));
                        else
                            amount.setText(R.string.default_number);
                    }
                });
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(mUser.getUid() + "/transactors/" + user.getUID() + "/owed");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, dataSnapshot.toString());
                        if (dataSnapshot.exists())
                            owed = dataSnapshot.getValue(Double.class);
                        if ((borrowButton.isChecked() && owed > 0) || (!borrowButton.isChecked() && owed < 0))
                            amount.setText(String.format(Locale.getDefault(),"%,.2f", Math.abs(owed)));
                        else
                            amount.setText(R.string.default_number);
                        if (owed > 0) {
                            borrowButton.setText(R.string.paid_me_back);
                            lendButton.setText(R.string.lend_more);
                        } else if (owed < 0) {
                            borrowButton.setText(R.string.borrow_more);
                            lendButton.setText(R.string.pay_back);
                        } else {
                            borrowButton.setText(R.string.borrow);
                            lendButton.setText(R.string.lend);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(this.getClass().toString(), databaseError.getMessage());
                    }
                });
                loadItemClickedState();
            }
        });
    }

    public static AddTransactionFragment newInstance(Bundle args) {
        AddTransactionFragment myFragment = new AddTransactionFragment();
        myFragment.setArguments(args);

        return myFragment;
    }
}
