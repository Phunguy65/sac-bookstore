package io.github.phunguy65.bookstore.shared.domain.validation;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class Require {
    private Require() {
    }

    public static <T> T notNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        return value;
    }

    public static String notBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    public static String maxLength(String value, int max, String fieldName) {
        if (value.length() > max) {
            throw new IllegalArgumentException(fieldName + " must be at most " + max + " characters");
        }
        return value;
    }

    public static String matches(String value, Pattern pattern, String fieldName, String message) {
        if (!pattern.matcher(value).matches()) {
            throw new IllegalArgumentException(fieldName + " " + message);
        }
        return value;
    }

    public static int positive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
        return value;
    }

    public static int nonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " must not be negative");
        }
        return value;
    }

    public static long nonNegative(long value, String fieldName) {
        if (value < 0L) {
            throw new IllegalArgumentException(fieldName + " must not be negative");
        }
        return value;
    }

    public static BigDecimal nonNegative(BigDecimal value, String fieldName) {
        notNull(value, fieldName);
        if (value.signum() < 0) {
            throw new IllegalArgumentException(fieldName + " must not be negative");
        }
        return value;
    }

    public static String nullableMaxLength(String value, int max, String fieldName) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return maxLength(trimmed, max, fieldName);
    }
}
