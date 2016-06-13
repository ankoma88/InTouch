package com.ankoma88.intouch.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import com.ankoma88.intouch.R;
import com.ankoma88.intouch.interfaces.AuthListener;
import com.ankoma88.intouch.models.User;
import com.ankoma88.intouch.ui.activities.AuthActivity;
import com.ankoma88.intouch.utils.Validations;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ankoma88 on 12.06.16.
 */
public class RegistrationFragment extends AuthFragment {
    public static final String TAG = RegistrationFragment.class.getSimpleName();

    private AuthListener authListener;

    @Bind(R.id.nickname)
    TextInputEditText etNickname;

    @Bind(R.id.first_name)
    TextInputEditText etFirstName;

    @Bind(R.id.last_name)
    TextInputEditText etLastName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        authListener = (AuthListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        authListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);
        ButterKnife.bind(this, rootView);
        setListeners();
        fillCredentials();
        return rootView;
    }

    private void fillCredentials() {
        etEmail.setText(getArguments().getString(AuthActivity.EXTRA_EMAIL));
        etPassword.setText(getArguments().getString(AuthActivity.EXTRA_PASSWORD));
    }

    private void setListeners() {
        btnLogin.setOnClickListener(view -> register());

        etPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == R.id.login || i == EditorInfo.IME_NULL) {
                register();
                return true;
            }
            return false;
        });
    }

    private void register() {
        Log.d(TAG, "register");
        clearFields();

        String nickname = etNickname.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (isVerified(nickname, firstName, lastName, email, password)) {
            showProgress(true);
            User newUser = new User(nickname, firstName, lastName, email);
            authListener.onRegister(newUser, password);
        }
    }

    private void clearFields() {
        etNickname.setError(null);
        etFirstName.setError(null);
        etLastName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
    }

    private boolean isVerified(String nickname, String firstName, String lastName, String email, String password) {
        boolean isReady = true;

        if (TextUtils.isEmpty(nickname) || !Validations.isNicknameValid(nickname)) {
            etNickname.setError(getString(R.string.error_invalid_nickname));
            isReady = false;
        }

        if (TextUtils.isEmpty(firstName) || !Validations.isNameValid(firstName)) {
            etFirstName.setError(getString(R.string.error_invalid_first_name));
            isReady = false;
        }

        if (TextUtils.isEmpty(lastName) || !Validations.isNameValid(lastName)) {
            etLastName.setError(getString(R.string.error_invalid_last_name));
            isReady = false;
        }

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
}
