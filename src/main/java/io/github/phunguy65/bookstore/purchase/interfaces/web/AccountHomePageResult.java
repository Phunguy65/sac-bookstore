package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

public record AccountHomePageResult(
        PageAction action,
        AccountHomePageModel model
) implements Serializable {
}
