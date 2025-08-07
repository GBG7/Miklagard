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

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private List<Document> documentList;
    private Context context;

    public interface OnDocumentOptionsClickListener {
        void onDownloadClick(Document document, int position);
        void onEditClick(Document document, int position);
        void onDeleteClick(Document document, int position);
    }

    private OnDocumentOptionsClickListener optionsClickListener;

    public DocumentAdapter(List<Document> documentList, Context context, OnDocumentOptionsClickListener listener) {
        this.documentList = documentList;
        this.context = context;
        this.optionsClickListener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_document, parent, false);
        return new DocumentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document currentDocument = documentList.get(position);
        String currentDocumentTitle = String.format("Document title:\n %s", currentDocument.getDocumentTitle());
        String currentDocumentUploadDateTime = String.format("Upload date and time:\n %s", currentDocument.getUploadDateTime());
        String currentDocumentDescription = String.format("Description:\n %s", currentDocument.getDescription());

        holder.textViewDocumentTitle.setText(currentDocumentTitle);
        holder.textViewUploadDateTime.setText(currentDocumentUploadDateTime);
        holder.textViewDescription.setText(currentDocumentDescription);

        holder.buttonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view, position, currentDocument);
            }
        });
    }

    private void showPopupMenu(View view, int position, Document document) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.document_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_download_document) {
                    if (optionsClickListener != null) {
                        optionsClickListener.onDownloadClick(document, position);
                    }
                    return true;
                } else if (itemId == R.id.action_edit_document) {
                    if (optionsClickListener != null) {
                        optionsClickListener.onEditClick(document, position);
                    }
                    return true;
                } else if (itemId == R.id.action_delete_document) {
                    if (optionsClickListener != null) {
                        optionsClickListener.onDeleteClick(document, position);
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
        return documentList.size();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDocumentTitle;
        TextView textViewUploadDateTime;
        TextView textViewDescription;
        ImageButton buttonOptions;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDocumentTitle = itemView.findViewById(R.id.textViewDocumentTitle);
            textViewUploadDateTime = itemView.findViewById(R.id.textViewUploadDateTime);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            buttonOptions = itemView.findViewById(R.id.buttonOptions);
        }
    }
}
