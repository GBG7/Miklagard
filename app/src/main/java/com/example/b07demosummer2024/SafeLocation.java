package com.example.b07demosummer2024;

public class SafeLocation extends EmergencyInformation {
    private String safeLocationName;
    private String address;
    private String note;

    public SafeLocation() {
        super(null, "Safe Location", "Safe Location address");
    }

    public SafeLocation(String id, String safeLocationName, String address, String note) {
        super(id, "Safe Location", "Safe Location address");
        this.safeLocationName = safeLocationName;
        this.address = address;
        this.note = note;
    }

    public String getSafeLocationName() {
        return safeLocationName;
    }

    public void setSafeLocationName(String safeLocationName) {
        this.safeLocationName = safeLocationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
