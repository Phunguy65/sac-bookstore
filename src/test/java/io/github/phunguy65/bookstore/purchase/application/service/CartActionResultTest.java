package io.github.phunguy65.bookstore.purchase.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartActionResultTest {
    @Test
    void successResultHasNoErrorState() {
        CartActionResult result = CartActionResult.success();

        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
        assertNull(result.getFieldError("quantity"));
    }

    @Test
    void failureResultStoresErrorMessage() {
        CartActionResult result = CartActionResult.failure("boom");

        assertFalse(result.isSuccess());
        assertEquals("boom", result.getErrorMessage());
    }

    @Test
    void failureResultStoresFieldErrors() {
        CartActionResult result = CartActionResult.failure("invalid", java.util.Map.of("quantity", "too much"));

        assertFalse(result.isSuccess());
        assertEquals("too much", result.getFieldError("quantity"));
    }
}
