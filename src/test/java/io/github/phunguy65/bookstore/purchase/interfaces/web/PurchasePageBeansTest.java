package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.purchase.application.service.CartActionResult;
import io.github.phunguy65.bookstore.purchase.application.service.AccountCatalogApplicationService;
import io.github.phunguy65.bookstore.purchase.application.service.CatalogBookView;
import io.github.phunguy65.bookstore.purchase.application.service.CartApplicationService;
import io.github.phunguy65.bookstore.purchase.application.service.CartView;
import io.github.phunguy65.bookstore.purchase.application.service.CheckoutAddressInput;
import io.github.phunguy65.bookstore.purchase.application.service.CheckoutApplicationService;
import io.github.phunguy65.bookstore.purchase.application.service.CheckoutResult;
import io.github.phunguy65.bookstore.purchase.application.service.CheckoutView;
import io.github.phunguy65.bookstore.purchase.application.service.OrderLookupResult;
import io.github.phunguy65.bookstore.purchase.application.service.OrderQueryApplicationService;
import io.github.phunguy65.bookstore.purchase.application.service.OrderSummaryView;
import io.github.phunguy65.bookstore.purchase.application.service.OrderAddressView;
import io.github.phunguy65.bookstore.purchase.application.service.OrderDetailView;
import io.github.phunguy65.bookstore.purchase.application.service.OrderItemDetailView;
import io.github.phunguy65.bookstore.purchase.domain.valueobject.OrderStatus;
import io.github.phunguy65.bookstore.book.domain.valueobject.AuthorName;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookDescription;
import io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle;
import io.github.phunguy65.bookstore.book.domain.valueobject.Isbn;
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurchasePageBeansTest {
    @Test
    void purchaseBeansAreStatelessEjbs() {
        assertStatelessWithRemoteInterface(AccountHomePageBean.class, AccountHomePage.class);
        assertStatelessWithRemoteInterface(CartPageBean.class, CartPage.class);
        assertStatelessWithRemoteInterface(CheckoutPageBean.class, CheckoutPage.class);
        assertStatelessWithRemoteInterface(OrderHistoryPageBean.class, OrderHistoryPage.class);
        assertStatelessWithRemoteInterface(OrderDetailPageBean.class, OrderDetailPage.class);
    }

    @Test
    void constructorInjectedPurchaseBeansDeclareRequiredDependencies() {
        assertHasParameterizedConstructor(AccountHomePageBean.class, AccountCatalogApplicationService.class);
        assertHasParameterizedConstructor(CartPageBean.class, CartApplicationService.class);
        assertHasParameterizedConstructor(CheckoutPageBean.class, CheckoutApplicationService.class);
        assertHasParameterizedConstructor(OrderHistoryPageBean.class, OrderQueryApplicationService.class);
        assertHasParameterizedConstructor(OrderDetailPageBean.class, OrderQueryApplicationService.class);
    }

    @Test
    void accountHomeShowsBooksOnGet() {
        StubAccountCatalogApplicationService service = new StubAccountCatalogApplicationService();
        service.books = List.of(book(11L, "Clean Code", 3), book(12L, "DDD", 0));
        AccountHomePageBean bean = new AccountHomePageBean(service);

        AccountHomePageResult result = bean.handle(new AccountHomePageRequest("GET", null, null));

        assertEquals(PageAction.RENDER, result.action());
        assertEquals(2, result.model().getBooks().size());
        assertEquals(0, result.model().getBooks().get(1).availableStock().value());
        assertEquals("1", result.model().getQuantityValue(11L));
        assertNull(result.model().getErrorMessage());
    }

    @Test
    void accountHomeMapsQuantityValidationErrorToSubmittedBook() {
        StubAccountCatalogApplicationService service = new StubAccountCatalogApplicationService();
        service.books = List.of(book(11L, "Clean Code", 3));
        service.result = CartActionResult.failure("So luong khong hop le.", java.util.Map.of("quantity", "So luong khong hop le."));
        AccountHomePageBean bean = new AccountHomePageBean(service);

        AccountHomePageResult result = bean.handle(new AccountHomePageRequest("POST", "11", "0"));

        assertEquals(PageAction.RENDER, result.action());
        assertTrue(result.model().hasLineQuantityError(11L));
        assertEquals("So luong khong hop le.", result.model().getLineQuantityError());
        assertEquals("0", result.model().getQuantityValue(11L));
        assertNull(result.model().getInfoMessage());
    }

    @Test
    void accountHomeRejectsInvalidBookId() {
        StubAccountCatalogApplicationService service = new StubAccountCatalogApplicationService();
        service.books = List.of(book(11L, "Clean Code", 3));
        AccountHomePageBean bean = new AccountHomePageBean(service);

        AccountHomePageResult result = bean.handle(new AccountHomePageRequest("POST", "abc", "2"));

        assertEquals(PageAction.RENDER, result.action());
        assertEquals("Ma sach khong hop le.", result.model().getErrorMessage());
        assertNull(result.model().getInfoMessage());
    }

    @Test
    void accountHomeRejectsMissingQuantity() {
        StubAccountCatalogApplicationService service = new StubAccountCatalogApplicationService();
        service.books = List.of(book(11L, "Clean Code", 3));
        AccountHomePageBean bean = new AccountHomePageBean(service);

        AccountHomePageResult result = bean.handle(new AccountHomePageRequest("POST", "11", null));

        assertEquals(PageAction.RENDER, result.action());
        assertTrue(result.model().hasLineQuantityError(11L));
        assertEquals("So luong khong hop le.", result.model().getLineQuantityError());
        assertNull(result.model().getInfoMessage());
    }

    @Test
    void accountHomeShowsSuccessMessageForAdd() {
        StubAccountCatalogApplicationService service = new StubAccountCatalogApplicationService();
        service.books = List.of(book(11L, "Clean Code", 3));
        service.result = CartActionResult.success();
        AccountHomePageBean bean = new AccountHomePageBean(service);

        AccountHomePageResult result = bean.handle(new AccountHomePageRequest("POST", "11", "2"));

        assertEquals(PageAction.RENDER, result.action());
        assertEquals("Da them sach vao gio hang.", result.model().getInfoMessage());
        assertNull(result.model().getErrorMessage());
    }

    @Test
    void cartPageShowsInlineErrorForInvalidUpdateInput() {
        StubCartApplicationService service = new StubCartApplicationService();
        CartPageBean bean = new CartPageBean(service);

        CartPageResult result = bean.handle(new CartPageRequest("POST", "update", "11", "abc", null));

        assertEquals(PageAction.RENDER, result.action());
        assertEquals("So luong khong hop le.", result.model().getErrorMessage());
        assertEquals("So luong khong hop le.", result.model().getLineQuantityError());
        assertNull(result.model().getInfoMessage());
    }

    @Test
    void cartPageRejectsAddAction() {
        StubCartApplicationService service = new StubCartApplicationService();
        CartPageBean bean = new CartPageBean(service);

        CartPageResult result = bean.handle(new CartPageRequest("POST", "add", "11", "2", null));

        assertEquals(PageAction.RENDER, result.action());
        assertEquals("Hanh dong gio hang khong hop le.", result.model().getErrorMessage());
        assertNull(result.model().getInfoMessage());
    }

    @Test
    void cartPageRejectsMissingAction() {
        StubCartApplicationService service = new StubCartApplicationService();
        CartPageBean bean = new CartPageBean(service);

        CartPageResult result = bean.handle(new CartPageRequest("POST", null, "11", "2", null));

        assertEquals(PageAction.RENDER, result.action());
        assertEquals("Hanh dong gio hang khong hop le.", result.model().getErrorMessage());
        assertNull(result.model().getInfoMessage());
    }

    @Test
    void checkoutPageRedirectsEmptyCartBackToCart() {
        StubCheckoutApplicationService service = new StubCheckoutApplicationService();
        service.checkoutView = new CheckoutView(new CartView(List.of(), new Money(BigDecimal.ZERO)), null, true);
        CheckoutPageBean bean = new CheckoutPageBean(service);

        CheckoutPageResult result = bean.handle(new CheckoutPageRequest("GET", "", "", "", "", "", "", "", "", ""));

        assertEquals(PageAction.REDIRECT, result.action());
        assertEquals("/account/cart.jsp?info=emptyCheckout", result.redirectUrl());
    }

    @Test
    void cartPageMapsEmptyCheckoutInfoMessage() {
        StubCartApplicationService service = new StubCartApplicationService();
        CartPageBean bean = new CartPageBean(service);

        CartPageResult result = bean.handle(new CartPageRequest("GET", null, null, null, "emptyCheckout"));

        assertEquals(PageAction.RENDER, result.action());
        assertEquals("Gio hang dang trong. Vui long them sach truoc khi thanh toan.", result.model().getInfoMessage());
    }

    @Test
    void checkoutPageRedirectsToOrderDetailOnSuccess() {
        StubCheckoutApplicationService service = new StubCheckoutApplicationService();
        service.checkoutView = new CheckoutView(new CartView(List.of(new io.github.phunguy65.bookstore.purchase.application.service.CartLineView(new BookId(11L), new BookTitle("Clean Code"), new Money(new BigDecimal("12.50")), new Quantity(1), new Money(new BigDecimal("12.50")), new Quantity(3))), new Money(new BigDecimal("12.50"))), null, true);
        service.checkoutResult = CheckoutResult.success(new OrderId(99L));
        CheckoutPageBean bean = new CheckoutPageBean(service);

        CheckoutPageResult result = bean.handle(new CheckoutPageRequest("POST", "Nguyen Van A", "0901234567", "123 Main St", "", "Ward 1", "District 1", "Ho Chi Minh City", "Ho Chi Minh", "700000"));

        assertEquals(PageAction.REDIRECT, result.action());
        assertEquals("/account/order-detail.jsp?orderId=99", result.redirectUrl());
    }

    @Test
    void orderHistoryPageMapsMissingErrorParam() {
        StubOrderQueryApplicationService service = new StubOrderQueryApplicationService();
        service.history = List.of(new OrderSummaryView(new OrderId(50L), OrderStatus.PLACED, new Money(new BigDecimal("12.50")), 1, Instant.parse("2026-03-27T00:00:00Z")));
        OrderHistoryPageBean bean = new OrderHistoryPageBean(service);

        OrderHistoryPageResult result = bean.handle(new OrderHistoryPageRequest(null, "missing"));

        assertEquals(PageAction.RENDER, result.action());
        assertEquals("Khong tim thay don hang phu hop.", result.model().getErrorMessage());
        assertEquals(1, result.model().getOrders().size());
    }

    @Test
    void orderDetailPageRedirectsWhenOrderIsMissing() {
        StubOrderQueryApplicationService service = new StubOrderQueryApplicationService();
        service.lookupResult = OrderLookupResult.notFound("Khong tim thay don hang phu hop.");
        OrderDetailPageBean bean = new OrderDetailPageBean(service);

        OrderDetailPageResult result = bean.handle(new OrderDetailPageRequest("999"));

        assertEquals(PageAction.REDIRECT, result.action());
        assertEquals("/account/orders.jsp?error=missing", result.redirectUrl());
        assertNull(result.model());
    }

    @Test
    void orderDetailPageReturnsOwnedOrderWhenFound() {
        StubOrderQueryApplicationService service = new StubOrderQueryApplicationService();
        service.lookupResult = OrderLookupResult.found(new OrderDetailView(
                50L,
                OrderStatus.PLACED,
                new BigDecimal("12.50"),
                new OrderAddressView("Reader", "0901234567", "123 Main St", "", "Ward 1", "District 1", "Ho Chi Minh City", "Ho Chi Minh", "700000"),
                List.of(new OrderItemDetailView(11L, "Clean Code", "9786041234567", 1, new BigDecimal("12.50")))
        ));
        OrderDetailPageBean bean = new OrderDetailPageBean(service);

        OrderDetailPageResult result = bean.handle(new OrderDetailPageRequest("50"));

        assertEquals(PageAction.RENDER, result.action());
        assertEquals(50L, result.model().getOrder().orderId());
        assertNull(result.redirectUrl());
    }

    @Test
    void checkoutPageMapsFieldLevelValidationErrors() {
        StubCheckoutApplicationService service = new StubCheckoutApplicationService();
        service.checkoutView = new CheckoutView(new CartView(List.of(new io.github.phunguy65.bookstore.purchase.application.service.CartLineView(new BookId(11L), new BookTitle("Clean Code"), new Money(new BigDecimal("12.50")), new Quantity(1), new Money(new BigDecimal("12.50")), new Quantity(3))), new Money(new BigDecimal("12.50"))), null, true);
        service.checkoutResult = CheckoutResult.failure("recipientName must not be blank", java.util.Map.of("recipientName", "recipientName must not be blank"));
        CheckoutPageBean bean = new CheckoutPageBean(service);

        CheckoutPageResult result = bean.handle(new CheckoutPageRequest("POST", "", "0901234567", "123 Main St", "", "Ward 1", "District 1", "Ho Chi Minh City", "Ho Chi Minh", "700000"));

        assertEquals(PageAction.RENDER, result.action());
        assertNull(result.model().getErrorMessage());
        assertEquals("recipientName must not be blank", result.model().getFieldError("recipientName"));
    }

    private void assertStatelessWithRemoteInterface(Class<?> beanClass, Class<?> remoteInterface) {
        assertTrue(beanClass.isAnnotationPresent(jakarta.ejb.Stateless.class),
                beanClass.getSimpleName() + " should be @Stateless");
        assertFalse(beanClass.isAnnotationPresent(jakarta.enterprise.context.Dependent.class),
                beanClass.getSimpleName() + " should not be @Dependent");
        assertTrue(remoteInterface.isAssignableFrom(beanClass),
                beanClass.getSimpleName() + " should implement " + remoteInterface.getSimpleName());
        assertTrue(remoteInterface.isAnnotationPresent(jakarta.ejb.Remote.class),
                remoteInterface.getSimpleName() + " should be @Remote");
    }

    private void assertHasParameterizedConstructor(Class<?> beanClass, Class<?> dependencyType) {
        boolean hasNoArg = false;
        boolean hasParameterized = false;
        for (var ctor : beanClass.getDeclaredConstructors()) {
            if (ctor.getParameterCount() == 0) hasNoArg = true;
            if (ctor.getParameterCount() == 1 && ctor.getParameterTypes()[0] == dependencyType) hasParameterized = true;
        }
        assertTrue(hasNoArg, beanClass.getSimpleName() + " should have a no-arg constructor");
        assertTrue(hasParameterized, beanClass.getSimpleName() + " should have a constructor accepting " + dependencyType.getSimpleName());
    }

    private static final class StubCartApplicationService extends CartApplicationService {
        private CartActionResult result = CartActionResult.failure("failure", java.util.Map.of());
        private CartView cart = new CartView(List.of(), new Money(BigDecimal.ZERO));

        @Override
        public CartView getCart(CustomerId customerId) {
            return cart;
        }

        @Override
        public CartActionResult addBook(CustomerId customerId, long bookIdValue, int quantityValue) {
            return result;
        }

        @Override
        public CartActionResult updateQuantity(CustomerId customerId, long bookIdValue, int quantityValue) {
            return result;
        }

        @Override
        public CartActionResult removeBook(CustomerId customerId, long bookIdValue) {
            return result;
        }
    }

    private static final class StubAccountCatalogApplicationService extends AccountCatalogApplicationService {
        private List<CatalogBookView> books = List.of();
        private CartActionResult result = CartActionResult.failure("failure", java.util.Map.of());

        @Override
        public List<CatalogBookView> getActiveBooks() {
            return books;
        }

        @Override
        public CartActionResult addBook(CustomerId customerId, long bookIdValue, int quantityValue) {
            return result;
        }
    }

    private static CatalogBookView book(long id, String title, int stock) {
        return new CatalogBookView(
                new BookId(id),
                new Isbn("9786041234567"),
                new BookTitle(title),
                new AuthorName("Tac gia " + id),
                new BookDescription("Mo ta sach " + id),
                new Money(new BigDecimal("12.50")),
                new Quantity(stock)
        );
    }

    private static final class StubCheckoutApplicationService extends CheckoutApplicationService {
        private CheckoutView checkoutView;
        private CheckoutResult checkoutResult = CheckoutResult.failure("failure", java.util.Map.of());

        @Override
        public CheckoutView getCheckout(CustomerId customerId) {
            return checkoutView;
        }

        @Override
        public CheckoutResult placeOrder(CustomerId customerId, CheckoutAddressInput addressInput) {
            return checkoutResult;
        }
    }

    private static final class StubOrderQueryApplicationService extends OrderQueryApplicationService {
        private List<OrderSummaryView> history = List.of();
        private OrderLookupResult lookupResult = OrderLookupResult.notFound("Khong tim thay don hang phu hop.");

        @Override
        public List<OrderSummaryView> getOrderHistory(CustomerId customerId) {
            return history;
        }

        @Override
        public OrderLookupResult getOwnedOrder(CustomerId customerId, long orderIdValue) {
            return lookupResult;
        }
    }
}
