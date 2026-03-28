package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.auth.interfaces.web.AuthSession;
import io.github.phunguy65.bookstore.purchase.application.service.CheckoutApplicationService;
import io.github.phunguy65.bookstore.purchase.application.service.CheckoutResult;
import io.github.phunguy65.bookstore.purchase.application.service.CheckoutView;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Named
@Dependent
public class CheckoutPageBean {
    private final CheckoutApplicationService checkoutApplicationService;

    @Inject
    public CheckoutPageBean(CheckoutApplicationService checkoutApplicationService) {
        this.checkoutApplicationService = checkoutApplicationService;
    }

    public CheckoutPageModel handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var customerId = AuthSession.getCustomerId(request);
        CheckoutView checkout = checkoutApplicationService.getCheckout(customerId);
        if (checkout.cart().isEmpty()) {
            response.sendRedirect(request.getContextPath() + PurchasePaths.CART);
            return new CheckoutPageModel(checkout.cart(), AddressFormData.empty(), true, null, java.util.Map.of());
        }

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            AddressFormData form = checkout.shippingAddress() == null ? AddressFormData.empty() : AddressFormData.from(checkout.shippingAddress());
            return new CheckoutPageModel(checkout.cart(), form, checkout.requiresShippingAddressInput(), null, java.util.Map.of());
        }

        AddressFormData form = readAddressForm(request);
        CheckoutResult result = checkoutApplicationService.placeOrder(customerId, form.toInput());
        if (result.isSuccess()) {
            response.sendRedirect(request.getContextPath() + PurchasePaths.ORDER_DETAIL + "?orderId=" + result.getOrderId().value());
            return new CheckoutPageModel(checkout.cart(), form, checkout.requiresShippingAddressInput(), null, java.util.Map.of());
        }
        java.util.Map<String, String> fieldErrors = mapFieldErrors(result.getErrorMessage());
        String errorMessage = fieldErrors.isEmpty() ? result.getErrorMessage() : null;
        return new CheckoutPageModel(checkout.cart(), form, checkout.requiresShippingAddressInput(), errorMessage, fieldErrors);
    }

    private java.util.Map<String, String> mapFieldErrors(String message) {
        java.util.Map<String, String> fieldErrors = new java.util.HashMap<>();
        mapFieldError(fieldErrors, "recipientName", message);
        mapFieldError(fieldErrors, "phoneNumber", message);
        mapFieldError(fieldErrors, "line1", message);
        mapFieldError(fieldErrors, "ward", message);
        mapFieldError(fieldErrors, "district", message);
        mapFieldError(fieldErrors, "city", message);
        mapFieldError(fieldErrors, "province", message);
        mapFieldError(fieldErrors, "postalCode", message);
        return fieldErrors;
    }

    private void mapFieldError(java.util.Map<String, String> fieldErrors, String fieldName, String message) {
        if (message != null && message.startsWith(fieldName + " ")) {
            fieldErrors.put(fieldName, message);
        }
    }

    private AddressFormData readAddressForm(HttpServletRequest request) {
        return new AddressFormData(
                trimToEmpty(request.getParameter("recipientName")),
                trimToEmpty(request.getParameter("phoneNumber")),
                trimToEmpty(request.getParameter("line1")),
                trimToEmpty(request.getParameter("line2")),
                trimToEmpty(request.getParameter("ward")),
                trimToEmpty(request.getParameter("district")),
                trimToEmpty(request.getParameter("city")),
                trimToEmpty(request.getParameter("province")),
                trimToEmpty(request.getParameter("postalCode"))
        );
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
