package uk.ac.ncl.csc8404.pcretailer.customer;

import java.util.Objects;

/**
 * Immutable representation of a retailer customer.
 */
public final class Customer {

    // Customer tracking - learned this the hard way after mixing up IDs in my last project!
    private final String identifier;
    private final String displayName;

    // Private constructor to force factory method usage
    private Customer(String identifier, String displayName) {
        this.identifier = identifier;
        this.displayName = displayName;
    }

    /**
     * Creates a new customer instance with validation.
     *
     * @param identifier internal ID - should be unique but we're not enforcing that here
     * @param displayName what we show to users
     * @return new Customer object
     * @throws IllegalArgumentException if either param is null/empty
     */
    public static Customer of(String identifier, String displayName) {
        // Let's validate these inputs properly
        String cleanId = validateAndClean("identifier", identifier);
        String cleanName = validateAndClean("displayName", displayName);

        return new Customer(cleanId, cleanName);
    }

    // Standard getters
    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Override equals - only comparing by identifier since that should be unique
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Customer other = (Customer) obj;
        return Objects.equals(identifier, other.identifier);
    }

    @Override
    public int hashCode() {
        // Only using identifier for hash since that's what we compare in equals
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        // Keeping it simple for debugging
        return String.format("Customer{id='%s', name='%s'}", identifier, displayName);
    }

    /**
     * Helper method for input validation
     * Note: Could probably extract this to a utility class if we use it elsewhere
     */
    private static String validateAndClean(String fieldName, String input) {
        if (input == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty or just whitespace");
        }

        return trimmed;
    }
}