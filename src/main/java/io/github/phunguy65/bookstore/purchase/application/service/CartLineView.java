package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;

public record CartLineView(
        BookId bookId,
        BookTitle bookTitle,
        Money unitPrice,
        Quantity quantity,
        Money lineTotal,
        Quantity availableStock
) {
}
