package io.github.phunguy65.bookstore.purchase.domain.repository;

import io.github.phunguy65.bookstore.purchase.domain.model.Order;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(OrderId orderId);

    Optional<Order> findByCustomerIdAndId(CustomerId customerId, OrderId orderId);

    List<Order> findByCustomerId(CustomerId customerId);

    Order save(Order order);
}
