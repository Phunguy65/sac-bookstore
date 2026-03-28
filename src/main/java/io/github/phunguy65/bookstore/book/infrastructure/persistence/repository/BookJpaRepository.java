package io.github.phunguy65.bookstore.book.infrastructure.persistence.repository;

import io.github.phunguy65.bookstore.book.domain.model.Book;
import io.github.phunguy65.bookstore.book.domain.repository.BookRepository;
import io.github.phunguy65.bookstore.book.infrastructure.persistence.entity.BookEntity;
import io.github.phunguy65.bookstore.book.infrastructure.persistence.mapper.BookEntityMapper;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Stateless
public class BookJpaRepository implements BookRepository {
    @PersistenceContext(unitName = "bookstore")
    private EntityManager entityManager;

    @Override
    public List<Book> findActiveBooks() {
        return entityManager.createQuery(
                        "select b from BookEntity b where b.active = true order by b.title asc",
                        BookEntity.class
                )
                .getResultList()
                .stream()
                .map(BookEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Book> findByIds(Set<BookId> bookIds) {
        if (bookIds.isEmpty()) {
            return List.of();
        }
        List<Long> ids = bookIds.stream().map(BookId::value).toList();
        return entityManager.createQuery(
                        "select b from BookEntity b where b.id in :ids",
                        BookEntity.class
                )
                .setParameter("ids", ids)
                .getResultList()
                .stream()
                .map(BookEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Book> findById(BookId bookId) {
        BookEntity entity = entityManager.find(BookEntity.class, bookId.value());
        return entity == null ? Optional.empty() : Optional.of(BookEntityMapper.toDomain(entity));
    }

    @Override
    public Book save(Book book) {
        BookEntity entity = BookEntityMapper.toEntity(book);
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return BookEntityMapper.toDomain(entity);
        }

        BookEntity merged = entityManager.merge(entity);
        return BookEntityMapper.toDomain(merged);
    }
}
