package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.purchase.application.service.CartView;

public class CheckoutPageModel {
    private final CartView cart;
    private final AddressFormData addressForm;
    private final boolean requiresShippingAddressInput;
    private final String errorMessage;
    private final java.util.Map<String, String> fieldErrors;

    public CheckoutPageModel(CartView cart, AddressFormData addressForm, boolean requiresShippingAddressInput, String errorMessage, java.util.Map<String, String> fieldErrors) {
        this.cart = cart;
        this.addressForm = addressForm;
        this.requiresShippingAddressInput = requiresShippingAddressInput;
        this.errorMessage = errorMessage;
        this.fieldErrors = java.util.Map.copyOf(fieldErrors);
    }

    public CartView getCart() {
        return cart;
    }

    public AddressFormData getAddressForm() {
        return addressForm;
    }

    public boolean isRequiresShippingAddressInput() {
        return requiresShippingAddressInput;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getFieldError(String fieldName) {
        return fieldErrors.get(fieldName);
    }
}
