package com.example.b07demosummer2024;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

public class ReminderScheduler {
    public static void scheduleReminder(Context context, Reminder reminder) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, reminder.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        String[] timeParts = reminder.time.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        calendar.set(Calendar.SECOND, 0);

        long interval;
        switch (reminder.frequency.toLowerCase()) {
            case "daily":
                interval = AlarmManager.INTERVAL_DAY;
                break;
            case "weekly":
                interval = AlarmManager.INTERVAL_DAY * 7;
                break;
            case "monthly":
                interval = AlarmManager.INTERVAL_DAY * 30;
                break;
            default:
                interval = AlarmManager.INTERVAL_DAY;
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                interval,
                pendingIntent
        );
    }
}