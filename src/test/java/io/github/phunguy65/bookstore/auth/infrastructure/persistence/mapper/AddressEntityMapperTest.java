package io.github.phunguy65.bookstore.auth.infrastructure.persistence.mapper;

import io.github.phunguy65.bookstore.auth.domain.model.Address;
import io.github.phunguy65.bookstore.auth.infrastructure.persistence.entity.AddressEntity;
import io.github.phunguy65.bookstore.auth.infrastructure.persistence.entity.CustomerEntity;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressDetails;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressLine;
import io.github.phunguy65.bookstore.shared.domain.valueobject.City;
import io.github.phunguy65.bookstore.shared.domain.valueobject.CustomerId;
import io.github.phunguy65.bookstore.shared.domain.valueobject.District;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PhoneNumber;
import io.github.phunguy65.bookstore.shared.domain.valueobject.PostalCode;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Province;
import io.github.phunguy65.bookstore.shared.domain.valueobject.RecipientName;
import io.github.phunguy65.bookstore.shared.domain.valueobject.Ward;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressEntityMapperTest {
    @Test
    void mapsEntityToDomain() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);

        AddressEntity entity = new AddressEntity();
        entity.setId(2L);
        entity.setCustomer(customer);
        entity.setRecipientName("Nguyen Van A");
        entity.setPhone("0901234567");
        entity.setLine1("123 Main St");
        entity.setLine2("Apt 101");
        entity.setWard("Ward 1");
        entity.setDistrict("District 1");
        entity.setCity("Ho Chi Minh City");
        entity.setProvince("Ho Chi Minh");
        entity.setPostalCode("700000");
        entity.setDefault(true);
        entity.setVersion(1L);
        entity.setCreatedAt(Instant.parse("2026-03-27T00:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-03-27T00:00:00Z"));

        Address address = AddressEntityMapper.toDomain(entity);

        assertEquals(new AddressId(2L), address.id());
        assertEquals(new CustomerId(1L), address.customerId());
        assertEquals("Nguyen Van A", address.details().recipientName().value());
        assertEquals("0901234567", address.details().phoneNumber().value());
        assertEquals("123 Main St", address.details().line1().value());
        assertEquals("Apt 101", address.details().line2());
        assertEquals("Ward 1", address.details().ward().value());
        assertEquals("District 1", address.details().district().value());
        assertEquals("Ho Chi Minh City", address.details().city().value());
        assertEquals("Ho Chi Minh", address.details().province().value());
        assertEquals("700000", address.details().postalCode().value());
        assertEquals(true, address.isDefault());
        assertEquals(1L, address.version());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), address.createdAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), address.updatedAt());
    }

    @Test
    void mapsDomainToEntity() {
        Address address = new Address(
                new AddressId(2L),
                new CustomerId(1L),
                new AddressDetails(
                        new RecipientName("Nguyen Van A"),
                        new PhoneNumber("0901234567"),
                        new AddressLine("123 Main St"),
                        "Apt 101",
                        new Ward("Ward 1"),
                        new District("District 1"),
                        new City("Ho Chi Minh City"),
                        new Province("Ho Chi Minh"),
                        new PostalCode("700000")
                ),
                true,
                1L,
                Instant.parse("2026-03-27T00:00:00Z"),
                Instant.parse("2026-03-27T00:00:00Z")
        );

        AddressEntity entity = AddressEntityMapper.toEntity(address);

        assertEquals(2L, entity.getId());
        assertEquals(1L, entity.getCustomer().getId());
        assertEquals("Nguyen Van A", entity.getRecipientName());
        assertEquals("0901234567", entity.getPhone());
        assertEquals("123 Main St", entity.getLine1());
        assertEquals("Apt 101", entity.getLine2());
        assertEquals("Ward 1", entity.getWard());
        assertEquals("District 1", entity.getDistrict());
        assertEquals("Ho Chi Minh City", entity.getCity());
        assertEquals("Ho Chi Minh", entity.getProvince());
        assertEquals("700000", entity.getPostalCode());
        assertEquals(true, entity.isDefault());
        assertEquals(1L, entity.getVersion());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), entity.getCreatedAt());
        assertEquals(Instant.parse("2026-03-27T00:00:00Z"), entity.getUpdatedAt());
    }
}
