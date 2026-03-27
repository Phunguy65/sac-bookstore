package io.github.phunguy65.bookstore.auth.domain.model;

import io.github.phunguy65.bookstore.auth.domain.valueobject.CustomerStatus;
import io.github.phunguy65.bookstore.shared.domain.validation.Require;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Email;
import io.github.phunguy65.bookstore.shared.domain.valueobject.FullName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PasswordHash;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;

import java.time.Instant;

public record Customer(
        CustomerId id,
        Email email,
        PasswordHash passwordHash,
        FullName fullName,
        PhoneNumber phoneNumber,
        CustomerStatus status,
        long version,
        Instant createdAt,
        Instant updatedAt
) {
    public Customer {
        Require.notNull(email, "email");
        Require.notNull(passwordHash, "passwordHash");
        Require.notNull(fullName, "fullName");
        Require.notNull(phoneNumber, "phoneNumber");
        Require.notNull(status, "status");
        Require.nonNegative(version, "version");
        Require.notNull(createdAt, "createdAt");
        Require.notNull(updatedAt, "updatedAt");
    }
}
