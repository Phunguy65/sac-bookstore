package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

public record OrderDetailPageRequest(
        String orderIdParam
) implements Serializable {
}
