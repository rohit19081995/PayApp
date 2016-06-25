package com.csmdstudios.payapp;


import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTransactionFragment extends DialogFragment {


    private static final int THRESHOLD = 3;

    public AddTransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentLayout = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        DelayAutoCompleteTextView textView = (DelayAutoCompleteTextView) fragmentLayout.findViewById(R.id.name_search);
        textView.setThreshold(THRESHOLD);
        textView.setAdapter(new FirebaseSearchAdapter<User>(getActivity(), User.class, R.layout.image_dropdown) {
            @Override
            protected void populateView(View v, User model, int position) {
                User user = (User) getItem(position);
                ((TextView) v.findViewById(R.id.text1)).setText(user.getName());
                if (user.getPic_url() != null) {
                    ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
                    Glide.with(getActivity()).load(user.getPic_url()).into(imageView);
                }
            }
        });
        textView.setLoadingIndicator(((ProgressBar) fragmentLayout.findViewById(R.id.pb_loading_indicator)));
        textView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return fragmentLayout;
    }

}
