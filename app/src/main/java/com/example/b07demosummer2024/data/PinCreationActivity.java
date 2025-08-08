//package com.example.b07demosummer2024.data;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.security.crypto.EncryptedSharedPreferences;
//import androidx.security.crypto.MasterKey;
//
//import com.example.b07demosummer2024.BaseActivity;
//import com.example.b07demosummer2024.R;
//import com.example.b07demosummer2024.RelationshipStatusActivity;
//import com.example.b07demosummer2024.TipsActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.DatabaseReference;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//
///** Creates or updates a 4- or 6-digit PIN, stores it securely, then routes the user forward. */
//public class PinCreationActivity extends BaseActivity {
//
//    /* ---------- view ids ---------- */
//    private Button   createBtn;
//    private EditText pinField;
//    private TextView errorTxt, doneTxt;
//
//    private FirebaseUser user;
//
//    /* The child layout injected by BaseActivity */
//    @Override protected int getLayoutResourceId() {
//        return R.layout.activity_pin_creation;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//
//        // Edge-to-Edge padding
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v,insets)->{
//            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(bars.left,bars.top,bars.right,bars.bottom);
//            return insets;
//        });
//
//        /* ---- view look-ups ---- */
//        createBtn = findViewById(R.id.button5);
//        pinField  = findViewById(R.id.editTextNumberPassword2);
//        errorTxt  = findViewById(R.id.textView4);
//        doneTxt   = findViewById(R.id.textView5);
//
//        errorTxt.setVisibility(TextView.GONE);
//        doneTxt .setVisibility(TextView.GONE);
//
//        user = FirebaseAuth.getInstance().getCurrentUser();
//
//        createBtn.setOnClickListener(v -> savePinSecurely());
//    }
//
//    /* -------------------------------------------------------------------------- */
//
//    private void savePinSecurely() {
//        String pin = pinField.getText().toString().trim();
//
//        if (!(pin.length() == 4 || pin.length() == 6)) {
//            errorTxt.setVisibility(TextView.VISIBLE);
//            doneTxt .setVisibility(TextView.GONE);
//            Toast.makeText(this,"PIN must be 4 or 6 digits",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        errorTxt.setVisibility(TextView.GONE);
//
//        try {
//            /** ---------- encrypted SharedPreferences ---------- */
//            MasterKey key = new MasterKey.Builder(this)
//                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//                    .build();
//
//            SharedPreferences prefs = EncryptedSharedPreferences.create(
//                    this,
//                    "secure_prefs",
//                    key,
//                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
//
//            prefs.edit().putString("user_pin", pin).apply();
//
//            /** ---------- proceed only when e-mail is verified ---------- */
//            if (user == null) {
//                Toast.makeText(this,"User not logged in.",Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            user.reload().addOnCompleteListener(reloadTask -> {
//                if (!user.isEmailVerified()) {
//                    Toast.makeText(this,"Please verify your e-mail first.",Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                doneTxt.setVisibility(TextView.VISIBLE);
//                Toast.makeText(this,"PIN saved securely!",Toast.LENGTH_SHORT).show();
//
//                /* ---------- Has the user already completed the questionnaire? ---------- */
//                DatabaseReference ref = FirebaseDatabase.getInstance()
//                        .getReference("responses")
//                        .child(user.getUid());
//
//                ref.get().addOnCompleteListener(resTask -> {
//                    Class<?> next =
//                            (resTask.isSuccessful() && resTask.getResult().exists())
//                                    ? TipsActivity.class              // already answered → tips
//                                    : RelationshipStatusActivity.class; // fresh → questionnaire
//                    startActivity(new Intent(this, next));
//                    finish();
//                });
//            });
//
//        } catch (GeneralSecurityException | IOException ex) {
//            Toast.makeText(this,"Error saving PIN securely",Toast.LENGTH_SHORT).show();
//        }
//    }
//}
