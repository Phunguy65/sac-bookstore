package io.github.phunguy65.bookstore.purchase.domain.model;

import io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle;
import io.github.phunguy65.bookstore.book.domain.valueobject.Isbn;
import io.github.phunguy65.bookstore.shared.domain.validation.Require;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderItemId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;

import java.time.Instant;

public record OrderItem(
        OrderItemId id,
        BookId bookId,
        BookTitle bookTitleSnapshot,
        Isbn bookIsbnSnapshot,
        Money unitPriceSnapshot,
        Quantity quantity,
        Money lineTotal,
        Instant createdAt
) {
    public OrderItem {
        Require.notNull(bookId, "bookId");
        Require.notNull(bookTitleSnapshot, "bookTitleSnapshot");
        Require.notNull(bookIsbnSnapshot, "bookIsbnSnapshot");
        Require.notNull(unitPriceSnapshot, "unitPriceSnapshot");
        Require.notNull(quantity, "quantity");
        Require.positive(quantity.value(), "quantity");
        Require.notNull(lineTotal, "lineTotal");
        Require.notNull(createdAt, "createdAt");
    }
}
