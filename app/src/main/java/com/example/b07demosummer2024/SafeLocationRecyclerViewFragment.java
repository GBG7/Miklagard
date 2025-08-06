package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SafeLocationRecyclerViewFragment extends Fragment
        implements SafeLocationAdapter.OnSafeLocationOptionsClickListener {
    private static final String TAG = "SafeLocationRecyclerViewFragment";
    private RecyclerView recyclerView;
    private SafeLocationAdapter safeLocationAdapter;
    private List<SafeLocation> safeLocationList;
    private Spinner spinnerCategory;
    private static final int INITIAL_SPINNER_POSITION = 2;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AddSafeLocationFragment.setSafeLocationToEdit(null);
        View view = inflater.inflate(R.layout.fragment_recycler_view_emergency_information, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.emergency_information_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);


        Button buttonAddEmergencyInformation = view.findViewById(R.id.buttonAddEmergencyInformation);
        buttonAddEmergencyInformation.setText("Add Safe Location");

        safeLocationList = new ArrayList<>();
        safeLocationAdapter = new SafeLocationAdapter(safeLocationList, getContext(), this);
        recyclerView.setAdapter(safeLocationAdapter);

        db = FirebaseDatabase.getInstance("https://sample-project-20250710-default-rtdb.firebaseio.com/");

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:;
                        getParentFragmentManager().popBackStack();
                        loadFragment(new DocumentRecyclerViewFragment());
                        break;
                    case 1:
                        getParentFragmentManager().popBackStack();
                        loadFragment(new EmergencyContactRecyclerViewFragment());
                        break;
                    case 2:
                        fetchSafeLocationFromDatabase();
                        break;
                    case 3:
                        getParentFragmentManager().popBackStack();
                        loadFragment(new MedicationRecyclerViewFragment());
                        break;
                    default:
                        fetchSafeLocationFromDatabase();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        buttonAddEmergencyInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSafeLocationFragment.setSafeLocationToEdit(null);
                loadFragment(new AddSafeLocationFragment());
            }
        });

        if (adapter.getCount() > INITIAL_SPINNER_POSITION) {
            spinnerCategory.setSelection(INITIAL_SPINNER_POSITION, true);
        } else {
            Log.w(TAG, "Initial spinner position is out of bounds for the adapter.");
            if (adapter.getCount() > 0) {
                spinnerCategory.setSelection(0, true);
            }
        }

        return view;
    }

    private void fetchSafeLocationFromDatabase() {
        itemsRef = db.getReference("Emergency Information/Safe Location");

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                safeLocationList.clear();

                for (DataSnapshot safeLocationSnapshot : dataSnapshot.getChildren()) {
                    String newId = safeLocationSnapshot.getKey();
                    String newSafeLocationName = safeLocationSnapshot.child("safeLocationName").getValue(String.class);
                    String newAddress = safeLocationSnapshot.child("address").getValue(String.class);
                    String newNote = safeLocationSnapshot.child("note").getValue(String.class);

                    if (newSafeLocationName != null && newAddress != null) {
                        SafeLocation safeLocation = new SafeLocation(newId, newSafeLocationName, newAddress, newNote);
                        safeLocationList.add(safeLocation);
                    }
                }
                safeLocationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching safe locations: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onEditClick(SafeLocation safeLocation, int position) {
        AddSafeLocationFragment.setSafeLocationToEdit(safeLocation);
        loadFragment(new AddSafeLocationFragment());
    }

    @Override
    public void onDeleteClick(SafeLocation safeLocation, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Safe Location")
                .setMessage("Are you sure you want to delete " + safeLocation.getSafeLocationName() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            itemsRef = db.getReference("Emergency Information/Safe Location");
                            itemsRef.child(safeLocation.getId()).removeValue();
                            safeLocationList.remove(position);
                            safeLocationAdapter.notifyItemRemoved(position);
                            safeLocationAdapter.notifyItemRangeChanged(position, safeLocationList.size());
                            Toast.makeText(getContext(), "Safe location deleted successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error deleting safe location: " + e.getMessage());
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
