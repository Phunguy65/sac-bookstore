package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.purchase.application.service.CatalogBookView;

import java.util.List;

public class AccountHomePageModel {
    private final List<CatalogBookView> books;
    private final String errorMessage;
    private final String infoMessage;
    private final Long lineErrorBookId;
    private final String lineQuantityError;
    private final Long submittedBookId;
    private final String submittedQuantity;

    public AccountHomePageModel(
            List<CatalogBookView> books,
            String errorMessage,
            String infoMessage,
            Long lineErrorBookId,
            String lineQuantityError,
            Long submittedBookId,
            String submittedQuantity
    ) {
        this.books = List.copyOf(books);
        this.errorMessage = errorMessage;
        this.infoMessage = infoMessage;
        this.lineErrorBookId = lineErrorBookId;
        this.lineQuantityError = lineQuantityError;
        this.submittedBookId = submittedBookId;
        this.submittedQuantity = submittedQuantity;
    }

    public List<CatalogBookView> getBooks() {
        return books;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public String getQuantityValue(long bookId) {
        if (submittedBookId != null && submittedBookId == bookId && submittedQuantity != null && !submittedQuantity.isBlank()) {
            return submittedQuantity;
        }
        return "1";
    }

    public boolean hasLineQuantityError(long bookId) {
        return lineQuantityError != null && lineErrorBookId != null && lineErrorBookId == bookId;
    }

    public String getLineQuantityError() {
        return lineQuantityError;
    }
}
