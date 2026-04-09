package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record Province(String value) implements Serializable {
    public Province {
        String normalized = Require.notBlank(value, "province");
        Require.maxLength(normalized, 150, "province");
        value = normalized;
    }
}
