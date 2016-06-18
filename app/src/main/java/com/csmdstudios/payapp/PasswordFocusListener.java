package com.csmdstudios.payapp;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;

/**
 * Created by wayne on 18/6/16.
 */
public class PasswordFocusListener implements View.OnFocusChangeListener{


    private static final int MIN_PASSWORD_LENGTH = 8;
    private TextInputLayout password;
    private Context context;

    public PasswordFocusListener(Context context, TextInputLayout password) {
        this.password = password;
        this.context = context;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            Log.d("Password"," has no focus");
            if (password.getEditText().getText().length() < MIN_PASSWORD_LENGTH){
                password.setError(context.getString(R.string.password_length_exceeded));
            }
        } else {
            Log.d("Password"," has focus");
            password.setError(null);
        }
    }
}
