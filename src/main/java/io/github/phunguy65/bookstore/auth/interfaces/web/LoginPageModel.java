package io.github.phunguy65.bookstore.auth.interfaces.web;

public class LoginPageModel {
    private final String email;
    private final String errorMessage;
    private final String infoMessage;

    public LoginPageModel(String email, String errorMessage, String infoMessage) {
        this.email = email;
        this.errorMessage = errorMessage;
        this.infoMessage = infoMessage;
    }

    public String getEmail() {
        return email;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getInfoMessage() {
        return infoMessage;
    }
}
