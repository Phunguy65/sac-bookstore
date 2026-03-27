package io.github.phunguy65.bookstore.purchase.infrastructure.persistence.mapper;

import io.github.phunguy65.bookstore.book.infrastructure.persistence.entity.BookEntity;
import io.github.phunguy65.bookstore.purchase.domain.model.Cart;
import io.github.phunguy65.bookstore.purchase.domain.model.CartItem;
import io.github.phunguy65.bookstore.purchase.infrastructure.persistence.entity.CartEntity;
import io.github.phunguy65.bookstore.purchase.infrastructure.persistence.entity.CartItemEntity;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CartId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CartItemId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;

public final class CartEntityMapper {
    private CartEntityMapper() {
    }

    public static Cart toDomain(CartEntity entity) {
        return new Cart(
                entity.getId() == null ? null : new CartId(entity.getId()),
                new CustomerId(entity.getCustomer().getId()),
                entity.getItems().stream().map(CartEntityMapper::toDomainItem).toList(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static CartEntity toEntity(Cart domain) {
        CartEntity entity = new CartEntity();
        entity.setId(domain.id() == null ? null : domain.id().value());
        var customer = new io.github.phunguy65.bookstore.auth.infrastructure.persistence.entity.CustomerEntity();
        customer.setId(domain.customerId().value());
        entity.setCustomer(customer);
        entity.setVersion(domain.version());
        entity.setCreatedAt(domain.createdAt());
        entity.setUpdatedAt(domain.updatedAt());
        entity.setItems(domain.items().stream().map(item -> toEntityItem(item, entity)).toList());
        return entity;
    }

    private static CartItem toDomainItem(CartItemEntity entity) {
        return new CartItem(
                entity.getId() == null ? null : new CartItemId(entity.getId()),
                new BookId(entity.getBook().getId()),
                new Quantity(entity.getQuantity()),
                new Money(entity.getUnitPriceSnapshot()),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private static CartItemEntity toEntityItem(CartItem domain, CartEntity cart) {
        CartItemEntity entity = new CartItemEntity();
        entity.setId(domain.id() == null ? null : domain.id().value());
        entity.setCart(cart);
        BookEntity book = new BookEntity();
        book.setId(domain.bookId().value());
        entity.setBook(book);
        entity.setQuantity(domain.quantity().value());
        entity.setUnitPriceSnapshot(domain.unitPriceSnapshot().amount());
        entity.setVersion(domain.version());
        entity.setCreatedAt(domain.createdAt());
        entity.setUpdatedAt(domain.updatedAt());
        return entity;
    }
}
