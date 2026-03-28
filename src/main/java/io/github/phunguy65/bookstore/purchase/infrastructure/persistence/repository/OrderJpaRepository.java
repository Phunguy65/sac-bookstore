package io.github.phunguy65.bookstore.purchase.infrastructure.persistence.repository;

import io.github.phunguy65.bookstore.purchase.domain.model.Order;
import io.github.phunguy65.bookstore.purchase.domain.repository.OrderRepository;
import io.github.phunguy65.bookstore.purchase.infrastructure.persistence.entity.OrderEntity;
import io.github.phunguy65.bookstore.purchase.infrastructure.persistence.mapper.OrderEntityMapper;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Stateless
public class OrderJpaRepository implements OrderRepository {
    @PersistenceContext(unitName = "bookstore")
    private EntityManager entityManager;

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return findSingle(
                "select distinct o from OrderEntity o left join fetch o.items i where o.id = :orderId",
                "orderId",
                orderId.value()
        );
    }

    @Override
    public Optional<Order> findByCustomerIdAndId(CustomerId customerId, OrderId orderId) {
        List<OrderEntity> results = entityManager.createQuery(
                        "select distinct o from OrderEntity o left join fetch o.items i where o.customer.id = :customerId and o.id = :orderId",
                        OrderEntity.class
                )
                .setParameter("customerId", customerId.value())
                .setParameter("orderId", orderId.value())
                .setMaxResults(1)
                .getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(OrderEntityMapper.toDomain(results.getFirst()));
    }

    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        return entityManager.createQuery(
                        "select distinct o from OrderEntity o left join fetch o.items i where o.customer.id = :customerId order by o.createdAt desc",
                        OrderEntity.class
                )
                .setParameter("customerId", customerId.value())
                .getResultList()
                .stream()
                .map(OrderEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderEntityMapper.toEntity(order);
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return OrderEntityMapper.toDomain(entity);
        }

        OrderEntity merged = entityManager.merge(entity);
        return OrderEntityMapper.toDomain(merged);
    }

    private Optional<Order> findSingle(String query, String parameterName, Long parameterValue) {
        List<OrderEntity> results = entityManager.createQuery(query, OrderEntity.class)
                .setParameter(parameterName, parameterValue)
                .setMaxResults(1)
                .getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(OrderEntityMapper.toDomain(results.getFirst()));
    }
}
