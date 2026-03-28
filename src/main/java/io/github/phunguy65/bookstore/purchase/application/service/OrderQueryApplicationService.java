package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.purchase.domain.model.Order;
import io.github.phunguy65.bookstore.purchase.domain.repository.OrderRepository;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class OrderQueryApplicationService {
    private OrderRepository orderRepository;

    public OrderQueryApplicationService() {
    }

    @Inject
    public OrderQueryApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderSummaryView> getOrderHistory(CustomerId customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(order -> new OrderSummaryView(
                        order.id(),
                        order.status(),
                        order.totalAmount(),
                        order.items().size(),
                        order.placedAt()
                ))
                .toList();
    }

    public OrderLookupResult getOwnedOrder(CustomerId customerId, long orderIdValue) {
        try {
            OrderId orderId = new OrderId(orderIdValue);
            Order order = orderRepository.findByCustomerIdAndId(customerId, orderId).orElse(null);
            if (order == null) {
                return OrderLookupResult.notFound("Khong tim thay don hang phu hop.");
            }
            return OrderLookupResult.found(new OrderDetailView(
                    order.id().value(),
                    order.status(),
                    order.totalAmount().amount(),
                    new OrderAddressView(
                            order.shippingAddress().recipientName().value(),
                            order.shippingAddress().phoneNumber().value(),
                            order.shippingAddress().line1().value(),
                            order.shippingAddress().line2(),
                            order.shippingAddress().ward().value(),
                            order.shippingAddress().district().value(),
                            order.shippingAddress().city().value(),
                            order.shippingAddress().province().value(),
                            order.shippingAddress().postalCode().value()
                    ),
                    order.items().stream().map(item -> new OrderItemDetailView(
                            item.bookId().value(),
                            item.bookTitleSnapshot().value(),
                            item.bookIsbnSnapshot().value(),
                            item.quantity().value(),
                            item.lineTotal().amount()
                    )).toList()
            ));
        } catch (IllegalArgumentException ex) {
            return OrderLookupResult.notFound("Khong tim thay don hang phu hop.");
        }
    }
}
