package io.github.phunguy65.bookstore.shared.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record City(String value) {
    public City {
        String normalized = Require.notBlank(value, "city");
        Require.maxLength(normalized, 150, "city");
        value = normalized;
    }
}
