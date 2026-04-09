package io.github.phunguy65.bookstore.shared.domain.valueobject;

import java.io.Serializable;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) implements Serializable {
    public Money {
        BigDecimal normalized = Require.nonNegative(amount, "money").setScale(2, RoundingMode.HALF_UP);
        amount = normalized;
    }
}
