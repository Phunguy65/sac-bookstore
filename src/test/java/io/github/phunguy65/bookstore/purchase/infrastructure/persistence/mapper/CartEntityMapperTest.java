package io.github.phunguy65.bookstore.purchase.infrastructure.persistence.mapper;

import io.github.phunguy65.bookstore.auth.infrastructure.persistence.entity.CustomerEntity;
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
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartEntityMapperTest {
    @Test
    void mapsEntityToDomain() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        BookEntity book = new BookEntity();
        book.setId(2L);

        CartItemEntity item = new CartItemEntity();
        item.setId(3L);
        item.setBook(book);
        item.setQuantity(2);
        item.setUnitPriceSnapshot(new BigDecimal("100.00"));
        item.setVersion(1L);
        item.setCreatedAt(Instant.parse("2026-03-27T00:00:00Z"));
        item.setUpdatedAt(Instant.parse("2026-03-27T00:00:00Z"));

        CartEntity entity = new CartEntity();
        entity.setId(4L);
        entity.setCustomer(customer);
        entity.setItems(List.of(item));
        item.setCart(entity);
        entity.setVersion(1L);
        entity.setCreatedAt(Instant.parse("2026-03-27T00:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-03-27T00:00:00Z"));

        Cart cart = CartEntityMapper.toDomain(entity);

        assertEquals(new CartId(4L), cart.id());
        assertEquals(new CustomerId(1L), cart.customerId());
        assertEquals(1, cart.items().size());
        assertEquals(1L, cart.version());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), cart.createdAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), cart.updatedAt());
        CartItem itemDomain = cart.items().getFirst();
        assertEquals(new CartItemId(3L), itemDomain.id());
        assertEquals(new BookId(2L), itemDomain.bookId());
        assertEquals(new Quantity(2), itemDomain.quantity());
        assertEquals(new Money(new BigDecimal("100.00")), itemDomain.unitPriceSnapshot());
        assertEquals(1L, itemDomain.version());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), itemDomain.createdAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), itemDomain.updatedAt());
    }

    @Test
    void mapsDomainToEntity() {
        Cart cart = new Cart(
                new CartId(4L),
                new CustomerId(1L),
                List.of(new CartItem(
                        new CartItemId(3L),
                        new BookId(2L),
                        new Quantity(2),
                        new Money(new BigDecimal("100.00")),
                        1L,
                        Instant.parse("2026-03-27T00:00:00Z"),
                        Instant.parse("2026-03-27T00:00:00Z")
                )),
                1L,
                Instant.parse("2026-03-27T00:00:00Z"),
                Instant.parse("2026-03-27T00:00:00Z")
        );

        CartEntity entity = CartEntityMapper.toEntity(cart);

        assertEquals(4L, entity.getId());
        assertEquals(1L, entity.getCustomer().getId());
        assertEquals(1, entity.getItems().size());
        assertEquals(1L, entity.getVersion());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), entity.getCreatedAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), entity.getUpdatedAt());
        CartItemEntity itemEntity = entity.getItems().getFirst();
        assertEquals(3L, itemEntity.getId());
        assertEquals(2L, itemEntity.getBook().getId());
        assertEquals(2, itemEntity.getQuantity());
        assertEquals(new BigDecimal("100.00"), itemEntity.getUnitPriceSnapshot());
        assertEquals(1L, itemEntity.getVersion());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), itemEntity.getCreatedAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), itemEntity.getUpdatedAt());
    }

    @Test
    void mapsEmptyItemsCollection() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);

        CartEntity entity = new CartEntity();
        entity.setId(4L);
        entity.setCustomer(customer);
        entity.setItems(List.of());
        entity.setVersion(1L);
        entity.setCreatedAt(Instant.parse("2026-03-27T00:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-03-27T00:00:00Z"));

        Cart cart = CartEntityMapper.toDomain(entity);

        assertTrue(cart.items().isEmpty());
    }
}
