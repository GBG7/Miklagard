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

public class EditDocumentFragment extends Fragment {
    private static final String TAG = "EditDocumentFragment";
    private EditText editDocumentTitle, editDescription;
    private Button buttonUpdate;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private static Document documentToEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_document, container, false);

        editDocumentTitle = view.findViewById(R.id.editDocumentTitle);
        editDescription = view.findViewById(R.id.editDocumentDescription);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        setButtonUpdateAvailability(true);
        editDocumentTitle.setText(documentToEdit.getDocumentTitle());
        editDescription.setText(documentToEdit.getDescription());

        db = FirebaseDatabase.getInstance("https://sample-project-20250710-default-rtdb.firebaseio.com/");

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonUpdateAvailability(false);
                editDocument();
            }
        });

        return view;
    }

    private void editDocument() {
        itemsRef = db.getReference("Emergency Information/Document");
        String newDocumentTitle = editDocumentTitle.getText().toString().trim();
        String newDescription = editDescription.getText().toString().trim();

        if (newDocumentTitle.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in the document title", Toast.LENGTH_SHORT).show();
            setButtonUpdateAvailability(true);
            return;
        }

        Document newDocument = new Document(documentToEdit.getId(), newDocumentTitle,
                documentToEdit.getFilePath(), documentToEdit.getUploadDateTime(), newDescription);
        itemsRef.child(documentToEdit.getId()).setValue(newDocument).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Document metadata updated", Toast.LENGTH_SHORT).show();
                EditDocumentFragment.setDocumentToEdit(null);
                getParentFragmentManager().popBackStack();
            } else {
                Log.e(TAG, "Firebase setValue() failed");
                Toast.makeText(getContext(), "Failed to update Document metadata due to database error.\n"
                        + "Please press the back button to go back to last screen.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setButtonUpdateAvailability(boolean availability) {
        buttonUpdate.setEnabled(availability);
        buttonUpdate.setAlpha(availability ? 1.0f : 0.5f);
    }

    public static void setDocumentToEdit(Document document) {
        documentToEdit = document;
    }
}
