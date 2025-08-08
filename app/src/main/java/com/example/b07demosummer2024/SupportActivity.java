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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class SupportActivity extends BaseActivity {

    RecyclerView recyclerView;
    Button continueButton;
    SupportItemAdapter adapter;

    private HashMap<String, List<SupportConnection>> supportMap;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_support_connections;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.recyclerView);
        continueButton = findViewById(R.id.continueButton);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SupportActivity.this, LoginActivity.class));
            finish();
            return;
        }

        loadSupportsFromJson();

        String uid = user.getUid();
        DatabaseReference responsesRef = FirebaseDatabase.getInstance()
                .getReference("responses")
                .child(uid);

        responsesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                String city;
                List<SupportConnection> supportList = new ArrayList<>();

                if(snapshot.exists()){
                    city= snapshot.child("q1").getValue(String.class);
                    supportList = supportMap.get(city);
                }

                List<SupportConnectionItem> itemList = new ArrayList<>();;

                for(SupportConnection s : supportList){
                    itemList.add(new SupportConnectionItem(s.getName(), s.getDescription(), s.getUrl()));
                }

                adapter = new SupportItemAdapter(itemList);
                recyclerView.setLayoutManager(new LinearLayoutManager(SupportActivity.this));
                recyclerView.setAdapter(adapter);
            }



            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SupportActivity.this, "Failed to load tips.", Toast.LENGTH_SHORT).show();
                Log.e("SupportActivity", "Database error: " + error.getMessage());
            }
        });

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(SupportActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadSupportsFromJson(){
        try{
            InputStream is = getResources().openRawResource(R.raw.support_directory);
            InputStreamReader reader = new InputStreamReader(is);
            Type type = new TypeToken<HashMap<String, List<SupportConnection>>>(){}.getType();
            supportMap = new Gson().fromJson(reader, type);
            reader.close();
            is.close();
        } catch (Exception e){
            e.printStackTrace();
            supportMap = new HashMap<>();
        }
    }
}