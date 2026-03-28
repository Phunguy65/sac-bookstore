package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;

public final class CheckoutResult {
    private final boolean success;
    private final OrderId orderId;
    private final String errorMessage;

    private CheckoutResult(boolean success, OrderId orderId, String errorMessage) {
        this.success = success;
        this.orderId = orderId;
        this.errorMessage = errorMessage;
    }

    public static CheckoutResult success(OrderId orderId) {
        return new CheckoutResult(true, orderId, null);
    }

    public static CheckoutResult failure(String errorMessage) {
        return new CheckoutResult(false, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
