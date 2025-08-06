package com.example.b07demosummer2024.data;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.b07demosummer2024.R;
import com.example.b07demosummer2024.RelationshipStatusActivity;
import com.example.b07demosummer2024.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class PinCreationActivity extends AppCompatActivity {
    private Button createButton;
    private EditText pin;
    private TextView errorText;
    private TextView doneText;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pin_creation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createButton = findViewById(R.id.button5);
        pin = findViewById(R.id.editTextNumberPassword2);
        errorText = findViewById(R.id.textView4);
        doneText = findViewById(R.id.textView5);

        errorText.setVisibility(View.GONE);
        doneText.setVisibility(View.GONE);

        user = FirebaseAuth.getInstance().getCurrentUser(); // ✅ correct assignment to the field

        createButton.setOnClickListener(v -> savePinSecurely());
    }

    private void savePinSecurely() {
        String pin2 = pin.getText().toString().trim();

        if (!(pin2.length() == 4 || pin2.length() == 6)) {
            errorText.setVisibility(View.VISIBLE);
            doneText.setVisibility(View.GONE);
            Toast.makeText(this, "PIN must be 4 or 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        errorText.setVisibility(View.GONE);

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

            sharedPreferences.edit().putString("user_pin", pin2).apply();

            if (user != null) {
                user.reload().addOnCompleteListener(task -> {
                    if (user.isEmailVerified()) {
                        doneText.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "PIN saved securely!", Toast.LENGTH_SHORT).show();

                        String storedPin = sharedPreferences.getString("user_pin", null);
                        if (storedPin != null && !storedPin.isEmpty()) {
                            Intent intent = new Intent(PinCreationActivity.this, RelationshipStatusActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        pin.setText("");
                    } else {
                        Toast.makeText(this, "Please verify your email before proceeding.", Toast.LENGTH_LONG).show();
//                        FirebaseAuth.getInstance().signOut(); // optional: force logout
//                        Intent intent = new Intent(PinCreationActivity.this, SignUpActivity.class);
//                        startActivity(intent);
//                        finish();
                    }
                });
            }

        } catch (GeneralSecurityException | IOException e) {
            Toast.makeText(this, "Error saving PIN securely", Toast.LENGTH_SHORT).show();
        }
    }
}
