package io.github.phunguy65.bookstore.auth.domain.repository;

import io.github.phunguy65.bookstore.auth.domain.model.Customer;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Email;

import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findById(CustomerId id);

    Optional<Customer> findByEmail(Email email);

    Customer save(Customer customer);
}
