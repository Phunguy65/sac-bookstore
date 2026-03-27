package io.github.phunguy65.bookstore.shared.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdentityValueObjectsTest {
    @Test
    void acceptsPositiveIdentifiers() {
        assertEquals(1L, new CustomerId(1L).value());
        assertEquals(2L, new AddressId(2L).value());
        assertEquals(3L, new BookId(3L).value());
        assertEquals(4L, new CartId(4L).value());
        assertEquals(5L, new CartItemId(5L).value());
        assertEquals(6L, new OrderId(6L).value());
        assertEquals(7L, new OrderItemId(7L).value());
    }

    @Test
    void rejectsNonPositiveIdentifiers() {
        assertThrows(IllegalArgumentException.class, () -> new CustomerId(0L));
        assertThrows(IllegalArgumentException.class, () -> new AddressId(-1L));
        assertThrows(IllegalArgumentException.class, () -> new BookId(0L));
        assertThrows(IllegalArgumentException.class, () -> new CartId(-2L));
        assertThrows(IllegalArgumentException.class, () -> new CartItemId(0L));
        assertThrows(IllegalArgumentException.class, () -> new OrderId(-3L));
        assertThrows(IllegalArgumentException.class, () -> new OrderItemId(0L));
    }
}
