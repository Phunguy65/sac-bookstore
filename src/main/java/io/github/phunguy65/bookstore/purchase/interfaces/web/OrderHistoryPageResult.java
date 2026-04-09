package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

public record OrderHistoryPageResult(
        PageAction action,
        OrderHistoryPageModel model
) implements Serializable {
}
