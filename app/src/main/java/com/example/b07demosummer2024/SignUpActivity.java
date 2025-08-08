package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.b07demosummer2024.PinCreationActivity;


public class SignUpActivity extends BaseActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonSignUp, loginButton;
    private FirebaseAuth auth;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_signup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        loginButton = findViewById(R.id.button2);

        buttonSignUp.setOnClickListener(v -> signUpUser());
        loginButton.setOnClickListener(v -> goToLogin());
    }

    private void signUpUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            startActivity(new Intent(this, PinCreationActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to retrieve user after sign up.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("SignUp", "Sign-up failed", task.getException());
                        Toast.makeText(this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
