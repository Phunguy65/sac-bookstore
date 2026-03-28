package io.github.phunguy65.bookstore.purchase.application.service;

public final class CartActionResult {
    private final boolean success;
    private final String errorMessage;

    private CartActionResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static CartActionResult success() {
        return new CartActionResult(true, null);
    }

    public static CartActionResult failure(String errorMessage) {
        return new CartActionResult(false, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
