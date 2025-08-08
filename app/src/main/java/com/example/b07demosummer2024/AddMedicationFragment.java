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

public class AddMedicationFragment extends Fragment {
    private static final String TAG = "AddMedicationFragment";
    private EditText editMedicationName, editDosage;
    private Button buttonUpdate;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private static Medication medicationToEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_medication, container, false);

        editMedicationName = view.findViewById(R.id.editMedicationName);
        editDosage = view.findViewById(R.id.editDosage);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        setButtonUpdateAvailability(true);

        if (medicationToEdit != null) {
            if(medicationToEdit.getMedicationName() != null && !(medicationToEdit.getMedicationName().isEmpty()))
                editMedicationName.setText(medicationToEdit.getMedicationName());
            if(medicationToEdit.getDosage() != null && !(medicationToEdit.getDosage().isEmpty()))
                editDosage.setText(medicationToEdit.getDosage());
            buttonUpdate.setText("Edit Medication");
        } else {
            editMedicationName.setText("");
            editDosage.setText("");
            buttonUpdate.setText("Add Medication");
        }
        db = FirebaseDatabase.getInstance("https://sample-project-20250710-default-rtdb.firebaseio.com/");

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonUpdateAvailability(false);
                updateMedication();
            }
        });

        return view;
    }

    private void updateMedication() {
        String newMedicationName = editMedicationName.getText().toString().trim();
        String newDosage = editDosage.getText().toString().trim();
        String newId;
        Medication newMedication;

        if (newMedicationName.isEmpty() || newDosage.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            setButtonUpdateAvailability(true);
            return;
        }

        itemsRef = db.getReference("Emergency Information/Medication");
        if(medicationToEdit != null) {
            newId = medicationToEdit.getId();
        } else {
            newId = itemsRef.push().getKey();
        }
        if(newId != null) {
            newMedication = new Medication(newId, newMedicationName, newDosage);
            itemsRef.child(newId).setValue(newMedication).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Medication updated", Toast.LENGTH_SHORT).show();
                    AddMedicationFragment.setMedicationToEdit(null);
                    getParentFragmentManager().popBackStack();
                } else {
                    Log.e(TAG, "Firebase setValue() failed");
                    Toast.makeText(getContext(), "Failed to add Medication due to database error.\n"
                            + "Please press the back button to go back to last screen.", Toast.LENGTH_LONG).show();
                    setButtonUpdateAvailability(true);
                }
            });
        } else {
            Log.e(TAG, "Firebase push().getKey() returned null");
            Toast.makeText(getContext(), "Failed to add Medication due to database error.\n"
                    + "Please press the back button to go back to last screen.", Toast.LENGTH_LONG).show();
            setButtonUpdateAvailability(true);
        }
        AddMedicationFragment.setMedicationToEdit(null);
    }

    private void setButtonUpdateAvailability(boolean availability) {
        buttonUpdate.setEnabled(availability);
        buttonUpdate.setAlpha(availability ? 1.0f : 0.5f);
    }

    public static void setMedicationToEdit(Medication medication) {
        medicationToEdit = medication;
    }
}
