package io.github.phunguy65.bookstore.shared.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record Province(String value) {
    public Province {
        String normalized = Require.notBlank(value, "province");
        Require.maxLength(normalized, 150, "province");
        value = normalized;
    }
}
