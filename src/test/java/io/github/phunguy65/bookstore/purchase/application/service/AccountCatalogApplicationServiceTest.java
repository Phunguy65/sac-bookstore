package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountCatalogApplicationServiceTest {
    private static final CustomerId CUSTOMER_ID = new CustomerId(1L);

    @Test
    void getActiveBooksReturnsOnlyActiveBooks() {
        var books = new PurchaseServiceTestSupport.InMemoryBookRepository();
        books.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        books.put(PurchaseServiceTestSupport.book(12L, "Inactive", 8, false, "12.50"));

        AccountCatalogApplicationService service = new AccountCatalogApplicationService(books, new StubCartApplicationService());

        List<CatalogBookView> result = service.getActiveBooks();

        assertEquals(1, result.size());
        assertEquals(11L, result.getFirst().bookId().value());
    }

    @Test
    void getActiveBooksReturnsEmptyListWhenNoBooksAreActive() {
        var books = new PurchaseServiceTestSupport.InMemoryBookRepository();
        books.put(PurchaseServiceTestSupport.book(12L, "Inactive", 8, false, "12.50"));

        AccountCatalogApplicationService service = new AccountCatalogApplicationService(books, new StubCartApplicationService());

        assertTrue(service.getActiveBooks().isEmpty());
    }

    @Test
    void addBookDelegatesToCartService() {
        StubCartApplicationService cartService = new StubCartApplicationService();
        cartService.result = CartActionResult.success();
        AccountCatalogApplicationService service = new AccountCatalogApplicationService(new PurchaseServiceTestSupport.InMemoryBookRepository(), cartService);

        CartActionResult result = service.addBook(CUSTOMER_ID, 11L, 2);

        assertTrue(result.isSuccess());
        assertEquals(CUSTOMER_ID, cartService.customerId);
        assertEquals(11L, cartService.bookIdValue);
        assertEquals(2, cartService.quantityValue);
    }

    @Test
    void getActiveBooksIncludesOutOfStockBooks() {
        var books = new PurchaseServiceTestSupport.InMemoryBookRepository();
        books.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 0, true, "12.50"));

        AccountCatalogApplicationService service = new AccountCatalogApplicationService(books, new StubCartApplicationService());

        List<CatalogBookView> result = service.getActiveBooks();

        assertEquals(1, result.size());
        assertEquals(0, result.getFirst().availableStock().value());
    }

    @Test
    void addBookReturnsCartServiceFailure() {
        StubCartApplicationService cartService = new StubCartApplicationService();
        cartService.result = CartActionResult.failure("Out of stock");
        AccountCatalogApplicationService service = new AccountCatalogApplicationService(new PurchaseServiceTestSupport.InMemoryBookRepository(), cartService);

        CartActionResult result = service.addBook(CUSTOMER_ID, 11L, 2);

        assertFalse(result.isSuccess());
        assertEquals("Out of stock", result.getErrorMessage());
    }

    private static final class StubCartApplicationService extends CartApplicationService {
        private CartActionResult result = CartActionResult.failure("failure");
        private CustomerId customerId;
        private long bookIdValue;
        private int quantityValue;

        @Override
        public CartActionResult addBook(CustomerId customerId, long bookIdValue, int quantityValue) {
            this.customerId = customerId;
            this.bookIdValue = bookIdValue;
            this.quantityValue = quantityValue;
            return result;
        }
    }
}
