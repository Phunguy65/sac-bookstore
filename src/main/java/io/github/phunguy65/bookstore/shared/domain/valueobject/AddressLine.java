package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record AddressLine(String value) implements Serializable {
    public AddressLine {
        String normalized = Require.notBlank(value, "addressLine");
        Require.maxLength(normalized, 255, "addressLine");
        value = normalized;
    }
}
