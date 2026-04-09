package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.purchase.application.service.CheckoutApplicationService;
import io.github.phunguy65.bookstore.purchase.application.service.CheckoutResult;
import io.github.phunguy65.bookstore.purchase.application.service.CheckoutView;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class CheckoutPageBean implements CheckoutPage {
    private CheckoutApplicationService checkoutApplicationService;

    public CheckoutPageBean() {
    }

    @Inject
    public CheckoutPageBean(CheckoutApplicationService checkoutApplicationService) {
        this.checkoutApplicationService = checkoutApplicationService;
    }

    @Override
    public CheckoutPageResult handle(CheckoutPageRequest request) {
        var customerId = CustomerId.DEFAULT_CUSTOMER;
        CheckoutView checkout = checkoutApplicationService.getCheckout(customerId);
        if (checkout.cart().isEmpty()) {
            CheckoutPageModel model = new CheckoutPageModel(checkout.cart(), AddressFormData.empty(), true, null, java.util.Map.of());
            return new CheckoutPageResult(PageAction.REDIRECT, PurchasePaths.CART + "?info=emptyCheckout", model);
        }

        if (!"POST".equalsIgnoreCase(request.method())) {
            AddressFormData form = checkout.shippingAddress() == null ? AddressFormData.empty() : AddressFormData.from(checkout.shippingAddress());
            CheckoutPageModel model = new CheckoutPageModel(checkout.cart(), form, checkout.requiresShippingAddressInput(), null, java.util.Map.of());
            return new CheckoutPageResult(PageAction.RENDER, null, model);
        }

        AddressFormData form = readAddressForm(request);
        CheckoutResult result = checkoutApplicationService.placeOrder(customerId, form.toInput());
        if (result.isSuccess()) {
            CheckoutPageModel model = new CheckoutPageModel(checkout.cart(), form, checkout.requiresShippingAddressInput(), null, java.util.Map.of());
            return new CheckoutPageResult(PageAction.REDIRECT, PurchasePaths.ORDER_DETAIL + "?orderId=" + result.getOrderId().value(), model);
        }
        java.util.Map<String, String> fieldErrors = mapFieldErrors(result);
        String errorMessage = fieldErrors.isEmpty() ? result.getErrorMessage() : null;
        CheckoutPageModel model = new CheckoutPageModel(checkout.cart(), form, checkout.requiresShippingAddressInput(), errorMessage, fieldErrors);
        return new CheckoutPageResult(PageAction.RENDER, null, model);
    }

    private java.util.Map<String, String> mapFieldErrors(CheckoutResult result) {
        java.util.Map<String, String> fieldErrors = new java.util.HashMap<>();
        for (String fieldName : java.util.List.of("recipientName", "phoneNumber", "line1", "ward", "district", "city", "province", "postalCode")) {
            String fieldError = result.getFieldError(fieldName);
            if (fieldError != null) {
                fieldErrors.put(fieldName, fieldError);
            }
        }
        return fieldErrors;
    }

    private AddressFormData readAddressForm(CheckoutPageRequest request) {
        return new AddressFormData(
                trimToEmpty(request.recipientName()),
                trimToEmpty(request.phoneNumber()),
                trimToEmpty(request.line1()),
                trimToEmpty(request.line2()),
                trimToEmpty(request.ward()),
                trimToEmpty(request.district()),
                trimToEmpty(request.city()),
                trimToEmpty(request.province()),
                trimToEmpty(request.postalCode())
        );
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
