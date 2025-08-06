package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsCompat.Type;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import androidx.annotation.NonNull;
//import androidx.core.view.WindowInsetsCompat.Insets;
//import androidx.core.view.EdgeToEdge;


public class ResetPwActivity extends AppCompatActivity {
    private Button resetPWButton;
    private TextView confirmText;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_pw);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resetPWButton = findViewById(R.id.resetPasswordButton);
        confirmText = findViewById(R.id.emailSentMessage);
        email = findViewById(R.id.emailInput);
        confirmText.setVisibility(View.GONE);

        resetPWButton.setOnClickListener(v-> resetPW());
    }
    private static final String TAG = "ResetPwActivity";

    private void resetPW() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email2 = email.getText().toString().trim();

        if (email2.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.sendPasswordResetEmail(email2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password reset email sent.");
                            confirmText.setVisibility(View.VISIBLE);
                            confirmText.setText("Check your inbox for a reset link.");
                            Toast.makeText(ResetPwActivity.this, "Reset link sent to email", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error sending reset email", task.getException());
                            Toast.makeText(ResetPwActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}