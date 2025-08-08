package com.example.b07demosummer2024;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends BaseActivity {

    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Database
        db = FirebaseDatabase.getInstance("https://b07-demo-summer-2024-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = db.getReference("testDemo");

        // Optional demo values for testing
        myRef.setValue("B07 Demo!");
        myRef.child("movies").setValue("B07 Demo!");

        // Load HomeFragment only if this is the first creation
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }
}