package com.example.b07demosummer2024;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddDocumentFragment extends Fragment {

    private static final String TAG = "AddDocumentFragment";
    private EditText editDocumentTitle, editDescription;
    private Button buttonSelectFile, buttonUpload;
    private TextView textViewSelectedFile;
    private Uri selectedFileUri;

    private FirebaseStorage storage;
    private StorageReference storageRootRef;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        storageRootRef = storage.getReference();

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedFileUri = data.getData();
                            Log.d(TAG, "Selected File URI: " + selectedFileUri.toString());

                            String fileName = getFileNameFromUri(selectedFileUri);
                            String initialDocName = stripExtension(fileName);
                            textViewSelectedFile.setText("Selected: " + fileName);
                            if (initialDocName != null && !initialDocName.isEmpty()
                                    && editDocumentTitle.getText().toString().trim().isEmpty())
                                editDocumentTitle.setText(initialDocName);

                        } else {
                            selectedFileUri = null;
                            textViewSelectedFile.setText("No file selected");
                            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        selectedFileUri = null;
                        textViewSelectedFile.setText("No file selected");
                        Toast.makeText(getContext(), "File selection cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_document, container, false);

        editDocumentTitle = view.findViewById(R.id.editDocumentTitle);
        editDescription = view.findViewById(R.id.editDocumentDescription);
        buttonSelectFile = view.findViewById(R.id.buttonSelectFile);
        textViewSelectedFile = view.findViewById(R.id.textViewSelectedFile);
        buttonUpload = view.findViewById(R.id.buttonUpload);
        setButtonsAvailability(true);

        db = FirebaseDatabase.getInstance("https://sample-project-20250710-default-rtdb.firebaseio.com/");

        buttonSelectFile.setOnClickListener(v -> openFilePicker());

        buttonUpload.setOnClickListener(v -> {
            setButtonsAvailability(false);

            String documentTitle = editDocumentTitle.getText().toString().trim();

            if (documentTitle.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a Document name", Toast.LENGTH_SHORT).show();
                setButtonsAvailability(true);
                return;
            }
            if (selectedFileUri == null) {
                Toast.makeText(getContext(), "Please select a file first", Toast.LENGTH_SHORT).show();
                setButtonsAvailability(true);
                return;
            }

            String originalFileName = getFileNameFromUri(selectedFileUri);
            String fileExtension = getFileExtension(selectedFileUri, originalFileName);
            // Sanitize the user-provided document name using regex:
            // replaces anything not alphanumeric, dot, underscore, or hyphen with an underscore.
            String cleanDocumentTitle = documentTitle.replaceAll("[^a-zA-Z0-9._-]", "_");

            String fileNameForStorage;
            if (!fileExtension.isEmpty()) {
                if (cleanDocumentTitle.toLowerCase().endsWith(fileExtension.toLowerCase())) {
                    fileNameForStorage = cleanDocumentTitle;
                } else {
                    fileNameForStorage = cleanDocumentTitle + fileExtension;
                }
            } else {
                fileNameForStorage = cleanDocumentTitle;
                Log.w(TAG, "Uploading file without a determined extension: " + fileNameForStorage);
            }
            StorageReference documentFileRef = storageRootRef.child("documents/" + fileNameForStorage);

            Log.d(TAG, "Attempting to upload to path: " + documentFileRef.getPath());
            Log.d(TAG, "Selected File URI for upload: " + selectedFileUri.toString());

            InputStream inputStream = null;
            try {
                ContentResolver contentResolver = requireContext().getContentResolver();
                inputStream = contentResolver.openInputStream(selectedFileUri);
                if (inputStream == null) {
                    throw new FileNotFoundException("File not found: " + selectedFileUri.getPath());
                }

                UploadTask uploadTask = documentFileRef.putStream(inputStream);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "Document upload failed for path: " + documentFileRef.getPath(), exception);
                        Toast.makeText(getContext(), "Document upload failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                        if(exception instanceof StorageException) {
                            StorageException storageException = (StorageException)exception;
                            Log.e(TAG, "Error Code: " + storageException.getErrorCode());
                            Log.e(TAG, "HTTP Result Code: " + storageException.getHttpResultCode());
                            if (storageException.getCause() != null) {
                                Log.e(TAG, "Original Cause: " + storageException.getCause().getMessage());
                            }
                        }
                        setButtonsAvailability(true);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Document upload successful for path: " + taskSnapshot.getMetadata().getPath());
                        Toast.makeText(getContext(), "Document uploaded successfully", Toast.LENGTH_SHORT).show();
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            Log.d(TAG, "Download URL: " + downloadUri.toString());
                            updateDocument(fileNameForStorage);
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to get download URL", e);
                        });
                    }
                });
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found: " + selectedFileUri.getPath(), e);
                Toast.makeText(getContext(), "File not found: " + selectedFileUri.getPath(), Toast.LENGTH_LONG).show();
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ioException) {
                        Log.e(TAG, "Error closing input stream after FileNotFoundException", ioException);
                    }
                }
                setButtonsAvailability(true);
            } catch (SecurityException e) {
                Log.e(TAG, "Security exception while opening input stream", e);
                Toast.makeText(getContext(), "Security exception while opening input stream", Toast.LENGTH_LONG).show();
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ioException) {
                        Log.e(TAG, "Error closing input stream after SecurityException", ioException);
                    }
                }
                setButtonsAvailability(true);
            } catch (Exception e) {
                Log.e("AddDocumentFragment", "Unknown error while opening URI: " + selectedFileUri.toString(), e);
                Toast.makeText(getContext(), "Unknown error while opening URI: " + selectedFileUri.toString(), Toast.LENGTH_LONG).show();
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ioException) {
                        Log.e(TAG, "Error closing input stream after an unknown Exception", e);
                    }
                }
                setButtonsAvailability(true);
            }
        });
        return view;
    }

    private void setButtonsAvailability(boolean availability) {
        buttonUpload.setEnabled(availability);
        buttonUpload.setAlpha(availability ? 1.0f : 0.5f);
        buttonSelectFile.setEnabled(availability);
        buttonSelectFile.setAlpha(availability ? 1.0f : 0.5f);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Allow any MIME type
        filePickerLauncher.launch(intent);
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri.getScheme() != null && uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting file name from URI with ContentResolver", e);
            }
        }
        if (fileName == null) {
            fileName = uri.getLastPathSegment();
            if (fileName != null) {
                // Sanitize in case it's a full path from a less common URI type or contains problematic chars
                int lastSlash = fileName.lastIndexOf('/');
                if (lastSlash != -1) {
                    fileName = fileName.substring(lastSlash + 1);
                }
            }
        }
        return (fileName != null && !fileName.isEmpty()) ? fileName : "unknown_file";
    }

    private String getFileExtension(Uri uri, String originalFileName) {
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            try {
                extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            } catch (Exception e) {
                Log.w(TAG, "Could not get extension from originalFileName: " + originalFileName, e);
            }
        }

        // If not found from filename, try to get it from MIME type
        if (extension.isEmpty() && getContext() != null && uri != null) {
            ContentResolver contentResolver = getContext().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String type = contentResolver.getType(uri);
            if (type != null) {
                String extFromMime = mime.getExtensionFromMimeType(type);
                if (extFromMime != null) {
                    extension = "." + extFromMime;
                }
            }
        }
        return extension;
    }

    private String stripExtension(String fileName) {
        if (fileName == null) return null;
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
         return fileName.substring(0, dotIndex);
        }
        return fileName;
    }

    private void updateDocument(String newDownloadPath) {
        itemsRef = db.getReference("Emergency Information/Document");
        String newId = itemsRef.push().getKey();
        String newDocumentTitle = editDocumentTitle.getText().toString().trim();
        String newDescription = editDescription.getText().toString().trim();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String newUploadDateTime = now.format(formatter);

        Document newDocument;

        if(!newId.isEmpty()) {
            newDocument = new Document(newId, newDocumentTitle, newDownloadPath, newUploadDateTime, newDescription);
            itemsRef.child(newId).setValue(newDocument).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Document metadata updated", Toast.LENGTH_SHORT).show();
                    editDocumentTitle.setText("");
                    textViewSelectedFile.setText(R.string.default_text_for_no_file_selected);
                    selectedFileUri = null;
                    getParentFragmentManager().popBackStack();
                } else {
                    Log.e(TAG, "Firebase setValue() failed");
                    Toast.makeText(getContext(), "Failed to update Document metadata due to database error.\n"
                            + "Please press the back button to go back to last screen.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.e(TAG, "newID is empty");
            Toast.makeText(getContext(), "Failed to update Document metadata due to having an empty Document ID.\n"
                    + "Please press the back button to go back to last screen.", Toast.LENGTH_LONG).show();
        }
    }
}
