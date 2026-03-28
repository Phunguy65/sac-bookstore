package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.auth.domain.model.Address;
import io.github.phunguy65.bookstore.auth.domain.repository.AddressRepository;
import io.github.phunguy65.bookstore.book.domain.model.Book;
import io.github.phunguy65.bookstore.book.domain.repository.BookRepository;
import io.github.phunguy65.bookstore.purchase.domain.model.Cart;
import io.github.phunguy65.bookstore.purchase.domain.model.CartItem;
import io.github.phunguy65.bookstore.purchase.domain.model.Order;
import io.github.phunguy65.bookstore.purchase.domain.model.OrderItem;
import io.github.phunguy65.bookstore.purchase.domain.repository.CartRepository;
import io.github.phunguy65.bookstore.purchase.domain.repository.OrderRepository;
import io.github.phunguy65.bookstore.purchase.domain.valueobject.OrderStatus;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressDetails;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressLine;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.City;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.District;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PostalCode;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Province;
import io.github.phunguy65.bookstore.shared.domain.valueobject.RecipientName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Ward;
import io.github.phunguy65.bookstore.shared.domain.validation.FieldValidationException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CheckoutApplicationService {
    private CartRepository cartRepository;
    private BookRepository bookRepository;
    private AddressRepository addressRepository;
    private OrderRepository orderRepository;

    public CheckoutApplicationService() {
    }

    @Inject
    public CheckoutApplicationService(
            CartRepository cartRepository,
            BookRepository bookRepository,
            AddressRepository addressRepository,
            OrderRepository orderRepository
    ) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
    }

    public CheckoutView getCheckout(CustomerId customerId) {
        CartView cart = buildCartView(customerId);
        Address defaultAddress = addressRepository.findDefaultByCustomerId(customerId).orElse(null);
        return new CheckoutView(cart, defaultAddress == null ? null : defaultAddress.details(), defaultAddress == null);
    }

    public CheckoutResult placeOrder(CustomerId customerId, CheckoutAddressInput addressInput) {
        try {
            Cart cart = cartRepository.findByCustomerId(customerId).orElse(null);
            if (cart == null || cart.items().isEmpty()) {
                return CheckoutResult.failure("Gio hang dang trong.");
            }

            AddressResolution addressResolution = resolveShippingAddress(customerId, addressInput);
            PreparedOrder preparedOrder = prepareOrder(cart, addressResolution.shippingAddress());

            for (BookStockChange change : preparedOrder.bookStockChanges()) {
                bookRepository.save(change.updatedBook());
            }

            Order savedOrder = orderRepository.save(preparedOrder.order());
            if (addressResolution.newDefaultAddress() != null) {
                addressRepository.save(addressResolution.newDefaultAddress());
            }
            cartRepository.save(new Cart(cart.id(), cart.customerId(), List.of(), cart.version(), cart.createdAt(), Instant.now()));
            return CheckoutResult.success(savedOrder.id());
        } catch (IllegalArgumentException ex) {
            return checkoutFailure(ex);
        }
    }

    private AddressResolution resolveShippingAddress(CustomerId customerId, CheckoutAddressInput addressInput) {
        Address defaultAddress = addressRepository.findDefaultByCustomerId(customerId).orElse(null);
        if (defaultAddress != null) {
            return new AddressResolution(defaultAddress.details(), null);
        }
        if (addressInput == null) {
            throw new IllegalArgumentException("Dia chi giao hang la bat buoc.");
        }

        AddressDetails details = new AddressDetails(
                new RecipientName(addressInput.recipientName()),
                new PhoneNumber(addressInput.phoneNumber()),
                new AddressLine(addressInput.line1()),
                addressInput.line2(),
                new Ward(addressInput.ward()),
                new District(addressInput.district()),
                new City(addressInput.city()),
                new Province(addressInput.province()),
                new PostalCode(addressInput.postalCode())
        );
        Instant now = Instant.now();
        return new AddressResolution(details, new Address(null, customerId, details, true, 0L, now, now));
    }

    private PreparedOrder prepareOrder(Cart cart, AddressDetails shippingAddress) {
        Instant now = Instant.now();
        List<OrderItem> orderItems = new ArrayList<>();
        List<BookStockChange> stockChanges = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        java.util.Map<BookId, Book> booksById = new java.util.HashMap<>();
        for (Book book : bookRepository.findByIds(cart.items().stream().map(CartItem::bookId).collect(java.util.stream.Collectors.toSet()))) {
            booksById.put(book.id(), book);
        }

        for (CartItem cartItem : cart.items()) {
            Book book = booksById.get(cartItem.bookId());
            if (book == null) {
                throw new IllegalArgumentException("Khong tim thay sach trong gio hang.");
            }
            if (!book.active()) {
                throw new IllegalArgumentException("Co sach trong gio hang hien khong con mo ban.");
            }
            if (book.stockQuantity().value() < cartItem.quantity().value()) {
                throw new IllegalArgumentException("Khong du ton kho de hoan tat don hang.");
            }

            Money lineTotal = new Money(cartItem.unitPriceSnapshot().amount().multiply(BigDecimal.valueOf(cartItem.quantity().value())));
            total = total.add(lineTotal.amount());
            orderItems.add(new OrderItem(
                    null,
                    book.id(),
                    book.title(),
                    book.isbn(),
                    cartItem.unitPriceSnapshot(),
                    cartItem.quantity(),
                    lineTotal,
                    now
            ));
            stockChanges.add(new BookStockChange(new Book(
                    book.id(),
                    book.isbn(),
                    book.title(),
                    book.author(),
                    book.description(),
                    book.imageUrl(),
                    book.price(),
                    new io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity(book.stockQuantity().value() - cartItem.quantity().value()),
                    book.active(),
                    book.version(),
                    book.createdAt(),
                    Instant.now()
            )));
        }

        return new PreparedOrder(new Order(
                null,
                cart.customerId(),
                OrderStatus.PLACED,
                new Money(total),
                shippingAddress,
                orderItems,
                now,
                null,
                0L,
                now,
                now
        ), stockChanges);
    }

    private CartView buildCartView(CustomerId customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId).orElse(new Cart(null, customerId, List.of(), 0L, Instant.now(), Instant.now()));
        return CartViewAssembler.toCartView(cart, bookRepository);
    }

    private CheckoutResult checkoutFailure(IllegalArgumentException ex) {
        String message = ex.getMessage();
        java.util.Map<String, String> fieldErrors = new java.util.HashMap<>();
        if (ex instanceof FieldValidationException fieldValidationException) {
            fieldErrors.put(fieldValidationException.getFieldName(), message);
        }
        return CheckoutResult.failure(message, fieldErrors);
    }

    private record PreparedOrder(Order order, List<BookStockChange> bookStockChanges) {
    }

    private record BookStockChange(Book updatedBook) {
    }

    private record AddressResolution(AddressDetails shippingAddress, Address newDefaultAddress) {
    }
}
