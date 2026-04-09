package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record CartId(Long value) implements Serializable {
    public CartId {
        Require.notNull(value, "cartId");
        if (value <= 0L) {
            throw new IllegalArgumentException("cartId must be positive");
        }
    }
}
