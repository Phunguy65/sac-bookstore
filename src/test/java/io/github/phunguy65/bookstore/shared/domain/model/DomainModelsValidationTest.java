package io.github.phunguy65.bookstore.shared.domain.model;

import io.github.phunguy65.bookstore.auth.domain.model.Address;
import io.github.phunguy65.bookstore.auth.domain.model.Customer;
import io.github.phunguy65.bookstore.auth.domain.valueobject.CustomerStatus;
import io.github.phunguy65.bookstore.book.domain.model.Book;
import io.github.phunguy65.bookstore.book.domain.valueobject.AuthorName;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle;
import io.github.phunguy65.bookstore.book.domain.valueobject.Isbn;
import io.github.phunguy65.bookstore.purchase.domain.model.Cart;
import io.github.phunguy65.bookstore.purchase.domain.model.CartItem;
import io.github.phunguy65.bookstore.purchase.domain.model.Order;
import io.github.phunguy65.bookstore.purchase.domain.model.OrderItem;
import io.github.phunguy65.bookstore.purchase.domain.valueobject.OrderStatus;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressDetails;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressLine;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CartId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CartItemId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.City;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.District;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Email;
import io.github.phunguy65.bookstore.shared.domain.valueobject.FullName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderItemId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PasswordHash;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PostalCode;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Province;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;
import io.github.phunguy65.bookstore.shared.domain.valueobject.RecipientName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Ward;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DomainModelsValidationTest {
    @Test
    void customerRejectsNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Customer(
                new CustomerId(1L),
                null,
                new PasswordHash("hashed-password"),
                new FullName("Customer Name"),
                new PhoneNumber("0901234567"),
                CustomerStatus.ACTIVE,
                0L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void addressRejectsNullDetails() {
        assertThrows(IllegalArgumentException.class, () -> new Address(
                new AddressId(1L),
                new CustomerId(1L),
                null,
                true,
                0L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void bookRejectsNegativeVersion() {
        assertThrows(IllegalArgumentException.class, () -> new Book(
                new BookId(1L),
                new Isbn("9786041234567"),
                new BookTitle("Clean Architecture"),
                new AuthorName("Robert C. Martin"),
                null,
                null,
                new Money(new BigDecimal("100.00")),
                new Quantity(10),
                true,
                -1L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void cartRejectsNullItems() {
        assertThrows(IllegalArgumentException.class, () -> new Cart(
                new CartId(1L),
                new CustomerId(1L),
                null,
                0L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void cartRejectsNullCustomerId() {
        assertThrows(IllegalArgumentException.class, () -> new Cart(
                new CartId(1L),
                null,
                List.of(),
                0L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void cartRejectsNegativeVersion() {
        assertThrows(IllegalArgumentException.class, () -> new Cart(
                new CartId(1L),
                new CustomerId(1L),
                List.of(),
                -1L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void cartRejectsNullCreatedAt() {
        assertThrows(IllegalArgumentException.class, () -> new Cart(
                new CartId(1L),
                new CustomerId(1L),
                List.of(),
                0L,
                null,
                Instant.now()
        ));
    }

    @Test
    void cartRejectsNullUpdatedAt() {
        assertThrows(IllegalArgumentException.class, () -> new Cart(
                new CartId(1L),
                new CustomerId(1L),
                List.of(),
                0L,
                Instant.now(),
                null
        ));
    }

    @Test
    void orderRejectsNullShippingAddress() {
        assertThrows(IllegalArgumentException.class, () -> new Order(
                new OrderId(1L),
                new CustomerId(1L),
                OrderStatus.PLACED,
                new Money(new BigDecimal("100.00")),
                null,
                List.of(validOrderItem()),
                Instant.now(),
                null,
                0L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void orderRejectsNullCustomerId() {
        assertThrows(IllegalArgumentException.class, () -> new Order(
                new OrderId(1L),
                null,
                OrderStatus.PLACED,
                new Money(new BigDecimal("100.00")),
                validAddressDetails(),
                List.of(validOrderItem()),
                Instant.now(),
                null,
                0L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void orderRejectsNullStatus() {
        assertThrows(IllegalArgumentException.class, () -> new Order(
                new OrderId(1L),
                new CustomerId(1L),
                null,
                new Money(new BigDecimal("100.00")),
                validAddressDetails(),
                List.of(validOrderItem()),
                Instant.now(),
                null,
                0L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void orderRejectsNullTotalAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Order(
                new OrderId(1L),
                new CustomerId(1L),
                OrderStatus.PLACED,
                null,
                validAddressDetails(),
                List.of(validOrderItem()),
                Instant.now(),
                null,
                0L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void orderRejectsNullItems() {
        assertThrows(IllegalArgumentException.class, () -> new Order(
                new OrderId(1L),
                new CustomerId(1L),
                OrderStatus.PLACED,
                new Money(new BigDecimal("100.00")),
                validAddressDetails(),
                null,
                Instant.now(),
                null,
                0L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void orderRejectsNegativeVersion() {
        assertThrows(IllegalArgumentException.class, () -> new Order(
                new OrderId(1L),
                new CustomerId(1L),
                OrderStatus.PLACED,
                new Money(new BigDecimal("100.00")),
                validAddressDetails(),
                List.of(validOrderItem()),
                Instant.now(),
                null,
                -1L,
                Instant.now(),
                Instant.now()
        ));
    }

    @Test
    void orderRejectsNullCreatedAt() {
        assertThrows(IllegalArgumentException.class, () -> new Order(
                new OrderId(1L),
                new CustomerId(1L),
                OrderStatus.PLACED,
                new Money(new BigDecimal("100.00")),
                validAddressDetails(),
                List.of(validOrderItem()),
                Instant.now(),
                null,
                0L,
                null,
                Instant.now()
        ));
    }

    @Test
    void orderRejectsNullUpdatedAt() {
        assertThrows(IllegalArgumentException.class, () -> new Order(
                new OrderId(1L),
                new CustomerId(1L),
                OrderStatus.PLACED,
                new Money(new BigDecimal("100.00")),
                validAddressDetails(),
                List.of(validOrderItem()),
                Instant.now(),
                null,
                0L,
                Instant.now(),
                null
        ));
    }

    @Test
    void orderItemRejectsNullLineTotal() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(
                new OrderItemId(1L),
                new BookId(1L),
                new BookTitle("Clean Architecture"),
                new Isbn("9786041234567"),
                new Money(new BigDecimal("100.00")),
                new Quantity(1),
                null,
                Instant.now()
        ));
    }

    @Test
    void cartItemRejectsNullPriceSnapshot() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem(
                new CartItemId(1L),
                new BookId(1L),
                new Quantity(1),
                null,
                0L,
                Instant.now(),
                Instant.now()
        ));
    }

    private static OrderItem validOrderItem() {
        return new OrderItem(
                new OrderItemId(1L),
                new BookId(1L),
                new BookTitle("Clean Architecture"),
                new Isbn("9786041234567"),
                new Money(new BigDecimal("100.00")),
                new Quantity(1),
                new Money(new BigDecimal("100.00")),
                Instant.now()
        );
    }

    private static AddressDetails validAddressDetails() {
        return new AddressDetails(
                new RecipientName("Nguyen Van A"),
                new PhoneNumber("0901234567"),
                new AddressLine("123 Main St"),
                null,
                new Ward("Ward 1"),
                new District("District 1"),
                new City("Ho Chi Minh City"),
                new Province("Ho Chi Minh"),
                new PostalCode("700000")
        );
    }
}
