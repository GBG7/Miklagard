package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.b07demosummer2024.data.PinCreationActivity;

public class LoginActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        Button loginWithPinButton = findViewById(R.id.login_with_pin);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show();
            } else {
                // For now, assume any login is valid
                Intent intent = new Intent(this, RelationshipStatusActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }
        });

        loginWithPinButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PinCreationActivity.class);
            startActivity(intent);
        });
    }
}