package io.github.phunguy65.bookstore.shared.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record OrderId(Long value) {
    public OrderId {
        Require.notNull(value, "orderId");
        if (value <= 0L) {
            throw new IllegalArgumentException("orderId must be positive");
        }
    }
}
