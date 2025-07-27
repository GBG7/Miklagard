package com.example.b07demosummer2024;

import java.util.LinkedHashMap;

public class Question {

    private String id;
    private String text;
    private String type;
    private String tip;
    private LinkedHashMap<String, String> optionToTip;
    private LinkedHashMap<String, Question> followup;

    private String category;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getTip() {
        return tip;
    }
    public void setTip(String tip) {
        this.tip = tip;
    }

    public LinkedHashMap<String, String> getOptionToTip() {
        return optionToTip;
    }
    public void setOptionToTip(LinkedHashMap<String, String> optionToTip) {
        this.optionToTip = optionToTip;
    }

    public LinkedHashMap<String, Question> getFollowup() {
        return followup;
    }
    public void setFollowup(LinkedHashMap<String, Question> followup) {
        this.followup = followup;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}