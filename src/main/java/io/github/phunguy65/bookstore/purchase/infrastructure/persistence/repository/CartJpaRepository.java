package io.github.phunguy65.bookstore.purchase.infrastructure.persistence.repository;

import io.github.phunguy65.bookstore.purchase.domain.model.Cart;
import io.github.phunguy65.bookstore.purchase.domain.repository.CartRepository;
import io.github.phunguy65.bookstore.purchase.infrastructure.persistence.entity.CartEntity;
import io.github.phunguy65.bookstore.purchase.infrastructure.persistence.mapper.CartEntityMapper;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CartId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@Stateless
public class CartJpaRepository implements CartRepository {
    @PersistenceContext(unitName = "bookstore")
    private EntityManager entityManager;

    @Override
    public Optional<Cart> findById(CartId cartId) {
        return findSingle(
                "select distinct c from CartEntity c left join fetch c.items i where c.id = :cartId",
                "cartId",
                cartId.value()
        );
    }

    @Override
    public Optional<Cart> findByCustomerId(CustomerId customerId) {
        return findSingle(
                "select distinct c from CartEntity c left join fetch c.items i where c.customer.id = :customerId",
                "customerId",
                customerId.value()
        );
    }

    @Override
    public Cart save(Cart cart) {
        CartEntity entity = CartEntityMapper.toEntity(cart);
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return CartEntityMapper.toDomain(entity);
        }

        CartEntity merged = entityManager.merge(entity);
        return CartEntityMapper.toDomain(merged);
    }

    private Optional<Cart> findSingle(String query, String parameterName, Long parameterValue) {
        List<CartEntity> results = entityManager.createQuery(query, CartEntity.class)
                .setParameter(parameterName, parameterValue)
                .setMaxResults(1)
                .getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(CartEntityMapper.toDomain(results.getFirst()));
    }
}
