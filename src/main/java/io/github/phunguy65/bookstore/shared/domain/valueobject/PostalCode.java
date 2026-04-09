package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record PostalCode(String value) implements Serializable {
    public PostalCode {
        String normalized = Require.notBlank(value, "postalCode");
        Require.maxLength(normalized, 20, "postalCode");
        value = normalized;
    }
}
