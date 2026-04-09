package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.purchase.application.service.OrderQueryApplicationService;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class OrderHistoryPageBean implements OrderHistoryPage {
    private static final int PAGE_SIZE = 10;
    private OrderQueryApplicationService orderQueryApplicationService;

    public OrderHistoryPageBean() {
    }

    @Inject
    public OrderHistoryPageBean(OrderQueryApplicationService orderQueryApplicationService) {
        this.orderQueryApplicationService = orderQueryApplicationService;
    }

    @Override
    public OrderHistoryPageResult handle(OrderHistoryPageRequest request) {
        String errorMessage = null;
        if ("missing".equals(request.errorParam())) {
            errorMessage = "Khong tim thay don hang phu hop.";
        }
        var orders = orderQueryApplicationService.getOrderHistory(CustomerId.DEFAULT_CUSTOMER);
        int totalPages = Math.max(1, (int) Math.ceil((double) orders.size() / PAGE_SIZE));
        int currentPage = parsePage(request.pageParam(), totalPages);
        int fromIndex = Math.min((currentPage - 1) * PAGE_SIZE, orders.size());
        int toIndex = Math.min(fromIndex + PAGE_SIZE, orders.size());
        OrderHistoryPageModel model = new OrderHistoryPageModel(orders.subList(fromIndex, toIndex), errorMessage, currentPage, totalPages);
        return new OrderHistoryPageResult(PageAction.RENDER, model);
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
