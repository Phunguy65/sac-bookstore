package io.github.phunguy65.bookstore.shared.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

import java.util.regex.Pattern;

public record PhoneNumber(String value) {
    private static final Pattern PATTERN = Pattern.compile("^[0-9+()\\- ]{8,20}$");

    public PhoneNumber {
        String normalized = Require.notBlank(value, "phoneNumber");
        Require.matches(normalized, PATTERN, "phoneNumber", "must be a valid phone number");
        value = normalized;
    }
}
