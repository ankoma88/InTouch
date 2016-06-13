package com.ankoma88.intouch.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import com.ankoma88.intouch.R;
import com.ankoma88.intouch.utils.Validations;

import butterknife.ButterKnife;

/**
 * Created by ankoma88 on 12.06.16.
 */
public class LoginFragment extends AuthFragment {
    public static final String TAG = LoginFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);
        setListeners();
        return rootView;
    }


    private void setListeners() {
        btnLogin.setOnClickListener(view -> login());
        etPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == R.id.login || i == EditorInfo.IME_NULL) {
                login();
                return true;
            }
            return false;
        });
    }

    private void login() {
        Log.d(TAG, "login");
        clearFields();

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();


        if (!isVerified(email, password)) {

        } else {
            showProgress(true);
            callbacks.onLogin(email, password);
        }
    }

    protected boolean isVerified(String email, String password) {
        boolean isReady = true;
        if (TextUtils.isEmpty(password) || !Validations.isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            isReady = false;
        }

        if (TextUtils.isEmpty(email) || !Validations.isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            isReady = false;
        }
        return isReady;
    }

    private void clearFields() {
        etEmail.setError(null);
        etPassword.setError(null);
    }
}
