package io.github.phunguy65.bookstore.shared.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record CartItemId(Long value) {
    public CartItemId {
        Require.notNull(value, "cartItemId");
        if (value <= 0L) {
            throw new IllegalArgumentException("cartItemId must be positive");
        }
    }
}
