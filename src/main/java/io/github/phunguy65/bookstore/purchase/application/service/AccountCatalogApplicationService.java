package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.book.domain.model.Book;
import io.github.phunguy65.bookstore.book.domain.repository.BookRepository;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class AccountCatalogApplicationService {
    private BookRepository bookRepository;
    private CartApplicationService cartApplicationService;

    public AccountCatalogApplicationService() {
    }

    @Inject
    public AccountCatalogApplicationService(BookRepository bookRepository, CartApplicationService cartApplicationService) {
        this.bookRepository = bookRepository;
        this.cartApplicationService = cartApplicationService;
    }

    public List<CatalogBookView> getActiveBooks() {
        return bookRepository.findActiveBooks().stream()
                .map(this::toView)
                .toList();
    }

    public CartActionResult addBook(CustomerId customerId, long bookIdValue, int quantityValue) {
        return cartApplicationService.addBook(customerId, bookIdValue, quantityValue);
    }

    private CatalogBookView toView(Book book) {
        return new CatalogBookView(
                book.id(),
                book.isbn(),
                book.title(),
                book.author(),
                book.description(),
                book.price(),
                book.stockQuantity()
        );
    }
}
