package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class RelationshipStatusActivity extends AppCompatActivity {

    private Spinner relationshipStatusSpinner;
    private LinearLayout questionsLayout;
    private Map<String, List<Question>> questionMap;
    private List<Question> currentQuestions;
    private int currentQuestionIndex = 0;
    private LayoutInflater inflater;

    // Firebase
    private DatabaseReference databaseRef;
    private final String userId = "test_user"; // Change to FirebaseAuth user if needed
    private String selectedStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship_status);

        relationshipStatusSpinner = findViewById(R.id.relationship_status_spinner);
        questionsLayout = findViewById(R.id.questions_layout);
        inflater = LayoutInflater.from(this);

        // Firebase setup
        databaseRef = FirebaseDatabase.getInstance().getReference("responses");

        loadQuestionsFromJSON();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.relationship_status_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relationshipStatusSpinner.setAdapter(adapter);

        relationshipStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = (String) parent.getItemAtPosition(position);
                displayFirstQuestionForStatus(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadQuestionsFromJSON() {
        try {
            InputStream inputStream = getAssets().open("questionnaire.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            Type mapType = new TypeToken<Map<String, List<Question>>>(){}.getType();
            questionMap = new Gson().fromJson(reader, mapType);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load questions.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayFirstQuestionForStatus(String status) {
        questionsLayout.removeAllViews();
        currentQuestionIndex = 0;

        if (questionMap != null && questionMap.containsKey(status)) {
            currentQuestions = questionMap.get(status);
            displayNextQuestion();
        }
    }

    private void saveAnswerToFirebase(String questionId, String answer) {
        databaseRef.child(userId)
                .child(selectedStatus)
                .child(questionId)
                .setValue(answer);
    }

    private void displayNextQuestion() {
        if (currentQuestionIndex >= currentQuestions.size()) return;

        Question question = currentQuestions.get(currentQuestionIndex);
        View questionView = null;

        switch (question.getType()) {
            case "text":
                questionView = inflater.inflate(R.layout.question_text, null);
                TextView textLabel = questionView.findViewById(R.id.question_text);
                EditText editText = questionView.findViewById(R.id.edit_text);
                Button nextBtnText = questionView.findViewById(R.id.next_button);

                textLabel.setText(question.getQuestion());
                nextBtnText.setOnClickListener(v -> {
                    String answer = editText.getText().toString().trim();
                    if (!answer.isEmpty()) {
                        saveAnswerToFirebase(question.getId(), answer);
                        currentQuestionIndex++;
                        displayNextQuestion();
                    } else {
                        Toast.makeText(this, "Please enter an answer", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case "radio":
                questionView = inflater.inflate(R.layout.question_radio, null);
                TextView radioLabel = questionView.findViewById(R.id.question_text);
                RadioGroup radioGroup = questionView.findViewById(R.id.radio_group);
                Button nextBtnRadio = questionView.findViewById(R.id.next_button);

                radioLabel.setText(question.getQuestion());
                for (String option : question.getOptions()) {
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(option);
                    radioGroup.addView(radioButton);
                }

                nextBtnRadio.setOnClickListener(v -> {
                    int checkedId = radioGroup.getCheckedRadioButtonId();
                    if (checkedId != -1) {
                        RadioButton selected = radioGroup.findViewById(checkedId);
                        String answer = selected.getText().toString();
                        saveAnswerToFirebase(question.getId(), answer);
                        currentQuestionIndex++;
                        displayNextQuestion();
                    } else {
                        Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case "dropdown":
                questionView = inflater.inflate(R.layout.question_dropdown, null);
                TextView dropdownLabel = questionView.findViewById(R.id.question_text);
                Spinner dropdownSpinner = questionView.findViewById(R.id.dropdown_spinner);
                Button nextBtnDropdown = questionView.findViewById(R.id.next_button);

                dropdownLabel.setText(question.getQuestion());

                ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, question.getOptions()
                );
                dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdownSpinner.setAdapter(dropdownAdapter);

                nextBtnDropdown.setOnClickListener(v -> {
                    String answer = (String) dropdownSpinner.getSelectedItem();
                    if (answer != null && !answer.isEmpty()) {
                        saveAnswerToFirebase(question.getId(), answer);
                        currentQuestionIndex++;
                        displayNextQuestion();
                    } else {
                        Toast.makeText(this, "Please choose from dropdown", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }

        if (questionView != null) {
            questionsLayout.addView(questionView);
        }
    }
}