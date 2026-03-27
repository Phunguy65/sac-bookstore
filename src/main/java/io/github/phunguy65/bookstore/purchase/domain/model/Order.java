package io.github.phunguy65.bookstore.purchase.domain.model;

import io.github.phunguy65.bookstore.purchase.domain.valueobject.OrderStatus;
import io.github.phunguy65.bookstore.shared.domain.validation.Require;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressDetails;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;

import java.time.Instant;
import java.util.List;

public record Order(
        OrderId id,
        CustomerId customerId,
        OrderStatus status,
        Money totalAmount,
        AddressDetails shippingAddress,
        List<OrderItem> items,
        Instant placedAt,
        Instant cancelledAt,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
    public Order {
        Require.notNull(customerId, "customerId");
        Require.notNull(status, "status");
        Require.notNull(totalAmount, "totalAmount");
        Require.notNull(shippingAddress, "shippingAddress");
        items = List.copyOf(Require.notNull(items, "items"));
        Require.nonNegative(version, "version");
        Require.notNull(createdAt, "createdAt");
        Require.notNull(updatedAt, "updatedAt");
    }
}
