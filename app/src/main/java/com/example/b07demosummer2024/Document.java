package com.example.b07demosummer2024;

public class Document extends EmergencyInformation {
    private String documentTitle;
    private String filePath;
    private String uploadDateTime;
    private String description;

    public Document() {
        super(null, "Document", "Document name");
    }

    public Document(String id, String documentTitle, String filePath, String uploadDateTime, String description) {
        super(id, "Document", "Document name");
        this.documentTitle = documentTitle;
        this.filePath = filePath;
        this.uploadDateTime = uploadDateTime;
        this.description = description;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(String uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
