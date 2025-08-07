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

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private List<Medication> medicationList;
    private Context context;

    public interface OnMedicationOptionsClickListener {
        void onEditClick(Medication medication, int position);
        void onDeleteClick(Medication medication, int position);
    }

    private OnMedicationOptionsClickListener optionsClickListener;

    public MedicationAdapter(List<Medication> medicationList, Context context, OnMedicationOptionsClickListener listener) {
        this.medicationList = medicationList;
        this.context = context;
        this.optionsClickListener = listener;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_medication, parent, false);
        return new MedicationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication currentMedication = medicationList.get(position);
        String currentMedicationName = String.format("Medication name:\n %s", currentMedication.getMedicationName());
        String currentMedicationDosage = String.format("Dosage:\n %s", currentMedication.getDosage());

        holder.textViewMedicationName.setText(currentMedicationName);
        holder.textViewDosage.setText(currentMedicationDosage);

        holder.buttonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, position, currentMedication);
            }
        });
    }

    private void showPopupMenu(View view, int position, Medication medication) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.medication_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit_medication) {
                    if (optionsClickListener != null) {
                        optionsClickListener.onEditClick(medication, position);
                    }
                    return true;
                } else if (itemId == R.id.action_delete_medication) {
                    if (optionsClickListener != null) {
                        optionsClickListener.onDeleteClick(medication, position);
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
        return medicationList.size();
    }

    static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMedicationName;
        TextView textViewDosage;
        ImageButton buttonOptions;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMedicationName = itemView.findViewById(R.id.textViewMedicationName);
            textViewDosage = itemView.findViewById(R.id.textViewMedicationDosage);
            buttonOptions = itemView.findViewById(R.id.buttonOptions);
        }
    }
}
