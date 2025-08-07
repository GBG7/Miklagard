package com.example.b07demosummer2024;


import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SupportItemAdapter extends RecyclerView.Adapter<SupportItemAdapter.ItemViewHolder> {
    private List<SupportConnectionItem> itemList;

    public SupportItemAdapter(List<SupportConnectionItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public SupportItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_support_item_adapter, parent, false);

        return new SupportItemAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupportItemAdapter.ItemViewHolder holder, int position) {
        SupportConnectionItem item = itemList.get(position);
        holder.textViewName.setText(item.getName());
        holder.textViewDescription.setText(item.getDescription());
        holder.textViewUrl.setText("🔗: " + item.getUrl());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDescription, textViewUrl;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewUrl = itemView.findViewById(R.id.textViewUrl);

        }
    }


}