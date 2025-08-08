package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

public class TipsActivity extends BaseActivity {

    RecyclerView tipsRecyclerView;
    Button continueButton;
    TipAdapter adapter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_tips;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tipsRecyclerView = findViewById(R.id.tipsRecyclerView);
        continueButton = findViewById(R.id.continueButton);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TipsActivity.this, LoginActivity.class));
            finish();
            return;
        }

        String uid = user.getUid();
        DatabaseReference responsesRef = FirebaseDatabase.getInstance()
                .getReference("responses")
                .child(uid);

        responsesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, String> responses = new HashMap<>();
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        if (child.getValue() != null) {
                            responses.put(child.getKey(), child.getValue().toString());
                        }
                    }
                }

                List<String> tips = TipGenerator.generateTips(TipsActivity.this, responses);

                if (tips.isEmpty()) {
                    tips.add("Thank you for completing the questionnaire. Your responses have been saved.");
                }

                adapter = new TipAdapter(tips);
                tipsRecyclerView.setLayoutManager(new LinearLayoutManager(TipsActivity.this));
                tipsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(TipsActivity.this, "Failed to load tips.", Toast.LENGTH_SHORT).show();
                Log.e("TipsActivity", "Database error: " + error.getMessage());
            }
        });

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(TipsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}