package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record Ward(String value) implements Serializable {
    public Ward {
        String normalized = Require.notBlank(value, "ward");
        Require.maxLength(normalized, 150, "ward");
        value = normalized;
    }
}
