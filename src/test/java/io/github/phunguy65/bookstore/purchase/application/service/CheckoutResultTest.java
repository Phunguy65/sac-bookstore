package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckoutResultTest {
    @Test
    void successResultCarriesOrderId() {
        CheckoutResult result = CheckoutResult.success(new OrderId(55L));

        assertTrue(result.isSuccess());
        assertEquals(new OrderId(55L), result.getOrderId());
        assertNull(result.getErrorMessage());
    }

    @Test
    void failureResultCarriesMessage() {
        CheckoutResult result = CheckoutResult.failure("failed");

        assertFalse(result.isSuccess());
        assertEquals("failed", result.getErrorMessage());
        assertNull(result.getOrderId());
    }

    @Test
    void failureResultCarriesFieldErrors() {
        CheckoutResult result = CheckoutResult.failure("invalid", java.util.Map.of("recipientName", "blank"));

        assertFalse(result.isSuccess());
        assertEquals("blank", result.getFieldError("recipientName"));
    }
}
