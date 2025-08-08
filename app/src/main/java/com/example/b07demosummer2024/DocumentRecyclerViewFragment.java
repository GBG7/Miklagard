package com.example.b07demosummer2024;

import android.os.Bundle;
import android.os.Environment;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocumentRecyclerViewFragment extends Fragment
        implements DocumentAdapter.OnDocumentOptionsClickListener {
    private static final String TAG = "DocumentRecyclerViewFragment";
    private String localFileName = "";
    private RecyclerView recyclerView;
    private DocumentAdapter documentAdapter;
    private List<Document> documentList;
    private Spinner spinnerCategory;
    private static final int INITIAL_SPINNER_POSITION = 0;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view_emergency_information, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.emergency_information_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);


        Button buttonAddEmergencyInformation = view.findViewById(R.id.buttonAddEmergencyInformation);
        buttonAddEmergencyInformation.setText("Add Document");

        documentList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(documentList, getContext(), this);
        recyclerView.setAdapter(documentAdapter);

        db = FirebaseDatabase.getInstance("https://sample-project-20250710-default-rtdb.firebaseio.com/");

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:;
                        fetchDocumentFromDatabase();
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
                        getParentFragmentManager().popBackStack();
                        loadFragment(new MedicationRecyclerViewFragment());
                        break;
                    default:
                        fetchDocumentFromDatabase();
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
                loadFragment(new AddDocumentFragment());
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

    private void fetchDocumentFromDatabase() {
        itemsRef = db.getReference("Emergency Information/Document");

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                documentList.clear();

                for (DataSnapshot documentSnapshot : dataSnapshot.getChildren()) {
                    String newId = documentSnapshot.getKey();
                    String newDocumentTitle = documentSnapshot.child("documentTitle").getValue(String.class);
                    String newFilePath = documentSnapshot.child("filePath").getValue(String.class);
                    String newUploadDateTime = documentSnapshot.child("uploadDateTime").getValue(String.class);
                    String newDescription = documentSnapshot.child("description").getValue(String.class);

                    if (newDocumentTitle != null && newFilePath != null && newUploadDateTime != null) {
                        Document document = new Document(newId, newDocumentTitle, newFilePath, newUploadDateTime, newDescription);
                        documentList.add(document);
                    }
                }
                documentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching documents: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onDownloadClick(Document document, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm download")
                .setMessage("The document will be saved in the device's download folder. "
                        + "If you want to save it in another folder, feel free to use the file "
                        + "explorer on your phone or similar applications to move the document "
                        + "once it is downloaded. Do you want to download this document?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        localFileName = "";
                        localFileName += document.getDocumentTitle();
                        localFileName += document.getFilePath().substring(document.getFilePath().lastIndexOf("."));

                        Toast.makeText(getContext(), "Downloading " + localFileName + "...", Toast.LENGTH_SHORT).show();

                        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference fileRef = storageRef.child("documents/" + document.getFilePath());
                        File localFile = new File(downloadsFolder, localFileName);

                        Log.d(TAG, "File download objects set up successfully");
                        fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.d(TAG, localFileName + " downloaded successfully");
                                Toast.makeText(getContext(), localFileName + " downloaded successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e(TAG, "Error downloading " + localFileName + ": " + exception.getMessage());
                                Toast.makeText(getContext(), "Error downloading " + localFileName, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onEditClick(Document document, int position) {
        EditDocumentFragment.setDocumentToEdit(document);
        loadFragment(new EditDocumentFragment());
    }

    @Override
    public void onDeleteClick(Document document, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Document")
                .setMessage("Are you sure you want to delete " + document.getDocumentTitle() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            StorageReference fileRef = storageRef.child("documents/" + document.getFilePath());
                            fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    itemsRef = db.getReference("Emergency Information/Document");
                                    itemsRef.child(document.getId()).removeValue();
                                    documentList.remove(position);
                                    documentAdapter.notifyItemRemoved(position);
                                    documentAdapter.notifyItemRangeChanged(position, documentList.size());

                                    Toast.makeText(getContext(), "Document deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error deleting file from storage: " + e.getMessage());
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "Error deleting document: " + e.getMessage());
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
