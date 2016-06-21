package com.csmdstudios.payapp;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    // Firebase Initialisation
    FirebaseAuth mAuth;
    TextInputLayout password;
    TextInputLayout confirmPassword;
    TextInputLayout name;
    TextInputLayout email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);



        Button signUp = (Button) findViewById(R.id.sign_up_button);

        mAuth = FirebaseAuth.getInstance();




        // set textfield errors
        password = (TextInputLayout) findViewById(R.id.password_signup_layout);
        name = (TextInputLayout) findViewById(R.id.name_signup_layout);
        email = (TextInputLayout) findViewById(R.id.email_signup_layout);
        confirmPassword = (TextInputLayout) findViewById(R.id.confirm_password_signup_layout);
        password.getEditText().setOnFocusChangeListener(new PasswordFocusListener(getBaseContext(), password));
        confirmPassword.getEditText().setOnFocusChangeListener(new ConfirmPasswordFocusListener(getBaseContext(), confirmPassword, password.getEditText()));
        name.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d("Name"," has no focus");
                    if (name.getEditText().getText().length() < 1){
                        name.setError(getString(R.string.no_name_entered));
                    }
                } else {
                    Log.d("Name"," has focus");
                    name.setError(null);
                }
            }
        });

        email.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d("Email"," has no focus");
                    String target = email.getEditText().getText().toString();
                    if (TextUtils.isEmpty(target) || !android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()){
                        email.setError(getString(R.string.invalid_email));
                    }
                } else {
                    Log.d("Email"," has focus");
                    email.setError(null);
                }
            }
        });


        final OnCompleteListener signUpComplete = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseUser newUser = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(((EditText) findViewById(R.id.name)).getText().toString()).build();
                    newUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });
                }

                // ...
            }
        };
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValidate()) {
                    // Create the user
                    mAuth.createUserWithEmailAndPassword(((EditText) findViewById(R.id.email_signup)).getText().toString(), password.getEditText().getText().toString())
                            .addOnCompleteListener(SignUpActivity.this, signUpComplete);
                } else {
                    // Tell the user what is wrong
                    Toast.makeText(SignUpActivity.this, "Please correct the errors", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public boolean formValidate() {
        Boolean result = true;
        String target = email.getEditText().getText().toString();
        if(TextUtils.isEmpty(target) || !android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
            email.setError(getString(R.string.invalid_email));
            result = false;
        } if (name.getEditText().getText().length() < 1) {
            name.setError(getString(R.string.no_name_entered));
            result = false;
        } if (password.getEditText().getText().length() < PasswordFocusListener.MIN_PASSWORD_LENGTH) {
            password.setError(getString(R.string.password_length_exceeded));
            result = false;
        } if (!confirmPassword.getEditText().getText().toString().equals(password.getEditText().getText().toString())) {
            confirmPassword.setError(getString(R.string.password_match_error));
            result = false;
        }
        return result;
    }
}
