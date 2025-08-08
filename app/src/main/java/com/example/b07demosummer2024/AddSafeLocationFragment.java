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

public class AddSafeLocationFragment extends Fragment {
    private static final String TAG = "AddSafeLocationFragment";
    private EditText editSafeLocationName, editAddress, editNote;
    private Button buttonUpdate;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private static SafeLocation safeLocationToEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_safe_location, container, false);

        editSafeLocationName = view.findViewById(R.id.editSafeLocationName);
        editAddress = view.findViewById(R.id.editAddress);
        editNote = view.findViewById(R.id.editNote);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        setButtonUpdateAvailability(true);

        if (safeLocationToEdit != null) {
            if(safeLocationToEdit.getSafeLocationName() != null && !(safeLocationToEdit.getSafeLocationName().isEmpty()))
                editSafeLocationName.setText(safeLocationToEdit.getSafeLocationName());
            if(safeLocationToEdit.getAddress() != null && !(safeLocationToEdit.getAddress().isEmpty()))
                editAddress.setText(safeLocationToEdit.getAddress());
            if(safeLocationToEdit.getNote() != null && !(safeLocationToEdit.getNote().isEmpty()))
                editNote.setText(safeLocationToEdit.getNote());
            buttonUpdate.setText("Edit Safe Location");
        } else {
            editSafeLocationName.setText("");
            editAddress.setText("");
            editNote.setText("");
            buttonUpdate.setText("Add Safe Location");
        }
        db = FirebaseDatabase.getInstance("https://sample-project-20250710-default-rtdb.firebaseio.com/");

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonUpdateAvailability(false);
                updateSafeLocation();
            }
        });

        return view;
    }

    private void updateSafeLocation() {
        String newSafeLocationName = editSafeLocationName.getText().toString().trim();
        String newAddress = editAddress.getText().toString().trim();
        String newNote = editNote.getText().toString().trim();
        String newId;
        SafeLocation newSafeLocation;

        if (newSafeLocationName.isEmpty() || newAddress.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out both the name and the address for Safe Location", Toast.LENGTH_SHORT).show();
            setButtonUpdateAvailability(true);
            return;
        }

        itemsRef = db.getReference("Emergency Information/Safe Location");
        if(safeLocationToEdit != null) {
            newId = safeLocationToEdit.getId();
        } else {
            newId = itemsRef.push().getKey();
        }
        if(newId != null) {
            newSafeLocation = new SafeLocation(newId, newSafeLocationName, newAddress, newNote);
            itemsRef.child(newId).setValue(newSafeLocation).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Safe Location updated", Toast.LENGTH_SHORT).show();
                    AddSafeLocationFragment.setSafeLocationToEdit(null);
                    getParentFragmentManager().popBackStack();
                } else {
                    Log.e(TAG, "Firebase setValue() failed");
                    Toast.makeText(getContext(), "Failed to add Safe Location due to database error.\n"
                            + "Please press the back button to go back to last screen.", Toast.LENGTH_LONG).show();
                    setButtonUpdateAvailability(true);
                }
            });
        } else {
            Log.e(TAG, "Firebase push().getKey() returned null");
            Toast.makeText(getContext(), "Failed to add Safe Location due to database error.\n"
                    + "Please press the back button to go back to last screen.", Toast.LENGTH_LONG).show();
            setButtonUpdateAvailability(true);
        }
        AddSafeLocationFragment.setSafeLocationToEdit(null);
    }

    private void setButtonUpdateAvailability(boolean availability) {
        buttonUpdate.setEnabled(availability);
        buttonUpdate.setAlpha(availability ? 1.0f : 0.5f);
    }

    public static void setSafeLocationToEdit(SafeLocation safeLocation) {
        safeLocationToEdit = safeLocation;
    }
}
