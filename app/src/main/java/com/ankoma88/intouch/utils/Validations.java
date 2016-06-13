package com.ankoma88.intouch.utils;

import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by ankoma88 on 12.06.16.
 */
public class Validations {
    public static final String NICKNAME_SCHEMA = "^[a-z0-9_-]{3,15}$";
    public static final Pattern NICKNAME = Pattern.compile(NICKNAME_SCHEMA);
    public static final Pattern EMAIL = Pattern.compile(
            "\\b[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+\\b");

    public static boolean isEmailValid(String email) {
        return EMAIL.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    public static boolean isNicknameValid(String nickname) {
        return NICKNAME.matcher(nickname).matches();
    }

    public static boolean isNameValid(String name) {
        return name.length()>2;
    }
}
