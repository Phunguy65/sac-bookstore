package io.github.phunguy65.bookstore.auth.application.service;

import io.github.phunguy65.bookstore.auth.domain.model.Customer;

public final class RegisterResult {
    private final boolean success;
    private final Customer customer;
    private final String errorMessage;

    private RegisterResult(boolean success, Customer customer, String errorMessage) {
        this.success = success;
        this.customer = customer;
        this.errorMessage = errorMessage;
    }

    public static RegisterResult success(Customer customer) {
        return new RegisterResult(true, customer, null);
    }

    public static RegisterResult failure(String errorMessage) {
        return new RegisterResult(false, null, errorMessage);
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
