package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) implements Serializable {
    private static final Pattern PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        String normalized = Require.notBlank(value, "email").toLowerCase(Locale.ROOT);
        Require.maxLength(normalized, 255, "email");
        Require.matches(normalized, PATTERN, "email", "must be a valid email address");
        value = normalized;
    }
}
