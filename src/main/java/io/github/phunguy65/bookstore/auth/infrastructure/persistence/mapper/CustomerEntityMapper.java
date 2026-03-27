package io.github.phunguy65.bookstore.auth.infrastructure.persistence.mapper;

import io.github.phunguy65.bookstore.auth.domain.model.Customer;
import io.github.phunguy65.bookstore.auth.infrastructure.persistence.entity.CustomerEntity;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Email;
import io.github.phunguy65.bookstore.shared.domain.valueobject.FullName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PasswordHash;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;

public final class CustomerEntityMapper {
    private CustomerEntityMapper() {
    }

    public static Customer toDomain(CustomerEntity entity) {
        return new Customer(
                entity.getId() == null ? null : new CustomerId(entity.getId()),
                new Email(entity.getEmail()),
                new PasswordHash(entity.getPasswordHash()),
                new FullName(entity.getFullName()),
                new PhoneNumber(entity.getPhone()),
                entity.getStatus(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static CustomerEntity toEntity(Customer domain) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(domain.id() == null ? null : domain.id().value());
        entity.setEmail(domain.email().value());
        entity.setPasswordHash(domain.passwordHash().value());
        entity.setFullName(domain.fullName().value());
        entity.setPhone(domain.phoneNumber().value());
        entity.setStatus(domain.status());
        entity.setVersion(domain.version());
        entity.setCreatedAt(domain.createdAt());
        entity.setUpdatedAt(domain.updatedAt());
        return entity;
    }
}
