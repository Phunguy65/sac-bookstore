package io.github.phunguy65.bookstore.shared.domain.valueobject;

import org.junit.jupiter.api.Test;

import io.github.phunguy65.bookstore.auth.domain.valueobject.CustomerStatus;
import io.github.phunguy65.bookstore.purchase.domain.valueobject.OrderStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonAndAddressValueObjectsTest {
    @Test
    void personValueObjectsValidateContent() {
        assertEquals("Nguyen Van A", new FullName(" Nguyen Van A ").value());
        assertEquals("hashed-value", new PasswordHash("hashed-value").value());
        assertEquals("Nguyen Van A", new RecipientName("Nguyen Van A").value());
        assertThrows(IllegalArgumentException.class, () -> new FullName(" "));
        assertThrows(IllegalArgumentException.class, () -> new PasswordHash(" "));
    }

    @Test
    void addressValueObjectsValidateAndNormalize() {
        AddressDetails details = new AddressDetails(
                new RecipientName("Nguyen Van A"),
                new PhoneNumber("0901234567"),
                new AddressLine("123 Main St"),
                "  Apt 101  ",
                new Ward("Ward 1"),
                new District("District 1"),
                new City("Ho Chi Minh City"),
                new Province("Ho Chi Minh"),
                new PostalCode("700000")
        );

        assertEquals("Apt 101", details.line2());
        assertEquals("Ward 1", new Ward("Ward 1").value());
        assertEquals("District 1", new District("District 1").value());
        assertEquals("Ho Chi Minh City", new City("Ho Chi Minh City").value());
        assertEquals("Ho Chi Minh", new Province("Ho Chi Minh").value());
        assertEquals("700000", new PostalCode("700000").value());
    }

    @Test
    void addressDetailsAllowsNullLine2() {
        AddressDetails details = new AddressDetails(
                new RecipientName("Nguyen Van A"),
                new PhoneNumber("0901234567"),
                new AddressLine("123 Main St"),
                null,
                new Ward("Ward 1"),
                new District("District 1"),
                new City("Ho Chi Minh City"),
                new Province("Ho Chi Minh"),
                new PostalCode("700000")
        );

        assertNull(details.line2());
    }

    @Test
    void enumValueObjectsExposeExpectedConstants() {
        assertEquals(CustomerStatus.ACTIVE, CustomerStatus.valueOf("ACTIVE"));
        assertEquals(CustomerStatus.INACTIVE, CustomerStatus.valueOf("INACTIVE"));
        assertEquals(OrderStatus.PLACED, OrderStatus.valueOf("PLACED"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
    }
}
