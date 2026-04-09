package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

public record CheckoutPageRequest(
        String method,
        String recipientName,
        String phoneNumber,
        String line1,
        String line2,
        String ward,
        String district,
        String city,
        String province,
        String postalCode
) implements Serializable {
}
