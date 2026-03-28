package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.purchase.application.service.CartActionResult;
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
import io.github.phunguy65.bookstore.shared.domain.valueobject.BookId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Money;
import io.github.phunguy65.bookstore.shared.domain.valueobject.OrderId;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurchasePageBeansTest {
    @Test
    void jspResolvedPurchaseBeansUseDependentScope() {
        assertDependentScope(CartPageBean.class);
        assertDependentScope(CheckoutPageBean.class);
        assertDependentScope(OrderHistoryPageBean.class);
        assertDependentScope(OrderDetailPageBean.class);
    }

    @Test
    void constructorInjectedPurchaseBeansDeclareRequiredDependencies() {
        assertSingleConstructorDependency(CartPageBean.class, CartApplicationService.class);
        assertSingleConstructorDependency(CheckoutPageBean.class, CheckoutApplicationService.class);
        assertSingleConstructorDependency(OrderHistoryPageBean.class, OrderQueryApplicationService.class);
        assertSingleConstructorDependency(OrderDetailPageBean.class, OrderQueryApplicationService.class);
    }

    @Test
    void cartPageShowsInlineErrorForInvalidInput() {
        StubCartApplicationService service = new StubCartApplicationService();
        CartPageBean bean = new CartPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .authenticated(1L)
                .parameter("action", "add")
                .parameter("bookId", "abc")
                .parameter("quantity", "1");

        CartPageModel model = bean.handle(requestContext.proxy(), ServletApiTestSupport.response().proxy());

        assertEquals("Ma sach khong hop le.", model.getErrorMessage());
        assertEquals("Ma sach khong hop le.", model.getAddBookIdError());
        assertNull(model.getInfoMessage());
    }

    @Test
    void cartPageShowsSuccessMessageForMutation() {
        StubCartApplicationService service = new StubCartApplicationService();
        service.result = CartActionResult.success();
        CartPageBean bean = new CartPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .authenticated(1L)
                .parameter("action", "remove")
                .parameter("bookId", "11");

        CartPageModel model = bean.handle(requestContext.proxy(), ServletApiTestSupport.response().proxy());

        assertEquals("Gio hang da duoc cap nhat.", model.getInfoMessage());
        assertNull(model.getErrorMessage());
    }

    @Test
    void checkoutPageRedirectsEmptyCartBackToCart() throws IOException {
        StubCheckoutApplicationService service = new StubCheckoutApplicationService();
        service.checkoutView = new CheckoutView(new CartView(List.of(), new Money(BigDecimal.ZERO)), null, true);
        CheckoutPageBean bean = new CheckoutPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore").authenticated(1L);
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        bean.handle(requestContext.proxy(), responseContext.proxy());

        assertEquals("/bookstore/account/cart.jsp", responseContext.redirectedUrl());
    }

    @Test
    void checkoutPageRedirectsToOrderDetailOnSuccess() throws IOException {
        StubCheckoutApplicationService service = new StubCheckoutApplicationService();
        service.checkoutView = new CheckoutView(new CartView(List.of(new io.github.phunguy65.bookstore.purchase.application.service.CartLineView(new BookId(11L), new io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle("Clean Code"), new Money(new BigDecimal("12.50")), new io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity(1), new Money(new BigDecimal("12.50")), new io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity(3))), new Money(new BigDecimal("12.50"))), null, true);
        service.checkoutResult = CheckoutResult.success(new OrderId(99L));
        CheckoutPageBean bean = new CheckoutPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .authenticated(1L)
                .parameter("recipientName", "Nguyen Van A")
                .parameter("phoneNumber", "0901234567")
                .parameter("line1", "123 Main St")
                .parameter("line2", "")
                .parameter("ward", "Ward 1")
                .parameter("district", "District 1")
                .parameter("city", "Ho Chi Minh City")
                .parameter("province", "Ho Chi Minh")
                .parameter("postalCode", "700000");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        bean.handle(requestContext.proxy(), responseContext.proxy());

        assertEquals("/bookstore/account/order-detail.jsp?orderId=99", responseContext.redirectedUrl());
    }

    @Test
    void orderHistoryPageMapsMissingErrorParam() {
        StubOrderQueryApplicationService service = new StubOrderQueryApplicationService();
        service.history = List.of(new OrderSummaryView(new OrderId(50L), OrderStatus.PLACED, new Money(new BigDecimal("12.50")), 1, Instant.parse("2026-03-27T00:00:00Z")));
        OrderHistoryPageBean bean = new OrderHistoryPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore")
                .authenticated(1L)
                .parameter("error", "missing");

        OrderHistoryPageModel model = bean.handle(requestContext.proxy(), ServletApiTestSupport.response().proxy());

        assertEquals("Khong tim thay don hang phu hop.", model.getErrorMessage());
        assertEquals(1, model.getOrders().size());
    }

    @Test
    void orderDetailPageRedirectsWhenOrderIsMissing() throws IOException {
        StubOrderQueryApplicationService service = new StubOrderQueryApplicationService();
        service.lookupResult = OrderLookupResult.notFound("Khong tim thay don hang phu hop.");
        OrderDetailPageBean bean = new OrderDetailPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore")
                .authenticated(1L)
                .parameter("orderId", "999");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        OrderDetailPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertNull(model);
        assertEquals("/bookstore/account/orders.jsp?error=missing", responseContext.redirectedUrl());
    }

    @Test
    void orderDetailPageReturnsOwnedOrderWhenFound() throws IOException {
        StubOrderQueryApplicationService service = new StubOrderQueryApplicationService();
        service.lookupResult = OrderLookupResult.found(new OrderDetailView(
                50L,
                OrderStatus.PLACED,
                new BigDecimal("12.50"),
                new OrderAddressView("Reader", "0901234567", "123 Main St", "", "Ward 1", "District 1", "Ho Chi Minh City", "Ho Chi Minh", "700000"),
                List.of(new OrderItemDetailView(11L, "Clean Code", "9786041234567", 1, new BigDecimal("12.50")))
        ));
        OrderDetailPageBean bean = new OrderDetailPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("GET", "/bookstore")
                .authenticated(1L)
                .parameter("orderId", "50");
        ServletApiTestSupport.TestResponseContext responseContext = ServletApiTestSupport.response();

        OrderDetailPageModel model = bean.handle(requestContext.proxy(), responseContext.proxy());

        assertEquals(50L, model.getOrder().orderId());
        assertFalse(responseContext.committed());
    }

    @Test
    void checkoutPageMapsFieldLevelValidationErrors() throws IOException {
        StubCheckoutApplicationService service = new StubCheckoutApplicationService();
        service.checkoutView = new CheckoutView(new CartView(List.of(new io.github.phunguy65.bookstore.purchase.application.service.CartLineView(new BookId(11L), new io.github.phunguy65.bookstore.book.domain.valueobject.BookTitle("Clean Code"), new Money(new BigDecimal("12.50")), new io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity(1), new Money(new BigDecimal("12.50")), new io.github.phunguy65.bookstore.shared.domain.valueobject.Quantity(3))), new Money(new BigDecimal("12.50"))), null, true);
        service.checkoutResult = CheckoutResult.failure("recipientName must not be blank");
        CheckoutPageBean bean = new CheckoutPageBean(service);
        ServletApiTestSupport.TestRequestContext requestContext = ServletApiTestSupport.request("POST", "/bookstore")
                .authenticated(1L)
                .parameter("recipientName", "")
                .parameter("phoneNumber", "0901234567")
                .parameter("line1", "123 Main St")
                .parameter("ward", "Ward 1")
                .parameter("district", "District 1")
                .parameter("city", "Ho Chi Minh City")
                .parameter("province", "Ho Chi Minh")
                .parameter("postalCode", "700000");

        CheckoutPageModel model = bean.handle(requestContext.proxy(), ServletApiTestSupport.response().proxy());

        assertNull(model.getErrorMessage());
        assertEquals("recipientName must not be blank", model.getFieldError("recipientName"));
    }

    private void assertDependentScope(Class<?> beanClass) {
        assertTrue(beanClass.isAnnotationPresent(jakarta.enterprise.context.Dependent.class));
        assertFalse(beanClass.isAnnotationPresent(jakarta.enterprise.context.RequestScoped.class));
    }

    private void assertSingleConstructorDependency(Class<?> beanClass, Class<?> dependencyType) {
        assertEquals(1, beanClass.getDeclaredConstructors().length);
        assertEquals(1, beanClass.getDeclaredConstructors()[0].getParameterCount());
        assertEquals(dependencyType, beanClass.getDeclaredConstructors()[0].getParameterTypes()[0]);
    }

    private static final class StubCartApplicationService extends CartApplicationService {
        private CartActionResult result = CartActionResult.failure("failure");
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

    private static final class StubCheckoutApplicationService extends CheckoutApplicationService {
        private CheckoutView checkoutView;
        private CheckoutResult checkoutResult = CheckoutResult.failure("failure");

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
