package io.github.phunguy65.bookstore.shared.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoreValueObjectsTest {
    @Test
    void moneyRejectsNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("-1.00")));
    }

    @Test
    void moneyRoundsToTwoDecimalPlaces() {
        Money money = new Money(new BigDecimal("100.126"));

        assertEquals(new BigDecimal("100.13"), money.amount());
    }

    @Test
    void quantityRejectsNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(-1));
    }

    @Test
    void quantityAcceptsZero() {
        Quantity quantity = new Quantity(0);

        assertEquals(0, quantity.value());
    }

    @Test
    void quantityAcceptsPositiveValue() {
        Quantity quantity = new Quantity(3);

        assertEquals(3, quantity.value());
    }

    @Test
    void phoneNumberRejectsInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("abc@def!"));
    }

    @Test
    void phoneNumberAcceptsSupportedFormat() {
        PhoneNumber phoneNumber = new PhoneNumber("+84 (028) 123-4567");

        assertEquals("+84 (028) 123-4567", phoneNumber.value());
    }
}
