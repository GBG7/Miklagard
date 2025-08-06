package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnTips = findViewById(R.id.btnTips);
        Button btnReminders = findViewById(R.id.btnReminders);
        Button btnUpdatePlan = findViewById(R.id.btnUpdatePlan);
        Button btnEmergency = findViewById(R.id.btnEmergency);
        Button btnExit = findViewById(R.id.btnExit);

        btnTips.setOnClickListener(v -> startActivity(new Intent(this, TipsActivity.class)));
        btnReminders.setOnClickListener(v -> startActivity(new Intent(this, ReminderActivity.class)));
        btnUpdatePlan.setOnClickListener(v -> startActivity(new Intent(this, RelationshipStatusActivity.class)));
        btnEmergency.setOnClickListener(v -> {
            // Optional: Start an emergency info page or display a dialog
        });
        btnExit.setOnClickListener(v -> finishAffinity());
    }
}