package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.auth.interfaces.web.AuthSession;
import io.github.phunguy65.bookstore.purchase.application.service.OrderLookupResult;
import io.github.phunguy65.bookstore.purchase.application.service.OrderQueryApplicationService;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Named
@Dependent
public class OrderDetailPageBean {
    private final OrderQueryApplicationService orderQueryApplicationService;

    @Inject
    public OrderDetailPageBean(OrderQueryApplicationService orderQueryApplicationService) {
        this.orderQueryApplicationService = orderQueryApplicationService;
    }

    public OrderDetailPageModel handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long orderId = parseOrderId(request.getParameter("orderId"));
        if (orderId <= 0L) {
            response.sendRedirect(request.getContextPath() + PurchasePaths.ORDERS + "?error=missing");
            return null;
        }

        OrderLookupResult result = orderQueryApplicationService.getOwnedOrder(AuthSession.getCustomerId(request), orderId);
        if (!result.isFound()) {
            response.sendRedirect(request.getContextPath() + PurchasePaths.ORDERS + "?error=missing");
            return null;
        }
        return new OrderDetailPageModel(result.getOrder());
    }

    private long parseOrderId(String value) {
        try {
            return Long.parseLong(value == null ? "" : value.trim());
        } catch (NumberFormatException ex) {
            return -1L;
        }
    }
}
