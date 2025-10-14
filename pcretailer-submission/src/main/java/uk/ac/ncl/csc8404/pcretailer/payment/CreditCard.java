package uk.ac.ncl.csc8404.pcretailer.payment;

import java.util.Date;
import java.util.Objects;

/**
 * Immutable representation of a credit card used for payment.
 */
public final class CreditCard {

    //  clone date so we dont leak the real expiry outside

    private final String number;
    private final Date expiry;
    private final String holderName;

    CreditCard(String number, Date expiry, String holderName) {
        this.number = number;
        this.expiry = new Date(expiry.getTime());
        this.holderName = holderName;
    }

    /**
     * @return the eight digit card number.
     */
    public String getNumber() {
        return number;
    }

    /**
     * @return defensive copy of the expiry date.
     */
    public Date getExpiry() {
        return new Date(expiry.getTime());
    }

    public String getHolderName() {
        return holderName;
    }

    /**
     * Checks whether the card is valid relative to the supplied date.
     *
     * @param onDate the date to validate against.
     * @return {@code true} if the card is not expired on that date.
     */
    public boolean isValid(Date onDate) {
        Objects.requireNonNull(onDate, "onDate");
        return !expiry.before(onDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreditCard card)) {
            return false;
        }
        return number.equals(card.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "number='" + number + '\'' +
                ", expiry=" + expiry +
                ", holderName='" + holderName + '\'' +
                '}';
    }
}
