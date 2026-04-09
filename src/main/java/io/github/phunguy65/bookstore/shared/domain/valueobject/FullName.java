package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record FullName(String value) implements Serializable {
    public FullName {
        String normalized = Require.notBlank(value, "fullName");
        Require.maxLength(normalized, 150, "fullName");
        value = normalized;
    }
}
