package com.csmdstudios.payapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private String username;
    private String password;
    private String email;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentLayout = inflater.inflate(R.layout.fragment_sign_up, container, false);

        FragmentManager fragmentManager = getActivity().getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        final EditText userText = (EditText) fragmentLayout.findViewById(R.id.username);
        final EditText emailText = (EditText) fragmentLayout.findViewById(R.id.email);
        final EditText passText = (EditText) fragmentLayout.findViewById(R.id.password);
        final EditText confirmPassText = (EditText) fragmentLayout.findViewById(R.id.repeat_password);
        Button signUpButton = (Button) fragmentLayout.findViewById(R.id.sign_up_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                password = passText.getText().toString();
                if (password.equals(confirmPassText.getText().toString())) {
                    username = userText.getText().toString();
                    email = emailText.getText().toString();

                    new AsyncLogin(getActivity(), getString(R.string.sign_up_url), fragmentTransaction, SignUpFragment.this, AsyncLogin.ActionToPerform.SIGN_UP).execute(username, email, password);
                }
                else {
                    Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_LONG).show();
                }
            }
        });
        // Inflate the layout for this fragment
        return fragmentLayout;
    }

    public String getEmail() {
        return email;
    }
}
