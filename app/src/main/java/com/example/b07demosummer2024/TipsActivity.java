package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class TipsActivity extends BaseActivity {

    RecyclerView tipsRecyclerView;
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // 🔒 User not signed in — redirect to LoginActivity
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TipsActivity.this, LoginActivity.class));
            finish();
            return;
        }

        tipsRecyclerView = findViewById(R.id.tipsRecyclerView);
        continueButton = findViewById(R.id.continueButton);

        List<String> tips = getIntent().getStringArrayListExtra("tips");
        TipAdapter adapter = new TipAdapter(tips);
        tipsRecyclerView.setAdapter(adapter);
        tipsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(TipsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_tips;
    }
}