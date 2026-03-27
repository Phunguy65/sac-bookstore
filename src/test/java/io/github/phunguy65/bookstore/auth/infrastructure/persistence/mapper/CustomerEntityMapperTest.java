package io.github.phunguy65.bookstore.auth.infrastructure.persistence.mapper;

import io.github.phunguy65.bookstore.auth.domain.model.Customer;
import io.github.phunguy65.bookstore.auth.domain.valueobject.CustomerStatus;
import io.github.phunguy65.bookstore.auth.infrastructure.persistence.entity.CustomerEntity;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Email;
import io.github.phunguy65.bookstore.shared.domain.valueobject.FullName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PasswordHash;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerEntityMapperTest {
    @Test
    void mapsEntityToDomain() {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(1L);
        entity.setEmail("customer@example.com");
        entity.setPasswordHash("hashed-password");
        entity.setFullName("Customer Name");
        entity.setPhone("0901234567");
        entity.setStatus(CustomerStatus.ACTIVE);
        entity.setVersion(1L);
        entity.setCreatedAt(Instant.parse("2026-03-27T00:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-03-27T00:00:00Z"));

        Customer customer = CustomerEntityMapper.toDomain(entity);

        assertEquals(new CustomerId(1L), customer.id());
        assertEquals(new Email("customer@example.com"), customer.email());
        assertEquals(new PasswordHash("hashed-password"), customer.passwordHash());
        assertEquals(new FullName("Customer Name"), customer.fullName());
        assertEquals(new PhoneNumber("0901234567"), customer.phoneNumber());
        assertEquals(CustomerStatus.ACTIVE, customer.status());
        assertEquals(1L, customer.version());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), customer.createdAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), customer.updatedAt());
    }

    @Test
    void mapsDomainToEntity() {
        Customer customer = new Customer(
                new CustomerId(1L),
                new Email("customer@example.com"),
                new PasswordHash("hashed-password"),
                new FullName("Customer Name"),
                new PhoneNumber("0901234567"),
                CustomerStatus.ACTIVE,
                1L,
                Instant.parse("2026-03-27T00:00:00Z"),
                Instant.parse("2026-03-27T00:00:00Z")
        );

        CustomerEntity entity = CustomerEntityMapper.toEntity(customer);

        assertEquals(1L, entity.getId());
        assertEquals("customer@example.com", entity.getEmail());
        assertEquals("hashed-password", entity.getPasswordHash());
        assertEquals("Customer Name", entity.getFullName());
        assertEquals("0901234567", entity.getPhone());
        assertEquals(CustomerStatus.ACTIVE, entity.getStatus());
        assertEquals(1L, entity.getVersion());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), entity.getCreatedAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), entity.getUpdatedAt());
    }
}
