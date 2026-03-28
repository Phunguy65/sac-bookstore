package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.auth.domain.model.Address;
import io.github.phunguy65.bookstore.auth.domain.repository.AddressRepository;
import io.github.phunguy65.bookstore.book.domain.model.Book;
import io.github.phunguy65.bookstore.book.domain.repository.BookRepository;
import io.github.phunguy65.bookstore.book.domain.valueobject.AuthorName;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookDescription;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle;
import io.github.phunguy65.bookstore.book.domain.valueobject.ImageUrl;
import io.github.phunguy65.bookstore.book.domain.valueobject.Isbn;
import io.github.phunguy65.bookstore.purchase.domain.model.Cart;
import io.github.phunguy65.bookstore.purchase.domain.model.CartItem;
import io.github.phunguy65.bookstore.purchase.domain.model.Order;
import io.github.phunguy65.bookstore.purchase.domain.model.OrderItem;
import io.github.phunguy65.bookstore.purchase.domain.repository.CartRepository;
import io.github.phunguy65.bookstore.purchase.domain.repository.OrderRepository;
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
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderItemId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PostalCode;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Province;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;
import io.github.phunguy65.bookstore.shared.domain.valueobject.RecipientName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Ward;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class PurchaseServiceTestSupport {
    static final Instant NOW = Instant.parse("2026-03-27T00:00:00Z");

    private PurchaseServiceTestSupport() {
    }

    static Book book(long id, String title, int stock, boolean active, String price) {
        return new Book(
                new BookId(id),
                new Isbn("9786041234567"),
                new BookTitle(title),
                new AuthorName("Tac gia " + id),
                new BookDescription("Mo ta sach " + id),
                new ImageUrl("https://example.com/book-" + id + ".jpg"),
                new Money(new BigDecimal(price)),
                new Quantity(stock),
                active,
                0L,
                NOW,
                NOW
        );
    }

    static Cart cart(CustomerId customerId, CartItem... items) {
        return new Cart(new CartId(customerId.value()), customerId, List.of(items), 0L, NOW, NOW);
    }

    static CartItem cartItem(long itemId, long bookId, int quantity, String unitPrice) {
        return new CartItem(new CartItemId(itemId), new BookId(bookId), new Quantity(quantity), new Money(new BigDecimal(unitPrice)), 0L, NOW, NOW);
    }

    static Address address(long addressId, CustomerId customerId, boolean isDefault) {
        return new Address(new AddressId(addressId), customerId, addressDetails("Reader " + customerId.value()), isDefault, 0L, NOW, NOW);
    }

    static AddressDetails addressDetails(String recipientName) {
        return new AddressDetails(
                new RecipientName(recipientName),
                new PhoneNumber("0901234567"),
                new AddressLine("123 Main St"),
                "Apt 9",
                new Ward("Ward 1"),
                new District("District 1"),
                new City("Ho Chi Minh City"),
                new Province("Ho Chi Minh"),
                new PostalCode("700000")
        );
    }

    static Order order(long orderId, CustomerId customerId, OrderItem... items) {
        return new Order(new OrderId(orderId), customerId, OrderStatus.PLACED, new Money(new BigDecimal("50.00")), addressDetails("Order Owner"), List.of(items), NOW, null, 0L, NOW, NOW);
    }

    static OrderItem orderItem(long orderItemId, long bookId, int quantity, String unitPrice, String lineTotal) {
        return new OrderItem(new OrderItemId(orderItemId), new BookId(bookId), new BookTitle("Book " + bookId), new Isbn("9786041234567"), new Money(new BigDecimal(unitPrice)), new Quantity(quantity), new Money(new BigDecimal(lineTotal)), NOW);
    }

    static final class InMemoryBookRepository implements BookRepository {
        private final Map<Long, Book> books = new HashMap<>();

        void put(Book book) {
            books.put(book.id().value(), book);
        }

        @Override
        public List<Book> findActiveBooks() {
            return books.values().stream().filter(Book::active).sorted(Comparator.comparing(book -> book.title().value())).toList();
        }

        @Override
        public List<Book> findByIds(Set<BookId> bookIds) {
            return bookIds.stream().map(BookId::value).map(books::get).filter(java.util.Objects::nonNull).toList();
        }

        @Override
        public Optional<Book> findById(BookId bookId) {
            return Optional.ofNullable(books.get(bookId.value()));
        }

        @Override
        public Book save(Book book) {
            books.put(book.id().value(), book);
            return book;
        }
    }

    static final class InMemoryCartRepository implements CartRepository {
        private final Map<Long, Cart> cartsByCustomerId = new HashMap<>();

        void put(Cart cart) {
            cartsByCustomerId.put(cart.customerId().value(), cart);
        }

        @Override
        public Optional<Cart> findById(CartId cartId) {
            return cartsByCustomerId.values().stream().filter(cart -> cart.id() != null && cart.id().equals(cartId)).findFirst();
        }

        @Override
        public Optional<Cart> findByCustomerId(CustomerId customerId) {
            return Optional.ofNullable(cartsByCustomerId.get(customerId.value()));
        }

        @Override
        public Cart save(Cart cart) {
            Cart persisted = cart.id() == null
                    ? new Cart(new CartId(cart.customerId().value()), cart.customerId(), cart.items(), cart.version(), cart.createdAt(), cart.updatedAt())
                    : cart;
            cartsByCustomerId.put(persisted.customerId().value(), persisted);
            return persisted;
        }
    }

    static final class InMemoryAddressRepository implements AddressRepository {
        private final Map<Long, List<Address>> addressesByCustomerId = new HashMap<>();
        private long nextId = 10L;

        void put(Address address) {
            addressesByCustomerId.computeIfAbsent(address.customerId().value(), ignored -> new ArrayList<>()).add(address);
        }

        @Override
        public List<Address> findByCustomerId(CustomerId customerId) {
            return List.copyOf(addressesByCustomerId.getOrDefault(customerId.value(), List.of()));
        }

        @Override
        public Optional<Address> findDefaultByCustomerId(CustomerId customerId) {
            return addressesByCustomerId.getOrDefault(customerId.value(), List.of()).stream().filter(Address::isDefault).findFirst();
        }

        @Override
        public Optional<Address> findById(AddressId addressId) {
            return addressesByCustomerId.values().stream().flatMap(List::stream).filter(address -> address.id() != null && address.id().equals(addressId)).findFirst();
        }

        @Override
        public Address save(Address address) {
            List<Address> existing = new ArrayList<>(addressesByCustomerId.getOrDefault(address.customerId().value(), List.of()));
            if (address.isDefault()) {
                existing = existing.stream()
                        .map(item -> new Address(item.id(), item.customerId(), item.details(), false, item.version(), item.createdAt(), item.updatedAt()))
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            }
            Address persisted = address.id() == null
                    ? new Address(new AddressId(nextId++), address.customerId(), address.details(), address.isDefault(), address.version(), address.createdAt(), address.updatedAt())
                    : address;
            existing.removeIf(item -> item.id().equals(persisted.id()));
            existing.add(persisted);
            addressesByCustomerId.put(persisted.customerId().value(), existing);
            return persisted;
        }
    }

    static final class InMemoryOrderRepository implements OrderRepository {
        private final Map<Long, Order> orders = new HashMap<>();
        private long nextId = 100L;

        void put(Order order) {
            orders.put(order.id().value(), order);
        }

        @Override
        public Optional<Order> findById(OrderId orderId) {
            return Optional.ofNullable(orders.get(orderId.value()));
        }

        @Override
        public Optional<Order> findByCustomerIdAndId(CustomerId customerId, OrderId orderId) {
            return findById(orderId).filter(order -> order.customerId().equals(customerId));
        }

        @Override
        public List<Order> findByCustomerId(CustomerId customerId) {
            return orders.values().stream()
                    .filter(order -> order.customerId().equals(customerId))
                    .sorted(Comparator.comparing(Order::createdAt).reversed())
                    .toList();
        }

        @Override
        public Order save(Order order) {
            Order persisted = order.id() == null
                    ? new Order(new OrderId(nextId++), order.customerId(), order.status(), order.totalAmount(), order.shippingAddress(), order.items(), order.placedAt(), order.cancelledAt(), order.version(), order.createdAt(), order.updatedAt())
                    : order;
            orders.put(persisted.id().value(), persisted);
            return persisted;
        }
    }
}
