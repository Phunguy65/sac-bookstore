package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.purchase.application.service.OrderLookupResult;
import io.github.phunguy65.bookstore.purchase.application.service.OrderQueryApplicationService;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class OrderDetailPageBean implements OrderDetailPage {
    private OrderQueryApplicationService orderQueryApplicationService;

    public OrderDetailPageBean() {
    }

    @Inject
    public OrderDetailPageBean(OrderQueryApplicationService orderQueryApplicationService) {
        this.orderQueryApplicationService = orderQueryApplicationService;
    }

    @Override
    public OrderDetailPageResult handle(OrderDetailPageRequest request) {
        long orderId = parseOrderId(request.orderIdParam());
        if (orderId <= 0L) {
            return new OrderDetailPageResult(PageAction.REDIRECT, PurchasePaths.ORDERS + "?error=missing", null);
        }

        OrderLookupResult result = orderQueryApplicationService.getOwnedOrder(CustomerId.DEFAULT_CUSTOMER, orderId);
        if (!result.isFound()) {
            return new OrderDetailPageResult(PageAction.REDIRECT, PurchasePaths.ORDERS + "?error=missing", null);
        }
        return new OrderDetailPageResult(PageAction.RENDER, null, new OrderDetailPageModel(result.getOrder()));
    }

    private long parseOrderId(String value) {
        try {
            return Long.parseLong(value == null ? "" : value.trim());
        } catch (NumberFormatException ex) {
            return -1L;
        }
    }
}
