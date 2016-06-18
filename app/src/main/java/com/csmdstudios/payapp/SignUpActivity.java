package com.csmdstudios.payapp;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button signUp = (Button) findViewById(R.id.sign_up_button);

        // Firebase Initialisation
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();


        // set password errors
        final TextInputLayout password = (TextInputLayout) findViewById(R.id.password_signup_layout);
        password.getEditText().setOnFocusChangeListener(new PasswordFocusListener(getBaseContext(), password));
        TextInputLayout confirmPassword = (TextInputLayout) findViewById(R.id.confirm_password_signup_layout);
        confirmPassword.getEditText().setOnFocusChangeListener(new ConfirmPasswordFocusListener(getBaseContext(), confirmPassword, password.getEditText()));

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
                }

                // ...
            }
        };
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(((EditText) findViewById(R.id.email_signup)).getText().toString(), password.getEditText().getText().toString())
                        .addOnCompleteListener(SignUpActivity.this, signUpComplete);
            }
        });
    }
}
