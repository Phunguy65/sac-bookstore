package io.github.phunguy65.bookstore.auth.domain.repository;

import io.github.phunguy65.bookstore.auth.domain.model.Address;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;

import java.util.List;
import java.util.Optional;

public interface AddressRepository {
    List<Address> findByCustomerId(CustomerId customerId);

    Optional<Address> findDefaultByCustomerId(CustomerId customerId);

    Optional<Address> findById(AddressId addressId);

    Address save(Address address);
}
