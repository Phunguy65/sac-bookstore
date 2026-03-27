package io.github.phunguy65.bookstore.shared.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record BookId(Long value) {
    public BookId {
        Require.notNull(value, "bookId");
        if (value <= 0L) {
            throw new IllegalArgumentException("bookId must be positive");
        }
    }
}
