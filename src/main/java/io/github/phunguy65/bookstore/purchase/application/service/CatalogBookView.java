package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.book.domain.valueobject.AuthorName;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookDescription;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle;
import io.github.phunguy65.bookstore.book.domain.valueobject.Isbn;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;

public record CatalogBookView(
        BookId bookId,
        Isbn isbn,
        BookTitle title,
        AuthorName author,
        BookDescription description,
        Money price,
        Quantity availableStock
) {
}
