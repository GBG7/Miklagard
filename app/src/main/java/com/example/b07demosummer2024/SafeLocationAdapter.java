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

public class SafeLocationAdapter extends RecyclerView.Adapter<SafeLocationAdapter.SafeLocationViewHolder> {

    private List<SafeLocation> safeLocationList;
    private Context context;

    public interface OnSafeLocationOptionsClickListener {
        void onEditClick(SafeLocation safeLocation, int position);
        void onDeleteClick(SafeLocation safeLocation, int position);
    }

    private OnSafeLocationOptionsClickListener optionsClickListener;

    public SafeLocationAdapter(List<SafeLocation> safeLocationList, Context context, OnSafeLocationOptionsClickListener listener) {
        this.safeLocationList = safeLocationList;
        this.context = context;
        this.optionsClickListener = listener;
    }

    @NonNull
    @Override
    public SafeLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_safe_location, parent, false);
        return new SafeLocationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SafeLocationViewHolder holder, int position) {
        SafeLocation currentSafeLocation = safeLocationList.get(position);
        String currentSafeLocationName = String.format("Safe Location name:\n %s", currentSafeLocation.getSafeLocationName());
        String currentAddress = String.format("Address:\n %s", currentSafeLocation.getAddress());
        String currentNote = String.format("Note:\n %s", currentSafeLocation.getNote());

        holder.textViewSafeLocationName.setText(currentSafeLocationName);
        holder.textViewAddress.setText(currentAddress);
        holder.textViewNote.setText(currentNote);

        holder.buttonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, position, currentSafeLocation);
            }
        });
    }

    private void showPopupMenu(View view, int position, SafeLocation safeLocation) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.safe_location_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit_safe_location) {
                    if (optionsClickListener != null) {
                        optionsClickListener.onEditClick(safeLocation, position);
                    }
                    return true;
                } else if (itemId == R.id.action_delete_safe_location) {
                    if (optionsClickListener != null) {
                        optionsClickListener.onDeleteClick(safeLocation, position);
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
        return safeLocationList.size();
    }

    static class SafeLocationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSafeLocationName;
        TextView textViewAddress;
        TextView textViewNote;
        ImageButton buttonOptions;

        public SafeLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSafeLocationName = itemView.findViewById(R.id.textViewSafeLocationName);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewNote = itemView.findViewById(R.id.textViewNote);
            buttonOptions = itemView.findViewById(R.id.buttonOptions);
        }
    }
}
