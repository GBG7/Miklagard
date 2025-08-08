package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_welcome; // required for BaseActivity
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button signUpBtn = findViewById(R.id.button_signup);
        Button loginBtn = findViewById(R.id.button_login);

        signUpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}