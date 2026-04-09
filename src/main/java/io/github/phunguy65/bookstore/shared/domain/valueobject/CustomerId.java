package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record CustomerId(Long value) implements Serializable {
    public static final CustomerId DEFAULT_CUSTOMER = new CustomerId(1L);

    public CustomerId {
        Require.notNull(value, "customerId");
        if (value <= 0L) {
            throw new IllegalArgumentException("customerId must be positive");
        }
    }
}
