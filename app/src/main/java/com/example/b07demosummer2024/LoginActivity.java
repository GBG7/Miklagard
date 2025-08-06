package com.example.b07demosummer2024;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

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
    private EditText pinField;
    private Button loginButton;
    private FirebaseAuth mAuth;

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

        loginButton.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        int checkedId = loginMethodGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radioEmail) {
            loginWithEmailPassword();
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
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    checkIfQuestionnaireCompleted(user.getUid());
                } else {
                    startActivity(new Intent(LoginActivity.this, RelationshipStatusActivity.class));
                    finish();
                }
            } else {
                Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
            }

        } catch (GeneralSecurityException | IOException e) {
            Toast.makeText(this, "Error accessing secure storage", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginWithEmailPassword() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loginButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkIfQuestionnaireCompleted(user.getUid());
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Authentication failed.";
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkIfQuestionnaireCompleted(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("responses").child(uid);
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // ✅ Questionnaire exists → go to Home
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            } else {
                // ❌ Questionnaire missing → go to questionnaire
                startActivity(new Intent(LoginActivity.this, RelationshipStatusActivity.class));
            }
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Already signed in → go to HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}