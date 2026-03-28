package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;

public final class CheckoutResult {
    private final boolean success;
    private final OrderId orderId;
    private final String errorMessage;
    private final java.util.Map<String, String> fieldErrors;

    private CheckoutResult(boolean success, OrderId orderId, String errorMessage, java.util.Map<String, String> fieldErrors) {
        this.success = success;
        this.orderId = orderId;
        this.errorMessage = errorMessage;
        this.fieldErrors = java.util.Map.copyOf(fieldErrors);
    }

    public static CheckoutResult success(OrderId orderId) {
        return new CheckoutResult(true, orderId, null, java.util.Map.of());
    }

    public static CheckoutResult failure(String errorMessage) {
        return new CheckoutResult(false, null, errorMessage, java.util.Map.of());
    }

    public static CheckoutResult failure(String errorMessage, java.util.Map<String, String> fieldErrors) {
        return new CheckoutResult(false, null, errorMessage, fieldErrors);
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

    public String getFieldError(String fieldName) {
        return fieldErrors.get(fieldName);
    }
}
