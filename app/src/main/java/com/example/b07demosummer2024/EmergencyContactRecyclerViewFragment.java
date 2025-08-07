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

public class EmergencyContactRecyclerViewFragment extends Fragment
        implements EmergencyContactAdapter.OnEmergencyContactOptionsClickListener {
    private static final String TAG = "EmergencyContactRecyclerViewFragment";
    private RecyclerView recyclerView;
    private EmergencyContactAdapter emergencyContactAdapter;
    private List<EmergencyContact> emergencyContactList;
    private Spinner spinnerCategory;
    private static final int INITIAL_SPINNER_POSITION = 1;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AddEmergencyContactFragment.setEmergencyContactToEdit(null);
        View view = inflater.inflate(R.layout.fragment_recycler_view_emergency_information, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.emergency_information_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);


        Button buttonAddEmergencyInformation = view.findViewById(R.id.buttonAddEmergencyInformation);
        buttonAddEmergencyInformation.setText("Add Emergency Contact");

        emergencyContactList = new ArrayList<>();
        emergencyContactAdapter = new EmergencyContactAdapter(emergencyContactList, getContext(), this);
        recyclerView.setAdapter(emergencyContactAdapter);

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
                        fetchEmergencyContactFromDatabase();
                        break;
                    case 2:
                        getParentFragmentManager().popBackStack();
                        loadFragment(new SafeLocationRecyclerViewFragment());
                        break;
                    case 3:
                        getParentFragmentManager().popBackStack();
                        loadFragment(new MedicationRecyclerViewFragment());
                        break;
                    default:
                        fetchEmergencyContactFromDatabase();
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
                AddEmergencyContactFragment.setEmergencyContactToEdit(null);
                loadFragment(new AddEmergencyContactFragment());
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

    private void fetchEmergencyContactFromDatabase() {
        itemsRef = db.getReference("Emergency Information/Emergency Contact");

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                emergencyContactList.clear();

                for (DataSnapshot emergencyContactSnapshot : dataSnapshot.getChildren()) {
                    String newId = emergencyContactSnapshot.getKey();
                    String newEmergencyContactName = emergencyContactSnapshot.child("emergencyContactName").getValue(String.class);
                    String newRelationship = emergencyContactSnapshot.child("relationship").getValue(String.class);
                    String newPhoneNumber = emergencyContactSnapshot.child("phoneNumber").getValue(String.class);

                    if (newEmergencyContactName != null && newRelationship != null && newPhoneNumber != null) {
                        EmergencyContact emergencyContact = new EmergencyContact(newId, newEmergencyContactName, newRelationship, newPhoneNumber);
                        emergencyContactList.add(emergencyContact);
                    }
                }
                emergencyContactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching Emergency Contacts: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onEditClick(EmergencyContact emergencyContact, int position) {
        AddEmergencyContactFragment.setEmergencyContactToEdit(emergencyContact);
        loadFragment(new AddEmergencyContactFragment());
    }

    @Override
    public void onDeleteClick(EmergencyContact emergencyContact, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Emergency Contact")
                .setMessage("Are you sure you want to delete " + emergencyContact.getEmergencyContactName() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            itemsRef = db.getReference("Emergency Information/Emergency Contact");
                            itemsRef.child(emergencyContact.getId()).removeValue();
                            emergencyContactList.remove(position);
                            emergencyContactAdapter.notifyItemRemoved(position);
                            emergencyContactAdapter.notifyItemRangeChanged(position, emergencyContactList.size());
                            Toast.makeText(getContext(), "Emergency Contact deleted successfully", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error deleting Emergency Contact: " + e.getMessage());
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
