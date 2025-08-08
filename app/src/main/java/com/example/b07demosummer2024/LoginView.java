package com.example.b07demosummer2024;

public interface LoginView {
    void showEmailMode();
    void showPinMode();
    void showError(String msg);
    void setLoading(boolean loading);
    void navigateToHome();
    void navigateToQuestionnaire();
    void navigateToResetPw();
}
