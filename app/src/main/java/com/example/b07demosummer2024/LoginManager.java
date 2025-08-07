package com.example.b07demosummer2024;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public void checkPin(String pin, java.util.function.Consumer<String> onSuccess, java.util.function.Consumer<String> onError) {
        DatabaseReference pinsRef = FirebaseDatabase.getInstance()
                .getReference("pins");  // Path: /pins/<uid>/pin = "1234"

        pinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String storedPin = userSnap.child("pin").getValue(String.class);
                    if (storedPin != null && storedPin.equals(pin)) {
                        String uid = userSnap.getKey();
                        onSuccess.accept(uid);
                        return;
                    }
                }
                onError.accept("PIN not found or incorrect");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                onError.accept("Database error: " + error.getMessage());
            }
        });
    }
}