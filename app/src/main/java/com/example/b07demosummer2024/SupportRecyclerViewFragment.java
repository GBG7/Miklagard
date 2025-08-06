package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SupportRecyclerViewFragment extends Fragment{
    private RecyclerView recyclerView;
    private SupportItemAdapter itemAdapter;
    private List<SupportConnectionItem> itemList;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    private HashMap<String, List<SupportConnection>> supportMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support_recycler_view, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        itemAdapter = new SupportItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);


        db = FirebaseDatabase.getInstance("https://sample-project-20250710-default-rtdb.firebaseio.com/");

        loadSupportsFromJson();




        fetchItemsFromDatabase();

        return view;
    }

    private void fetchItemsFromDatabase() {
        itemsRef = db.getReference("Questionnaire/Warm-up/");
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String city = dataSnapshot.child("q1").getValue(String.class);
                List<SupportConnection> supportList = supportMap.get(city);


                itemList.clear();

                for(SupportConnection s : supportList){

                    itemList.add(new SupportConnectionItem(s.getName(), s.getDescription(), s.getUrl()));
                }

                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
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
