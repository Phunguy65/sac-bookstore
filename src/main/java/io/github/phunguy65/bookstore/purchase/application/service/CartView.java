package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;

import java.util.List;

public record CartView(
        List<CartLineView> items,
        Money totalAmount
) {
    public CartView {
        items = List.copyOf(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
