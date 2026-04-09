package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record AddressId(Long value) implements Serializable {
    public AddressId {
        Require.notNull(value, "addressId");
        if (value <= 0L) {
            throw new IllegalArgumentException("addressId must be positive");
        }
    }
}
