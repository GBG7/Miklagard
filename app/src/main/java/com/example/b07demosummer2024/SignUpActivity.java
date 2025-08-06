package com.example.b07demosummer2024;


import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.example.b07demosummer2024.data.PinCreationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.util.Log;
import android.content.Intent;
import android.widget.VideoView;


public class SignUpActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";
    private EditText emailField;
    private EditText passwordField;
    private Button signUpButton;
    private Button loginButton;
    private Button forgotPWButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.editTextTextEmailAddress);
        passwordField = findViewById(R.id.editTextTextPassword);
        signUpButton = findViewById(R.id.button2);
        loginButton = findViewById(R.id.button3);
        forgotPWButton = findViewById(R.id.button4);
        signUpButton.setOnClickListener(v -> signUpUser(emailField, passwordField));
        loginButton.setOnClickListener(v -> loginUser());
        forgotPWButton.setOnClickListener(v -> resetPW());
//        VideoView gifView = findViewById(R.id.loginGifView);
//        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.emoji_dance);
//
//        gifView.setVideoURI(videoUri);
//        gifView.setOnPreparedListener(mp -> {
//            mp.setLooping(true);
//            gifView.start();
//        });

        VideoView csgoSurfView = findViewById(R.id.csgoSurfView);
        Uri csgoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.csgo_surf);

        csgoSurfView.setVideoURI(csgoUri);
        csgoSurfView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            csgoSurfView.start();
        });
    }
    private void signUpUser(EditText emailField, EditText passwordField) {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email n password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null && !user.isEmailVerified()) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verifyTask -> {
                                        if (verifyTask.isSuccessful()) {
                                            Toast.makeText(this, "Verification email sent to " + email + ". Please verify before logging in.", Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(SignUpActivity.this, PinCreationActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loginUser(){
        Intent intent = new Intent(SignUpActivity.this,
                com.example.b07demosummer2024.LoginActivity.class);
        startActivity(intent);
    }
    private void resetPW(){
        Intent intent = new Intent(SignUpActivity.this,
                com.example.b07demosummer2024.ResetPwActivity.class);
        startActivity(intent);
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_signup;
    }
}