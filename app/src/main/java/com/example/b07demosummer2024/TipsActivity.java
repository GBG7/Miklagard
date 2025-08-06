package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TipsActivity extends AppCompatActivity {

    RecyclerView tipsRecyclerView;
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

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

}