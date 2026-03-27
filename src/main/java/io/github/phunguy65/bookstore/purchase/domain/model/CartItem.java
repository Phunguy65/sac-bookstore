package io.github.phunguy65.bookstore.purchase.domain.model;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CartItemId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;

import java.time.Instant;

public record CartItem(
        CartItemId id,
        BookId bookId,
        Quantity quantity,
        Money unitPriceSnapshot,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
    public CartItem {
        Require.notNull(bookId, "bookId");
        Require.notNull(quantity, "quantity");
        Require.notNull(unitPriceSnapshot, "unitPriceSnapshot");
        Require.nonNegative(version, "version");
        Require.notNull(createdAt, "createdAt");
        Require.notNull(updatedAt, "updatedAt");
    }
}
