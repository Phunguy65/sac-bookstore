package io.github.phunguy65.bookstore.shared.domain.valueobject;

import io.github.phunguy65.bookstore.shared.domain.validation.Require;

public record AddressDetails(
        RecipientName recipientName,
        PhoneNumber phoneNumber,
        AddressLine line1,
        String line2,
        Ward ward,
        District district,
        City city,
        Province province,
        PostalCode postalCode
) {
    public AddressDetails {
        Require.notNull(recipientName, "recipientName");
        Require.notNull(phoneNumber, "phoneNumber");
        Require.notNull(line1, "line1");
        line2 = Require.nullableMaxLength(line2, 255, "line2");
        Require.notNull(ward, "ward");
        Require.notNull(district, "district");
        Require.notNull(city, "city");
        Require.notNull(province, "province");
        Require.notNull(postalCode, "postalCode");
    }
}
