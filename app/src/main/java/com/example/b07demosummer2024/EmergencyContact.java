package com.example.b07demosummer2024;

public class EmergencyContact extends EmergencyInformation {
    private String emergencyContactName;
    private String relationship;
    private String phoneNumber;

    public EmergencyContact() {
        super(null, "Emergency Contact", "Emergency Contact name");
    }

    public EmergencyContact(String id, String emergencyContactName, String relationship, String phoneNumber) {
        super(id, "Emergency Contact", "Emergency Contact name");
        this.emergencyContactName = emergencyContactName;
        this.relationship = relationship;
        this.phoneNumber = phoneNumber;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
