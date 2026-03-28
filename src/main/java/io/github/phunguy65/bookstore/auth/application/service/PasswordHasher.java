package io.github.phunguy65.bookstore.auth.application.service;

public interface PasswordHasher {
    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
