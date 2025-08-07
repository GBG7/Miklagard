package com.example.b07demosummer2024;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07demosummer2024.databinding.ActivityReminderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class ReminderActivity extends BaseActivity {

    private ActivityReminderBinding binding;
    private DatabaseReference dbRef;
    private String uid;
    private List<Reminder> reminders = new ArrayList<>();
    private ReminderAdapter adapter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_reminder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("reminders").child(uid);

        binding.reminderList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReminderAdapter(reminders, this::deleteReminder, this::editReminder);
        binding.reminderList.setAdapter(adapter);

        binding.addReminderButton.setOnClickListener(v -> showReminderDialog(null));

        // TEMP: Test Notification Button
        binding.testNotifyButton.setOnClickListener(v -> NotificationUtils.showReminderNotification(this));

        loadReminders();
    }

    private void loadReminders() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reminders.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Reminder r = snap.getValue(Reminder.class);
                    reminders.add(r);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReminderActivity.this, "Failed to load reminders.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showReminderDialog(Reminder existing) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
        NumberPicker hourPicker = dialogView.findViewById(R.id.hourPicker);
        NumberPicker minutePicker = dialogView.findViewById(R.id.minutePicker);
        NumberPicker amPmPicker = dialogView.findViewById(R.id.amPmPicker);
        Spinner freqSpinner = dialogView.findViewById(R.id.frequencySpinner);

        hourPicker.setMinValue(1); hourPicker.setMaxValue(12);
        minutePicker.setMinValue(0); minutePicker.setMaxValue(59);
        amPmPicker.setMinValue(0); amPmPicker.setMaxValue(1);
        amPmPicker.setDisplayedValues(new String[]{"AM", "PM"});

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.frequency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqSpinner.setAdapter(adapter);

        String dialogTitle = (existing == null) ? "Add Reminder" : "Edit Reminder";
        String confirmButton = (existing == null) ? "Save" : "Update";

        if (existing != null) {
            int hour12 = (existing.getHour() % 12 == 0) ? 12 : existing.getHour() % 12;
            int amPm = (existing.getHour() < 12) ? 0 : 1;
            hourPicker.setValue(hour12);
            minutePicker.setValue(existing.getMinute());
            amPmPicker.setValue(amPm);
            int spinnerPos = adapter.getPosition(existing.getFrequency());
            freqSpinner.setSelection(spinnerPos);
        }

        new AlertDialog.Builder(this)
                .setTitle(dialogTitle)
                .setView(dialogView)
                .setPositiveButton(confirmButton, (dialog, which) -> {
                    int hour = hourPicker.getValue();
                    int minute = minutePicker.getValue();
                    int amPm = amPmPicker.getValue();
                    if (amPm == 1 && hour != 12) hour += 12;
                    if (amPm == 0 && hour == 12) hour = 0;

                    String freq = freqSpinner.getSelectedItem().toString();

                    if (existing == null) {
                        String id = dbRef.push().getKey();
                        Reminder r = new Reminder(id, hour, minute, freq);
                        dbRef.child(id).setValue(r);
                        ReminderScheduler.scheduleReminder(this, r);
                    } else {
                        existing.setHour(hour);
                        existing.setMinute(minute);
                        existing.setFrequency(freq);
                        dbRef.child(existing.getId()).setValue(existing);
                        ReminderScheduler.cancel(this, existing);
                        ReminderScheduler.scheduleReminder(this, existing);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteReminder(Reminder r) {
        dbRef.child(r.getId()).removeValue();
        ReminderScheduler.cancel(this, r);
    }

    private void editReminder(Reminder r) {
        showReminderDialog(r);
    }
}