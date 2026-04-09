package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record OrderItemId(Long value) implements Serializable {
    public OrderItemId {
        Require.notNull(value, "orderItemId");
        if (value <= 0L) {
            throw new IllegalArgumentException("orderItemId must be positive");
        }
    }
}
