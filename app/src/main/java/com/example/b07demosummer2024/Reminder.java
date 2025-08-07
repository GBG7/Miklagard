package com.example.b07demosummer2024;

public class Reminder {
    private String id;
    private int hour;
    private int minute;
    private String frequency;

    public Reminder() {}

    public Reminder(String id, int hour, int minute, String frequency) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.frequency = frequency;
    }

    public String getId() { return id; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public String getFrequency() { return frequency; }

    public void setId(String id) { this.id = id; }
    public void setHour(int hour) { this.hour = hour; }
    public void setMinute(int minute) { this.minute = minute; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
}