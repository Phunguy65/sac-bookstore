package io.github.phunguy65.bookstore.auth.application.service;

import io.github.phunguy65.bookstore.auth.domain.model.Customer;

public final class LoginResult {
    private final boolean success;
    private final Customer customer;
    private final String errorMessage;

    private LoginResult(boolean success, Customer customer, String errorMessage) {
        this.success = success;
        this.customer = customer;
        this.errorMessage = errorMessage;
    }

    public static LoginResult success(Customer customer) {
        return new LoginResult(true, customer, null);
    }

    public static LoginResult failure(String errorMessage) {
        return new LoginResult(false, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
