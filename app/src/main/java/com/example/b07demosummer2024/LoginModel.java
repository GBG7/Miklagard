package com.example.b07demosummer2024;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.function.Consumer;

/** Pure logic/data layer for Login. Keeps Firebase & storage details out of the Activity/Presenter. */
class LoginModel {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    /* Email/password sign-in. Returns uid on success. */
    void emailLogin(String email, String pw, Consumer<String> onUid, Consumer<String> onError) {
        auth.signInWithEmailAndPassword(email, pw)
                .addOnSuccessListener(r -> {
                    if (r.getUser() == null) { onError.accept("No user found after login"); return; }
                    onUid.accept(r.getUser().getUid());
                })
                .addOnFailureListener(e -> onError.accept(e.getMessage()));
    }

    /* PIN sign-in via your existing LoginManager (EncryptedSharedPreferences). Returns uid. */
    void pinLogin(Context ctx, String pin, Consumer<String> onUid, Consumer<String> onError) {
        LoginManager.getInstance().checkPin(ctx, pin, onUid, onError);
    }

    /* Check if questionnaire responses exist for uid. */
    void hasResponses(String uid, Consumer<Boolean> onResult, Consumer<String> onError) {
        root.child("responses").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snap) {
                        onResult.accept(snap.exists() && snap.hasChildren());
                    }
                    @Override public void onCancelled(DatabaseError error) {
                        onError.accept(error.getMessage());
                    }
                });
    }
}
