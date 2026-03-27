package io.github.phunguy65.bookstore.purchase.infrastructure.persistence.mapper;

import io.github.phunguy65.bookstore.auth.infrastructure.persistence.entity.CustomerEntity;
import io.github.phunguy65.bookstore.book.infrastructure.persistence.entity.BookEntity;
import io.github.phunguy65.bookstore.purchase.domain.model.Order;
import io.github.phunguy65.bookstore.purchase.domain.model.OrderItem;
import io.github.phunguy65.bookstore.purchase.domain.valueobject.OrderStatus;
import io.github.phunguy65.bookstore.purchase.infrastructure.persistence.entity.OrderEntity;
import io.github.phunguy65.bookstore.purchase.infrastructure.persistence.entity.OrderItemEntity;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressDetails;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressLine;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.City;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.District;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderItemId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PostalCode;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Province;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;
import io.github.phunguy65.bookstore.shared.domain.valueobject.RecipientName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Ward;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle;
import io.github.phunguy65.bookstore.book.domain.valueobject.Isbn;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderEntityMapperTest {
    @Test
    void mapsEntityToDomain() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        BookEntity book = new BookEntity();
        book.setId(2L);

        OrderItemEntity item = new OrderItemEntity();
        item.setId(3L);
        item.setBook(book);
        item.setBookTitleSnapshot("Clean Architecture");
        item.setBookIsbnSnapshot("9786041234567");
        item.setUnitPriceSnapshot(new BigDecimal("100.00"));
        item.setQuantity(2);
        item.setLineTotal(new BigDecimal("200.00"));
        item.setCreatedAt(Instant.parse("2026-03-27T00:00:00Z"));

        OrderEntity entity = new OrderEntity();
        entity.setId(4L);
        entity.setCustomer(customer);
        entity.setStatus(OrderStatus.PLACED);
        entity.setTotalAmount(new BigDecimal("200.00"));
        entity.setShippingFullName("Nguyen Van A");
        entity.setShippingPhone("0901234567");
        entity.setShippingLine1("123 Main St");
        entity.setShippingLine2("Apt 101");
        entity.setShippingWard("Ward 1");
        entity.setShippingDistrict("District 1");
        entity.setShippingCity("Ho Chi Minh City");
        entity.setShippingProvince("Ho Chi Minh");
        entity.setShippingPostalCode("700000");
        entity.setPlacedAt(Instant.parse("2026-03-27T00:00:00Z"));
        entity.setVersion(1L);
        entity.setCreatedAt(Instant.parse("2026-03-27T00:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-03-27T00:00:00Z"));
        entity.setItems(List.of(item));
        item.setOrder(entity);

        Order order = OrderEntityMapper.toDomain(entity);

        assertEquals(new OrderId(4L), order.id());
        assertEquals(new CustomerId(1L), order.customerId());
        assertEquals(OrderStatus.PLACED, order.status());
        assertEquals(new Money(new BigDecimal("200.00")), order.totalAmount());
        assertEquals(1, order.items().size());
        assertEquals("Nguyen Van A", order.shippingAddress().recipientName().value());
        assertEquals("0901234567", order.shippingAddress().phoneNumber().value());
        assertEquals("123 Main St", order.shippingAddress().line1().value());
        assertEquals("Apt 101", order.shippingAddress().line2());
        assertEquals("Ward 1", order.shippingAddress().ward().value());
        assertEquals("District 1", order.shippingAddress().district().value());
        assertEquals("Ho Chi Minh City", order.shippingAddress().city().value());
        assertEquals("Ho Chi Minh", order.shippingAddress().province().value());
        assertEquals("700000", order.shippingAddress().postalCode().value());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), order.placedAt());
        assertNull(order.cancelledAt());
        assertEquals(1L, order.version());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), order.createdAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), order.updatedAt());
        OrderItem itemDomain = order.items().getFirst();
        assertEquals(new OrderItemId(3L), itemDomain.id());
        assertEquals(new BookId(2L), itemDomain.bookId());
        assertEquals(new BookTitle("Clean Architecture"), itemDomain.bookTitleSnapshot());
        assertEquals(new Isbn("9786041234567"), itemDomain.bookIsbnSnapshot());
        assertEquals(new Money(new BigDecimal("100.00")), itemDomain.unitPriceSnapshot());
        assertEquals(new Quantity(2), itemDomain.quantity());
        assertEquals(new Money(new BigDecimal("200.00")), itemDomain.lineTotal());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), itemDomain.createdAt());
    }

    @Test
    void mapsDomainToEntity() {
        Order order = new Order(
                new OrderId(4L),
                new CustomerId(1L),
                OrderStatus.PLACED,
                new Money(new BigDecimal("200.00")),
                new AddressDetails(
                        new RecipientName("Nguyen Van A"),
                        new PhoneNumber("0901234567"),
                        new AddressLine("123 Main St"),
                        "Apt 101",
                        new Ward("Ward 1"),
                        new District("District 1"),
                        new City("Ho Chi Minh City"),
                        new Province("Ho Chi Minh"),
                        new PostalCode("700000")
                ),
                List.of(new OrderItem(
                        new OrderItemId(3L),
                        new BookId(2L),
                        new BookTitle("Clean Architecture"),
                        new Isbn("9786041234567"),
                        new Money(new BigDecimal("100.00")),
                        new Quantity(2),
                        new Money(new BigDecimal("200.00")),
                        Instant.parse("2026-03-27T00:00:00Z")
                )),
                Instant.parse("2026-03-27T00:00:00Z"),
                null,
                1L,
                Instant.parse("2026-03-27T00:00:00Z"),
                Instant.parse("2026-03-27T00:00:00Z")
        );

        OrderEntity entity = OrderEntityMapper.toEntity(order);

        assertEquals(4L, entity.getId());
        assertEquals(1L, entity.getCustomer().getId());
        assertEquals(OrderStatus.PLACED, entity.getStatus());
        assertEquals(new BigDecimal("200.00"), entity.getTotalAmount());
        assertEquals(1, entity.getItems().size());
        assertEquals("Nguyen Van A", entity.getShippingFullName());
        assertEquals("0901234567", entity.getShippingPhone());
        assertEquals("123 Main St", entity.getShippingLine1());
        assertEquals("Apt 101", entity.getShippingLine2());
        assertEquals("Ward 1", entity.getShippingWard());
        assertEquals("District 1", entity.getShippingDistrict());
        assertEquals("Ho Chi Minh City", entity.getShippingCity());
        assertEquals("Ho Chi Minh", entity.getShippingProvince());
        assertEquals("700000", entity.getShippingPostalCode());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), entity.getPlacedAt());
        assertNull(entity.getCancelledAt());
        assertEquals(1L, entity.getVersion());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), entity.getCreatedAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), entity.getUpdatedAt());
        OrderItemEntity itemEntity = entity.getItems().getFirst();
        assertEquals(3L, itemEntity.getId());
        assertEquals(2L, itemEntity.getBook().getId());
        assertEquals("Clean Architecture", itemEntity.getBookTitleSnapshot());
        assertEquals("9786041234567", itemEntity.getBookIsbnSnapshot());
        assertEquals(new BigDecimal("100.00"), itemEntity.getUnitPriceSnapshot());
        assertEquals(2, itemEntity.getQuantity());
        assertEquals(new BigDecimal("200.00"), itemEntity.getLineTotal());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), itemEntity.getCreatedAt());
    }

    @Test
    void mapsEmptyOrderItemsCollection() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);

        OrderEntity entity = new OrderEntity();
        entity.setId(4L);
        entity.setCustomer(customer);
        entity.setStatus(OrderStatus.PENDING);
        entity.setTotalAmount(new BigDecimal("0.00"));
        entity.setShippingFullName("Nguyen Van A");
        entity.setShippingPhone("0901234567");
        entity.setShippingLine1("123 Main St");
        entity.setShippingWard("Ward 1");
        entity.setShippingDistrict("District 1");
        entity.setShippingCity("Ho Chi Minh City");
        entity.setShippingProvince("Ho Chi Minh");
        entity.setShippingPostalCode("700000");
        entity.setItems(List.of());
        entity.setVersion(1L);
        entity.setCreatedAt(Instant.parse("2026-03-27T00:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-03-27T00:00:00Z"));

        Order order = OrderEntityMapper.toDomain(entity);

        assertTrue(order.items().isEmpty());
    }
}
