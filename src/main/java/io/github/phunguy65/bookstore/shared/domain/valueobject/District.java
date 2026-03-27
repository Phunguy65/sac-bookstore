package io.github.phunguy65.bookstore.shared.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record District(String value) {
    public District {
        String normalized = Require.notBlank(value, "district");
        Require.maxLength(normalized, 150, "district");
        value = normalized;
    }
}
