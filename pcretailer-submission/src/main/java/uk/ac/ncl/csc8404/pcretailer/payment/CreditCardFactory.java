package uk.ac.ncl.csc8404.pcretailer.payment;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Factory that guarantees unique credit card numbers within its scope.
 */
public final class CreditCardFactory {

    // same card number twice is bad idea so we block it

    private final Set<String> allocatedNumbers = new TreeSet<>();

    /**
     * Registers a credit card with the supplied data.
     *
     * @param number     eight digit unique number.
     * @param expiryDate expiry date; copied defensively.
     * @param holderName holder name; must be non-blank.
     * @return immutable {@link CreditCard}.
     * @throws IllegalStateException if the number has already been allocated.
     */
    public CreditCard register(String number, Date expiryDate, String holderName) {
        String safeNumber = validateNumber(number);
        Objects.requireNonNull(expiryDate, "expiryDate");
        String safeHolder = requireText("holderName", holderName);
        if (!allocatedNumbers.add(safeNumber)) {
            throw new IllegalStateException("credit card number already allocated: " + safeNumber);
        }
        return new CreditCard(safeNumber, new Date(expiryDate.getTime()), safeHolder);
    }

    /**
     * Releases a card number making it available for future use.
     *
     * @param card the card to release.
     */
    public void release(CreditCard card) {
        if (card != null) {
            allocatedNumbers.remove(card.getNumber());
        }
    }

    private static String validateNumber(String number) {
        if (number == null) {
            throw new IllegalArgumentException("number is required");
        }
        String trimmed = number.trim();
        if (!trimmed.matches("\\d{8}")) {
            throw new IllegalArgumentException("number must be exactly 8 digits");
        }
        return trimmed;
    }

    private static String requireText(String label, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must be non-blank");
        }
        return value.trim();
    }
}
