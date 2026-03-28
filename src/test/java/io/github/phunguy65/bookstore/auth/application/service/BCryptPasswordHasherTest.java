package io.github.phunguy65.bookstore.auth.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BCryptPasswordHasherTest {
    private final BCryptPasswordHasher passwordHasher = new BCryptPasswordHasher();

    @Test
    void hashesAndMatchesPassword() {
        String hash = passwordHasher.hash("secret-123");

        assertNotEquals("secret-123", hash);
        assertTrue(passwordHasher.matches("secret-123", hash));
    }

    @Test
    void rejectsNonMatchingPassword() {
        String hash = passwordHasher.hash("secret-123");

        assertFalse(passwordHasher.matches("different", hash));
    }

    @Test
    void rejectsBlankPasswordWhenHashing() {
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.hash(" "));
    }

    @Test
    void rejectsBlankPasswordWhenMatching() {
        String hash = passwordHasher.hash("secret-123");

        assertThrows(IllegalArgumentException.class, () -> passwordHasher.matches("", hash));
    }

    @Test
    void rejectsBlankHashWhenMatching() {
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.matches("secret-123", " "));
    }

    @Test
    void rejectsMalformedHash() {
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.matches("secret-123", "not-a-bcrypt-hash"));
    }

    @Test
    void producesDifferentHashesForSamePassword() {
        String first = passwordHasher.hash("secret-123");
        String second = passwordHasher.hash("secret-123");

        assertNotEquals(first, second);
        assertDoesNotThrow(() -> passwordHasher.matches("secret-123", first));
        assertDoesNotThrow(() -> passwordHasher.matches("secret-123", second));
    }
}
