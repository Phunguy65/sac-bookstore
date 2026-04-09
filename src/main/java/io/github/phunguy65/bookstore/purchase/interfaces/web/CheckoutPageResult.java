package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

public record CheckoutPageResult(
        PageAction action,
        String redirectUrl,
        CheckoutPageModel model
) implements Serializable {
}
