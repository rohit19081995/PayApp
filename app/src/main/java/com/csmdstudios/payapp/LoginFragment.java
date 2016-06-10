package com.csmdstudios.payapp;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private static final String SAVE_PASSWORD = "SAVE_PASSWORD";
    private static final String LOGGED_IN = "LOGGED_IN";
    private static final String USER_LOGGED_IN  = "USER_LOGGED_IN";
    private String password;
    private String username;
    CheckBox savePasswordCheckBox;
    CheckBox loggedInCheckBox;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View fragmentLayout = inflater.inflate(R.layout.fragment_login, container, false);

        FragmentManager fragmentManager = getActivity().getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LinearLayout linearLayout = (LinearLayout) fragmentLayout.findViewById(R.id.sign_in_layout);
        final EditText userText = (EditText) linearLayout.findViewById(R.id.username);
        final EditText passText = (EditText) linearLayout.findViewById(R.id.password);
        savePasswordCheckBox = (CheckBox) linearLayout.findViewById(R.id.save_password);
        loggedInCheckBox = (CheckBox) linearLayout.findViewById(R.id.logged_in);
        Button signInButton = (Button) linearLayout.findViewById(R.id.sign_in_button);
        TextView signUpText = (TextView) fragmentLayout.findViewById(R.id.sign_up_text_view);

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.getMyPreferences(), Context.MODE_PRIVATE);

        Boolean savePass = sharedPreferences.getBoolean(SAVE_PASSWORD, false);
        savePasswordCheckBox.setChecked(savePass);
        if(!savePass) {
            loggedInCheckBox.setChecked(false);
            loggedInCheckBox.setEnabled(false);
        }
        else {
            userText.setText(sharedPreferences.getString("USERNAME", ""));
            passText.setText(sharedPreferences.getString("PASSWORD", ""));
            loggedInCheckBox.setEnabled(true);
            loggedInCheckBox.setChecked(sharedPreferences.getBoolean(LOGGED_IN, false));
        }

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        savePasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("SAVE_PASSWORD", isChecked);
                editor.apply();
                if (isChecked) {
                    loggedInCheckBox.setEnabled(true);
                }
                else {
                    loggedInCheckBox.setChecked(false);
                    loggedInCheckBox.setEnabled(false);
                }
            }
        });

        loggedInCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("LOGGED_IN", isChecked);
                editor.apply();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                username = userText.getText().toString();
                password = passText.getText().toString();

                new AsyncLogin(getActivity(), getString(R.string.login_url), fragmentTransaction, LoginFragment.this, AsyncLogin.ActionToPerform.LOGIN).execute(username,password);
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SignUpFragment signUpFragment = new SignUpFragment();
                fragmentTransaction.replace(R.id.plain_layout, signUpFragment, "SIGN_UP_FRAGMENT");
                fragmentTransaction.commit();
            }
        });
        // Inflate the layout for this fragment
        return fragmentLayout;
    }

    public static String getUserLoggedIn() {
        return USER_LOGGED_IN;
    }

    public static String getSavePassword() {
        return SAVE_PASSWORD;
    }

    public static String getLoggedIn() {
        return LOGGED_IN;
    }

    public boolean savePasswordCheckBoxIsChecked() {
        return savePasswordCheckBox.isChecked();
    }

    public boolean loggedInCheckBoxIsChecked() {
        return loggedInCheckBox.isChecked();
    }
}
