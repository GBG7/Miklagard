package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RelationshipStatusActivity extends AppCompatActivity {

    private LinearLayout questionContainer;
    private Map<String, List<Question>> categorizedQuestions = new LinkedHashMap<>();
    private List<Question> warmupQuestions = new ArrayList<>();
    private List<Question> followupQuestions = new ArrayList<>();
    private List<Question> filteredStatusQuestions = new ArrayList<>();

    private int warmupIndex = 0;
    private int statusIndex = 0;
    private int followupIndex = 0;

    private String selectedStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship_status);
        questionContainer = findViewById(R.id.questionContainer);
        loadQuestionsFromJson();
        startQuestionnaire();
    }

    private void loadQuestionsFromJson() {
        try {
            InputStream is = getResources().openRawResource(R.raw.questionnaire);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JsonObject root = new Gson().fromJson(json, JsonObject.class);
            Gson gson = new Gson();
            Type questionListType = new TypeToken<List<Question>>() {}.getType();

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                String key = entry.getKey();
                JsonArray arr = entry.getValue().getAsJsonArray();
                List<Question> questions = gson.fromJson(arr, questionListType);
                categorizedQuestions.put(key, questions);
            }

            warmupQuestions = categorizedQuestions.getOrDefault("Warm-up", new ArrayList<>());
            followupQuestions = categorizedQuestions.getOrDefault("Follow-up", new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load questionnaire", Toast.LENGTH_LONG).show();
        }
    }

    private void startQuestionnaire() {
        showNextWarmupQuestion();
    }

    private void showNextWarmupQuestion() {
        if (warmupIndex < warmupQuestions.size()) {
            Question q = warmupQuestions.get(warmupIndex);
            displayQuestion(q, () -> {
                warmupIndex++;
                showNextWarmupQuestion();
            });
        } else {
            filterStatusQuestions();
            showNextStatusQuestion();
        }
    }

    private void filterStatusQuestions() {
        if (selectedStatus != null && categorizedQuestions.containsKey(selectedStatus)) {
            filteredStatusQuestions = categorizedQuestions.get(selectedStatus);
        }
    }

    private void showNextStatusQuestion() {
        if (statusIndex < filteredStatusQuestions.size()) {
            Question q = filteredStatusQuestions.get(statusIndex);
            displayQuestion(q, () -> {
                statusIndex++;
                showNextStatusQuestion();
            });
        } else {
            showNextFollowupQuestion();
        }
    }

    private void showNextFollowupQuestion() {
        if (followupIndex < followupQuestions.size()) {
            Question q = followupQuestions.get(followupIndex);
            displayQuestion(q, () -> {
                followupIndex++;
                showNextFollowupQuestion();
            });
        } else {
            goToTipsPage();
        }
    }

    private void displayQuestion(Question question, Runnable onAnswered) {
        TextView tv = new TextView(this);
        tv.setText(question.getText());
        tv.setPadding(0, 40, 0, 10);
        questionContainer.addView(tv);

        switch (question.getType()) {
            case "radio":
            case "dropdown":
                Spinner spinner = new Spinner(this);
                List<String> options = new ArrayList<>(question.getOptionToTip().keySet());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                questionContainer.addView(spinner);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    boolean isFirst = true;
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (isFirst) {
                            isFirst = false;
                            return;
                        }
                        String selected = options.get(position);
                        if (question.getText().toLowerCase().contains("relationship status")) {
                            selectedStatus = selected;
                        }
                        spinner.setEnabled(false);
                        onAnswered.run();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
                break;

            case "checkbox":
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                List<CheckBox> checkBoxes = new ArrayList<>();
                for (String option : question.getOptionToTip().keySet()) {
                    CheckBox cb = new CheckBox(this);
                    cb.setText(option);
                    layout.addView(cb);
                    checkBoxes.add(cb);
                }
                Button cbSubmit = new Button(this);
                cbSubmit.setText("Submit");
                cbSubmit.setOnClickListener(v -> {
                    for (CheckBox cb : checkBoxes) cb.setEnabled(false);
                    cbSubmit.setEnabled(false);
                    onAnswered.run();
                });
                questionContainer.addView(layout);
                questionContainer.addView(cbSubmit);
                break;

            case "text":
            case "date":
                EditText et = new EditText(this);
                et.setHint(question.getType().equals("date") ? "e.g. yyyy-MM-dd" : "Enter your answer");
                if (question.getType().equals("date")) {
                    et.setInputType(InputType.TYPE_CLASS_DATETIME);
                }
                questionContainer.addView(et);
                Button textSubmit = new Button(this);
                textSubmit.setText("Submit");
                textSubmit.setOnClickListener(v -> {
                    if (!et.getText().toString().trim().isEmpty()) {
                        et.setEnabled(false);
                        textSubmit.setEnabled(false);
                        onAnswered.run();
                    }
                });
                questionContainer.addView(textSubmit);
                break;

            case "compound":
                Spinner compoundSpinner = new Spinner(this);
                List<String> compoundOptions = new ArrayList<>(question.getOptionToTip().keySet());
                ArrayAdapter<String> compAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, compoundOptions);
                compAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                compoundSpinner.setAdapter(compAdapter);
                questionContainer.addView(compoundSpinner);

                compoundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    boolean isFirst = true;
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (isFirst) {
                            isFirst = false;
                            return;
                        }
                        compoundSpinner.setEnabled(false);
                        String selected = compoundOptions.get(position);
                        if (question.getFollowup() != null && question.getFollowup().containsKey(selected)) {
                            Question follow = question.getFollowup().get(selected);
                            displayQuestion(follow, onAnswered);
                        } else {
                            onAnswered.run();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
                break;

            default:
                Toast.makeText(this, "Unsupported type: " + question.getType(), Toast.LENGTH_SHORT).show();
                onAnswered.run();
        }
    }

    private void goToTipsPage() {
        Intent intent = new Intent(RelationshipStatusActivity.this, TipsActivity.class);
        startActivity(intent);
        finish(); // Prevent back-navigation to questionnaire
    }
}