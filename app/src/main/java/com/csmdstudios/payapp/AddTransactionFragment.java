package com.csmdstudios.payapp;


import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTransactionFragment extends DialogFragment {


    private static final int THRESHOLD = 3;
    private static final String TAG = "AddTransactionFragment";

    public AddTransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fragmentLayout = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        DelayAutoCompleteTextView textView = (DelayAutoCompleteTextView) fragmentLayout.findViewById(R.id.name_search);
        final Button button = (Button) fragmentLayout.findViewById(R.id.button);
        textView.setThreshold(THRESHOLD);
        ProgressBar mLoadingIndicator = ((ProgressBar) fragmentLayout.findViewById(R.id.pb_loading_indicator));
        textView.setAdapter(new FirebaseSearchAdapter<User>(getActivity(), User.class, R.layout.image_dropdown, mLoadingIndicator) {
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
        });
        textView.setLoadingIndicator(mLoadingIndicator);
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Item" + position + "selected");
                // Animate this
                button.setVisibility(View.VISIBLE);
            }
        });

        return fragmentLayout;
    }

}
