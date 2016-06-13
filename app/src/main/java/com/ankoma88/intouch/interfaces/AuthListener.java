package com.ankoma88.intouch.interfaces;

import com.ankoma88.intouch.models.User;

/**
 * Created by ankoma88 on 12.06.16.
 */

public interface AuthListener {
    void onLogin(String email, String password);
    void onRegister(User newUser, String password);
}

