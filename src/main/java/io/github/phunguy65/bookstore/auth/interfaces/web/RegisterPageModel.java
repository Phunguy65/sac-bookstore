package io.github.phunguy65.bookstore.auth.interfaces.web;

public class RegisterPageModel {
    private final String email;
    private final String fullName;
    private final String phoneNumber;
    private final String errorMessage;

    public RegisterPageModel(String email, String fullName, String phoneNumber, String errorMessage) {
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.errorMessage = errorMessage;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
