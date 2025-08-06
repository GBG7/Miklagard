package com.example.b07demosummer2024;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class ReminderActivity extends BaseActivity {
    private Spinner frequencySpinner;
    private Button timeButton, saveButton;
    private TextView selectedTimeText;
    private int selectedHour = -1, selectedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        frequencySpinner = findViewById(R.id.spinnerFrequency);
        timeButton = findViewById(R.id.buttonTime);
        saveButton = findViewById(R.id.buttonSaveReminder);
        selectedTimeText = findViewById(R.id.textSelectedTime);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_frequencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);

        timeButton.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                selectedTimeText.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
            dialog.show();
        });

        saveButton.setOnClickListener(v -> {
            if (selectedHour == -1 || selectedMinute == -1) {
                Toast.makeText(this, "Please select a time.", Toast.LENGTH_SHORT).show();
                return;
            }

            String frequency = frequencySpinner.getSelectedItem().toString();
            String id = UUID.randomUUID().toString();
            Reminder reminder = new Reminder(id, frequency, selectedHour + ":" + selectedMinute);
            ReminderScheduler.scheduleReminder(this, reminder);
            Toast.makeText(this, "Reminder set", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_reminder;
    }
}