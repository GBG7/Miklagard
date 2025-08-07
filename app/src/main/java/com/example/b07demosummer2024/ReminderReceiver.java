package com.example.b07demosummer2024;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        String reminderId = intent.getStringExtra("reminderId");

        NotificationUtils.showNotification(
                context,
                "Safety Plan Reminder",
                message != null ? message : "Check your safety plan.",
                reminderId.hashCode()
        );
    }
}