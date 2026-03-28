package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.purchase.domain.valueobject.OrderStatus;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;

import java.time.Instant;

public record OrderSummaryView(
        OrderId orderId,
        OrderStatus status,
        Money totalAmount,
        int itemCount,
        Instant placedAt
) {
}
