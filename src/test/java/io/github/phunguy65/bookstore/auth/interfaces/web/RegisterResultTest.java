package io.github.phunguy65.bookstore.auth.interfaces.web;

import io.github.phunguy65.bookstore.auth.application.service.RegisterResult;
import io.github.phunguy65.bookstore.auth.domain.model.Customer;
import io.github.phunguy65.bookstore.auth.domain.valueobject.CustomerStatus;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Email;
import io.github.phunguy65.bookstore.shared.domain.valueobject.FullName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PasswordHash;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterResultTest {
    @Test
    void successKeepsCustomerAndClearsError() {
        RegisterResult result = RegisterResult.success(customer());

        assertTrue(result.isSuccess());
        assertEquals("reader@example.com", result.getCustomer().email().value());
        assertNull(result.getErrorMessage());
    }

    @Test
    void failureKeepsErrorAndClearsCustomer() {
        RegisterResult result = RegisterResult.failure("Oops");

        assertFalse(result.isSuccess());
        assertNull(result.getCustomer());
        assertEquals("Oops", result.getErrorMessage());
    }

    private static Customer customer() {
        Instant now = Instant.parse("2026-03-27T00:00:00Z");
        return new Customer(
                new CustomerId(1L),
                new Email("reader@example.com"),
                new PasswordHash("HASH::secret-123"),
                new FullName("Nguyen Van Doc"),
                new PhoneNumber("0901234567"),
                CustomerStatus.ACTIVE,
                0L,
                now,
                now
        );
    }
}
