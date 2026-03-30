package io.github.phunguy65.bookstore.shared.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record Quantity(int value) {
    public Quantity {
        Require.nonNegative(value, "quantity");
    }
}
