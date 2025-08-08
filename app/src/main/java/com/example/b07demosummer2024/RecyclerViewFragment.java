package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ListIterator;

public class RecyclerViewFragment extends Fragment {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private Spinner spinnerCategory;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    private HashMap<String, List<Question>> questionMap;
    private String branchSpecificName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);


        db = FirebaseDatabase.getInstance("https://sample-project-20250710-default-rtdb.firebaseio.com/");

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();
                if(position == 1)
                    category = branchSpecificName;

                loadQuestionsFromJson();

                fetchItemsFromDatabase(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return view;
    }

    private void fetchItemsFromDatabase(String category) {
        itemsRef = db.getReference("Questionnaire/"+category);
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = "";
                String tip;
                List<Question> questions = questionMap.get(category);
                Map<String, String> hm = new HashMap<>();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getValue() instanceof List){
                        String value = "";
                        for(DataSnapshot s : snapshot.getChildren()){
                            value += s.getValue(String.class);
                            
                        }
                        hm.put(snapshot.getKey(), value);
                    } else
                        hm.put(snapshot.getKey(), snapshot.getValue(String.class));
                }


                itemList.clear();

                for(Question q : questions){

                    if(q.getTip() != null){
                        tip = q.getTip();
                    } else {
                        key = hm.get(q.getId());
                        tip = q.getOptionToTip().get(key);
                    }
                    if(q.getId().equals("q0")) branchSpecificName = key;
                    for(String i : hm.keySet()){
                        tip = tip.replace("{" + i + "}", hm.get(i));
                    }
                    itemList.add(new Item(q.getId() + ": " + tip));
                }

                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void loadQuestionsFromJson(){
        try{
            InputStream is = getResources().openRawResource(R.raw.questionnaire);
            InputStreamReader reader = new InputStreamReader(is);
            Type type = new TypeToken<HashMap<String, List<Question>>>(){}.getType();
            questionMap = new Gson().fromJson(reader, type);
            reader.close();
            is.close();
        } catch (Exception e){
            e.printStackTrace();
            questionMap = new HashMap<>();
        }
    }


}
