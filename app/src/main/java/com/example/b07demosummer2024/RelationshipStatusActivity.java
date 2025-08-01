package com.example.b07demosummer2024;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
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
    private List<Question> statusQuestions = new ArrayList<>();

    private int warmupIndex = 0;
    private int statusIndex = 0;
    private int followupIndex = 0;
    private List<Question> filteredStatusQuestions = new ArrayList<>();

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
        }
    }

    private void displayQuestion(Question question, Runnable onAnswered) {
        TextView tv = new TextView(this);
        tv.setText(question.getText());
        tv.setPadding(0, 40, 0, 10);
        questionContainer.addView(tv);

        switch (question.getType()) {
            case "radio":
            case "dropdown": {
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
            }

            case "checkbox": {
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                List<CheckBox> checkBoxes = new ArrayList<>();
                for (String option : question.getOptionToTip().keySet()) {
                    CheckBox cb = new CheckBox(this);
                    cb.setText(option);
                    layout.addView(cb);
                    checkBoxes.add(cb);
                }
                Button submit = new Button(this);
                submit.setText("Submit");
                submit.setOnClickListener(v -> {
                    for (CheckBox cb : checkBoxes) cb.setEnabled(false);
                    submit.setEnabled(false);
                    onAnswered.run();
                });
                questionContainer.addView(layout);
                questionContainer.addView(submit);
                break;
            }

            case "text": {
                EditText et = new EditText(this);
                et.setHint("Enter your answer");
                questionContainer.addView(et);
                Button submit = new Button(this);
                submit.setText("Submit");
                submit.setOnClickListener(v -> {
                    if (!et.getText().toString().trim().isEmpty()) {
                        et.setEnabled(false);
                        submit.setEnabled(false);
                        onAnswered.run();
                    }
                });
                questionContainer.addView(submit);
                break;
            }

            case "date": {
                EditText et = new EditText(this);
                et.setHint("e.g. yyyy-MM-dd");
                et.setInputType(InputType.TYPE_CLASS_DATETIME);
                questionContainer.addView(et);
                Button submit = new Button(this);
                submit.setText("Submit");
                submit.setOnClickListener(v -> {
                    if (!et.getText().toString().trim().isEmpty()) {
                        et.setEnabled(false);
                        submit.setEnabled(false);
                        onAnswered.run();
                    }
                });
                questionContainer.addView(submit);
                break;
            }

            case "compound": {
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
                        spinner.setEnabled(false);
                        String selected = options.get(position);
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
            }

            default:
                Toast.makeText(this, "Unsupported question type: " + question.getType(), Toast.LENGTH_SHORT).show();
                onAnswered.run();
        }
    }
}