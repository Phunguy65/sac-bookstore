package io.github.phunguy65.bookstore.purchase.interfaces.web;

import io.github.phunguy65.bookstore.purchase.application.service.CartView;

public class CartPageModel {
    private final CartView cart;
    private final String errorMessage;
    private final String infoMessage;
    private final String addBookIdError;
    private final String addQuantityError;
    private final Long lineErrorBookId;
    private final String lineQuantityError;

    public CartPageModel(CartView cart, String errorMessage, String infoMessage, String addBookIdError, String addQuantityError, Long lineErrorBookId, String lineQuantityError) {
        this.cart = cart;
        this.errorMessage = errorMessage;
        this.infoMessage = infoMessage;
        this.addBookIdError = addBookIdError;
        this.addQuantityError = addQuantityError;
        this.lineErrorBookId = lineErrorBookId;
        this.lineQuantityError = lineQuantityError;
    }

    public CartView getCart() {
        return cart;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public String getAddBookIdError() {
        return addBookIdError;
    }

    public String getAddQuantityError() {
        return addQuantityError;
    }

    public Long getLineErrorBookId() {
        return lineErrorBookId;
    }

    public String getLineQuantityError() {
        return lineQuantityError;
    }
}
