package com.example.b07demosummer2024;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.Toast;

import com.example.b07demosummer2024.data.PinCreationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class LoginActivity extends AppCompatActivity {
    private RadioGroup loginMethodGroup;
    private LinearLayout emailLoginLayout;
    private LinearLayout pinLoginLayout;
    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private EditText pinField;
    private FirebaseAuth mAuth;
    int checkedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        loginMethodGroup = findViewById(R.id.loginMethodGroup);
        emailLoginLayout = findViewById(R.id.emailLoginLayout);
        pinLoginLayout = findViewById(R.id.pinLoginLayout);
        emailField = findViewById(R.id.editTextTextEmailAddress2);
        passwordField = findViewById(R.id.editTextTextPassword2);
        pinField = findViewById(R.id.editTextNumberPassword);
        loginButton = findViewById(R.id.button);
        emailLoginLayout.setVisibility(View.VISIBLE);
        pinLoginLayout.setVisibility(View.GONE);

        loginMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean emailSelected = checkedId == R.id.radioEmail;

            emailLoginLayout.setVisibility(emailSelected ? View.VISIBLE : View.GONE);
            pinLoginLayout.setVisibility(emailSelected ? View.GONE : View.VISIBLE);
        });

        loginButton.setOnClickListener(v -> performLogin(checkedID));
    }
    private void performLogin(int checkedID) {
        int checkedId = loginMethodGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radioEmail) {
            loginWithEmailPassword(emailField, passwordField);
        } else if (checkedId == R.id.radioPin) {
            loginWithPin();
        } else {
            Toast.makeText(this, "Please choose a login method.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginWithPin() {
        String pin2 = pinField.getText().toString().trim();

        if (!(pin2.length() == 4 || pin2.length() == 6)) {
            Toast.makeText(this, "Incorrect Pin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String storedPin = sharedPreferences.getString("user_pin", null);

            if (storedPin != null && storedPin.equals(pin2)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                // Start next activity or perform login success action here
                Intent intent = new Intent(LoginActivity.this, RelationshipStatusActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
            }

        } catch (GeneralSecurityException | IOException e) {
            Toast.makeText(this, "Error accessing secure storage", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
        }
    }

    private void loginWithEmailPassword(EditText emailField, EditText passwordField) {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password.", Toast.LENGTH_SHORT).show();
            return;
        }
//      loginButton.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, RelationshipStatusActivity.class);
                        // If you want to clear the back stack so user can't go back to Login:
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        finish();
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Authentication failed.";
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });

    }

}