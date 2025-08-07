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

public class MedicationRecyclerViewFragment extends Fragment
        implements MedicationAdapter.OnMedicationOptionsClickListener {
    private static final String TAG = "MedicationRecyclerViewFragment";
    private RecyclerView recyclerView;
    private MedicationAdapter medicationAdapter;
    private List<Medication> medicationList;
    private Spinner spinnerCategory;
    private static final int INITIAL_SPINNER_POSITION = 3;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AddMedicationFragment.setMedicationToEdit(null);
        View view = inflater.inflate(R.layout.fragment_recycler_view_emergency_information, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.emergency_information_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);


        Button buttonAddEmergencyInformation = view.findViewById(R.id.buttonAddEmergencyInformation);
        buttonAddEmergencyInformation.setText("Add Medication");

        medicationList = new ArrayList<>();
        medicationAdapter = new MedicationAdapter(medicationList, getContext(), this);
        recyclerView.setAdapter(medicationAdapter);

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
                        getParentFragmentManager().popBackStack();
                        loadFragment(new SafeLocationRecyclerViewFragment());
                        break;
                    case 3:
                        fetchMedicationFromDatabase();
                        break;
                    default:
                        fetchMedicationFromDatabase();
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
                AddMedicationFragment.setMedicationToEdit(null);
                loadFragment(new AddMedicationFragment());
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

    private void fetchMedicationFromDatabase() {
        itemsRef = db.getReference("Emergency Information/Medication");

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicationList.clear();

                for (DataSnapshot medicationSnapshot : dataSnapshot.getChildren()) {
                    String newId = medicationSnapshot.getKey();
                    String newMedicationName = medicationSnapshot.child("medicationName").getValue(String.class);
                    String newDosage = medicationSnapshot.child("dosage").getValue(String.class);

                    if (newMedicationName != null && newDosage != null) {
                        Medication medication = new Medication(newId, newMedicationName, newDosage);
                        medicationList.add(medication);
                    }
                }
                medicationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching Medications: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onEditClick(Medication medication, int position) {
        AddMedicationFragment.setMedicationToEdit(medication);
        loadFragment(new AddMedicationFragment());
    }

    @Override
    public void onDeleteClick(Medication medication, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Medication")
                .setMessage("Are you sure you want to delete " + medication.getMedicationName() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            itemsRef = db.getReference("Emergency Information/Medication");
                            itemsRef.child(medication.getId()).removeValue();
                            medicationList.remove(position);
                            medicationAdapter.notifyItemRemoved(position);
                            medicationAdapter.notifyItemRangeChanged(position, medicationList.size());
                            Toast.makeText(getContext(), "Medication deleted successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                                    Log.e(TAG, "Error deleting Medication: " + e.getMessage());
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
