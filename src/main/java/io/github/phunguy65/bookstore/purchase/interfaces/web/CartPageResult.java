package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

public record CartPageResult(
        PageAction action,
        CartPageModel model
) implements Serializable {
}
