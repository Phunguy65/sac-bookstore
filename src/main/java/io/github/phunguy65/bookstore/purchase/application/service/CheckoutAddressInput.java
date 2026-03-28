package io.github.phunguy65.bookstore.purchase.application.service;

public record CheckoutAddressInput(
        String recipientName,
        String phoneNumber,
        String line1,
        String line2,
        String ward,
        String district,
        String city,
        String province,
        String postalCode
) {
}
