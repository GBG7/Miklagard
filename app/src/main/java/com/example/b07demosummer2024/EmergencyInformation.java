package com.example.b07demosummer2024;

public abstract class EmergencyInformation {
    protected String id;
    protected String category;
    protected String placeholderText;

    public EmergencyInformation(String id, String category, String placeholderText) {
        this.id = id;
        this.category = category;
        this.placeholderText = placeholderText;
    }

    protected String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected String getCategory() {
        return category;
    }

    protected void setCategory(String category) {
        this.category = category;
    }

    protected String getPlaceholderText() {
        return placeholderText;
    }

    protected void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }
}

