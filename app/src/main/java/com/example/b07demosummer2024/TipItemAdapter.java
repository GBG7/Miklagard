package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TipItemAdapter extends RecyclerView.Adapter<TipItemAdapter.ItemViewHolder> {
private List<TipItem> itemList;

public TipItemAdapter(List<TipItem> itemList) {
    this.itemList = itemList;
}

@NonNull
@Override
public TipItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_tip_item_adapater, parent, false);
    return new TipItemAdapter.ItemViewHolder(view);
}

@Override
public void onBindViewHolder(@NonNull TipItemAdapter.ItemViewHolder holder, int position) {
    TipItem item = itemList.get(position);
    holder.textViewTip.setText(item.getTip());
}

@Override
public int getItemCount() {
    return itemList.size();
}

public static class ItemViewHolder extends RecyclerView.ViewHolder {
    TextView textViewTip;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewTip = itemView.findViewById(R.id.textViewTip);
    }
}
}