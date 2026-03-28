package io.github.phunguy65.bookstore.purchase.application.service;

import java.math.BigDecimal;

public record OrderItemDetailView(
        long bookId,
        String bookTitle,
        String bookIsbn,
        int quantity,
        BigDecimal lineTotal
) {
}
