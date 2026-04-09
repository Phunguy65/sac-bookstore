package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

import io.github.phunguy65.bookstore.purchase.application.service.OrderSummaryView;

import java.util.List;

public class OrderHistoryPageModel implements Serializable {
    private final List<OrderSummaryView> orders;
    private final String errorMessage;
    private final int currentPage;
    private final int totalPages;

    public OrderHistoryPageModel(List<OrderSummaryView> orders, String errorMessage, int currentPage, int totalPages) {
        this.orders = List.copyOf(orders);
        this.errorMessage = errorMessage;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public List<OrderSummaryView> getOrders() {
        return orders;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasPreviousPage() {
        return currentPage > 1;
    }

    public boolean isHasNextPage() {
        return currentPage < totalPages;
    }
}
