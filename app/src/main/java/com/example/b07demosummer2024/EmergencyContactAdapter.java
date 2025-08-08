package com.example.b07demosummer2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.EmergencyContactViewHolder> {

    private List<EmergencyContact> emergencyContactList;
    private Context context;

    public interface OnEmergencyContactOptionsClickListener {
        void onEditClick(EmergencyContact emergencyContact, int position);
        void onDeleteClick(EmergencyContact emergencyContact, int position);
    }

    private OnEmergencyContactOptionsClickListener optionsClickListener;

    public EmergencyContactAdapter(List<EmergencyContact> emergencyContactList, Context context, OnEmergencyContactOptionsClickListener listener) {
        this.emergencyContactList = emergencyContactList;
        this.context = context;
        this.optionsClickListener = listener;
    }

    @NonNull
    @Override
    public EmergencyContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_emergency_contact, parent, false);
        return new EmergencyContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyContactViewHolder holder, int position) {
        EmergencyContact currentEmergencyContact = emergencyContactList.get(position);
        String currentEmergencyContactName = String.format("Emergency Contact name:\n %s", currentEmergencyContact.getEmergencyContactName());
        String currentEmergencyContactRelationship = String.format("Relationship:\n %s", currentEmergencyContact.getRelationship());
        String currentEmergencyContactPhoneNumber = String.format("Phone Number:\n %s", currentEmergencyContact.getPhoneNumber());

        holder.textViewEmergencyContactName.setText(currentEmergencyContactName);
        holder.textViewRelationship.setText(currentEmergencyContactRelationship);
        holder.textViewPhoneNumber.setText(currentEmergencyContactPhoneNumber);

        holder.buttonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, position, currentEmergencyContact);
            }
        });
    }

    private void showPopupMenu(View view, int position, EmergencyContact emergencyContact) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.emergency_contact_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit_emergency_contact) {
                    if (optionsClickListener != null) {
                        optionsClickListener.onEditClick(emergencyContact, position);
                    }
                    return true;
                } else if (itemId == R.id.action_delete_emergency_contact) {
                    if (optionsClickListener != null) {
                        optionsClickListener.onDeleteClick(emergencyContact, position);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        popup.show();
    }


    @Override
    public int getItemCount() {
        return emergencyContactList.size();
    }

    static class EmergencyContactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEmergencyContactName;
        TextView textViewRelationship;
        TextView textViewPhoneNumber;
        ImageButton buttonOptions;

        public EmergencyContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEmergencyContactName = itemView.findViewById(R.id.textViewEmergencyContactName);
            textViewRelationship = itemView.findViewById(R.id.textViewRelationship);
            textViewPhoneNumber = itemView.findViewById(R.id.textViewPhoneNumber);
            buttonOptions = itemView.findViewById(R.id.buttonOptions);
        }
    }
}
