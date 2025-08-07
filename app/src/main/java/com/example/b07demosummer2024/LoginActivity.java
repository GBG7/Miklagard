package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class LoginActivity extends BaseActivity {

    private EditText emailEditText, passwordEditText, pinEditText;
    private Button loginButton;
    private Button forgotpwbutton;
    private RadioGroup radioGroup;
    private RadioButton radioEmail, radioPin;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        emailEditText = findViewById(R.id.editTextTextEmailAddress2);
        passwordEditText = findViewById(R.id.editTextTextPassword2);
        pinEditText = findViewById(R.id.editTextNumberPassword);
        loginButton = findViewById(R.id.button);

        radioGroup = findViewById(R.id.radioGroup);
        radioEmail = findViewById(R.id.radioEmail);
        radioPin = findViewById(R.id.radioPin);
        forgotpwbutton = findViewById(R.id.buttonForgotPassword);

        toggleLoginInputs();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> toggleLoginInputs());

        loginButton.setOnClickListener(v -> {
            if (radioEmail.isChecked()) {
                loginWithEmail();
            } else {
                loginWithPin();
            }
        });
        forgotpwbutton.setOnClickListener(v-> resetPW());
    }

    private void resetPW(){
        startActivity(new Intent(this, ResetPwActivity.class));
    }
    private void toggleLoginInputs() {
        if (radioEmail.isChecked()) {
            emailEditText.setVisibility(View.VISIBLE);
            passwordEditText.setVisibility(View.VISIBLE);
            pinEditText.setVisibility(View.GONE);
        } else {
            emailEditText.setVisibility(View.GONE);
            passwordEditText.setVisibility(View.GONE);
            pinEditText.setVisibility(View.VISIBLE);
        }
    }

    private void loginWithEmail() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        checkQuestionnaireCompletion(user.getUid());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void loginWithPin() {
        String pin = pinEditText.getText().toString().trim();
        if (pin.isEmpty()) {
            Toast.makeText(this, "Please enter your PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginManager.getInstance().checkPin(
                this,
                pin,
                uid -> checkQuestionnaireCompletion(uid),
                errorMsg -> Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show()
        );

    }

    private void checkQuestionnaireCompletion(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("responses").child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Intent intent;
                if (snapshot.exists() && snapshot.hasChildren()) {
                    intent = new Intent(LoginActivity.this, HomeActivity.class);
                } else {
                    intent = new Intent(LoginActivity.this, RelationshipStatusActivity.class);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error checking data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}