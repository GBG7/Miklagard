package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class LoginActivity extends BaseActivity implements LoginView {

    private EditText emailEditText, passwordEditText, pinEditText;
    private Button   loginButton, forgotpwbutton;
    private RadioGroup radioGroup;
    private RadioButton radioEmail, radioPin;

    private LoginPresenter presenter;

    @Override protected int getLayoutResourceId() { return R.layout.activity_login; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new LoginPresenter(this);

        emailEditText   = findViewById(R.id.editTextTextEmailAddress2);
        passwordEditText= findViewById(R.id.editTextTextPassword2);
        pinEditText     = findViewById(R.id.editTextNumberPassword);
        loginButton     = findViewById(R.id.button);
        forgotpwbutton  = findViewById(R.id.buttonForgotPassword);

        radioGroup = findViewById(R.id.radioGroup);
        radioEmail = findViewById(R.id.radioEmail);
        radioPin   = findViewById(R.id.radioPin);

        // initial mode
        presenter.onEmailSelected();

        radioGroup.setOnCheckedChangeListener((g, id) -> {
            if (id == R.id.radioEmail) presenter.onEmailSelected();
            else                       presenter.onPinSelected();
        });

        loginButton.setOnClickListener(v -> {
            if (radioEmail.isChecked()) {
                presenter.onEmailLogin(
                        emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString()
                );
            } else {
                presenter.onPinLogin(this, pinEditText.getText().toString().trim());
            }
        });

        forgotpwbutton.setOnClickListener(v -> presenter.onForgotPassword());
    }

    /* -------- LoginView impl -------- */
    @Override public void showEmailMode() {
        emailEditText.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        pinEditText.setVisibility(View.GONE);
        radioEmail.setChecked(true);
    }
    @Override public void showPinMode() {
        emailEditText.setVisibility(View.GONE);
        passwordEditText.setVisibility(View.GONE);
        pinEditText.setVisibility(View.VISIBLE);
        radioPin.setChecked(true);
    }
    @Override public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    @Override public void setLoading(boolean loading) {
        loginButton.setEnabled(!loading);
        forgotpwbutton.setEnabled(!loading);
        // add a ProgressBar if you want; for now we just disable buttons
    }
    @Override public void navigateToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
    @Override public void navigateToQuestionnaire() {
        startActivity(new Intent(this, RelationshipStatusActivity.class));
        finish();
    }
    @Override public void navigateToResetPw() {
        startActivity(new Intent(this, ResetPwActivity.class));
    }
}
