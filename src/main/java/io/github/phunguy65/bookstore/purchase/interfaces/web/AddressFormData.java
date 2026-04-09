package io.github.phunguy65.bookstore.purchase.interfaces.web;

import java.io.Serializable;

import io.github.phunguy65.bookstore.purchase.application.service.CheckoutAddressInput;
import io.github.phunguy65.bookstore.shared.domain.valueobject.AddressDetails;

public class AddressFormData implements Serializable {
    private final String recipientName;
    private final String phoneNumber;
    private final String line1;
    private final String line2;
    private final String ward;
    private final String district;
    private final String city;
    private final String province;
    private final String postalCode;

    public AddressFormData(String recipientName, String phoneNumber, String line1, String line2, String ward, String district, String city, String province, String postalCode) {
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.line1 = line1;
        this.line2 = line2;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
    }

    public static AddressFormData empty() {
        return new AddressFormData("", "", "", "", "", "", "", "", "");
    }

    public static AddressFormData from(AddressDetails details) {
        return new AddressFormData(
                details.recipientName().value(),
                details.phoneNumber().value(),
                details.line1().value(),
                details.line2() == null ? "" : details.line2(),
                details.ward().value(),
                details.district().value(),
                details.city().value(),
                details.province().value(),
                details.postalCode().value()
        );
    }

    public CheckoutAddressInput toInput() {
        return new CheckoutAddressInput(recipientName, phoneNumber, line1, line2, ward, district, city, province, postalCode);
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getWard() {
        return ward;
    }

    public String getDistrict() {
        return district;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getPostalCode() {
        return postalCode;
    }
}
