package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

import io.github.phunguy65.bookstore.purchase.application.service.CartView;

public class CartPageModel implements Serializable {
    private final CartView cart;
    private final String errorMessage;
    private final String infoMessage;
    private final Long lineErrorBookId;
    private final String lineQuantityError;

    public CartPageModel(CartView cart, String errorMessage, String infoMessage, Long lineErrorBookId, String lineQuantityError) {
        this.cart = cart;
        this.errorMessage = errorMessage;
        this.infoMessage = infoMessage;
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

    public Long getLineErrorBookId() {
        return lineErrorBookId;
    }

    public String getLineQuantityError() {
        return lineQuantityError;
    }
}
