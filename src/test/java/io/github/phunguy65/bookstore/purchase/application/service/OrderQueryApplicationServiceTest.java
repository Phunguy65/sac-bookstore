package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderQueryApplicationServiceTest {
    @Test
    void getOrderHistoryReturnsOnlyOrdersForCustomer() {
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        orderRepository.put(PurchaseServiceTestSupport.order(10L, new CustomerId(1L), PurchaseServiceTestSupport.orderItem(1L, 11L, 1, "12.50", "12.50")));
        orderRepository.put(PurchaseServiceTestSupport.order(11L, new CustomerId(2L), PurchaseServiceTestSupport.orderItem(2L, 12L, 1, "10.00", "10.00")));
        OrderQueryApplicationService service = new OrderQueryApplicationService(orderRepository);

        var history = service.getOrderHistory(new CustomerId(1L));

        assertEquals(1, history.size());
        assertEquals(10L, history.getFirst().orderId().value());
    }

    @Test
    void getOwnedOrderReturnsFoundForOwnerAndNotFoundForForeignOrder() {
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        orderRepository.put(PurchaseServiceTestSupport.order(10L, new CustomerId(1L), PurchaseServiceTestSupport.orderItem(1L, 11L, 1, "12.50", "12.50")));
        OrderQueryApplicationService service = new OrderQueryApplicationService(orderRepository);

        OrderLookupResult owned = service.getOwnedOrder(new CustomerId(1L), 10L);
        OrderLookupResult foreign = service.getOwnedOrder(new CustomerId(2L), 10L);

        assertTrue(owned.isFound());
        assertFalse(foreign.isFound());
        assertEquals("Khong tim thay don hang phu hop.", foreign.getErrorMessage());
    }

    @Test
    void getOwnedOrderReturnsNotFoundForMissingOrder() {
        var orderRepository = new PurchaseServiceTestSupport.InMemoryOrderRepository();
        OrderQueryApplicationService service = new OrderQueryApplicationService(orderRepository);

        OrderLookupResult result = service.getOwnedOrder(new CustomerId(1L), 999L);

        assertFalse(result.isFound());
        assertEquals("Khong tim thay don hang phu hop.", result.getErrorMessage());
    }
}
