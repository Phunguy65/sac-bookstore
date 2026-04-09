package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record Quantity(int value) implements Serializable {
    public Quantity {
        Require.nonNegative(value, "quantity");
    }
}
