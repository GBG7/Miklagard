package com.example.b07demosummer2024;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginManager {
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_LOGGED_IN = "loggedIn";
    private static LoginManager instance;

    private LoginManager() {}

    public static LoginManager getInstance() {
        if (instance == null) instance = new LoginManager();
        return instance;
    }

    public boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply();
    }
}