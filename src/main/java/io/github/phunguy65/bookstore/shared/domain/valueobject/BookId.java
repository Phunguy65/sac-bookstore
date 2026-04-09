package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record BookId(Long value) implements Serializable {
    public BookId {
        Require.notNull(value, "bookId");
        if (value <= 0L) {
            throw new IllegalArgumentException("bookId must be positive");
        }
    }
}
