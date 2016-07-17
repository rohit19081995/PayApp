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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
    double newAmount = 0;


    private FirebaseUser mUser;
    private View fragmentLayout;
    private TextInputLayout inputLayout;
    private EditText amount;
    private RadioGroup radioGroup;
    private RadioButton borrowButton;
    private RadioButton lendButton;
    private ProgressBar mLoadingIndicator;
    private DatabaseReference database;

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
        database = FirebaseDatabase.getInstance().getReference();
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
        mLoadingIndicator = ((ProgressBar) fragmentLayout.findViewById(R.id.pb_loading_indicator));


        if ((borrowButton.isChecked() && owed > 0) || (!borrowButton.isChecked() && owed < 0))
            amount.setText(String.format(Locale.getDefault(),"%,.2f", Math.abs(owed)));
        else
            amount.setText(R.string.default_number);

        borrowButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ((isChecked && owed > 0) || (!isChecked && owed < 0))
                    amount.setText(String.format(Locale.getDefault(),"%,.2f", Math.abs(owed)));
                else
                    amount.setText(R.string.default_number);
            }
        });

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
        inputLayout.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.GONE);
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
                try {
                    newAmount = NumberFormat.getInstance(Locale.getDefault()).parse(amount.getText().toString()).doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                database.child(mUser.getUid() + "/transactors/" + user.getUID() + "/owed").runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if (mutableData.getValue() != null)
                            owed = mutableData.getValue(double.class);
                        else owed = 0;
                        String description = descriptionLayout.getEditText().getText().toString();
                        addTransaction(newAmount, description);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (databaseError != null)
                            Log.d("Complete Transaction", databaseError.getMessage() + "\n");
                        Log.d("Complete Transaction", Boolean.toString(b) + "\n" + dataSnapshot.toString());
                    }
                });
                dismiss();
            }
        });
    }

    public void loadUserSelectState() {
        final DelayAutoCompleteTextView textView = (DelayAutoCompleteTextView) fragmentLayout.findViewById(R.id.name_search);
        textView.setThreshold(THRESHOLD);
        final FirebaseSearchAdapter<User> searchAdapter = new FirebaseSearchAdapter<User>(getActivity(), User.class, R.layout.image_dropdown) {
            @Override
            protected void populateView(View v, User model, int position) {
                ((TextView) v.findViewById(R.id.text1)).setText(model.getName());
                if (model.getPic_url() != null) {
                    ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
                    Glide.with(getActivity()).load(model.getPic_url()).into(imageView);


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
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(mUser.getUid() + "/transactors/" + user.getUID() + "/owed");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, dataSnapshot.toString());
                        if (dataSnapshot.exists())
                            owed = dataSnapshot.getValue(Double.class);
                        if ((borrowButton.isChecked() && owed > 0) || (!borrowButton.isChecked() && owed < 0))
                            amount.setText(String.format(Locale.getDefault(), "%,.2f", Math.abs(owed)));
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

    public void addTransaction(double newAmount, String description) {
        if (newAmount > 0) {
            DatabaseReference userTransactionRef = database.child(mUser.getUid() + "/transactions/" + user.getUID());
            DatabaseReference mUserTransactionRef = database.child(user.getUID() + "/transactions/" + mUser.getUid());
            String key = userTransactionRef.push().getKey();
            userTransactionRef = userTransactionRef.child(key);
            mUserTransactionRef = mUserTransactionRef.child(key);

            Transactor userTransactor = new Transactor(user.getName());
            Transactor mUserTransactor = new Transactor(mUser.getDisplayName());
            AppTransaction userAppTransaction;
            AppTransaction mUserAppTransaction;
            userTransactor.setPic_url(user.getPic_url());

            if (mUser.getPhotoUrl() != null)
                mUserTransactor.setPic_url(mUser.getPhotoUrl().toString());
            double newOwed;
            if (borrowButton.isChecked()) {
                //Do not validate
                newOwed = owed - newAmount;
                if (newOwed == 0) {
                    userTransactor = null;
                    mUserTransactor = null;
                } else {
                    userTransactor.setOwed(newOwed);
                    mUserTransactor.setOwed(-newOwed);
                }
                userAppTransaction = new AppTransaction(-newAmount);
                mUserAppTransaction = new AppTransaction(newAmount);
            } else {
                //validate
                newOwed = owed + newAmount;
                if (newOwed == 0) {
                    userTransactor = null;
                    mUserTransactor = null;
                } else {
                    userTransactor.setOwed(newOwed);
                    userTransactor.setUnvalidated(true);
                    mUserTransactor.setOwed(-newOwed);
                    mUserTransactor.setUnvalidated(true);
                }
                userAppTransaction = new AppTransaction(newAmount);
                userAppTransaction.setUnvalidated(true);
                mUserAppTransaction = new AppTransaction(-newAmount);
                mUserAppTransaction.setUnvalidated(true);
            }
            if ( description.length() > 0) {
                userAppTransaction.setDescription(description);
                mUserAppTransaction.setDescription(description);
            }
            Map<String, Object> updatedUserData = new HashMap<String, Object>();
            Map<String, Object> userPut = new ObjectMapper().convertValue(userTransactor, Map.class);
            Map<String, Object> mUserPut = new ObjectMapper().convertValue(mUserTransactor, Map.class);
            updatedUserData.put(mUser.getUid() + "/transactors/" + user.getUID(), userPut);
            updatedUserData.put(user.getUID() + "/transactors/" + mUser.getUid(), mUserPut);
            database.updateChildren(updatedUserData);
            userTransactionRef.setValue(userAppTransaction);
            mUserTransactionRef.setValue(mUserAppTransaction);
        }
    }
}
