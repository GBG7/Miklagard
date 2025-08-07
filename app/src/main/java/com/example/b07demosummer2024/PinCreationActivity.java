package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class PinCreationActivity extends BaseActivity {

    private static final String TAG = "PinCreationActivity";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pin_creation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView4 = findViewById(R.id.textView4);
        TextView textView5 = findViewById(R.id.textView5);
        EditText pinEditText = findViewById(R.id.editTextNumberPassword2);
        Button continueButton = findViewById(R.id.button5);

        if (pinEditText == null || continueButton == null) {
            Log.e(TAG, "One or more required views are missing. Check your XML IDs.");
            Toast.makeText(this, "Layout error. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        continueButton.setOnClickListener(v -> {
            String pin = pinEditText.getText().toString().trim();
            if (pin.length() != 4) {
                Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Log.e(TAG, "Firebase user is null");
                Toast.makeText(this, "User not logged in. Please restart app.", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = user.getUid();
            FirebaseDatabase.getInstance()
                    .getReference("pins")
                    .child(uid)
                    .child("pin")
                    .setValue(pin)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "PIN saved successfully");
                        Intent intent = new Intent(PinCreationActivity.this, RelationshipStatusActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save PIN: " + e.getMessage());
                        Toast.makeText(this, "Failed to save PIN", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}