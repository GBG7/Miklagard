package com.example.b07demosummer2024;

import android.content.Context;

class LoginPresenter {

    private final LoginView view;
    private final LoginModel model;

    LoginPresenter(LoginView v) {
        this.view  = v;
        this.model = new LoginModel();
    }

    /* UI toggles */
    void onEmailSelected() { view.showEmailMode(); }
    void onPinSelected()   { view.showPinMode();   }

    /* Email flow */
    void onEmailLogin(String email, String pw) {
        if (email.isEmpty() || pw.isEmpty()) { view.showError("Please enter both email and password"); return; }
        view.setLoading(true);
        model.emailLogin(email, pw, this::routeAfterAuth, err -> {
            view.setLoading(false);
            view.showError("Login failed: " + err);
        });
    }

    /* PIN flow */
    void onPinLogin(Context ctx, String pin) {
        if (!(pin.length() == 4 || pin.length() == 6)) { view.showError("PIN must be 4 or 6 digits"); return; }
        view.setLoading(true);
        model.pinLogin(ctx, pin, this::routeAfterAuth, err -> {
            view.setLoading(false);
            view.showError(err);
        });
    }

    /* Forgot password */
    void onForgotPassword() { view.navigateToResetPw(); }

    /* Decide next screen based on responses */
    private void routeAfterAuth(String uid) {
        model.hasResponses(uid, has -> {
            view.setLoading(false);
            if (has) view.navigateToHome();
            else     view.navigateToQuestionnaire();
        }, err -> {
            view.setLoading(false);
            view.showError("Error checking data: " + err);
        });
    }
}
