package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

public record CartPageRequest(
        String method,
        String action,
        String bookId,
        String quantity,
        String infoParam
) implements Serializable {
}
