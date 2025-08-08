package com.example.b07demosummer2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.security.GeneralSecurityException;

/** Creates or updates a 4- or 6-digit PIN in EncryptedSharedPreferences, then routes the user. */
public class PinCreationActivity extends BaseActivity {

    private Button   createBtn;
    private EditText pinField;
    private TextView statusTxt;

    private FirebaseUser user;

    @Override protected int getLayoutResourceId() {
        return R.layout.activity_pin_creation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        createBtn = findViewById(R.id.button5);
        pinField  = findViewById(R.id.editTextNumberPassword2);
        statusTxt = findViewById(R.id.textStatus);

        // allow up to 6 digits in the field; logic below enforces 4 OR 6
        pinField.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(6) });
        statusTxt.setVisibility(View.GONE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        createBtn.setOnClickListener(v -> savePinSecurely());
    }

    private void savePinSecurely() {
        String pin = pinField.getText().toString().trim();

        if (!(pin.length() == 4 || pin.length() == 6)) {
            statusTxt.setText("PIN must be 4 or 6 digits");
            statusTxt.setVisibility(View.VISIBLE);
            Toast.makeText(this, "PIN must be 4 or 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            MasterKey key = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences prefs = EncryptedSharedPreferences.create(
                    this,
                    "secure_prefs",
                    key,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );


//            prefs.edit().putString("user_pin", pin).apply();
            prefs.edit()
                    .putString("user_pin", pin)
                    .putString("user_uid", user.getUid())
                    .apply();


            if (user == null) {
                Toast.makeText(this,"User not logged in.",Toast.LENGTH_LONG).show();
                return;
            }

            user.reload().addOnCompleteListener(rt -> {
                if (!user.isEmailVerified()) {
                    // Optionally nudge them again
                    user.sendEmailVerification();
                    statusTxt.setText("Email not verified. Check your inbox for the verification link.");
                    statusTxt.setVisibility(View.VISIBLE);
                    return;
                }

                statusTxt.setText("PIN saved securely!");
                statusTxt.setVisibility(View.VISIBLE);
                Toast.makeText(this,"PIN saved securely!",Toast.LENGTH_SHORT).show();

                // Decide next screen based on whether they already have responses
                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference("responses")
                        .child(user.getUid());

                ref.get().addOnCompleteListener(resTask -> {
                    Class<?> next = (resTask.isSuccessful() && resTask.getResult().exists())
                            ? TipsActivity.class
                            : RelationshipStatusActivity.class;
                    startActivity(new Intent(this, next));
                    finish();
                });
            });

        } catch (GeneralSecurityException | IOException ex) {
            Toast.makeText(this,"Error saving PIN securely",Toast.LENGTH_SHORT).show();
        }
    }
}
