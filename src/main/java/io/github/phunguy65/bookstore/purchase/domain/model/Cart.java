package io.github.phunguy65.bookstore.purchase.domain.model;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CartId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;

import java.time.Instant;
import java.util.List;

public record Cart(
        CartId id,
        CustomerId customerId,
        List<CartItem> items,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
    public Cart {
        Require.notNull(customerId, "customerId");
        items = List.copyOf(Require.notNull(items, "items"));
        Require.nonNegative(version, "version");
        Require.notNull(createdAt, "createdAt");
        Require.notNull(updatedAt, "updatedAt");
    }
}
