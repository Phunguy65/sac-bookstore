package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.auth.interfaces.web.AuthSession;
import io.github.phunguy65.bookstore.purchase.application.service.OrderQueryApplicationService;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Named
@Dependent
public class OrderHistoryPageBean {
    private static final int PAGE_SIZE = 10;
    private final OrderQueryApplicationService orderQueryApplicationService;

    @Inject
    public OrderHistoryPageBean(OrderQueryApplicationService orderQueryApplicationService) {
        this.orderQueryApplicationService = orderQueryApplicationService;
    }

    public OrderHistoryPageModel handle(HttpServletRequest request, HttpServletResponse response) {
        String errorMessage = null;
        if ("missing".equals(request.getParameter("error"))) {
            errorMessage = "Khong tim thay don hang phu hop.";
        }
        var orders = orderQueryApplicationService.getOrderHistory(AuthSession.getCustomerId(request));
        int totalPages = Math.max(1, (int) Math.ceil((double) orders.size() / PAGE_SIZE));
        int currentPage = parsePage(request.getParameter("page"), totalPages);
        int fromIndex = Math.min((currentPage - 1) * PAGE_SIZE, orders.size());
        int toIndex = Math.min(fromIndex + PAGE_SIZE, orders.size());
        return new OrderHistoryPageModel(orders.subList(fromIndex, toIndex), errorMessage, currentPage, totalPages);
    }

    private int parsePage(String rawPage, int totalPages) {
        try {
            int page = Integer.parseInt(rawPage == null ? "1" : rawPage.trim());
            if (page < 1) {
                return 1;
            }
            return Math.min(page, totalPages);
        } catch (NumberFormatException ex) {
            return 1;
        }
    }
}
