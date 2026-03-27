package io.github.phunguy65.bookstore.book.domain.repository;

import io.github.phunguy65.bookstore.book.domain.model.Book;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    List<Book> findActiveBooks();

    Optional<Book> findById(BookId bookId);

    Book save(Book book);
}
