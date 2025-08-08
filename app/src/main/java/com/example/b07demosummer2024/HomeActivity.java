package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference responsesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            // Redirect to login if not signed in
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        responsesRef = FirebaseDatabase.getInstance().getReference("responses").child(user.getUid());

        Button btnTips = findViewById(R.id.btnTips);
        Button btnReminders = findViewById(R.id.btnReminders);
        Button btnUpdatePlan = findViewById(R.id.btnUpdatePlan);
        Button btnEmergency = findViewById(R.id.btnEmergency);
        Button btnExit = findViewById(R.id.btnExit);

        btnTips.setOnClickListener(v -> loadTipsAndOpenActivity());

        btnReminders.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ReminderActivity.class)));

        btnUpdatePlan.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, RelationshipStatusActivity.class)));

        btnEmergency.setOnClickListener(v -> {
            // Optionally add emergency info here
        });

        btnExit.setOnClickListener(v -> finishAffinity());
    }

    private void loadTipsAndOpenActivity() {
        responsesRef.child("tips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> tipsList = new ArrayList<>();
                for (DataSnapshot tipSnapshot : snapshot.getChildren()) {
                    String tip = tipSnapshot.getValue(String.class);
                    if (tip != null) {
                        tipsList.add(tip);
                    }
                }

                if (tipsList.isEmpty()) {
                    Toast.makeText(HomeActivity.this, "No tips found for your responses.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(HomeActivity.this, TipsActivity.class);
                    intent.putStringArrayListExtra("tips", tipsList);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Failed to load tips: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }
}