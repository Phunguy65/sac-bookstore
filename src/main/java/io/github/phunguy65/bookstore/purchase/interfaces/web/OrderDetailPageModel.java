package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.purchase.application.service.OrderDetailView;

public class OrderDetailPageModel {
    private final OrderDetailView order;

    public OrderDetailPageModel(OrderDetailView order) {
        this.order = order;
    }

    public OrderDetailView getOrder() {
        return order;
    }
}
