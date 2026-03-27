package io.github.phunguy65.bookstore.purchase.domain.repository;

import io.github.phunguy65.bookstore.purchase.domain.model.Cart;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CartId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;

import java.util.Optional;

public interface CartRepository {
    Optional<Cart> findById(CartId cartId);

    Optional<Cart> findByCustomerId(CustomerId customerId);

    Cart save(Cart cart);
}
