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
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookEntityMapperTest {
    @Test
    void mapsEntityToDomain() {
        BookEntity entity = new BookEntity();
        entity.setId(10L);
        entity.setIsbn("9786041234567");
        entity.setTitle("Domain-Driven Bookstore");
        entity.setAuthor("Nguyen Van A");
        entity.setDescription("A clean architecture sample.");
        entity.setImageUrl("https://example.com/books/10.jpg");
        entity.setPrice(new BigDecimal("125000.00"));
        entity.setStockQuantity(20);
        entity.setActive(true);
        entity.setVersion(2L);
        entity.setCreatedAt(Instant.parse("2026-03-27T00:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-03-27T00:00:00Z"));

        Book book = BookEntityMapper.toDomain(entity);

        assertEquals(new BookId(10L), book.id());
        assertEquals(new Isbn("9786041234567"), book.isbn());
        assertEquals(new BookTitle("Domain-Driven Bookstore"), book.title());
        assertEquals(new AuthorName("Nguyen Van A"), book.author());
        assertEquals(new BookDescription("A clean architecture sample."), book.description());
        assertEquals(new ImageUrl("https://example.com/books/10.jpg"), book.imageUrl());
        assertEquals(new Money(new BigDecimal("125000.00")), book.price());
        assertEquals(new Quantity(20), book.stockQuantity());
        assertTrue(book.active());
        assertEquals(2L, book.version());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), book.createdAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), book.updatedAt());
    }

    @Test
    void mapsDomainToEntity() {
        Book book = new Book(
                new BookId(10L),
                new Isbn("9786041234567"),
                new BookTitle("Domain-Driven Bookstore"),
                new AuthorName("Nguyen Van A"),
                new BookDescription("A clean architecture sample."),
                new ImageUrl("https://example.com/books/10.jpg"),
                new Money(new BigDecimal("125000.00")),
                new Quantity(20),
                true,
                2L,
                Instant.parse("2026-03-27T00:00:00Z"),
                Instant.parse("2026-03-27T00:00:00Z")
        );

        BookEntity entity = BookEntityMapper.toEntity(book);

        assertEquals(10L, entity.getId());
        assertEquals("9786041234567", entity.getIsbn());
        assertEquals("Domain-Driven Bookstore", entity.getTitle());
        assertEquals("Nguyen Van A", entity.getAuthor());
        assertEquals("A clean architecture sample.", entity.getDescription());
        assertEquals("https://example.com/books/10.jpg", entity.getImageUrl());
        assertEquals(new BigDecimal("125000.00"), entity.getPrice());
        assertEquals(20, entity.getStockQuantity());
        assertTrue(entity.isActive());
        assertEquals(2L, entity.getVersion());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), entity.getCreatedAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), entity.getUpdatedAt());
    }

    @Test
    void mapsNullOptionalBookFields() {
        BookEntity entity = new BookEntity();
        entity.setId(11L);
        entity.setIsbn("9786041234567");
        entity.setTitle("Minimal Book");
        entity.setAuthor("Nguyen Van B");
        entity.setPrice(new BigDecimal("99000.00"));
        entity.setStockQuantity(5);
        entity.setActive(true);
        entity.setVersion(0L);
        entity.setCreatedAt(Instant.parse("2026-03-27T00:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-03-27T00:00:00Z"));

        Book book = BookEntityMapper.toDomain(entity);

        assertNull(book.description());
        assertNull(book.imageUrl());
        assertEquals(new BookId(11L), book.id());
        assertNotNull(book.isbn());
        assertNotNull(book.title());
        assertNotNull(book.author());
        assertEquals(new Money(new BigDecimal("99000.00")), book.price());
    }
}
