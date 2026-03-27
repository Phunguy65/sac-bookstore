package io.github.phunguy65.bookstore.shared.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {
    @Test
    void normalizesEmailToLowerCase() {
        Email email = new Email("Customer@Example.com");

        assertEquals("customer@example.com", email.value());
    }

    @Test
    void rejectsInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
    }

    @Test
    void rejectsBlankEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email("   "));
    }

    @Test
    void rejectsNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    void acceptsEmailAtLengthBoundary() {
        String localPart = "a".repeat(243);
        String raw = localPart + "@example.com";

        Email email = new Email(raw);

        assertEquals(raw.toLowerCase(Locale.ROOT), email.value());
    }

    @Test
    void rejectsEmailExceedingLengthBoundary() {
        String raw = "a".repeat(244) + "@example.com";

        assertThrows(IllegalArgumentException.class, () -> new Email(raw));
    }
}
