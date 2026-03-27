package io.github.phunguy65.bookstore.book.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record BookTitle(String value) {
    public BookTitle {
        String normalized = Require.notBlank(value, "bookTitle");
        Require.maxLength(normalized, 255, "bookTitle");
        value = normalized;
    }
}
