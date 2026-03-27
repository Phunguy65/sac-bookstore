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

public final class AddressEntityMapper {
    private AddressEntityMapper() {
    }

    public static Address toDomain(AddressEntity entity) {
        return new Address(
                entity.getId() == null ? null : new AddressId(entity.getId()),
                new CustomerId(entity.getCustomer().getId()),
                new AddressDetails(
                        new RecipientName(entity.getRecipientName()),
                        new PhoneNumber(entity.getPhone()),
                        new AddressLine(entity.getLine1()),
                        entity.getLine2(),
                        new Ward(entity.getWard()),
                        new District(entity.getDistrict()),
                        new City(entity.getCity()),
                        new Province(entity.getProvince()),
                        new PostalCode(entity.getPostalCode())
                ),
                entity.isDefault(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static AddressEntity toEntity(Address domain) {
        AddressEntity entity = new AddressEntity();
        entity.setId(domain.id() == null ? null : domain.id().value());
        CustomerEntity customer = new CustomerEntity();
        customer.setId(domain.customerId().value());
        entity.setCustomer(customer);
        entity.setRecipientName(domain.details().recipientName().value());
        entity.setPhone(domain.details().phoneNumber().value());
        entity.setLine1(domain.details().line1().value());
        entity.setLine2(domain.details().line2());
        entity.setWard(domain.details().ward().value());
        entity.setDistrict(domain.details().district().value());
        entity.setCity(domain.details().city().value());
        entity.setProvince(domain.details().province().value());
        entity.setPostalCode(domain.details().postalCode().value());
        entity.setDefault(domain.isDefault());
        entity.setVersion(domain.version());
        entity.setCreatedAt(domain.createdAt());
        entity.setUpdatedAt(domain.updatedAt());
        return entity;
    }
}
