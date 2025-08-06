package com.example.b07demosummer2024;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {
    private Spinner frequencySpinner;
    private TimePicker timePicker;
    private Button setReminderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        frequencySpinner = findViewById(R.id.frequencySpinner);
        timePicker = findViewById(R.id.timePicker);
        setReminderBtn = findViewById(R.id.setReminderBtn);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_frequencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);

        setReminderBtn.setOnClickListener(v -> setReminder());
    }

    private void setReminder() {
        String frequency = frequencySpinner.getSelectedItem().toString();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        long intervalMillis = getIntervalMillis(frequency);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, pendingIntent);
            Toast.makeText(this, "Reminder set for " + frequency, Toast.LENGTH_SHORT).show();
        }
    }

    private long getIntervalMillis(String frequency) {
        switch (frequency) {
            case "Daily":
                return AlarmManager.INTERVAL_DAY;
            case "Weekly":
                return AlarmManager.INTERVAL_DAY * 7;
            case "Monthly":
                return AlarmManager.INTERVAL_DAY * 30;
            default:
                return AlarmManager.INTERVAL_DAY;
        }
    }
}