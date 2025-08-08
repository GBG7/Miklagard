package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminders;
    private final ReminderAction deleteAction;
    private final ReminderAction editAction;

    public ReminderAdapter(List<Reminder> reminders, ReminderAction deleteAction, ReminderAction editAction) {
        this.reminders = reminders;
        this.deleteAction = deleteAction;
        this.editAction = editAction;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder r = reminders.get(position);
        holder.time.setText(String.format("%02d:%02d", r.getHour(), r.getMinute()));
        holder.freq.setText(r.getFrequency());
        holder.edit.setOnClickListener(v -> editAction.execute(r));
        holder.delete.setOnClickListener(v -> deleteAction.execute(r));
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView time, freq;
        Button edit, delete;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.reminder_time);
            freq = itemView.findViewById(R.id.reminder_frequency);
            edit = itemView.findViewById(R.id.edit_button);
            delete = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface ReminderAction {
        void execute(Reminder r);
    }
}