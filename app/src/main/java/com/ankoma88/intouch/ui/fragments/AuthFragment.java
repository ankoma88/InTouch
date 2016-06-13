package com.ankoma88.intouch.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ankoma88.intouch.R;
import com.ankoma88.intouch.interfaces.AuthListener;

import butterknife.Bind;

/**
 * Created by ankoma88 on 12.06.16.
 */
public class AuthFragment extends Fragment {
    private static final String LOG_TAG = AuthFragment.class.getSimpleName();
    protected AuthListener callbacks;

    @Bind(R.id.email)
    TextInputEditText etEmail;

    @Bind(R.id.password)
    TextInputEditText etPassword;

    @Bind(R.id.login_button)
    View btnLogin;

    @Bind(R.id.progress)
    View pbProgress;

    @Bind(R.id.login_form)
    View svLoginForm;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (AuthListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    protected void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        svLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        svLoginForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                svLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        pbProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        pbProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                pbProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void hideProgress() {
        showProgress(false);
    }

    public AuthFragment() {
    }
}
