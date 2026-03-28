package io.github.phunguy65.bookstore.purchase.application.service;

public final class OrderLookupResult {
    private final OrderDetailView order;
    private final String errorMessage;

    private OrderLookupResult(OrderDetailView order, String errorMessage) {
        this.order = order;
        this.errorMessage = errorMessage;
    }

    public static OrderLookupResult found(OrderDetailView order) {
        return new OrderLookupResult(order, null);
    }

    public static OrderLookupResult notFound(String errorMessage) {
        return new OrderLookupResult(null, errorMessage);
    }

    public boolean isFound() {
        return order != null;
    }

    public OrderDetailView getOrder() {
        return order;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
