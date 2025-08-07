package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminders;

    public ReminderAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.reminderTextView.setText(reminder.getTimeString());
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView reminderTextView;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderTextView = itemView.findViewById(R.id.reminderTextView);
        }
    }
}