package io.github.phunguy65.bookstore.purchase.interfaces.web;

import jakarta.ejb.Remote;

@Remote
public interface OrderHistoryPage {
    OrderHistoryPageResult handle(OrderHistoryPageRequest request);
}
