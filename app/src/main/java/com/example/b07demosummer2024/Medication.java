package com.example.b07demosummer2024;

public class Medication extends EmergencyInformation {
    private String medicationName;
    private String dosage;

    public Medication() {
        super(null, "Medication", "Medication name");
    }

    public Medication(String id, String medicationName, String dosage) {
        super(id, "Medication", "Medication name");
        this.medicationName = medicationName;
        this.dosage = dosage;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
}
