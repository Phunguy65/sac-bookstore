package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record PasswordHash(String value) implements Serializable {
    public PasswordHash {
        String normalized = Require.notBlank(value, "passwordHash");
        Require.maxLength(normalized, 255, "passwordHash");
        value = normalized;
    }
}
