package io.github.phunguy65.bookstore.book.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record AuthorName(String value) implements Serializable {
    public AuthorName {
        String normalized = Require.notBlank(value, "authorName");
        Require.maxLength(normalized, 150, "authorName");
        value = normalized;
    }
}
