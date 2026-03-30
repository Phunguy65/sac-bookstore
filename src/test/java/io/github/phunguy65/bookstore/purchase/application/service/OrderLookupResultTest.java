package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.purchase.domain.valueobject.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderLookupResultTest {
    @Test
    void foundResultCarriesOrder() {
        OrderDetailView order = new OrderDetailView(
                77L,
                OrderStatus.PLACED,
                new BigDecimal("12.50"),
                new OrderAddressView("Reader", "0901234567", "123 Main St", "", "Ward 1", "District 1", "Ho Chi Minh City", "Ho Chi Minh", "700000"),
                List.of()
        );

        OrderLookupResult result = OrderLookupResult.found(order);

        assertTrue(result.isFound());
        assertEquals(order, result.getOrder());
        assertNull(result.getErrorMessage());
    }

    @Test
    void notFoundResultCarriesMessage() {
        OrderLookupResult result = OrderLookupResult.notFound("missing");

        assertFalse(result.isFound());
        assertNull(result.getOrder());
        assertEquals("missing", result.getErrorMessage());
    }
}
