package io.github.phunguy65.bookstore.auth.application.service;

public interface DemoModeSettings {
    boolean isEnabled();

    String getEmail();

    String getPassword();
}
