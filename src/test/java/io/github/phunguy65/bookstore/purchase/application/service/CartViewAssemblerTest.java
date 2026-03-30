package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartViewAssemblerTest {
    @Test
    void toCartViewReturnsEmptyViewForEmptyCart() {
        CartView view = CartViewAssembler.toCartView(
                PurchaseServiceTestSupport.cart(new CustomerId(1L)),
                new PurchaseServiceTestSupport.InMemoryBookRepository()
        );

        assertTrue(view.isEmpty());
        assertEquals(BigDecimal.ZERO.setScale(2), view.totalAmount().amount());
    }

    @Test
    void toCartViewCalculatesTotalsFromItems() {
        var books = new PurchaseServiceTestSupport.InMemoryBookRepository();
        books.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));

        CartView view = CartViewAssembler.toCartView(
                PurchaseServiceTestSupport.cart(new CustomerId(1L), PurchaseServiceTestSupport.cartItem(1L, 11L, 2, "12.50")),
                books
        );

        assertEquals(1, view.items().size());
        assertEquals(new BigDecimal("25.00"), view.totalAmount().amount());
    }

    @Test
    void toCartViewThrowsWhenBookCannotBeResolved() {
        assertThrows(
                IllegalStateException.class,
                () -> CartViewAssembler.toCartView(
                        PurchaseServiceTestSupport.cart(new CustomerId(1L), PurchaseServiceTestSupport.cartItem(1L, 99L, 1, "12.50")),
                        new PurchaseServiceTestSupport.InMemoryBookRepository()
                )
        );
    }
}
