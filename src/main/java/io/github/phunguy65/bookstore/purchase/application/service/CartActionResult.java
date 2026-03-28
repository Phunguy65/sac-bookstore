package io.github.phunguy65.bookstore.purchase.application.service;

public final class CartActionResult {
    private final boolean success;
    private final String errorMessage;
    private final java.util.Map<String, String> fieldErrors;

    private CartActionResult(boolean success, String errorMessage, java.util.Map<String, String> fieldErrors) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.fieldErrors = java.util.Map.copyOf(fieldErrors);
    }

    public static CartActionResult success() {
        return new CartActionResult(true, null, java.util.Map.of());
    }

    public static CartActionResult failure(String errorMessage) {
        return new CartActionResult(false, errorMessage, java.util.Map.of());
    }

    public static CartActionResult failure(String errorMessage, java.util.Map<String, String> fieldErrors) {
        return new CartActionResult(false, errorMessage, fieldErrors);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getFieldError(String fieldName) {
        return fieldErrors.get(fieldName);
    }
}
