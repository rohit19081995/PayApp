package com.csmdstudios.payapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


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

        LinearLayout linearLayout = (LinearLayout) fragmentLayout.findViewById(R.id.ll2);
        final EditText userText = (EditText) linearLayout.findViewById(R.id.username);
        final EditText passText = (EditText) linearLayout.findViewById(R.id.password);
        savePasswordCheckBox = (CheckBox) linearLayout.findViewById(R.id.save_password);
        loggedInCheckBox = (CheckBox) linearLayout.findViewById(R.id.logged_in);
        Button signInButton = (Button) linearLayout.findViewById(R.id.sign_in_button);

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.getMyPreferences(), Context.MODE_PRIVATE);

        Boolean savePass = sharedPreferences.getBoolean(SAVE_PASSWORD, false);
        savePasswordCheckBox.setChecked(savePass);
        if(!savePass) {
            loggedInCheckBox.setChecked(false);
            loggedInCheckBox.setEnabled(false);
        }
        else {
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

                new AsyncLogin().execute(username,password);
            }
        });
        // Inflate the layout for this fragment
        return fragmentLayout;
    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://192.168.1.4/webservice/login.inc.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "Malformed URL exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                //conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "Output Stream exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "Input Stream exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */


                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.getMyPreferences(), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("USERNAME", username);
                editor.putString("PASSWORD", password);
                Boolean savePass = savePasswordCheckBox.isChecked();
                Boolean loggedIn = loggedInCheckBox.isChecked();
                if (savePass) {
                    editor.putBoolean(LOGGED_IN, loggedIn);
                    if (loggedIn) {
                            editor.putBoolean(USER_LOGGED_IN, true);
                    }
                    else {
                        editor.putBoolean(USER_LOGGED_IN, false);
                    }
                }
                else {
                    editor.putBoolean(USER_LOGGED_IN, false);
                }
                editor.putBoolean(SAVE_PASSWORD, savePass);

                editor.apply();

                Log.d("Successful Login", "Successfully logged in");
                Toast.makeText(getActivity(), "Successfully logged in", Toast.LENGTH_LONG).show();

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Log.d("Invalid username", "Invalid email or password");
                Toast.makeText(getActivity(), "Invalid username or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Log.d("Connection problem", "OOPs! Something went wrong. Connection Problem.");
                Toast.makeText(getActivity(), "OOPs! Something went wrong. Connection Problem." + result, Toast.LENGTH_LONG).show();

            } else {
                Log.d("huh, why?", result);
            }
        }

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
}