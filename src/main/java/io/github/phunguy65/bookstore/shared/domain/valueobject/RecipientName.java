package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record RecipientName(String value) implements Serializable {
    public RecipientName {
        String normalized = Require.notBlank(value, "recipientName");
        Require.maxLength(normalized, 150, "recipientName");
        value = normalized;
    }
}
