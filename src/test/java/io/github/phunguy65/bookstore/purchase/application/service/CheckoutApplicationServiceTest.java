package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckoutApplicationServiceTest {
    private static final CustomerId CUSTOMER_ID = new CustomerId(1L);

    @Test
    void getCheckoutPrefillsDefaultAddressWhenAvailable() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addressRepository = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50")));
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        addressRepository.put(PurchaseServiceTestSupport.address(1L, CUSTOMER_ID, true));
        CheckoutApplicationService service = new CheckoutApplicationService(cartRepository, bookRepository, addressRepository, orderRepository);

        CheckoutView checkout = service.getCheckout(CUSTOMER_ID);

        assertFalse(checkout.requiresShippingAddressInput());
        assertEquals("Reader 1", checkout.shippingAddress().recipientName().value());
    }

    @Test
    void successfulOrderPlacementCreatesOrderDecrementsStockAndClearsCart() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addressRepository = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 2, "12.50")));
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        addressRepository.put(PurchaseServiceTestSupport.address(1L, CUSTOMER_ID, true));
        CheckoutApplicationService service = new CheckoutApplicationService(cartRepository, bookRepository, addressRepository, orderRepository);

        CheckoutResult result = service.placeOrder(CUSTOMER_ID, new CheckoutAddressInput("", "", "", "", "", "", "", "", ""));

        assertTrue(result.isSuccess());
        assertNotNull(result.getOrderId());
        assertEquals(6, bookRepository.findById(new io.github.phunguy65.bookstore.shared.domain.valueobject.BookId(11L)).orElseThrow().stockQuantity().value());
        assertTrue(cartRepository.findByCustomerId(CUSTOMER_ID).orElseThrow().items().isEmpty());
        assertEquals(1, orderRepository.findByCustomerId(CUSTOMER_ID).size());
    }

    @Test
    void failedOrderPlacementLeavesCartUnchangedAndDoesNotCreateDefaultAddress() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addressRepository = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 3, "12.50")));
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 2, true, "12.50"));
        CheckoutApplicationService service = new CheckoutApplicationService(cartRepository, bookRepository, addressRepository, orderRepository);

        CheckoutResult result = service.placeOrder(CUSTOMER_ID, new CheckoutAddressInput(
                "Nguyen Van A", "0901234567", "123 Main St", "", "Ward 1", "District 1", "Ho Chi Minh City", "Ho Chi Minh", "700000"
        ));

        assertFalse(result.isSuccess());
        assertEquals(1, cartRepository.findByCustomerId(CUSTOMER_ID).orElseThrow().items().size());
        assertTrue(addressRepository.findDefaultByCustomerId(CUSTOMER_ID).isEmpty());
        assertTrue(orderRepository.findByCustomerId(CUSTOMER_ID).isEmpty());
    }

    @Test
    void orderPlacementWithoutDefaultAddressSavesSubmittedAddressAsNewDefault() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addressRepository = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50")));
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        CheckoutApplicationService service = new CheckoutApplicationService(cartRepository, bookRepository, addressRepository, orderRepository);

        CheckoutResult result = service.placeOrder(CUSTOMER_ID, new CheckoutAddressInput(
                "Nguyen Van A", "0901234567", "123 Main St", "", "Ward 1", "District 1", "Ho Chi Minh City", "Ho Chi Minh", "700000"
        ));

        assertTrue(result.isSuccess());
        assertEquals("Nguyen Van A", addressRepository.findDefaultByCustomerId(CUSTOMER_ID).orElseThrow().details().recipientName().value());
    }

    @Test
    void getCheckoutRequiresAddressInputWhenNoDefaultExists() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addressRepository = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50")));
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        CheckoutApplicationService service = new CheckoutApplicationService(cartRepository, bookRepository, addressRepository, orderRepository);

        CheckoutView checkout = service.getCheckout(CUSTOMER_ID);

        assertTrue(checkout.requiresShippingAddressInput());
        assertNull(checkout.shippingAddress());
    }

    @Test
    void placeOrderFailsWhenBookBecomesInactive() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addressRepository = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50")));
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, false, "12.50"));
        addressRepository.put(PurchaseServiceTestSupport.address(1L, CUSTOMER_ID, true));
        CheckoutApplicationService service = new CheckoutApplicationService(cartRepository, bookRepository, addressRepository, orderRepository);

        CheckoutResult result = service.placeOrder(CUSTOMER_ID, new CheckoutAddressInput("", "", "", "", "", "", "", "", ""));

        assertFalse(result.isSuccess());
        assertEquals("Co sach trong gio hang hien khong con mo ban.", result.getErrorMessage());
    }

    @Test
    void placeOrderFailsWhenAnyItemInMultiItemCartIsOutOfStock() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addressRepository = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        cartRepository.put(PurchaseServiceTestSupport.cart(
                CUSTOMER_ID,
                PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50"),
                PurchaseServiceTestSupport.cartItem(2L, 12L, 2, "25.00")
        ));
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        bookRepository.put(PurchaseServiceTestSupport.book(12L, "DDD", 1, true, "25.00"));
        addressRepository.put(PurchaseServiceTestSupport.address(1L, CUSTOMER_ID, true));
        CheckoutApplicationService service = new CheckoutApplicationService(cartRepository, bookRepository, addressRepository, orderRepository);

        CheckoutResult result = service.placeOrder(CUSTOMER_ID, new CheckoutAddressInput("", "", "", "", "", "", "", "", ""));

        assertFalse(result.isSuccess());
        assertEquals("Khong du ton kho de hoan tat don hang.", result.getErrorMessage());
        assertEquals(2, cartRepository.findByCustomerId(CUSTOMER_ID).orElseThrow().items().size());
    }

    @Test
    void placeOrderWithDefaultAddressKeepsExistingDefaultAddress() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addressRepository = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 11L, 1, "12.50")));
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        addressRepository.put(PurchaseServiceTestSupport.address(1L, CUSTOMER_ID, true));
        CheckoutApplicationService service = new CheckoutApplicationService(cartRepository, bookRepository, addressRepository, orderRepository);

        CheckoutResult result = service.placeOrder(CUSTOMER_ID, new CheckoutAddressInput(
                "Changed Name", "0909999999", "456 Other St", "", "Ward X", "District X", "City X", "Province X", "999999"
        ));

        assertTrue(result.isSuccess());
        assertEquals("Reader 1", addressRepository.findDefaultByCustomerId(CUSTOMER_ID).orElseThrow().details().recipientName().value());
    }

    @Test
    void placeOrderCalculatesTotalAcrossMultipleItems() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addressRepository = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        cartRepository.put(PurchaseServiceTestSupport.cart(
                CUSTOMER_ID,
                PurchaseServiceTestSupport.cartItem(1L, 11L, 2, "12.50"),
                PurchaseServiceTestSupport.cartItem(2L, 12L, 1, "25.00")
        ));
        bookRepository.put(PurchaseServiceTestSupport.book(11L, "Clean Code", 8, true, "12.50"));
        bookRepository.put(PurchaseServiceTestSupport.book(12L, "DDD", 8, true, "25.00"));
        addressRepository.put(PurchaseServiceTestSupport.address(1L, CUSTOMER_ID, true));
        CheckoutApplicationService service = new CheckoutApplicationService(cartRepository, bookRepository, addressRepository, orderRepository);

        CheckoutResult result = service.placeOrder(CUSTOMER_ID, new CheckoutAddressInput("", "", "", "", "", "", "", "", ""));

        assertTrue(result.isSuccess());
        assertEquals("50.00", orderRepository.findByCustomerId(CUSTOMER_ID).getFirst().totalAmount().amount().toPlainString());
    }

    @Test
    void placeOrderFailsWhenCartBookCannotBeFound() {
        var bookRepository = new PurchaseServiceTestSupport.InMemoryBookRepository();
        var cartRepository = new PurchaseServiceTestSupport.InMemoryCartRepository();
        var addressRepository = new PurchaseServiceTestSupport.InMemoryAddressRepository();
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        cartRepository.put(PurchaseServiceTestSupport.cart(CUSTOMER_ID, PurchaseServiceTestSupport.cartItem(1L, 12L, 1, "12.50")));
        addressRepository.put(PurchaseServiceTestSupport.address(1L, CUSTOMER_ID, true));
        CheckoutApplicationService service = new CheckoutApplicationService(cartRepository, bookRepository, addressRepository, orderRepository);

        CheckoutResult result = service.placeOrder(CUSTOMER_ID, new CheckoutAddressInput("", "", "", "", "", "", "", "", ""));

        assertFalse(result.isSuccess());
        assertEquals("Khong tim thay sach trong gio hang.", result.getErrorMessage());
        assertEquals(1, cartRepository.findByCustomerId(CUSTOMER_ID).orElseThrow().items().size());
    }
}
