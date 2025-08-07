package com.example.b07demosummer2024;

public class Reminder {
    public String id;
    public int hour;
    public int minute;
    public String ampm;
    public String frequency;

    public Reminder() {}

    public Reminder(String id, int hour, int minute, String ampm, String frequency) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.ampm = ampm;
        this.frequency = frequency;
    }

    public String getTimeString() {
        return String.format("%02d:%02d %s (%s)", hour, minute, ampm, frequency);
    }
}