package com.example.b07demosummer2024;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.InputStream;

public class TipsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TipAdapter adapter;
    List<String> tips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tips = loadTipsFromFirebaseOrJson(); // Replace this with actual Firebase data logic
        adapter = new TipAdapter(tips);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.buttonContinue).setOnClickListener(v -> {
            startActivity(new Intent(TipsActivity.this, HomeActivity.class));
            finish();
        });
    }

    private List<String> loadTipsFromFirebaseOrJson() {
        List<String> tipsList = new ArrayList<>();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.questionnaire);
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String jsonString = scanner.hasNext() ? scanner.next() : "";

            JSONObject json = (JSONObject) new JSONTokener(jsonString).nextValue();

            JSONArray keys = json.names();
            if (keys != null) {
                for (int i = 0; i < keys.length(); i++) {
                    String category = keys.getString(i);
                    // Now you can safely use the `category` key
                    JSONArray tipsArray = json.getJSONArray(category);
                    for (int j = 0; j < tipsArray.length(); j++) {
                        tips.add(tipsArray.getString(j));
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading tips", Toast.LENGTH_SHORT).show();
        }
        return tipsList;
    }
}