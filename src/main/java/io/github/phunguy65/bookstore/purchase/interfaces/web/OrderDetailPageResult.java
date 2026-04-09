package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

public record OrderDetailPageResult(
        PageAction action,
        String redirectUrl,
        OrderDetailPageModel model
) implements Serializable {
}
