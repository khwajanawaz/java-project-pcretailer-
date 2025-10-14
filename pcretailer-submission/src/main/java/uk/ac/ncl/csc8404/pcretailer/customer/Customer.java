package uk.ac.ncl.csc8404.pcretailer.customer;

import java.util.Objects;

/**
 * Immutable representation of a retailer customer.
 */
public final class Customer {

    // gotta keep track of customer ids or else i get lost

    private final String identifier;
    private final String displayName;

    private Customer(String identifier, String displayName) {
        this.identifier = identifier;
        this.displayName = displayName;
    }

    /**
     * Factory method that performs basic validation.
     *
     * @param identifier internal identifier; must be non-blank.
     * @param displayName friendly name; must be non-blank.
     * @return customer instance.
     */
    public static Customer of(String identifier, String displayName) {
        String safeId = requireText("identifier", identifier);
        String safeName = requireText("displayName", displayName);
        return new Customer(safeId, safeName);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer customer)) {
            return false;
        }
        return identifier.equals(customer.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "identifier='" + identifier + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }

    private static String requireText(String label, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must be non-blank");
        }
        return value.trim();
    }
}
