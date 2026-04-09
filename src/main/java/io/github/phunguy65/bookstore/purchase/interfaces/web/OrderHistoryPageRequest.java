package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

public record OrderHistoryPageRequest(
        String pageParam,
        String errorParam
) implements Serializable {
}
