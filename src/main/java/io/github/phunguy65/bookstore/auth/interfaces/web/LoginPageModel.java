package io.github.phunguy65.bookstore.auth.interfaces.web;

public class LoginPageModel {
    private final String email;
    private final String errorMessage;
    private final String infoMessage;
    private final boolean showDevCredentials;
    private final String devEmail;
    private final String devPassword;

    public LoginPageModel(String email, String errorMessage, String infoMessage, boolean showDevCredentials, String devEmail, String devPassword) {
        this.email = email;
        this.errorMessage = errorMessage;
        this.infoMessage = infoMessage;
        this.showDevCredentials = showDevCredentials;
        this.devEmail = devEmail;
        this.devPassword = devPassword;
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

    public boolean isShowDevCredentials() {
        return showDevCredentials;
    }

    public String getDevEmail() {
        return devEmail;
    }

    public String getDevPassword() {
        return devPassword;
    }
}
