package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartApplicationServiceTest {
    private static final CustomerId CUSTOMER_ID = new CustomerId(1L);

    @Test
    void addBookToEmptyCartCreatesCartItemWithSnapshot() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        CartApplicationService service = new CartApplicationService(bookRepository, cartRepository);

        CartActionResult result = service.addBook(CUSTOMER_ID, 11L, 2);

        assertTrue(result.isSuccess());
        CartView cart = service.getCart(CUSTOMER_ID);
        assertEquals(1, cart.items().size());
        assertEquals(2, cart.items().getFirst().quantity().value());
        assertEquals("12.50", cart.items().getFirst().unitPrice().amount().toPlainString());
    }

    @Test
    void addExistingBookIncrementsQuantityInsteadOfDuplicating() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50")));
        CartApplicationService service = new CartApplicationService(bookRepository, cartRepository);

        CartActionResult result = service.addBook(CUSTOMER_ID, 11L, 3);

        assertTrue(result.isSuccess());
        CartView cart = service.getCart(CUSTOMER_ID);
        assertEquals(1, cart.items().size());
        assertEquals(4, cart.items().getFirst().quantity().value());
    }

    @Test
    void updateQuantityPersistsNewQuantity() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50")));
        CartApplicationService service = new CartApplicationService(bookRepository, cartRepository);

        CartActionResult result = service.updateQuantity(CUSTOMER_ID, 11L, 5);

        assertTrue(result.isSuccess());
        assertEquals(5, service.getCart(CUSTOMER_ID).items().getFirst().quantity().value());
    }

    @Test
    void removeBookDeletesItemFromCart() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50")));
        CartApplicationService service = new CartApplicationService(bookRepository, cartRepository);

        CartActionResult result = service.removeBook(CUSTOMER_ID, 11L);

        assertTrue(result.isSuccess());
        assertTrue(service.getCart(CUSTOMER_ID).isEmpty());
    }

    @Test
    void rejectsQuantityThatExceedsStock() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 2, true, "12.50"));
        CartApplicationService service = new CartApplicationService(bookRepository, cartRepository);

        CartActionResult result = service.addBook(CUSTOMER_ID, 11L, 3);

        assertFalse(result.isSuccess());
        assertEquals("So luong vuot qua ton kho hien tai.", result.getErrorMessage());
    }

    @Test
    void rejectsAddingInactiveBook() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, false, "12.50"));
        CartApplicationService service = new CartApplicationService(bookRepository, cartRepository);

        CartActionResult result = service.addBook(CUSTOMER_ID, 11L, 1);

        assertFalse(result.isSuccess());
        assertEquals("Sach nay hien khong con mo ban.", result.getErrorMessage());
    }

    @Test
    void rejectsUpdateQuantityThatExceedsStock() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 3, true, "12.50"));
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50")));
        CartApplicationService service = new CartApplicationService(bookRepository, cartRepository);

        CartActionResult result = service.updateQuantity(CUSTOMER_ID, 11L, 5);

        assertFalse(result.isSuccess());
        assertEquals("So luong vuot qua ton kho hien tai.", result.getErrorMessage());
    }

    @Test
    void removeMissingBookBehavesAsNoOp() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50")));
        CartApplicationService service = new CartApplicationService(bookRepository, cartRepository);

        CartActionResult result = service.removeBook(CUSTOMER_ID, 99L);

        assertTrue(result.isSuccess());
        assertEquals(1, service.getCart(CUSTOMER_ID).items().size());
    }

    @Test
    void rejectsAddingNonExistentBook() {
        CartApplicationService service = new CartApplicationService(
                new PurchaseServiceTestSupport.InMemoryBookRepository(),
                new PurchaseServiceTestSupport.InMemoryCartRepository()
        );

        CartActionResult result = service.addBook(CUSTOMER_ID, 99L, 1);

        assertFalse(result.isSuccess());
        assertEquals("Khong tim thay sach duoc yeu cau.", result.getErrorMessage());
    }

    @Test
    void rejectsZeroQuantityWhenAddingBook() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        CartApplicationService service = new CartApplicationService(bookRepository, cartRepository);

        CartActionResult result = service.addBook(CUSTOMER_ID, 11L, 0);

        assertFalse(result.isSuccess());
        assertEquals("quantity must be positive", result.getErrorMessage());
    }

    @Test
    void rejectsNegativeQuantityWhenUpdatingBook() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50")));
        CartApplicationService service = new CartApplicationService(bookRepository, cartRepository);

        CartActionResult result = service.updateQuantity(CUSTOMER_ID, 11L, -5);

        assertFalse(result.isSuccess());
        assertEquals("quantity must be positive", result.getErrorMessage());
    }
}
