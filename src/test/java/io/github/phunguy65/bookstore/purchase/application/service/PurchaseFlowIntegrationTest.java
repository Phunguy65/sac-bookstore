package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurchaseFlowIntegrationTest {
    @Test
    void checkoutFlowPreservesCrossRepositorySideEffectsOnSuccess() {
        var customerId = new CustomerId(7L);
        var books = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var carts = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addresses = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orders = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        books.put(PurchaseServiceTestSupport.book(21L, "Domain-Driven Design", 4, true, "30.00"));
        carts.put(PurchaseServiceTestSupport.cart(customerId, PurchaseServiceTestSupport.cartItem(1L, 21L, 2, "30.00")));
        CheckoutApplicationService checkout = new CheckoutApplicationService(carts, books, addresses, orders);

        var result = checkout.placeOrder(customerId, new CheckoutAddressInput(
                "Pham Thi B", "0901234567", "456 River St", "", "Ward 2", "District 2", "Ho Chi Minh City", "Ho Chi Minh", "700001"
        ));

        assertTrue(result.isSuccess());
        assertEquals(2, books.findById(new io.github.phunguy65.bookstore.shared.domain.valueobject.BookId(21L)).orElseThrow().stockQuantity().value());
        assertTrue(carts.findByCustomerId(customerId).orElseThrow().items().isEmpty());
        assertEquals(1, orders.findByCustomerId(customerId).size());
        assertEquals("Pham Thi B", addresses.findDefaultByCustomerId(customerId).orElseThrow().details().recipientName().value());
    }

    @Test
    void checkoutFlowKeepsCartSnapshotPricesAcrossRepositories() {
        var customerId = new CustomerId(8L);
        var books = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var carts = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addresses = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orders = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        books.put(PurchaseServiceTestSupport.book(21L, "Domain-Driven Design", 5, true, "45.00"));
        books.put(PurchaseServiceTestSupport.book(22L, "Clean Architecture", 5, true, "30.00"));
        carts.put(PurchaseServiceTestSupport.cart(
                customerId,
                PurchaseServiceTestSupport.cartItem(1L, 21L, 1, "30.00"),
                PurchaseServiceTestSupport.cartItem(2L, 22L, 2, "20.00")
        ));
        addresses.put(PurchaseServiceTestSupport.address(1L, customerId, true));
        CheckoutApplicationService checkout = new CheckoutApplicationService(carts, books, addresses, orders);

        var result = checkout.placeOrder(customerId, new CheckoutAddressInput("", "", "", "", "", "", "", "", ""));

        assertTrue(result.isSuccess());
        var savedOrder = orders.findByCustomerId(customerId).getFirst();
        assertEquals("70.00", savedOrder.totalAmount().amount().toPlainString());
        assertEquals("30.00", savedOrder.items().get(0).unitPriceSnapshot().amount().toPlainString());
        assertEquals("20.00", savedOrder.items().get(1).unitPriceSnapshot().amount().toPlainString());
    }

    @Test
    void checkoutFlowLeavesCartUntouchedWhenStockIsInsufficient() {
        var customerId = new CustomerId(9L);
        var books = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var carts = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addresses = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orders = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        books.put(PurchaseServiceTestSupport.book(21L, "Domain-Driven Design", 1, true, "30.00"));
        carts.put(PurchaseServiceTestSupport.cart(customerId, PurchaseServiceTestSupport.cartItem(1L, 21L, 2, "30.00")));
        addresses.put(PurchaseServiceTestSupport.address(1L, customerId, true));
        CheckoutApplicationService checkout = new CheckoutApplicationService(carts, books, addresses, orders);

        var result = checkout.placeOrder(customerId, new CheckoutAddressInput("", "", "", "", "", "", "", "", ""));

        assertFalse(result.isSuccess());
        assertEquals(1, carts.findByCustomerId(customerId).orElseThrow().items().size());
        assertTrue(orders.findByCustomerId(customerId).isEmpty());
    }
}
