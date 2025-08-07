package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEmergencyContactFragment extends Fragment {
    private static final String TAG = "AddEmergencyContactFragment";
    private EditText editEmergencyContactName, editRelationship, editPhoneNumber;
    private Button buttonUpdate;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private static EmergencyContact emergencyContactToEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_emergency_contact, container, false);

        editEmergencyContactName = view.findViewById(R.id.editEmergencyContactName);
        editRelationship = view.findViewById(R.id.editRelationship);
        editPhoneNumber = view.findViewById(R.id.editPhoneNumber);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        setButtonUpdateAvailability(true);

        if (emergencyContactToEdit != null) {
            if(emergencyContactToEdit.getEmergencyContactName() != null && !(emergencyContactToEdit.getEmergencyContactName().isEmpty()))
                editEmergencyContactName.setText(emergencyContactToEdit.getEmergencyContactName());
            if(emergencyContactToEdit.getRelationship() != null && !(emergencyContactToEdit.getRelationship().isEmpty()))
                editRelationship.setText(emergencyContactToEdit.getRelationship());
            if(emergencyContactToEdit.getPhoneNumber() != null && !(emergencyContactToEdit.getPhoneNumber().isEmpty()))
                editPhoneNumber.setText(emergencyContactToEdit.getPhoneNumber());
            buttonUpdate.setText("Edit Emergency Contact");
        } else {
            editEmergencyContactName.setText("");
            editRelationship.setText("");
            editPhoneNumber.setText("");
            buttonUpdate.setText("Add Emergency Contact");
        }
        db = FirebaseDatabase.getInstance("https://sample-project-20250710-default-rtdb.firebaseio.com/");

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonUpdateAvailability(false);
                updateMEmergencyContact();
            }
        });

        return view;
    }

    private void updateMEmergencyContact() {
        String newEmergencyContactName = editEmergencyContactName.getText().toString().trim();
        String newRelationship = editRelationship.getText().toString().trim();
        String newPhoneNumber = editPhoneNumber.getText().toString().trim();
        String newId;
        EmergencyContact newEmergencyContact;

        if (newEmergencyContactName.isEmpty() || newRelationship.isEmpty() || newPhoneNumber.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            setButtonUpdateAvailability(true);
            return;
        }

        itemsRef = db.getReference("Emergency Information/Emergency Contact");
        if(emergencyContactToEdit != null) {
            newId = emergencyContactToEdit.getId();
        } else {
            newId = itemsRef.push().getKey();
        }
        if(newId != null) {
            newEmergencyContact = new EmergencyContact(newId, newEmergencyContactName, newRelationship, newPhoneNumber);
            itemsRef.child(newId).setValue(newEmergencyContact).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Emergency Contact updated", Toast.LENGTH_SHORT).show();
                    AddEmergencyContactFragment.setEmergencyContactToEdit(null);
                    getParentFragmentManager().popBackStack();
                } else {
                    Log.e(TAG, "Firebase setValue() failed");
                    Toast.makeText(getContext(), "Failed to add Emergency Contact due to database error.\n"
                            + "Please press the back button to go back to last screen.", Toast.LENGTH_LONG).show();
                    setButtonUpdateAvailability(true);
                }
            });
        } else {
            Log.e(TAG, "Firebase push().getKey() returned null");
            Toast.makeText(getContext(), "Failed to add Emergency Contact due to database error.\n"
                    + "Please press the back button to go back to last screen.", Toast.LENGTH_LONG).show();
            setButtonUpdateAvailability(true);
        }
        AddEmergencyContactFragment.setEmergencyContactToEdit(null);
    }

    private void setButtonUpdateAvailability(boolean availability) {
        buttonUpdate.setEnabled(availability);
        buttonUpdate.setAlpha(availability ? 1.0f : 0.5f);
    }

    public static void setEmergencyContactToEdit(EmergencyContact emergencyContact) {
        emergencyContactToEdit = emergencyContact;
    }
}
