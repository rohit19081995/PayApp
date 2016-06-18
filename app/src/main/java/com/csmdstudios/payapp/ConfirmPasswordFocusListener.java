package com.csmdstudios.payapp;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

/**
 * Created by wayne on 18/6/16.
 */
public class ConfirmPasswordFocusListener implements View.OnFocusChangeListener{

    private TextInputLayout confirmPassword;
    private EditText password;
    private Context context;

    public ConfirmPasswordFocusListener(Context context, TextInputLayout confirmPassword, EditText password) {
        this.confirmPassword = confirmPassword;
        this.password = password;
        this.context = context;
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if(!password.getText().toString().equals(confirmPassword.getEditText().getText().toString())) {
                confirmPassword.setError(context.getString(R.string.password_match_error));
            }
        } else {
            confirmPassword.setError(null);
        }
    }
}
