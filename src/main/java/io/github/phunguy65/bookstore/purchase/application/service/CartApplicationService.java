package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.book.domain.model.Book;
import io.github.phunguy65.bookstore.book.domain.repository.BookRepository;
import io.github.phunguy65.bookstore.purchase.domain.model.Cart;
import io.github.phunguy65.bookstore.purchase.domain.model.CartItem;
import io.github.phunguy65.bookstore.purchase.domain.repository.CartRepository;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;
import io.github.phunguy65.bookstore.shared.domain.validation.FieldValidationException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CartApplicationService {
    private BookRepository bookRepository;
    private CartRepository cartRepository;

    public CartApplicationService() {
    }

    @Inject
    public CartApplicationService(BookRepository bookRepository, CartRepository cartRepository) {
        this.bookRepository = bookRepository;
        this.cartRepository = cartRepository;
    }

    public CartView getCart(CustomerId customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> emptyCart(customerId));
        return CartViewAssembler.toCartView(cart, bookRepository);
    }

    public CartActionResult addBook(CustomerId customerId, long bookIdValue, int quantityValue) {
        try {
            Book book = requireAvailableBook(new BookId(bookIdValue));
            Quantity requestedQuantity = new Quantity(quantityValue);
            Cart cart = cartRepository.findByCustomerId(customerId).orElseGet(() -> emptyCart(customerId));

            List<CartItem> updatedItems = new ArrayList<>(cart.items());
            int existingIndex = indexOfBook(updatedItems, book.id());
            int nextQuantity = requestedQuantity.value();
            if (existingIndex >= 0) {
                nextQuantity += updatedItems.get(existingIndex).quantity().value();
            }
            ensureWithinStock(book, nextQuantity);

            CartItem updatedItem = new CartItem(
                    existingIndex >= 0 ? updatedItems.get(existingIndex).id() : null,
                    book.id(),
                    new Quantity(nextQuantity),
                    book.price(),
                    existingIndex >= 0 ? updatedItems.get(existingIndex).version() : 0L,
                    existingIndex >= 0 ? updatedItems.get(existingIndex).createdAt() : Instant.now(),
                    Instant.now()
            );
            if (existingIndex >= 0) {
                updatedItems.set(existingIndex, updatedItem);
            } else {
                updatedItems.add(updatedItem);
            }

            cartRepository.save(copyCart(cart, updatedItems));
            return CartActionResult.success();
        } catch (IllegalArgumentException ex) {
            return cartFailure(ex);
        }
    }

    public CartActionResult updateQuantity(CustomerId customerId, long bookIdValue, int quantityValue) {
        try {
            BookId bookId = new BookId(bookIdValue);
            Quantity quantity = new Quantity(quantityValue);
            Book book = requireAvailableBook(bookId);
            ensureWithinStock(book, quantity.value());

            Cart cart = cartRepository.findByCustomerId(customerId).orElseGet(() -> emptyCart(customerId));
            List<CartItem> updatedItems = new ArrayList<>(cart.items());
            int existingIndex = indexOfBook(updatedItems, bookId);
            if (existingIndex < 0) {
                return CartActionResult.failure("Sach khong ton tai trong gio hang.");
            }

            CartItem existingItem = updatedItems.get(existingIndex);
            updatedItems.set(existingIndex, new CartItem(
                    existingItem.id(),
                    existingItem.bookId(),
                    quantity,
                    book.price(),
                    existingItem.version(),
                    existingItem.createdAt(),
                    Instant.now()
            ));
            cartRepository.save(copyCart(cart, updatedItems));
            return CartActionResult.success();
        } catch (IllegalArgumentException ex) {
            return cartFailure(ex);
        }
    }

    public CartActionResult removeBook(CustomerId customerId, long bookIdValue) {
        try {
            BookId bookId = new BookId(bookIdValue);
            Cart cart = cartRepository.findByCustomerId(customerId).orElseGet(() -> emptyCart(customerId));
            List<CartItem> updatedItems = cart.items().stream()
                    .filter(item -> !item.bookId().equals(bookId))
                    .toList();
            cartRepository.save(copyCart(cart, updatedItems));
            return CartActionResult.success();
        } catch (IllegalArgumentException ex) {
            return CartActionResult.failure(ex.getMessage());
        }
    }

    private Book requireAvailableBook(BookId bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new FieldValidationException("bookId", "Khong tim thay sach duoc yeu cau."));
        if (!book.active()) {
            throw new FieldValidationException("quantity", "Sach nay hien khong con mo ban.");
        }
        if (book.stockQuantity().value() <= 0) {
            throw new FieldValidationException("quantity", "Sach nay da het hang.");
        }
        return book;
    }

    private void ensureWithinStock(Book book, int quantity) {
        if (quantity > book.stockQuantity().value()) {
            throw new IllegalArgumentException("So luong vuot qua ton kho hien tai.");
        }
    }

    private CartActionResult cartFailure(IllegalArgumentException ex) {
        String message = ex.getMessage();
        java.util.Map<String, String> fieldErrors = new java.util.HashMap<>();
        if (ex instanceof FieldValidationException fieldValidationException) {
            fieldErrors.put(fieldValidationException.getFieldName(), message);
        }
        return CartActionResult.failure(message, fieldErrors);
    }

    private int indexOfBook(List<CartItem> items, BookId bookId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).bookId().equals(bookId)) {
                return i;
            }
        }
        return -1;
    }

    private Cart emptyCart(CustomerId customerId) {
        Instant now = Instant.now();
        return new Cart(null, customerId, List.of(), 0L, now, now);
    }

    private Cart copyCart(Cart cart, List<CartItem> items) {
        return new Cart(
                cart.id(),
                cart.customerId(),
                items,
                cart.version(),
                cart.createdAt(),
                Instant.now()
        );
    }
}
