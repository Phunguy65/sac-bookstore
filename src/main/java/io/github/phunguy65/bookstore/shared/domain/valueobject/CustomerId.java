package io.github.phunguy65.bookstore.shared.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record CustomerId(Long value) {
    public CustomerId {
        Require.notNull(value, "customerId");
        if (value <= 0L) {
            throw new IllegalArgumentException("customerId must be positive");
        }
    }
}
