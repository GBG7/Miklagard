package com.example.b07demosummer2024;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class LoginManager {
    private static LoginManager instance;
    private LoginManager() {}
    public static synchronized LoginManager getInstance() {
        if (instance == null) instance = new LoginManager();
        return instance;
    }

    // Simple “remember” flag (unchanged)
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_LOGGED_IN = "loggedIn";

    public boolean isLoggedIn(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return p.getBoolean(KEY_LOGGED_IN, false);
    }
    public void setLoggedIn(Context ctx, boolean v) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        p.edit().putBoolean(KEY_LOGGED_IN, v).apply();
    }

    /** Check PIN against local encrypted storage (not Firebase) and return stored UID on success. */
    public void checkPin(
            Context ctx,
            String candidatePin,
            java.util.function.Consumer<String> onSuccess,      // returns uid
            java.util.function.Consumer<String> onError
    ) {
        if (candidatePin == null) candidatePin = "";
        if (!(candidatePin.length() == 4 || candidatePin.length() == 6)) {
            onError.accept("PIN must be 4 or 6 digits");
            return;
        }

        try {
            MasterKey key = new MasterKey.Builder(ctx)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences secure = EncryptedSharedPreferences.create(
                    ctx,
                    "secure_prefs", // must match where you saved it
                    key,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String storedPin = secure.getString("user_pin", null);
            String storedUid = secure.getString("user_uid", null);

            if (storedPin == null || storedUid == null) {
                onError.accept("No PIN set on this device");
                return;
            }
            if (!storedPin.equals(candidatePin)) {
                onError.accept("Pin not found or incorrect");
                return;
            }
            onSuccess.accept(storedUid);

        } catch (GeneralSecurityException | IOException e) {
            onError.accept("Secure-storage error");
        }
    }
}
