package io.github.phunguy65.bookstore.shared.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record Ward(String value) {
    public Ward {
        String normalized = Require.notBlank(value, "ward");
        Require.maxLength(normalized, 150, "ward");
        value = normalized;
    }
}
