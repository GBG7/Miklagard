package com.example.b07demosummer2024;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TipGenerator {

    public static List<String> generateTips(Context context, Map<String, String> responses) {
        List<String> tips = new ArrayList<>();
        JSONObject questionnaire = loadQuestionnaire(context);
        if (questionnaire == null) return tips;
        if (responses == null) responses = new HashMap<>();

        try {
            // Get relationship status (q0)
            String status = responses.get("q0");
            if (status == null || !questionnaire.has("Warm-up")) return tips;

            List<String> sections = new ArrayList<>();
            sections.add("Warm-up");
            if (questionnaire.has(status)) sections.add(status);
            if (questionnaire.has("Follow-up")) sections.add("Follow-up");

            for (String section : sections) {
                JSONArray questions = questionnaire.getJSONArray(section);
                for (int i = 0; i < questions.length(); i++) {
                    JSONObject question = questions.getJSONObject(i);
                    String qid = question.getString("id");
                    String type = question.getString("type");

                    if (!responses.containsKey(qid)) continue;

                    // optionToTip
                    if (question.has("optionToTip")) {
                        JSONObject optionToTip = question.getJSONObject("optionToTip");
                        String raw = responses.get(qid);
                        String[] answers = raw.split(",\\s*");

                        for (String ans : answers) {
                            if (optionToTip.has(ans)) {
                                String tip = optionToTip.getString(ans);
                                tips.add(substituteTokens(tip, responses));
                            }
                        }
                    }

                    // direct tip
                    if (question.has("tip")) {
                        String tip = question.getString("tip");
                        tips.add(substituteTokens(tip, responses));
                    }

                    // followup
                    if (question.has("followup")) {
                        JSONObject followup = question.getJSONObject("followup");
                        Iterator<String> followupKeys = followup.keys();
                        while (followupKeys.hasNext()) {
                            String followupKey = followupKeys.next();
                            JSONObject followupQ = followup.getJSONObject(followupKey);
                            String fid = followupQ.getString("id");

                            if (!responses.containsKey(fid)) continue;

                            if (followupQ.has("tip")) {
                                String tip = followupQ.getString("tip");
                                tips.add(substituteTokens(tip, responses));
                            }

                            if (followupQ.has("optionToTip")) {
                                JSONObject followupTips = followupQ.getJSONObject("optionToTip");
                                String response = responses.get(fid);
                                if (followupTips.has(response)) {
                                    String tip = followupTips.getString(response);
                                    tips.add(substituteTokens(tip, responses));
                                }
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            Log.e("TipGenerator", "JSON parsing error: " + e.getMessage());
        }

        return tips;
    }

    private static String substituteTokens(String tip, Map<String, String> responses) {
        for (Map.Entry<String, String> entry : responses.entrySet()) {
            tip = tip.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return tip;
    }

    private static JSONObject loadQuestionnaire(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.questionnaire);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            return new JSONObject(json);
        } catch (Exception e) {
            Log.e("TipGenerator", "Failed to load questionnaire: " + e.getMessage());
            return null;
        }
    }
}