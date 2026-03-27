package io.github.phunguy65.bookstore.book.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record BookDescription(String value) {
    public BookDescription {
        String normalized = Require.notBlank(value, "bookDescription");
        Require.maxLength(normalized, 4000, "bookDescription");
        value = normalized;
    }
}
