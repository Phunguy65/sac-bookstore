package io.github.phunguy65.bookstore.book.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookValueObjectsTest {
    @Test
    void acceptsValidBookMetadata() {
        assertEquals("9786041234567", new Isbn("978-6041234567").value());
        assertEquals("043942089X", new Isbn("0-439-42089-X").value());
        assertEquals("Clean Architecture", new BookTitle("Clean Architecture").value());
        assertEquals("Robert C. Martin", new AuthorName("Robert C. Martin").value());
        assertEquals("A software architecture book.", new BookDescription("A software architecture book.").value());
        assertEquals("https://example.com/book.jpg", new ImageUrl("https://example.com/book.jpg").value());
    }

    @Test
    void rejectsInvalidBookMetadata() {
        assertThrows(IllegalArgumentException.class, () -> new Isbn("12345"));
        assertThrows(IllegalArgumentException.class, () -> new Isbn("0-439-42089-0"));
        assertThrows(IllegalArgumentException.class, () -> new Isbn("9780451524934"));
        assertThrows(IllegalArgumentException.class, () -> new BookTitle(" "));
        assertThrows(IllegalArgumentException.class, () -> new AuthorName(" "));
        assertThrows(IllegalArgumentException.class, () -> new BookDescription(" "));
        assertThrows(IllegalArgumentException.class, () -> new ImageUrl("/relative/path.jpg"));
    }
}
