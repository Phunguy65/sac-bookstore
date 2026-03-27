package io.github.phunguy65.bookstore.book.domain.model;

import io.github.phunguy65.bookstore.book.domain.valueobject.AuthorName;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookDescription;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle;
import io.github.phunguy65.bookstore.book.domain.valueobject.ImageUrl;
import io.github.phunguy65.bookstore.book.domain.valueobject.Isbn;
import io.github.phunguy65.bookstore.shared.domain.validation.Require;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;

import java.time.Instant;

public record Book(
        BookId id,
        Isbn isbn,
        BookTitle title,
        AuthorName author,
        BookDescription description,
        ImageUrl imageUrl,
        Money price,
        Quantity stockQuantity,
        boolean active,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
    public Book {
        Require.notNull(isbn, "isbn");
        Require.notNull(title, "title");
        Require.notNull(author, "author");
        Require.notNull(price, "price");
        Require.notNull(stockQuantity, "stockQuantity");
        Require.nonNegative(version, "version");
        Require.notNull(createdAt, "createdAt");
        Require.notNull(updatedAt, "updatedAt");
    }
}
