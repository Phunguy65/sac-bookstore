package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.purchase.domain.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderDetailView(
        long orderId,
        OrderStatus status,
        BigDecimal totalAmount,
        OrderAddressView shippingAddress,
        List<OrderItemDetailView> items
) {
    public OrderDetailView {
        items = List.copyOf(items);
    }
}
