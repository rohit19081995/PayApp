package com.csmdstudios.payapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
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
import java.net.URL;

/**
 * Created by wayne on 10/6/16.
 */
public class AsyncLogin extends AsyncTask<String, String, String> {
    FragmentTransaction fragmentTransaction;
    Fragment fragment;
    Context context;
    ProgressDialog pdLoading;
    HttpURLConnection conn;
    URL url = null;
    String urlString;
    String username;
    String password;
    ActionToPerform actionToPerform;

    public enum ActionToPerform {LOGIN, CHECK, SIGN_UP}

    public AsyncLogin(Context context, String urlString, FragmentTransaction fragmentTransaction, Fragment fragment, ActionToPerform action) {
        this.fragmentTransaction = fragmentTransaction;
        this.fragment = fragment;
        this.context = context;
        pdLoading = new ProgressDialog(context);
        this.actionToPerform = action;
        this.urlString = urlString;
    }

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
            url = new URL(urlString);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Malformed URL exception";
        }
        try {
            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(LoginFragment.READ_TIMEOUT);
            conn.setConnectTimeout(LoginFragment.CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");

            // setDoInput and setDoOutput method depict handling of both send and receive
            conn.setDoInput(true);
            conn.setDoOutput(true);

            username = params[0];
            password = params[1];

            // Append parameters to URL
            Uri.Builder builder = new Uri.Builder();
            if (urlString.contains("login")) {
                builder.appendQueryParameter("username", username)
                       .appendQueryParameter("password", password);
            } else {
                builder.appendQueryParameter("username", username)
                        .appendQueryParameter("email", ((SignUpFragment) fragment).getEmail())
                       .appendQueryParameter("password", password);
            }
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
        switch (actionToPerform) {
            case LOGIN:
                forLogin(result);
                break;
            case CHECK:
                forMain(result);
                break;
            case SIGN_UP:
                forSignup(result);
                break;
        }
    }

    public void forMain(String result) {
        if(result.equalsIgnoreCase("true"))
        {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
            LoggedInFragment loggedInFragment = new LoggedInFragment();
            fragmentTransaction.add(R.id.plain_layout, loggedInFragment, "LOGGED_IN_FRAGMENT");
            Log.d("Verification Success", "Credentials verified");
            Toast.makeText(context, "Credentials Verified", Toast.LENGTH_LONG).show();

        }else if (result.equalsIgnoreCase("false")){

            // If username and password does not match display a error message
            LoginFragment loginFragment = new LoginFragment();
            fragmentTransaction.add(R.id.plain_layout, loginFragment, "LOGIN_FRAGMENT");
            Log.d("Invalid username", "Invalid email or password");
            Toast.makeText(context, "The username or password is not valid anymore.\nPlease register again.", Toast.LENGTH_LONG).show();

        } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

            LoginFragment loginFragment = new LoginFragment();
            fragmentTransaction.add(R.id.plain_layout, loginFragment, "LOGIN_FRAGMENT");
            Log.d("Connection problem", "OOPs! Something went wrong. Connection Problem.");
            Toast.makeText(context, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(context, "OOPs! Something went wrong.", Toast.LENGTH_LONG).show();
            Log.d("huh, why?", result);
        }
        fragmentTransaction.commit();
    }

    public void forLogin(String result) {
        if(result.equalsIgnoreCase("true"))
        {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */


            SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.getMyPreferences(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("USERNAME", username);
            editor.putString("PASSWORD", password);
            Boolean savePass = ((LoginFragment) fragment).savePasswordCheckBoxIsChecked();
            Boolean loggedIn = ((LoginFragment) fragment).loggedInCheckBoxIsChecked();
            if (savePass) {
                editor.putBoolean(LoginFragment.getLoggedIn(), loggedIn);
                if (loggedIn) {
                    editor.putBoolean(LoginFragment.getUserLoggedIn(), true);
                }
                else {
                    editor.putBoolean(LoginFragment.getUserLoggedIn(), false);
                }
            }
            else {
                editor.putBoolean(LoginFragment.getUserLoggedIn(), false);
            }
            editor.putBoolean(LoginFragment.getSavePassword(), savePass);

            editor.apply();

            LoggedInFragment loggedInFragment = new LoggedInFragment();
            fragmentTransaction.replace(R.id.plain_layout, loggedInFragment, "LOGGED_IN_FRAGMENT");

            Log.d("Successful Login", "Successfully logged in");
            Toast.makeText(context, "Successfully logged in", Toast.LENGTH_LONG).show();

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            fragmentTransaction.commit();

        } else if (result.equalsIgnoreCase("false")){

            // If username and password does not match display a error message
            Log.d("Invalid username", "Invalid email or password");
            Toast.makeText(context, "Invalid username or password", Toast.LENGTH_LONG).show();

        } else if (result.equalsIgnoreCase("verify")){

            Log.d("Not verified", "user has not verified email");
            Toast.makeText(context, "Please verify your account.", Toast.LENGTH_LONG).show();

        } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

            Log.d("Connection problem", "OOPs! Something went wrong. Connection Problem.");
            Toast.makeText(context, "OOPs! Something went wrong. Connection Problem." + result, Toast.LENGTH_LONG).show();

        } else {
            Log.d("huh, why?", result);
        }
    }

    public void forSignup(String result) {
        if(result.equalsIgnoreCase("user added"))
        {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
            Log.d("User added", "Signed up successfully");
            //Toast.makeText(getActivity(), "Signed up successfully", Toast.LENGTH_LONG).show();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setNeutralButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    LoginFragment loginFragment = new LoginFragment();
                    fragmentTransaction.replace(R.id.plain_layout, loginFragment, "LOGIN_FRAGMENT");

                    SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.getMyPreferences(), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USERNAME", username);
                    editor.putString("PASSWORD", password);
                    editor.putBoolean(LoginFragment.getLoggedIn(), true);
                    editor.putBoolean(LoginFragment.getSavePassword(), true);
                    editor.putBoolean(LoginFragment.getUserLoggedIn(), false);
                    editor.apply();
                    fragmentTransaction.commit();

                }
            });

            alertDialogBuilder.setTitle(R.string.email_alert_title);
            alertDialogBuilder.setMessage(context.getString(R.string.email_alert_message1)+ " " + ((SignUpFragment) fragment).getEmail() + context.getString(R.string.email_alert_message2));

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        } else if (result.equalsIgnoreCase("email registered")) {

            Log.d("Existing email", "Email Already exits");
            Toast.makeText(context, "That Email ID has already been registered", Toast.LENGTH_LONG).show();

        } else if (result.equalsIgnoreCase("user already exists")){

            // If username and password does not match display a error message
            Log.d("Existing user", "User Already exits");
            Toast.makeText(context, "That username is taken", Toast.LENGTH_LONG).show();

        } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

            Log.d("Connection problem", "OOPs! Something went wrong. Connection Problem."+result);
            Toast.makeText(context, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

        } else if (result.equalsIgnoreCase("mail failed")) {

            Log.d("Mail failed", "The email ID you entered might not exist."+result);
            Toast.makeText(context, "The email ID you entered might not exist.", Toast.LENGTH_LONG).show();

        } else {
            Log.d("huh, why?", result);
        }
    }

}
