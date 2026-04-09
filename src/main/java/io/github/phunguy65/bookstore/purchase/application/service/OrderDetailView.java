package io.github.phunguy65.bookstore.purchase.application.service;

import java.io.Serializable;

import io.github.phunguy65.bookstore.purchase.domain.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderDetailView(
        long orderId,
        OrderStatus status,
        BigDecimal totalAmount,
        OrderAddressView shippingAddress,
        List<OrderItemDetailView> items
) implements Serializable {
    public OrderDetailView {
        items = List.copyOf(items);
    }
}
