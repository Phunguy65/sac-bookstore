package io.github.phunguy65.bookstore.book.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

import java.util.regex.Pattern;

public record Isbn(String value) implements Serializable {
    private static final Pattern PATTERN = Pattern.compile("^(97(8|9))?\\d{9}(\\d|X)$");

    public Isbn {
        String normalized = Require.notBlank(value, "isbn").replace("-", "").toUpperCase();
        Require.matches(normalized, PATTERN, "isbn", "must be a valid ISBN-10 or ISBN-13");
        if (!isValidChecksum(normalized)) {
            throw new IllegalArgumentException("isbn must have a valid checksum");
        }
        value = normalized;
    }

    private static boolean isValidChecksum(String normalized) {
        return normalized.length() == 10 ? isValidIsbn10(normalized) : isValidIsbn13(normalized);
    }

    private static boolean isValidIsbn10(String normalized) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (10 - i) * Character.digit(normalized.charAt(i), 10);
        }
        char checkChar = normalized.charAt(9);
        sum += (checkChar == 'X' ? 10 : Character.digit(checkChar, 10));
        return sum % 11 == 0;
    }

    private static boolean isValidIsbn13(String normalized) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.digit(normalized.charAt(i), 10);
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int expectedCheckDigit = (10 - (sum % 10)) % 10;
        int actualCheckDigit = Character.digit(normalized.charAt(12), 10);
        return expectedCheckDigit == actualCheckDigit;
    }
}
