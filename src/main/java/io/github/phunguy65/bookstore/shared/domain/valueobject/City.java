package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record City(String value) implements Serializable {
    public City {
        String normalized = Require.notBlank(value, "city");
        Require.maxLength(normalized, 150, "city");
        value = normalized;
    }
}
