package io.github.phunguy65.bookstore.book.infrastructure.persistence.mapper;

import io.github.phunguy65.bookstore.book.domain.model.Book;
import io.github.phunguy65.bookstore.book.domain.valueobject.AuthorName;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookDescription;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle;
import io.github.phunguy65.bookstore.book.domain.valueobject.ImageUrl;
import io.github.phunguy65.bookstore.book.domain.valueobject.Isbn;
import io.github.phunguy65.bookstore.book.infrastructure.persistence.entity.BookEntity;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;

public final class BookEntityMapper {
    private BookEntityMapper() {
    }

    public static Book toDomain(BookEntity entity) {
        return new Book(
                entity.getId() == null ? null : new BookId(entity.getId()),
                new Isbn(entity.getIsbn()),
                new BookTitle(entity.getTitle()),
                new AuthorName(entity.getAuthor()),
                entity.getDescription() == null ? null : new BookDescription(entity.getDescription()),
                entity.getImageUrl() == null ? null : new ImageUrl(entity.getImageUrl()),
                new Money(entity.getPrice()),
                new Quantity(entity.getStockQuantity()),
                entity.isActive(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static BookEntity toEntity(Book domain) {
        BookEntity entity = new BookEntity();
        entity.setId(domain.id() == null ? null : domain.id().value());
        entity.setIsbn(domain.isbn().value());
        entity.setTitle(domain.title().value());
        entity.setAuthor(domain.author().value());
        entity.setDescription(domain.description() == null ? null : domain.description().value());
        entity.setImageUrl(domain.imageUrl() == null ? null : domain.imageUrl().value());
        entity.setPrice(domain.price().amount());
        entity.setStockQuantity(domain.stockQuantity().value());
        entity.setActive(domain.active());
        entity.setVersion(domain.version());
        entity.setCreatedAt(domain.createdAt());
        entity.setUpdatedAt(domain.updatedAt());
        return entity;
    }
}
