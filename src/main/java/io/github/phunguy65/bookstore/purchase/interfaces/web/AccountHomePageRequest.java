package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

public record AccountHomePageRequest(
        String method,
        String bookId,
        String quantity
) implements Serializable {
}
