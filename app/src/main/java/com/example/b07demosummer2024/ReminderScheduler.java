package com.example.b07demosummer2024;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class ReminderScheduler {

    public static void scheduleReminder(Context context, Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("reminderId", reminder.id);
        intent.putExtra("message", "Reminder: Check your safety plan.");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        int hour = reminder.ampm.equals("PM") && reminder.hour < 12 ? reminder.hour + 12 : reminder.hour;
        if (reminder.ampm.equals("AM") && reminder.hour == 12) hour = 0;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, reminder.minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long triggerAtMillis = calendar.getTimeInMillis();
        if (triggerAtMillis < System.currentTimeMillis()) {
            // If time is in the past, schedule for next interval
            switch (reminder.frequency) {
                case "Daily":
                    triggerAtMillis += AlarmManager.INTERVAL_DAY;
                    break;
                case "Weekly":
                    triggerAtMillis += AlarmManager.INTERVAL_DAY * 7;
                    break;
                case "Monthly":
                    triggerAtMillis += AlarmManager.INTERVAL_DAY * 30;
                    break;
            }
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }
}