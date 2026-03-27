package io.github.phunguy65.bookstore.purchase.infrastructure.persistence.mapper;

import io.github.phunguy65.bookstore.auth.infrastructure.persistence.entity.CustomerEntity;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle;
import io.github.phunguy65.bookstore.book.domain.valueobject.Isbn;
import io.github.phunguy65.bookstore.book.infrastructure.persistence.entity.BookEntity;
import io.github.phunguy65.bookstore.purchase.domain.model.Order;
import io.github.phunguy65.bookstore.purchase.domain.model.OrderItem;
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

public final class OrderEntityMapper {
    private OrderEntityMapper() {
    }

    public static Order toDomain(OrderEntity entity) {
        return new Order(
                entity.getId() == null ? null : new OrderId(entity.getId()),
                new CustomerId(entity.getCustomer().getId()),
                entity.getStatus(),
                new Money(entity.getTotalAmount()),
                new AddressDetails(
                        new RecipientName(entity.getShippingFullName()),
                        new PhoneNumber(entity.getShippingPhone()),
                        new AddressLine(entity.getShippingLine1()),
                        entity.getShippingLine2(),
                        new Ward(entity.getShippingWard()),
                        new District(entity.getShippingDistrict()),
                        new City(entity.getShippingCity()),
                        new Province(entity.getShippingProvince()),
                        new PostalCode(entity.getShippingPostalCode())
                ),
                entity.getItems().stream().map(OrderEntityMapper::toDomainItem).toList(),
                entity.getPlacedAt(),
                entity.getCancelledAt(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static OrderEntity toEntity(Order domain) {
        OrderEntity entity = new OrderEntity();
        entity.setId(domain.id() == null ? null : domain.id().value());
        CustomerEntity customer = new CustomerEntity();
        customer.setId(domain.customerId().value());
        entity.setCustomer(customer);
        entity.setStatus(domain.status());
        entity.setTotalAmount(domain.totalAmount().amount());
        entity.setShippingFullName(domain.shippingAddress().recipientName().value());
        entity.setShippingPhone(domain.shippingAddress().phoneNumber().value());
        entity.setShippingLine1(domain.shippingAddress().line1().value());
        entity.setShippingLine2(domain.shippingAddress().line2());
        entity.setShippingWard(domain.shippingAddress().ward().value());
        entity.setShippingDistrict(domain.shippingAddress().district().value());
        entity.setShippingCity(domain.shippingAddress().city().value());
        entity.setShippingProvince(domain.shippingAddress().province().value());
        entity.setShippingPostalCode(domain.shippingAddress().postalCode().value());
        entity.setPlacedAt(domain.placedAt());
        entity.setCancelledAt(domain.cancelledAt());
        entity.setVersion(domain.version());
        entity.setCreatedAt(domain.createdAt());
        entity.setUpdatedAt(domain.updatedAt());
        entity.setItems(domain.items().stream().map(item -> toEntityItem(item, entity)).toList());
        return entity;
    }

    private static OrderItem toDomainItem(OrderItemEntity entity) {
        return new OrderItem(
                entity.getId() == null ? null : new OrderItemId(entity.getId()),
                new BookId(entity.getBook().getId()),
                new BookTitle(entity.getBookTitleSnapshot()),
                new Isbn(entity.getBookIsbnSnapshot()),
                new Money(entity.getUnitPriceSnapshot()),
                new Quantity(entity.getQuantity()),
                new Money(entity.getLineTotal()),
                entity.getCreatedAt()
        );
    }

    private static OrderItemEntity toEntityItem(OrderItem domain, OrderEntity order) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(domain.id() == null ? null : domain.id().value());
        entity.setOrder(order);
        BookEntity book = new BookEntity();
        book.setId(domain.bookId().value());
        entity.setBook(book);
        entity.setBookTitleSnapshot(domain.bookTitleSnapshot().value());
        entity.setBookIsbnSnapshot(domain.bookIsbnSnapshot().value());
        entity.setUnitPriceSnapshot(domain.unitPriceSnapshot().amount());
        entity.setQuantity(domain.quantity().value());
        entity.setLineTotal(domain.lineTotal().amount());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
