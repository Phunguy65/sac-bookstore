package io.github.phunguy65.bookstore.auth.domain.model;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressDetails;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;

import java.time.Instant;

public record Address(
        AddressId id,
        CustomerId customerId,
        AddressDetails details,
        boolean isDefault,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
    public Address {
        Require.notNull(customerId, "customerId");
        Require.notNull(details, "details");
        Require.nonNegative(version, "version");
        Require.notNull(createdAt, "createdAt");
        Require.notNull(updatedAt, "updatedAt");
    }
}
