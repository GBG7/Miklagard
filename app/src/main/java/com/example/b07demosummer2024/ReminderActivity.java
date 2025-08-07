package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReminderActivity extends BaseActivity {

    NumberPicker hourPicker, minutePicker;
    Spinner ampmSpinner, frequencySpinner;
    Button setReminderButton;
    RecyclerView reminderRecyclerView;
    ReminderAdapter adapter;
    List<Reminder> reminderList = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_reminder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        ampmSpinner = findViewById(R.id.ampmSpinner);
        frequencySpinner = findViewById(R.id.frequencySpinner);
        setReminderButton = findViewById(R.id.setReminderButton);
        reminderRecyclerView = findViewById(R.id.reminderRecyclerView);

        // Hour picker (1–12)
        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(12);

        // Minute picker (0–59)
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setFormatter(i -> String.format("%02d", i));

        // AM/PM dropdown
        ArrayAdapter<String> ampmAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"AM", "PM"});
        ampmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ampmSpinner.setAdapter(ampmAdapter);

        // Frequency dropdown
        ArrayAdapter<String> freqAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Daily", "Weekly", "Monthly"});
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(freqAdapter);

        // RecyclerView
        adapter = new ReminderAdapter(reminderList);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderRecyclerView.setAdapter(adapter);

        setReminderButton.setOnClickListener(v -> {
            int hour = hourPicker.getValue();
            int minute = minutePicker.getValue();
            String ampm = ampmSpinner.getSelectedItem().toString();
            String frequency = frequencySpinner.getSelectedItem().toString();

            String id = UUID.randomUUID().toString();
            Reminder reminder = new Reminder(id, hour, minute, ampm, frequency);
            reminderList.add(reminder);
            adapter.notifyItemInserted(reminderList.size() - 1);

            // Schedule notification
            ReminderScheduler.scheduleReminder(this, reminder);

            // Save to Firebase
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference("reminders")
                        .child(user.getUid())
                        .child(id);
                ref.setValue(reminder);
            }
        });
    }
}