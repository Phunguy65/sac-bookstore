package io.github.phunguy65.bookstore.purchase.application.service;

import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressDetails;

import java.io.Serializable;

public record CheckoutView(
        CartView cart,
        AddressDetails shippingAddress,
        boolean requiresShippingAddressInput
) implements Serializable {
}
